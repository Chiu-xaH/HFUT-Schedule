package com.hfut.schedule.logic.network.OkHttp

import android.content.Context
import android.preference.PreferenceManager
import android.text.TextUtils
import com.chiuxah.weather.MyApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

// 创建一个接收拦截器，实现Interceptor接口，重写intercept方法
class ReceiveCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取响应对象
        val response = chain.proceed(chain.request())
        // 获取响应头中的Set-Cookie字段
        val cookies = response.headers("Set-Cookie")
        // 定义一个变量用于存储JSESSIONID
        var cookieValue = ""
        // 遍历cookies列表，找到JSESSIONID
        for (cookie in cookies) {
            if (cookie.contains("JSESSIONID")) {
                // 提取出JSESSIONID的值
                cookieValue = cookie.substringBefore(";")
                break
            }
        }
        // 如果cookieValue不为空，就将其保存到SharedPreferences中
        if (cookieValue.isNotEmpty()) {
            val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            sp.edit().putString("cookie", cookieValue).apply()
        }
        // 返回响应对象
        return response
    }
}