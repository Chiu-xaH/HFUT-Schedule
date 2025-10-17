package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response
//获取AESKey保存到SharedPreferences
class AESKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cookies = response.headers("Set-Cookie")
        //Log.d("cookieAES",cookies.toString())
        var cookieValue = ""
        var cookiekey = ""
        // 遍历cookies列表，找到LOGIN_FLAVORING
        for (cookie in cookies) {
            if (cookie.contains("LOGIN_FLAVORING")) {
                // 提取出LOGIN_FLAVORING的值
                cookieValue = cookie.substringBefore(";")
                cookiekey = cookieValue.substringAfter("=")
                break
            }
        }
    // Log.d("测试2",cookiekey)
        // 如果cookieValue不为空，就将其保存到SharedPreferences中

        SharedPrefs.saveString("LOGIN_FLAVORING", cookiekey)


        return response
    }
}