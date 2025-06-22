package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import okhttp3.Interceptor
import okhttp3.Response

class GotoInterceptor : Interceptor {
    var num = 1

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
           // response.
            val code = response.headers("Location").toString()
            if (code.contains("code=")) SharedPrefs.saveString("code",code)

          //  Log.d("响应tou",response.headers.toString())
            if (response.headers.toString().contains("synjones")) {
            var key =   response.headers("Location").toString()
                key = key.substringAfter("synjones-auth=")
                key = key.substringBefore("]")
                SharedPrefs.saveString("auth",key)
                if(num == 1) {
                    showToast("一卡通登录成功")
                    num++
                }
            }
            //response.body?.let { Log.d("响应", it.string()) }
            return response
        }
}