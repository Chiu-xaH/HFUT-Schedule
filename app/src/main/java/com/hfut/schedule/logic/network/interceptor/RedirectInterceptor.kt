package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import okhttp3.Interceptor
import okhttp3.Response

class RedirectInterceptor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.isRedirect) {
                val location = response.header("Location").toString()
//                Log.d("测试1",location)
                val ticket = location.substringAfter("=")

                SharedPrefs.saveString("TICKET",ticket)
                val newRequest = request.newBuilder()
                    .url(location)
                    .build()
                val newResponse = chain.proceed(newRequest)
                val str = newResponse.headers
                Log.d("测试0",str.toString())

//                val regex = "Set-Cookie: ([^;]+);".toRegex()
//                val matches = regex.findAll(str)
                val cookie = str.toString().substringAfter("SESSION=").substringBefore(";")
                Log.d("测试1",cookie.toString())
                SharedPrefs.saveString("redirect", "SESSION=$cookie")
//                cookie?.let {
//                    val jxglstuCookie = cookie.split(";").find { it.contains("SESSION") }
//                    if(jxglstuCookie == null) {
//                        Log.d("测试2","未解析到教务Session")
//                    }
//                    Log.d("测试3",jxglstuCookie.toString())
//                    SharedPrefs.saveString("redirect",jxglstuCookie )
//                }
//                val sb = StringBuilder()

//                for (match in matches) {
//                    val cookie = match.groupValues[1]
//                    sb.append(cookie).append("; ")
//                }
                //优化代码
//                Log.d("测试3",sb.toString())
//if(sb.length > 30 ) {
//    sb.delete(sb.length - 2, sb.length)
//    val redirectcookie = sb.toString()

//   SharedPrefs.saveString("redirect", redirectcookie)

  //  sp.getString("redirect", "")?.let { Log.d("测试11", it) }
//}
            }
            return response
        }
    }
