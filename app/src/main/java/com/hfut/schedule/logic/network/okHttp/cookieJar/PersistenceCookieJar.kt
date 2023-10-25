package com.hfut.schedule.logic.network.okHttp.cookieJar

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistenceCookieJar : CookieJar {
    private val cookieMap = mutableMapOf<String, MutableList<Cookie>>()
    // 从响应中获取Cookie并存储到本地
    private var aeskey : String = ""
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

        val job = GlobalScope.launch {
            val host = url.host
            val oldCookies = cookieMap[host]
            Log.d("就1",oldCookies.toString())
             cookieMap[host] = cookies.toMutableList()
           // Log.d("判断3", cookies.toMutableList().toString())
            Log.d("测试55",cookies.joinToString("; ") { "${it.name}=${it.value}" })
        }
        runBlocking {
            job.join()
            val cook = cookies.joinToString(";")
             aeskey = cook.substring(16,32)
            Log.d("密钥5555555",aeskey)
        }

    }
    // 从本地加载Cookie并添加到请求中
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host // 获取域名
        val cookies = cookieMap[host]// 从Map中取出对应的Cookie列表
        return cookies ?: listOf()// 如果没有找到，就返回一个空列表
    }
}
