package com.hfut.schedule.logic.network.repo

import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object NetWork {
    //引入接口
    // 通用的网络请求方法，支持自定义的操作
    @JvmStatic
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: (MutableLiveData<T>)? = null,
        onSuccess: ((Response<ResponseBody>) -> Unit)? = null
    ) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && liveData != null) {
                    val responseBody = response.body()?.string()
                    val result: T? = parseResponse(responseBody)
                    liveData.value = result
                }

                // 执行自定义操作
                onSuccess?.invoke(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // 通用方法用于解析响应（根据需要进行调整）
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }
}