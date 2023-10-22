package com.hfut.schedule.logic.network.OkHttp

import android.preference.PreferenceManager
import android.util.Log
import com.hfut.schedule.MyApplication.Companion.context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 自定义请求拦截器
 * Created by ge
 */

// 创建一个添加拦截器，实现Interceptor接口，重写intercept方法
class AddCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取请求对象
        val request = chain.request()
        // 从SharedPreferences中获取保存的cookie值
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val cookieValue = sp.getString("cookie", "")
        cookieValue?.let { Log.d("测试3", it) }
        // 如果cookie值不为空，就将其添加到请求头中的Cookie字段
        if (cookieValue != null) {
            if (cookieValue.isNotEmpty()) {
                // 创建一个新的请求对象，添加请求头
                val newRequest = request.newBuilder()
                    .addHeader("Cookie", cookieValue)
                    .build()
                // 返回新的请求对象
                return chain.proceed(newRequest)
            }
        }
        // 否则，返回原始的请求对象
        return chain.proceed(request)
    }
}