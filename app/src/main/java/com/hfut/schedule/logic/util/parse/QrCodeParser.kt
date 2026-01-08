package com.hfut.schedule.logic.util.parse

import com.xah.uicommon.util.LogUtil

data class WifiInfo(
    val ssid: String,
    val password: String?,
    val type: String,   // WPA/WPA2/WEP/nopass
    val hidden: Boolean
) {
    override fun toString() : String = "Wifi: ${ssid}\n密码: ${password ?: "无"}" + if(hidden) "\n隐藏连接" else ""
}

fun isWifiContent(content: String) : Boolean = content.startsWith("WIFI:")

fun parseWifiQrCode(content: String): WifiInfo? {
    if (!isWifiContent(content)) return null

    return try {
        // 去掉前缀 WIFI: 和末尾 ;;
        val body = content.removePrefix("WIFI:").removeSuffix(";;")

        var ssid = ""
        var password: String? = null
        var type = "nopass"
        var hidden = false

        // 按 ; 分割字段
        val parts = body.split(";")
        for (part in parts) {
            when {
                part.startsWith("S:") -> ssid = part.removePrefix("S:")
                part.startsWith("P:") -> password = part.removePrefix("P:")
                part.startsWith("T:") -> type = part.removePrefix("T:")
                part.startsWith("H:") -> hidden = part.removePrefix("H:").equals("true", ignoreCase = true)
            }
        }

        WifiInfo(
            ssid = ssid,
            password = password,
            type = type,
            hidden = hidden
        )
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }
}

