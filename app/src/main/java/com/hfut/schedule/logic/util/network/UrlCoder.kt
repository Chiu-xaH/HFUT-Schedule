package com.hfut.schedule.logic.util.network

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


fun encodeUrl(url: String): String {
    return URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
}

fun decodeUrl(url: String): String {
    return URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
}