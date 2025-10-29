package com.hfut.schedule.logic.network.repo.hfut

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.model.guagua.UseCodeResponse
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.ui.screen.shower.home.function.StatusMsgResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.util.Locale

object GuaGuaRepository {
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)

    fun getGuaGuaUserInfo(guaGuaUserInfo : MutableLiveData<String?>) {
        val loginCode = SharedPrefs.prefs.getString("loginCode","") ?: ""
        val phoneNumber = SharedPrefs.prefs.getString("PHONENUM","") ?: ""
        val call = phoneNumber.let { loginCode.let { it1 -> guaGua.getUserInfo(it, it1) } }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                guaGuaUserInfo.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    suspend fun guaGuaLogin(phoneNumber : String, password : String,loginResult : StateHolder<GuaGuaLoginResponse>) =
        launchRequestState(
            holder = loginResult,
            request = { guaGua.login(phoneNumber, password) },
            transformSuccess = { _, json -> parseGuaGuaLogin(json) },
        )

    suspend fun guaGuaStartShower(phoneNumber: String, macLocation : String, loginCode : String,startShowerResult : StateHolder<String>) =
        launchRequestState(
            holder = startShowerResult,
            request = {
                guaGua.startShower(
                    phoneNumber = phoneNumber,
                    loginCode = loginCode,
                    macLocation = macLocation
                )
            },
            transformSuccess = { _, json -> parseGuaGuaStartShower(json) },
        )

    suspend fun guaGuaGetBills(billsResult : StateHolder<GuaguaBillsResponse>) =
        launchRequestState(
            holder = billsResult,
            request = {
                guaGua.getBills(
                    phoneNumber = SharedPrefs.prefs.getString("PHONENUM", "") ?: "",
                    loginCode = SharedPrefs.prefs.getString("loginCode", "") ?: ""
                )
            },
            transformSuccess = { _, json -> parseGuaGuaBills(json) },
        )

    suspend fun guaGuaGetUseCode(useCodeResult : StateHolder<String>) = launchRequestState(
        holder = useCodeResult,
        request = {
            guaGua.getUseCode(
                phoneNumber = SharedPrefs.prefs.getString("PHONENUM", "") ?: "",
                loginCode = SharedPrefs.prefs.getString("loginCode", "") ?: ""
            )
        },
        transformSuccess = { _, json -> parseGuaGuaUseCode(json) }
    )

    suspend fun guaGuaReSetUseCode(newCode : String,reSetCodeResult : StateHolder<String>) =
        launchRequestState(
            holder = reSetCodeResult,
            request = {
                val psk = SharedPrefs.prefs.getString("GuaGuaPsk", "") ?: ""
                val encrypted = Crypto.md5Hash(psk).uppercase(Locale.ROOT)
                guaGua.reSetUseCode(
                    phoneNumber = SharedPrefs.prefs.getString("PHONENUM", "") ?: "",
                    encrypted,
                    loginCode = SharedPrefs.prefs.getString("loginCode", "") ?: "",
                    newCode
                )
            },
            transformSuccess = { _, json -> parseGuaGuaReSetUseCode(json) }
        )

    @JvmStatic
    private fun parseGuaGuaLogin(result: String): GuaGuaLoginResponse = try {
        val data = Gson().fromJson(result, GuaGuaLoginResponse::class.java)
        if(data.message.contains("成功")) {
            SharedPrefs.saveString("GuaGuaPersonInfo", result)
            SharedPrefs.saveString("loginCode", data.data?.loginCode)
        }
        data
    } catch (e: Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaStartShower(result: String): String = try {
        Gson().fromJson(result, StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaBills(result: String) : GuaguaBillsResponse = try {
        Gson().fromJson(result, GuaguaBillsResponse::class.java)
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaUseCode(result: String) : String = try {
        if(result.contains("成功"))
            Gson().fromJson(result, UseCodeResponse::class.java).data.randomCode
        else throw Exception("解析错误")
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaReSetUseCode(result: String) : String = try {
        Gson().fromJson(result, StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }

}