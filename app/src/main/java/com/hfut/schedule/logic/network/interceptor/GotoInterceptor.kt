package com.hfut.schedule.logic.network.interceptor

import android.preference.PreferenceManager
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
            //Log.d("响应tou",request.url.toString())
          //  Log.d("请求",request.body.toString())
            //response.body?.let { Log.d("响应", it.string()) }
            return response
        }



}