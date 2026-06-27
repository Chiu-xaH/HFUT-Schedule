package com.hfut.schedule.logic.database.repository

import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.ElectricBalanceRecordEntity
import com.hfut.schedule.logic.database.util.ElectricMeterKeyFactory
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

object ElectricHistoryRepository {

    private val dao get() = DataBaseManager.electricBalanceRecordDao

    private const val DUPLICATE_THRESHOLD = 0.005
    private const val DUPLICATE_TIME_MS = 60 * 60 * 1000L
    private const val EXPIRE_DAYS = 365

    private val recordMutex = Mutex()

    fun observeHistory(meterKey: String): Flow<List<ElectricBalanceRecordEntity>> {
        return dao.observeByMeter(meterKey)
    }

    suspend fun recordSnapshot(
        meterKey: String,
        campusRegion: String,
        roomName: String,
        balance: Double,
        sampledAt: Long = System.currentTimeMillis()
    ): Boolean = recordMutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                if (!ElectricMeterKeyFactory.isValid(meterKey)) {
                    LogUtil.error("meterKey格式无效，跳过保存: $meterKey")
                    return@withContext false
                }
                if (roomName.isBlank()) {
                    LogUtil.error("roomName为空，跳过保存")
                    return@withContext false
                }
                if (balance.isNaN() || balance.isInfinite()) {
                    LogUtil.error("余额数值无效: $balance，跳过保存")
                    return@withContext false
                }
                if (balance < -1000.0) {
                    LogUtil.error("余额数值异常: $balance，跳过保存")
                    return@withContext false
                }

                val latest = dao.getLatest(meterKey)
                if (latest != null) {
                    if (sampledAt < latest.sampledAt) {
                        LogUtil.error("采样时间早于最新记录，跳过保存: meterKey=$meterKey, sampledAt=$sampledAt, latestAt=${latest.sampledAt}")
                        return@withContext false
                    }
                    val balanceDiff = kotlin.math.abs(balance - latest.remainingBalance)
                    val timeDiff = sampledAt - latest.sampledAt
                    if (balanceDiff <= DUPLICATE_THRESHOLD && timeDiff in 0 until DUPLICATE_TIME_MS) {
                        LogUtil.debug("余额未变化且时间间隔过短，跳过保存: meterKey=$meterKey, balance=$balance")
                        return@withContext false
                    }
                }

                val record = ElectricBalanceRecordEntity(
                    meterKey = meterKey,
                    campusRegion = campusRegion,
                    roomName = roomName,
                    remainingBalance = balance,
                    sampledAt = sampledAt
                )
                dao.insert(record)
                LogUtil.info("电费余额快照已保存: meterKey=$meterKey, balance=$balance")

                try {
                    val expireBefore = System.currentTimeMillis() - EXPIRE_DAYS * 24 * 60 * 60 * 1000L
                    dao.deleteBefore(expireBefore)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    LogUtil.error(e, "清理过期电费记录失败")
                }

                true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LogUtil.error(e, "保存电费余额快照失败")
                false
            }
        }
    }

    suspend fun clearHistory(meterKey: String) = recordMutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                dao.deleteByMeter(meterKey)
                LogUtil.info("已清除电费历史: meterKey=$meterKey")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LogUtil.error(e, "清除电费历史失败: meterKey=$meterKey")
            }
        }
    }
}
