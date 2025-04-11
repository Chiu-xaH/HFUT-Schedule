package com.hfut.schedule.ui.screen.home.cube.sub.update

import com.hfut.schedule.logic.model.Update
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.parsePatchFile
import org.jsoup.Jsoup


fun getUpdates() : Update {
    val html = prefs.getString("versions","")
    return try {
        val doc = Jsoup.parse(html)
        // 提取版本号
        val version = doc.title().substringAfter(" ").substringBefore(" ·")
        // 提取后面的内容
        val content = doc.select("textarea.content").text()
        Update(version, content ?: "正在获取")
    } catch (e : Exception) {
        Update("正在获取","正在获取")
    }
}

fun getPatchVersions() : List<Patch> {
    val html = prefs.getString("versions","")
    return try {
        val document = Jsoup.parse(html)
        val versions = document.select("a[href]").mapNotNull { element ->
            // 只提取补丁包
            val text = element.text().trim()
            parsePatchFile(text)
        }
        versions
    } catch (e : Exception) {
        emptyList()
    }
}