package com.hfut.schedule.logic.network.repo


import com.xah.common.logic.model.Campus
import com.hfut.schedule.network.api.model.response.json.one.OneFeeData
import com.hfut.schedule.network.api.model.response.json.one.OneFeeResponse
import com.hfut.schedule.network.api.model.response.json.one.OneBuilding
import com.hfut.schedule.network.api.model.response.json.one.OneBuildingResponse
import com.hfut.schedule.network.api.model.response.json.one.OneClassroomRecord
import com.hfut.schedule.network.api.model.response.json.one.OneClassroomResponse
import com.hfut.schedule.network.api.model.response.json.one.OneLoginResponse
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.impl.OneFormServiceCreator
import com.hfut.schedule.network.api.impl.OneServiceCreator
import com.hfut.schedule.network.api.inf.OneFormService
import com.hfut.schedule.network.api.inf.OneService
import com.hfut.schedule.network.api.model.response.json.one.OneSchoolEmailResponse
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormStudentAchievementData
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormStudentAchievementResponse
import com.hfut.schedule.network.api.repo.OneRepositoryInf
import com.hfut.schedule.network.api.util.CryptoUtil
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.awaitResponse

object OneRepository : OneRepositoryInf {
    private val one = OneServiceCreator.create(OneService::class.java)
    private val oneForm = OneFormServiceCreator.create(OneFormService::class.java)

    override suspend fun getPay(holder : UiStateHolder<OneFeeData>) = launchRequestState(
        holder = holder,
        request = { one.getPay(getPersonInfo().getStudentIdFinally()) },
        transformSuccess = { _, json -> parsePayFee(json) }
    )
    @JvmStatic
    private fun parsePayFee(result : String) : OneFeeData = try {
        GsonInstance.fromJson(result, OneFeeResponse::class.java).data ?: throw Exception("数据为空")
    } catch (e : Exception) { throw e }

    override suspend fun getMailURL(token : String, holder : UiStateHolder<OneSchoolEmailResponse>)  =
        launchRequestState(
            holder = holder,
            request = {
                val secret = CryptoUtil.generateRandomHexString()
                val email = getSchoolEmail() ?: ""
                val chipperText = CryptoUtil.encryptAesECB(email, secret)
                val cookie = "secret=$secret"
                one.getMailURL(chipperText, token, cookie)
            },
            transformSuccess = { _, json -> parseMailUrl(json) }
        )
    @JvmStatic
    private fun parseMailUrl(result: String) : OneSchoolEmailResponse = try {
        if(result.contains("success"))
            GsonInstance.fromJson(result, OneSchoolEmailResponse::class.java)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    override suspend fun getClassroomInfo(code : String, token : String, holder : UiStateHolder<List<OneClassroomRecord>>)  =
        launchRequestState(
            holder = holder,
            request = { one.getClassroomInfo(code, token) },
            transformSuccess = { _, json -> parseClassroom(json) }
        )
    @JvmStatic
    private fun parseClassroom(result: String) : List<OneClassroomRecord> = try {
        if(result.contains("success"))
            GsonInstance.fromJson(result, OneClassroomResponse::class.java).data.records
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    override suspend fun getBuildings(campus : Campus, token : String, holder: UiStateHolder<Pair<Campus, List<OneBuilding>>>)  =
        launchRequestState(
            holder = holder,
            request = {
                val code = when (campus) {
                    Campus.XC -> "03"
                    Campus.FCH -> "02"
                    Campus.TXL -> "01"
                }
                one.getBuildings(code, token)
            },
            transformSuccess = { _, json -> parseBuildings(campus, json) }
        )
    @JvmStatic
    private fun parseBuildings(campus: Campus, result: String) : Pair<Campus, List<OneBuilding>> = try {
        if(result.contains("success"))
            Pair(campus, GsonInstance.fromJson(result, OneBuildingResponse::class.java).data)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    override suspend fun checkOneLogin(token : String, holder : UiStateHolder<Boolean>) = launchRequestState(
        holder = holder,
        request = { one.checkLogin(token) },
        transformSuccess = { _, json -> parseCheckOneLogin(json) }
    )
    @JvmStatic
    private fun parseCheckOneLogin(json : String) : Boolean = try {
        if(json.contains("success")) {
            true
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw  e }

    override suspend fun getStudentAchievement(
        token: String,
        holder: UiStateHolder<OneFormStudentAchievementData>
    ) {
        holder.setLoading()
        val authorization = normalizeOneFormAuthorization(token)
        if (authorization.isEmpty()) {
            holder.emitError(
                IllegalStateException("信息门户登录状态失效"),
                StatusCode.UNAUTHORIZED.code
            )
            return
        }

        try {
            val response = withContext(Dispatchers.IO) {
                val httpResponse = oneForm.getStudentAchievement(authorization).awaitResponse()
                if (!httpResponse.isSuccessful) throw HttpException(httpResponse)
                parseStudentAchievement(httpResponse.body()?.string().orEmpty())
            }

            when (response.code) {
                null -> holder.emitError(
                    IllegalStateException("信息门户综合报表响应缺少状态码"),
                    PARSE_ERROR_CODE
                )
                1 -> holder.emitData(response.data ?: OneFormStudentAchievementData())
                else -> holder.emitError(
                    IllegalStateException(response.msg ?: "信息门户综合报表请求失败"),
                    response.code
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            holder.emitError(e, e.code())
        } catch (e: OneFormParseException) {
            holder.emitError(e, PARSE_ERROR_CODE)
        } catch (e: Exception) {
            holder.emitError(e)
        }
    }

    private fun normalizeOneFormAuthorization(value: String): String {
        val authorization = value.trim()
        val rawToken = if (authorization.startsWith("Bearer ", ignoreCase = true)) {
            authorization.substringAfter(' ').trim()
        } else {
            authorization
        }
        return if (rawToken.isEmpty()) "" else "Bearer $rawToken"
    }

    private fun parseStudentAchievement(json: String): OneFormStudentAchievementResponse = try {
        GsonInstance.fromJson(json, OneFormStudentAchievementResponse::class.java)
            ?: throw IllegalStateException("一表通成绩响应为空")
    } catch (e: Exception) {
        LogUtil.error(e)
        throw OneFormParseException(e)
    }

    private class OneFormParseException(cause: Throwable) :
        IllegalStateException("一表通成绩解析失败", cause)

    override suspend fun loginOne(code : String)  {
        val response = try {
            one.getToken(code,code.substringAfter("code=")).awaitResponse()
        } catch (e: Exception) {
            showToast("信息门户登陆失败")
            LogUtil.error(e)
            return
        }

        try {
            val json = response.body()?.string()
            val data = GsonInstance.fromJson(json, OneLoginResponse::class.java)
            if (data.msg.contains("success")) {
                val bearer = "Bearer ${data.data.token}"
                try {
                    DataStoreManager.saveOneBearer(bearer)
                } catch (e: Exception) {
                    LogUtil.error(e)
                }
                showToast("信息门户登陆成功")
            }
        } catch (e : Exception) {
            LogUtil.error(e)
        }
    }

    override suspend fun loginOneForm(code: String) {
        try {
            withContext(Dispatchers.IO) {
                val response = oneForm.getToken(
                    redirect = code,
                    code = code.substringAfter("code=").substringBefore("&")
                ).awaitResponse()
                if (!response.isSuccessful) throw HttpException(response)
                val json = response.body()?.string()
                    ?: throw IllegalStateException("信息门户综合报表登录响应为空")
                val data = GsonInstance.fromJson(json, OneLoginResponse::class.java)
                if (!data.msg.contains("success", ignoreCase = true)) {
                    throw IllegalStateException(data.msg)
                }
                DataStoreManager.saveOneFormBearer("Bearer ${data.data.token}")
            }
            showToast("信息门户综合报表登录成功")
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            showToast("信息门户综合报表登录失败 ${e.code()}")
            LogUtil.error(e)
        } catch (e: Exception) {
            showToast("信息门户综合报表登录失败")
            LogUtil.error(e)
        }
    }

}
