package com.hfut.schedule.logic.util.network.state

import org.jsoup.Jsoup

// HFUT Central Authentication Service 统一认证登录
// 准备v5.0重写
object CasInHFUT {
    // cookie 模块
    // studentId 模块
    // bizTypeId 模块
    // WEBVPN 模块
    var studentId : Int? = null
    var bizTypeId : Int? = null
    var jSessionId : String? = null
    var sessionId : String? = null
    var loginFlavoring : String? = null
//    fun getCasCookies() : String = jSessionId + sessionId + loginFlavoring

    fun getBizTypeId(html : String) : Int? {
        val doc = Jsoup.parse(html)
        try {
            val scriptElements = doc.select("script")
            val regex = """bizTypeId\s*:\s*(\d+)""".toRegex()
            for (script in scriptElements) {
                val scriptText = script.html()
                val matchResult = regex.find(scriptText)
                if (matchResult != null) {
                    val id = matchResult.groupValues[1].toIntOrNull()
                    if(id != null) {
                        bizTypeId = id
                    }
                    return id
                }
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    var casCookies : String? = null
}