package com.hfut.schedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.beans.zjgd.ReturnCard
import com.hfut.schedule.logic.network.NetWork
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.getIdentifyID
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.WebInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UIViewModel : ViewModel()  {
    private val Gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val LoginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val LoginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)

    val findNewCourse = MutableLiveData<Boolean>()
    var CardValue = MutableLiveData<ReturnCard>()
    var electricValue = MutableLiveData<String?>()
    var webValue = MutableLiveData<WebInfo>()

    fun getUpdate() {

        val call = Gitee.getUpdate()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("versions",response.body()?.string())
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
    val result2Value = MutableLiveData<String?>()
    fun loginWeb2() {

        val call = username?.let { getIdentifyID()?.let { it1 -> LoginWeb2.loginWeb(it, it1,"宣州Login") } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    result2Value.value = response?.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    result2Value.value = "Error"
                }
            })
        }
    }

    fun logoutWeb() {
        val call =  LoginWeb.logoutWeb()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                resultValue.value = response.body()?.string()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                resultValue.value = "Error"
            }
        })
    }
    val infoValue = MutableLiveData<String?>()
    fun getWebInfo() {
        val call =  LoginWeb.getInfo()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                infoValue.value = response.body()?.string()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                infoValue.value = "Error"
            }
        })
    }
    fun getWebInfo2() {
        val call =  LoginWeb2.getInfo()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                infoValue.value = response.body()?.string()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                infoValue.value = "Error"
            }
        })
    }
}