package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.helper.Constant
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

object SchoolNetSelfServiceCreator : BaseServiceCreator(
    url = Constant.SCHOOL_NET_SELF_URL,
    client = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val oldCookies = cookieStore[url.host].orEmpty()
                val newCookies = oldCookies
                    .filterNot { old -> cookies.any { it.name == old.name } }
                    .toMutableList()
                newCookies.addAll(cookies)
                cookieStore[url.host] = newCookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host].orEmpty()
            }
        })
        .build()
)
