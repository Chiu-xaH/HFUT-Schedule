package com.hfut.schedule.logic.util.network.parse

import android.util.Log
import org.jsoup.Jsoup

object JxglstuParseUtils {
    var bizTypeId : Int? = null

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
//                    Log.d("bizTypeId", id.toString())
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