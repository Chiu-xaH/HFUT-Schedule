package com.hfut.schedule.logic.network.OkHttp

import android.content.Context
import android.text.TextUtils

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 自定义请求拦截器
 * Created by ge
 */

class AddCookiesInterceptor(private val mContext: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val cookie = getCookie(request.url().toString(), request.url().host())
        if (!TextUtils.isEmpty(cookie)) {
            builder.addHeader("Cookie", cookie!!)
        }

        return chain.proceed(builder.build())
    }

    private fun getCookie(url: String, domain: String): String? {
        val sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE)
        if (!TextUtils.isEmpty(url) && sp.contains(url) && !TextUtils.isEmpty(sp.getString(url, ""))) {
            return sp.getString(url, "")
        }
        if (!TextUtils.isEmpty(domain) && sp.contains(domain) && !TextUtils.isEmpty(sp.getString(domain, ""))) {
            return sp.getString(domain, "")
        }

        return null
    }

    companion object {

        private val COOKIE_PREF = "cookies_prefs"
    }
}

