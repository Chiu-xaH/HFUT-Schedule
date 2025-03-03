package com.hfut.schedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.NetWork
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.utils.parse.Encrypt
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class GuaGuaViewModel : ViewModel() {
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val loginCode = SharePrefs.prefs.getString("loginCode","") ?: ""
    private val phoneNumber = SharePrefs.prefs.getString("PHONENUM","") ?: ""

    var loginResult = MutableLiveData<String?>()
    fun login(phoneNumber : String,password : String) = NetWork.makeRequest(guaGua.login(phoneNumber,password),loginResult)

    var startShowerData = MutableLiveData<String?>()
    fun startShower(phoneNumber: String,macLocation : String,loginCode : String) = NetWork.makeRequest(guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation),startShowerData)

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
}