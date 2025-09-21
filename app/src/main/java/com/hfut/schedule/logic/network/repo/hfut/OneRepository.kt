package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.model.PayData
import com.hfut.schedule.logic.model.PayResponse
import com.hfut.schedule.logic.model.one.BuildingBean
import com.hfut.schedule.logic.model.one.BuildingResponse
import com.hfut.schedule.logic.model.one.ClassroomBean
import com.hfut.schedule.logic.model.one.ClassroomResponse
import com.hfut.schedule.logic.model.one.getTokenResponse
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.network.servicecreator.OneServiceCreator
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.one.mail.MailResponse
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

object OneRepository {
    private val one = OneServiceCreator.create(OneService::class.java)

    suspend fun getPay(holder : StateHolder<PayData>) = launchRequestSimple(
        holder = holder,
        request = { one.getPay(getPersonInfo().studentId).awaitResponse() },
        transformSuccess = { _, json -> parsePayFee(json) }
    )
    @JvmStatic
    private fun parsePayFee(result : String) : PayData = try {
        Gson().fromJson(result, PayResponse::class.java).data ?: throw Exception("数据为空")
    } catch (e : Exception) { throw e }

    suspend fun getMailURL(token : String,holder : StateHolder<MailResponse>)  =
        launchRequestSimple(
            holder = holder,
            request = {
                val secret = Crypto.generateRandomHexString()
                val email = getSchoolEmail() ?: ""
                val chipperText = Crypto.encryptAesECB(email, secret)
                val cookie = "secret=$secret"
                one.getMailURL(chipperText, token, cookie).awaitResponse()
            },
            transformSuccess = { _, json -> parseMailUrl(json) }
        )
    @JvmStatic
    private fun parseMailUrl(result: String) : MailResponse = try {
        if(result.contains("success"))
            Gson().fromJson(result, MailResponse::class.java)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    suspend fun getClassroomInfo(code : String,token : String,holder : StateHolder<List<ClassroomBean>>)  =
        launchRequestSimple(
            holder = holder,
            request = { one.getClassroomInfo(code, token).awaitResponse() },
            transformSuccess = { _, json -> parseClassroom(json) }
        )
    @JvmStatic
    private fun parseClassroom(result: String) : List<ClassroomBean> = try {
        if(result.contains("success"))
            Gson().fromJson(result, ClassroomResponse::class.java).data.records
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    suspend fun getBuildings(campus : Campus, token : String, holder: StateHolder<Pair<Campus, List<BuildingBean>>>)  =
        launchRequestSimple(
            holder = holder,
            request = {
                val code = when (campus) {
                    Campus.XC -> "03"
                    Campus.FCH -> "02"
                    Campus.TXL -> "01"
                }
                one.getBuildings(code, token).awaitResponse()
            },
            transformSuccess = { _, json -> parseBuildings(campus, json) }
        )
    @JvmStatic
    private fun parseBuildings(campus: Campus, result: String) : Pair<Campus, List<BuildingBean>> = try {
        if(result.contains("success"))
            Pair(campus, Gson().fromJson(result, BuildingResponse::class.java).data)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    suspend fun checkOneLogin(token : String,holder : StateHolder<Boolean>) = launchRequestSimple(
        holder = holder,
        request = { one.checkLogin(token).awaitResponse() },
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

    fun loginOne(code : String)  {
        val call = one.getToken(code,code.substringAfter("code="))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                try {
                    val data = Gson().fromJson(json, getTokenResponse::class.java)
                    if (data.msg.contains("success")) {
                        SharedPrefs.saveString("bearer", "Bearer " + data.data.access_token)
                        showToast("信息门户登陆成功")
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showToast("信息门户登陆失败")
                t.printStackTrace()
            }
        })
    }
}