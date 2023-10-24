package com.hfut.cookiedemo

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class GETCookieJar : CookieJar {

    // 用一个Map来存储Cookie，键是域名，值是对应的Cookie列表

    private val cookieMap = mutableMapOf<String, MutableList<Cookie>>()




    // 从响应中获取Cookie并存储到本地

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

        val host = url.host

        val oldCookies = cookieMap[host]

        if (oldCookies != null) {
            // 先移除oldCookies中和cookies相同name的cookie
            oldCookies.removeAll { oldCookie ->
                cookies.any { newCookie ->
                    oldCookie.name == newCookie.name
                }
            }
            // 再添加新的cookie
            oldCookies.addAll(cookies)
        } else  cookieMap[host] = cookies.toMutableList()
        Log.d("判断3", cookies.toMutableList().toString())
        Log.d("测试55",cookies.joinToString("; ") { "${it.name}=${it.value}" })
    }

    // 从本地加载Cookie并添加到请求中
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host // 获取域名
        Log.d("判断4",host)
        Log.d("判断5",url.toString())
        val cookies = cookieMap[host]// 从Map中取出对应的Cookie列表
        cookies?.joinToString(";")?.let { Log.d("TGGGG", it) }


        return cookies ?: listOf()// 如果没有找到，就返回一个空列表
    }

}