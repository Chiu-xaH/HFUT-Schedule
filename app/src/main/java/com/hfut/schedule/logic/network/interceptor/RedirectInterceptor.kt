package com.hfut.schedule.logic.network.interceptor

import android.preference.PreferenceManager
import android.util.Log
import com.hfut.schedule.MyApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

    class RedirectInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.isRedirect) {
                val location = response.header("Location").toString()
                val newRequest = request.newBuilder()
                    .url(location)
                    .build()
                val newResponse = chain.proceed(newRequest)
                val str = newResponse.headers.toString()
               // Log.d("测试1",str)

                val regex = "Set-Cookie: ([^;]+);".toRegex()
                val matches = regex.findAll(str)
                val sb = StringBuilder()

                for (match in matches) {
                    val cookie = match.groupValues[1]
                    sb.append(cookie).append("; ")
                }
                //优化代码
               // Log.d("测试1",sb.toString())
if(sb.length > 30 ) {
    sb.delete(sb.length - 2, sb.length)
    val redirectcookie = sb.toString()

    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getString("redirect", "") != redirectcookie) {
        sp.edit().putString("redirect", redirectcookie).apply()
    }

  //  sp.getString("redirect", "")?.let { Log.d("测试11", it) }
}
            }
            return response
        }



    }
