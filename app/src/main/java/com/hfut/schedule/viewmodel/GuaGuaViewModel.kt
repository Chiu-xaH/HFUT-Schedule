package com.hfut.schedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.utils.SharePrefs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuaGuaViewModel : ViewModel() {
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)

    var loginResult = MutableLiveData<String?>()
    fun login(phoneNumber : String,password : String) {
        val call = guaGua.login(phoneNumber,password)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                loginResult.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    var startShowerData = MutableLiveData<String?>()
    fun startShower(phoneNumber: String,macLocation : String,loginCode : String) {
        //val loginCode = SharePrefs.prefs.getString("loginCode","") ?: ""
        //val phoneNumber = SharePrefs.prefs.getString("PHONENUM","") ?: ""
        val call = guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                startShowerData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    var billsResult = MutableLiveData<String?>()
    fun getBills() {
        val loginCode = SharePrefs.prefs.getString("loginCode","") ?: ""
        val phoneNumber = SharePrefs.prefs.getString("PHONENUM","") ?: ""
        val call = guaGua.getBills(phoneNumber, loginCode)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                billsResult.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}