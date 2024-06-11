package com.hfut.schedule.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.datamodel.zjgd.ReturnCard
import com.hfut.schedule.logic.network.ServiceCreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb.getIdentifyID
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UIViewModel : ViewModel()  {
    private val Gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val LoginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    val findNewCourse = MutableLiveData<Boolean>()
    var CardValue = MutableLiveData<ReturnCard>()
    var CardAuth = MutableLiveData<String?>(prefs.getString("auth",""))


    fun getUpdate() {

        val call = Gitee.getUpdate()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
              //  UpdateValue.value = response.body()?.string()
                Save("versions",response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val resultValue = MutableLiveData<String?>()
    val username = prefs.getString("Username","")
    fun loginWeb() {

        val call = username?.let { getIdentifyID()?.let { it1 -> LoginWeb.loginWeb(it, it1,"宣州Login") } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    resultValue.value = response?.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    resultValue.value = "Error"
                }
            })
        }
    }
    fun loginWeb2() {
        val call = username?.let { getIdentifyID()?.let { it1 -> LoginWeb.loginWeb2(it, it1,"宣州Login") } }
        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
        }
    }
    fun logoutWeb() {
        val call =  LoginWeb.logoutWeb()
        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    resultValue.value = response?.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    resultValue.value = "Error"
                }
            })
        }
    }
    val infoValue = MutableLiveData<String?>()
    fun getWebInfo() {
        val call =  LoginWeb.getInfo()
        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    infoValue.value = response?.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    infoValue.value = "Error"
                }
            })
        }
    }

}