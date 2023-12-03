package com.hfut.schedule.logic.network.interceptor

import android.preference.PreferenceManager
import android.util.Log
import com.hfut.schedule.MyApplication
import okhttp3.Interceptor
import okhttp3.Response

class GotoInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
           // response.
            val code = response.headers("Location").toString()
            if (code.contains("code=")) {

                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if (sp.getString("code", "") != code) {
                    sp.edit().putString("code", code).apply()
                }

            }
          //  Log.d("响应tou",response.headers.toString())
            if (response.headers.toString().contains("synjones")) {
            var key =   response.headers("Location").toString()
                key = key.substringAfter("synjones-auth=")
                key = key.substringBefore("]")
                Log.d("key",key)
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if (sp.getString("auth", "") != key) {
                    sp.edit().putString("auth", key).apply()
                }
            }

            //response.body?.let { Log.d("响应", it.string()) }
            return response
        }



}