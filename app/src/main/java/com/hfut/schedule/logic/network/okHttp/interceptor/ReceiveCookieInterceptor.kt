package com.hfut.schedule.logic.network.okHttp.interceptor

import android.preference.PreferenceManager
import android.util.Log
import com.hfut.schedule.MyApplication.Companion.context
import okhttp3.Interceptor
import okhttp3.Response


// 创建一个接收拦截器，实现Interceptor接口，重写intercept方法
class ReceiveCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取响应对象
        val response = chain.proceed(chain.request())
        // 获取响应头中的Set-Cookie字段
        val cookies = response.headers("Set-Cookie")
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
      Log.d("测试2",cookiekey)
        // 如果cookieValue不为空，就将其保存到SharedPreferences中

            val sp = PreferenceManager.getDefaultSharedPreferences(context)
        if(sp.getString("cookie","") != cookiekey){
            sp.edit().putString("cookie", cookiekey).apply()
        }


        return response
    }
}