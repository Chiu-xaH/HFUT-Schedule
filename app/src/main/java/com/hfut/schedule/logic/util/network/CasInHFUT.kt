package com.hfut.schedule.logic.util.network

import com.xah.common.logic.util.LogUtil
import org.jsoup.Jsoup

// HFUT Central Authentication Service 统一认证登录
@Deprecated("不需要这个类了，里面的逻辑可以转移到该去的地方")
object CasInHFUT {
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
                    return id
                }
            }
        } catch (e: Exception) {
            LogUtil.error(e)
            return null
        }
        return null
    }

    var casCookies : String? = null
}