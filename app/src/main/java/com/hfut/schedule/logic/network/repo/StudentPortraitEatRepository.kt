package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.inf.OfficeHallService
import com.hfut.schedule.network.api.impl.OfficeHallServiceCreator
import com.hfut.schedule.network.api.inf.StudentPortraitEatService
import com.hfut.schedule.network.api.model.request.studentportrait.StudentPortraitEatRequest
import com.hfut.schedule.network.api.model.response.json.one.OneLoginResponse
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatAnnualRecord
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatAnnualResponse
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatAverage
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatAverageResponse
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatData
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatFrequencyResponse
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatFrequencyValue
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatOneDay
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatOneDayResponse
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatTop3Response
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatTop3Value
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatYearAverage
import com.hfut.schedule.network.api.repo.StudentPortraitEatRepositoryInf
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.state.UiStateHolder
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.awaitResponse

object StudentPortraitEatRepository : StudentPortraitEatRepositoryInf {
    private val service = OfficeHallServiceCreator.create(StudentPortraitEatService::class.java)
    private val officeHall = OfficeHallServiceCreator.create(OfficeHallService::class.java)

    override suspend fun getReport(
        authorization: String,
        holder: UiStateHolder<StudentPortraitEatData>
    ) {
        holder.setLoading()
        val value = authorization.trim()
        val token = if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substringAfter(' ').trim()
        } else {
            value
        }
        if (token.isEmpty()) {
            holder.emitError(
                IllegalStateException("信息门户学生画像登录状态失效"),
                StatusCode.UNAUTHORIZED.code
            )
            return
        }
        val normalizedAuthorization = "Bearer $token"
        val cookie = "TOKEN=$token"

        try {
            val data = withContext(Dispatchers.IO) {
                val top3 = parseTop3(
                    fetch(service.getTop3(normalizedAuthorization, cookie, body = StudentPortraitEatRequest()))
                )
                fetchReport(
                    authorization = normalizedAuthorization,
                    cookie = cookie,
                    top3 = top3
                )
            }
            holder.emitData(data)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            holder.emitError(e, e.code())
        } catch (e: StudentPortraitResponseException) {
            holder.emitError(e, e.responseCode)
        } catch (e: StudentPortraitParseException) {
            holder.emitError(e, PARSE_ERROR_CODE)
        } catch (e: Exception) {
            holder.emitError(e)
        }
    }

    override suspend fun loginEhall(code: String) {
        try {
            withContext(Dispatchers.IO) {
                val response = officeHall.getToken(
                    redirect = code,
                    code = code.substringAfter("code=").substringBefore("&")
                ).awaitResponse()
                if (!response.isSuccessful) throw HttpException(response)
                val json = response.body()?.string()
                    ?: throw IllegalStateException("信息门户消费画像登录响应为空")
                val data = GsonInstance.fromJson(json, OneLoginResponse::class.java)
                if (!data.msg.contains("success", ignoreCase = true)) {
                    throw IllegalStateException(data.msg)
                }
                DataStoreManager.saveEhallBearer("Bearer ${data.data.token}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            showToast("信息门户消费画像登录失败 ${e.code()}")
            LogUtil.error(e)
        } catch (e: Exception) {
            showToast("信息门户消费画像登录失败")
            LogUtil.error(e)
        }
    }

    private suspend fun fetchReport(
        authorization: String,
        cookie: String,
        top3: StudentPortraitEatTop3Value
    ): StudentPortraitEatData {
        val personInfo = withContext(Dispatchers.Default) { getPersonInfo() }
        val enrollmentYear = personInfo.startDate
            ?.take(4)
            ?.toIntOrNull()
            ?: personInfo.getStudentIdFinally()?.take(4)?.toIntOrNull()
        val studyDurationYears = personInfo.studyTime
            ?.toDoubleOrNull()
            ?.toInt()
        val annualRequest = StudentPortraitEatRequest(
            year = null,
            enrollmentYear = enrollmentYear,
            studyDurationYears = (studyDurationYears ?: 4).toString()
        )
        val commonRequest = StudentPortraitEatRequest()

        return coroutineScope {
            val annual = async {
                if (enrollmentYear == null) {
                    emptyList()
                } else {
                    parseAnnual(fetch(service.getAnnual(authorization, cookie, body = annualRequest)))
                }
            }
            val frequency = async {
                parseFrequency(fetch(service.getFrequency(authorization, cookie, body = commonRequest)))
            }
            val oneDay = async {
                parseOneDay(fetch(service.getOneDay(authorization, cookie, body = commonRequest)))
            }
            val average = async {
                parseAverage(fetch(service.getAverage(authorization, cookie, body = commonRequest)))
            }

            val annualData = annual.await()
            val yearAverages = annualData
                .mapNotNull { it.academicYear?.takeIf(String::isNotBlank) }
                .distinct()
                .map { academicYear ->
                    val yearQuery = academicYear.trim().take(4).toIntOrNull()?.toString()
                        ?: academicYear.trim()
                    async {
                        try {
                            StudentPortraitEatYearAverage(
                                academicYear = academicYear,
                                average = parseAverage(
                                    fetch(
                                        service.getAverage(
                                            authorization,
                                            cookie,
                                            body = StudentPortraitEatRequest(year = yearQuery)
                                        )
                                    )
                                )
                            )
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            LogUtil.error(e, "${academicYear}学年消费均值加载失败")
                            null
                        }
                    }
                }
                .awaitAll()
                .filterNotNull()
            StudentPortraitEatData(
                top3 = top3,
                annual = annualData,
                frequency = frequency.await(),
                oneDay = oneDay.await(),
                average = average.await(),
                yearAverages = yearAverages,
                enrollmentYear = enrollmentYear
            )
        }
    }

    private suspend fun fetch(call: Call<ResponseBody>): String {
        val response = call.awaitResponse()
        if (!response.isSuccessful) throw HttpException(response)
        return response.body()?.string()?.takeIf(String::isNotBlank)
            ?: throw StudentPortraitParseException(
                IllegalStateException("信息门户学生画像消费接口返回为空")
            )
    }

    private fun parseTop3(json: String): StudentPortraitEatTop3Value {
        val response = parseJson<StudentPortraitEatTop3Response>(json)
        return requireValue(response.success, response.code, response.message, response.value)
    }

    private fun parseAnnual(json: String): List<StudentPortraitEatAnnualRecord> {
        val response = parseJson<StudentPortraitEatAnnualResponse>(json)
        return requireValue(response.success, response.code, response.message, response.value)
    }

    private fun parseFrequency(json: String): StudentPortraitEatFrequencyValue {
        val response = parseJson<StudentPortraitEatFrequencyResponse>(json)
        return requireValue(response.success, response.code, response.message, response.value)
    }

    private fun parseOneDay(json: String): StudentPortraitEatOneDay {
        val response = parseJson<StudentPortraitEatOneDayResponse>(json)
        return requireValue(response.success, response.code, response.message, response.value)
    }

    private fun parseAverage(json: String): StudentPortraitEatAverage {
        val response = parseJson<StudentPortraitEatAverageResponse>(json)
        return requireValue(response.success, response.code, response.message, response.value)
    }

    private inline fun <reified T> parseJson(json: String): T = try {
        GsonInstance.fromJson(json, T::class.java)
            ?: throw IllegalStateException("信息门户学生画像消费响应为空")
    } catch (e: Exception) {
        throw StudentPortraitParseException(e)
    }

    private fun <T> requireValue(
        success: Boolean?,
        code: Int?,
        message: String?,
        value: T?
    ): T {
        if (success != true) {
            throw StudentPortraitResponseException(
                message = message ?: "信息门户学生画像消费请求失败",
                responseCode = code
            )
        }
        return value ?: throw StudentPortraitResponseException(
            message = message ?: "信息门户学生画像消费数据为空",
            responseCode = code
        )
    }

    private class StudentPortraitResponseException(
        message: String,
        val responseCode: Int?
    ) : IllegalStateException(
        if (responseCode == null) message else "$message（code=$responseCode）"
    )

    private class StudentPortraitParseException(
        cause: Throwable
    ) : IllegalStateException(
        "信息门户学生画像消费数据解析失败",
        cause
    )
}
