package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.num
import okhttp3.Interceptor
import okhttp3.Response

class GotoInterceptor() : Interceptor {
    var num = 1

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
           // response.
            val code = response.headers("Location").toString()
            if (code.contains("code=")) SharePrefs.Save("code",code)

          //  Log.d("响应tou",response.headers.toString())
            if (response.headers.toString().contains("synjones")) {
            var key =   response.headers("Location").toString()
                key = key.substringAfter("synjones-auth=")
                key = key.substringBefore("]")
                SharePrefs.Save("auth",key)
                if(num == 1) {
                    MyToast("一卡通登录成功")
                    num++
                }
            }
            //response.body?.let { Log.d("响应", it.string()) }
            return response
        }
}