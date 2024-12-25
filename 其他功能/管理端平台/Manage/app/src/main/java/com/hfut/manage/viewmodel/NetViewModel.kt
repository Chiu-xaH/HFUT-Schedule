package com.hfut.manage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.manage.datamodel.enums.PrefsKey
import com.hfut.manage.logic.network.api.GithubService
import com.hfut.manage.logic.network.api.ServerService
import com.hfut.manage.logic.network.servicecreator.GithubServiceCreator
import com.hfut.manage.logic.network.servicecreator.ServerServiceCreator
import com.hfut.manage.logic.utils.SharePrefs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NetViewModel : ViewModel() {
    private val github = GithubServiceCreator.create(GithubService::class.java)
    private val server = ServerServiceCreator.create(ServerService::class.java)

    fun getURL() {
        val call = github.getData()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save(PrefsKey.GUTHUB.name,response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getUserData() {
        val call = server.getUserData()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save(PrefsKey.GUTHUB.name,response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    fun addData(type : String,title : String,info : String,remark : String,startTime : String,endTime : String) {
        val call = server.addData(type,title,info,remark, startTime, endTime)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //result.value = response?.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val result = MutableLiveData<String>()
    fun getData() {
        val call = server.getData()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                result.value = response?.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun deleteData(type : String,title: String) {
        val call = server.deleteData(type,title)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}