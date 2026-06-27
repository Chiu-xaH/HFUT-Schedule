package com.hfut.schedule.viewmodel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfut.schedule.logic.database.entity.ElectricBalanceRecordEntity
import com.hfut.schedule.logic.database.model.ElectricUsageSummary
import com.hfut.schedule.logic.database.repository.ElectricHistoryRepository
import com.hfut.schedule.logic.database.util.ElectricUsageCalculator
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ElectricChartMode {
    BALANCE,
    CONSUMPTION_RATE
}

enum class ElectricHistoryRange {
    SEVEN_DAYS,
    THIRTY_DAYS,
    ALL
}

data class ElectricHistoryUiState(
    val meterKey: String? = null,
    val roomName: String = "",
    val allRecords: List<ElectricBalanceRecordEntity> = emptyList(),
    val filteredRecords: List<ElectricBalanceRecordEntity> = emptyList(),
    val summary: ElectricUsageSummary? = null,
    val chartMode: ElectricChartMode = ElectricChartMode.BALANCE,
    val range: ElectricHistoryRange = ElectricHistoryRange.THIRTY_DAYS,
    val recent7DaysConsumption: Double = 0.0,
    val totalRecordCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class ElectricHistoryViewModel : ViewModel() {

    private val _meterKey = MutableStateFlow<String?>(null)
    private val _roomName = MutableStateFlow("")
    private val _selectedRange = MutableStateFlow(ElectricHistoryRange.THIRTY_DAYS)
    private val _chartMode = MutableStateFlow(ElectricChartMode.BALANCE)

    val uiState: StateFlow<ElectricHistoryUiState> =
        combine(
            _meterKey,
            _roomName,
            _selectedRange,
            _chartMode
        ) { key, roomName, range, mode ->
            ElectricHistoryUiState(
                meterKey = key,
                roomName = roomName,
                chartMode = mode,
                range = range
            )
        }.flatMapLatest { state ->
            val key = state.meterKey
            if (key == null) {
                flowOf(state)
            } else {
                ElectricHistoryRepository.observeHistory(key).map { records ->
                    val compactRecords = compactConsecutiveDuplicateBalances(records)
                    val filtered = filterRecordsByRange(compactRecords, state.range)
                    val summary = try {
                        ElectricUsageCalculator.calculate(filtered)
                    } catch (e: IllegalArgumentException) {
                        LogUtil.error(e, "计算电费历史摘要失败")
                        null
                    }
                    val recent7Days = try {
                        ElectricUsageCalculator.calculateRecent7DaysConsumption(compactRecords)
                    } catch (e: IllegalArgumentException) {
                        LogUtil.error(e, "计算电费历史统计失败")
                        0.0
                    }
                    state.copy(
                        allRecords = compactRecords,
                        filteredRecords = filtered,
                        summary = summary,
                        recent7DaysConsumption = recent7Days,
                        totalRecordCount = compactRecords.size
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ElectricHistoryUiState()
        )

    fun setMeterKey(meterKey: String?, roomName: String) {
        _meterKey.value = meterKey
        _roomName.value = roomName
    }

    fun setRange(range: ElectricHistoryRange) {
        _selectedRange.value = range
    }

    fun setChartMode(mode: ElectricChartMode) {
        _chartMode.value = mode
    }

    fun clearHistory() {
        val key = _meterKey.value ?: return
        viewModelScope.launch {
            ElectricHistoryRepository.clearHistory(key)
        }
    }

    private fun filterRecordsByRange(
        records: List<ElectricBalanceRecordEntity>,
        range: ElectricHistoryRange
    ): List<ElectricBalanceRecordEntity> {
        if (range == ElectricHistoryRange.ALL) return records

        val now = System.currentTimeMillis()
        val durationMs = when (range) {
            ElectricHistoryRange.SEVEN_DAYS -> 7 * 24 * 60 * 60 * 1000L
            ElectricHistoryRange.THIRTY_DAYS -> 30 * 24 * 60 * 60 * 1000L
            ElectricHistoryRange.ALL -> return records
        }
        val cutoff = now - durationMs
        return records.filter { it.sampledAt >= cutoff }
    }

    private fun compactConsecutiveDuplicateBalances(
        records: List<ElectricBalanceRecordEntity>
    ): List<ElectricBalanceRecordEntity> {
        if (records.size < 2) return records

        val sorted = records.sortedBy { it.sampledAt }
        val compacted = mutableListOf<ElectricBalanceRecordEntity>()
        var pending = sorted.first()

        for (record in sorted.drop(1)) {
            val sameBalance = kotlin.math.abs(record.remainingBalance - pending.remainingBalance) <= 0.005
            if (sameBalance) {
                pending = record
            } else {
                compacted.add(pending)
                pending = record
            }
        }
        compacted.add(pending)
        return compacted
    }
}
