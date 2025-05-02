package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.NetWork
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
        transform = { _,json -> parseGuaguaLogin(json) },
    )

    val startShowerData = SimpleStateHolder<String>()
    suspend fun startShower(phoneNumber: String, macLocation : String, loginCode : String) = launchRequestSimple(
        holder = startShowerData,
        request = { guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation).awaitResponse() },
        transform = { _,json -> parseStartShower(json) },
    )

    var billsResult = MutableLiveData<String?>()
    fun getBills() {
        val call = guaGua.getBills(phoneNumber, loginCode)
        NetWork.makeRequest(call,billsResult)
    }

    var userCode = MutableLiveData<String?>()
    fun getUseCode() = NetWork.makeRequest(guaGua.getUseCode(phoneNumber,loginCode),userCode)

    var reSetCodeResult = MutableLiveData<String?>()
    fun reSetUseCode(newCode : String) {
        val psk = prefs.getString("GuaGuaPsk","") ?: ""
        val encrypted = Encrypt.md5Hash(psk).uppercase(Locale.ROOT)
        val call = guaGua.reSetUseCode(
            phoneNumber,
            encrypted,
            loginCode,
            newCode,
        )
        NetWork.makeRequest(call,reSetCodeResult)
    }

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
}

