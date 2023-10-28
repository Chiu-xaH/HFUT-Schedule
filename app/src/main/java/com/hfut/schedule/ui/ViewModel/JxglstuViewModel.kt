package com.hfut.schedule.ui.ViewModel

import android.os.Parcelable.Creator
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.ServiceCreator.JxglstuServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.LoginServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JxglstuViewModel : ViewModel() {
    val api = JxglstuServiceCreator.create(JxglstuService::class.java)
    var livedata = MutableLiveData<String>()

    fun jxglstu() {

        val call = api.Cookieget()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                livedata.value = body?.string()
                if(response.isSuccessful()){

                    Log.d("测试","成功")
                    Log.d("响应码", response.code().toString())
                    Log.d("响应头", response.headers().toString())
                    Log.d("响应信息",response.message())
                    Log.d("响应主体",response.body()?.toString()!!)

                }
                else {
                    Log.d("测试","失败")
                    Log.d("响应码", response.code().toString())
                    Log.d("响应头", response.headers().toString())
                    Log.d("响应信息",response.message())
                    Log.d("响应主体",response.body()?.toString()!!)
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }
}