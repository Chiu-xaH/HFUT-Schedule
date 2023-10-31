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

                val regex = "Set-Cookie: ([^;]+);".toRegex()
                val matches = regex.findAll(str)
                val sb = StringBuilder()
                for (match in matches) {
                    val cookie = match.groupValues[1]
                    sb.append(cookie).append("; ")
                }
                sb.delete(sb.length - 2, sb.length)
                val redirectcookie = sb.toString()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("redirect","") != redirectcookie){
                    sp.edit().putString("redirect", redirectcookie).apply()
                }
            }
            return response
        }



    }
