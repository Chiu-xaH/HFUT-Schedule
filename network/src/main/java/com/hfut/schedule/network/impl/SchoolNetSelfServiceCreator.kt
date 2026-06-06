package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import com.xah.shared.LogUtil
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okio.Buffer

private class SchoolNetSelfCookieJar : CookieJar {
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        LogUtil.debug("SchoolNetSelf save cookies host=${url.host}, cookies=${cookies.map { "${it.name}=${it.value.take(8)}..." }}")
        val oldCookies = cookieStore[url.host].orEmpty()
        val newCookies = oldCookies
            .filterNot { old -> cookies.any { it.name == old.name } }
            .toMutableList()
        newCookies.addAll(cookies)
        cookieStore[url.host] = newCookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore[url.host].orEmpty()
        LogUtil.debug("SchoolNetSelf load cookies host=${url.host}, cookies=${cookies.map { "${it.name}=${it.value.take(8)}..." }}")
        return cookies
    }
}

private fun requestBodyToString(request: okhttp3.Request): String {
    return try {
        val copy = request.newBuilder().build()
        val buffer = Buffer()
        copy.body?.writeTo(buffer)
        buffer.readUtf8()
    } catch (_: Exception) {
        "<unable to read body>"
    }
}

object SchoolNetSelfServiceCreator : BaseServiceCreator(
    url = Constant.SCHOOL_NET_SELF_URL,
    client = OkHttpClient.Builder()
        .cookieJar(SchoolNetSelfCookieJar())
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor { chain ->
            val request = chain.request()
            val reqBody = requestBodyToString(request)
            val response = chain.proceed(request)
            val preview = response.peekBody(2000).string()
            LogUtil.debug(
                """
SchoolNetSelf Request
  url=${request.url}
  method=${request.method}
  headers=${request.headers}
  body=$reqBody
SchoolNetSelf Response
  code=${response.code}
  message=${response.message}
  contentType=${response.body?.contentType()}
  contentLength=${response.body?.contentLength()}
  responseUrl=${response.request.url}
  preview=${preview.take(500)}
                """.trimIndent()
            )
            response
        }
        .build()
)
