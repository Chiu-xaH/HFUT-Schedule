package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.model.guagua.UseCodeResponse
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.NetWork.launchRequestSimple
import com.hfut.schedule.logic.util.network.SimpleStateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.screen.shower.home.function.StatusMsgResponse
import retrofit2.awaitResponse
import java.util.Locale

// 5个函数
class GuaGuaViewModel : ViewModel() {
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val loginCode = prefs.getString("loginCode","") ?: ""
    private val phoneNumber = prefs.getString("PHONENUM","") ?: ""

    var loginResult = SimpleStateHolder<GuaGuaLoginResponse>()
    suspend fun login(phoneNumber : String, password : String) = launchRequestSimple(
        holder = loginResult,
        request = { guaGua.login(phoneNumber,password).awaitResponse() },
        transformSuccess = { _, json -> parseGuaguaLogin(json) },
    )

    val startShowerResult = SimpleStateHolder<String>()
    suspend fun startShower(phoneNumber: String, macLocation : String, loginCode : String) = launchRequestSimple(
        holder = startShowerResult,
        request = { guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation).awaitResponse() },
        transformSuccess = { _, json -> parseStartShower(json) },
    )

    var billsResult = SimpleStateHolder<GuaguaBillsResponse>()
    suspend fun getBills() = launchRequestSimple(
        holder = billsResult,
        request = { guaGua.getBills(phoneNumber, loginCode).awaitResponse() },
        transformSuccess = { _, json -> parseBills(json) },
    )

    var useCodeResult = SimpleStateHolder<String>()
    suspend fun getUseCode() = launchRequestSimple(
        holder = useCodeResult,
        request = { guaGua.getUseCode(phoneNumber,loginCode).awaitResponse() },
        transformSuccess = { _, json -> parseUseCode(json) }
    )

    var reSetCodeResult = SimpleStateHolder<String>()
    suspend fun reSetUseCode(newCode : String) = launchRequestSimple(
        holder = reSetCodeResult,
        request = {
            val psk = prefs.getString("GuaGuaPsk","") ?: ""
            val encrypted = Encrypt.md5Hash(psk).uppercase(Locale.ROOT)
            guaGua.reSetUseCode(phoneNumber, encrypted, loginCode, newCode,).awaitResponse() },
        transformSuccess = { _, json -> parseReSetUseCode(json) }
    )

    private fun parseGuaguaLogin(result: String): GuaGuaLoginResponse? = try {
        val data = Gson().fromJson(result, GuaGuaLoginResponse::class.java)
        if(data.message.contains("成功")) {
            saveString("GuaGuaPersonInfo",result)
            saveString("loginCode",data.data?.loginCode)
        }
        data
    } catch (e: Exception) {
        null
    }

    private fun parseStartShower(result: String): String? = try {
        Gson().fromJson(result, StatusMsgResponse::class.java).message
    } catch (e : Exception) {
        null
    }
    private fun parseBills(result: String) : GuaguaBillsResponse? = try {
        Gson().fromJson(result, GuaguaBillsResponse::class.java)
    } catch (e : Exception) {
        null
    }
    private fun parseUseCode(result: String) : String? = try {
        Gson().fromJson(result,UseCodeResponse::class.java).data.randomCode
    } catch (e : Exception) {
        null
    }
    private fun parseReSetUseCode(result: String) : String? = try {
        Gson().fromJson(result,StatusMsgResponse::class.java).message
    } catch (e : Exception) {
        null
    }
}

