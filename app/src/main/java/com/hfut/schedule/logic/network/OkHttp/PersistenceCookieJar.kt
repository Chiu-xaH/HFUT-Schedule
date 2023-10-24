package com.hfut.schedule.logic.network.OkHttp

import android.util.Log
import kotlinx.coroutines.delay
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistenceCookieJar : CookieJar {
var key : String = ""
    val cookieMap = mutableMapOf<String, MutableList<Cookie>>()
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
        val cook = cookies.joinToString(";")

        key = cook.substring(16,32)
        Log.d("密钥",key)

    }

    // 从本地加载Cookie并添加到请求中
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host // 获取域名
        val cookies = cookieMap[host]// 从Map中取出对应的Cookie列表
        return cookies ?: listOf()// 如果没有找到，就返回一个空列表
    }
    //等待获取成功，先挂起get()
    fun get() : String {
       // Thread.sleep(10000)
        return key
    }


}
