package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.model.guagua.UseCodeResponse
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.screen.shower.home.function.StatusMsgResponse
import retrofit2.awaitResponse
import java.util.Locale

object GuaGuaRepository {
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)

    suspend fun guaGuaLogin(phoneNumber : String, password : String,loginResult : StateHolder<GuaGuaLoginResponse>) = launchRequestSimple(
        holder = loginResult,
        request = { guaGua.login(phoneNumber,password).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaLogin(json) },
    )

    suspend fun guaGuaStartShower(phoneNumber: String, macLocation : String, loginCode : String,startShowerResult : StateHolder<String>) = launchRequestSimple(
        holder = startShowerResult,
        request = { guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaStartShower(json) },
    )

    suspend fun guaGuaGetBills(billsResult : StateHolder<GuaguaBillsResponse>) = launchRequestSimple(
        holder = billsResult,
        request = { guaGua.getBills(phoneNumber= prefs.getString("PHONENUM","") ?: "", loginCode= prefs.getString("loginCode","") ?: "").awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaBills(json) },
    )

    suspend fun guaGuaGetUseCode(useCodeResult : StateHolder<String>) = launchRequestSimple(
        holder = useCodeResult,
        request = { guaGua.getUseCode(phoneNumber= prefs.getString("PHONENUM","") ?: "",loginCode= prefs.getString("loginCode","") ?: "").awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaUseCode(json) }
    )

    suspend fun guaGuaReSetUseCode(newCode : String,reSetCodeResult : StateHolder<String>) = launchRequestSimple(
        holder = reSetCodeResult,
        request = {
            val psk = prefs.getString("GuaGuaPsk","") ?: ""
            val encrypted = Encrypt.md5Hash(psk).uppercase(Locale.ROOT)
            guaGua.reSetUseCode(phoneNumber= prefs.getString("PHONENUM","") ?: "", encrypted, loginCode= prefs.getString("loginCode","") ?: "", newCode).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaReSetUseCode(json) }
    )

    @JvmStatic
    private fun parseGuaGuaLogin(result: String): GuaGuaLoginResponse = try {
        val data = Gson().fromJson(result, GuaGuaLoginResponse::class.java)
        if(data.message.contains("成功")) {
            saveString("GuaGuaPersonInfo",result)
            saveString("loginCode",data.data?.loginCode)
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
            Gson().fromJson(result,UseCodeResponse::class.java).data.randomCode
        else throw Exception("解析错误")
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaReSetUseCode(result: String) : String = try {
        Gson().fromJson(result,StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }

}