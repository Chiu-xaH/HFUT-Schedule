package com.hfut.schedule.logic

import android.os.Build
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import java.util.Base64
object FWDTPsk {

    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeBase64(input: String): String {
        val bytes = input.toByteArray()
        val encodedString = Base64.getEncoder().encodeToString(bytes)
        return encodedString
    }

    fun GetFWDTPsk() : String {
        val info = SharePrefs.prefs.getString("info","")
        val doc = info?.let { Jsoup.parse(it) }
        val chineseid = doc?.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text()
        val seven = chineseid?.takeLast(7)
        var id = ""
        if (seven != null) {
            if(seven.last() == 'X') id = seven.take(6)
            else id = seven.takeLast(6)
        }
        return id
    }

}