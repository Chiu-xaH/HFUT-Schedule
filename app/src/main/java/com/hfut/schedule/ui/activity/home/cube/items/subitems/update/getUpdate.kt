package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import com.hfut.schedule.logic.beans.Update
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import org.jsoup.Jsoup


fun getUpdates() : Update {
    return try {
        val html = prefs.getString("versions","")
        val doc = Jsoup.parse(html)
        val pElement = doc.select("div.markdown-body > p").first()

        // 提取版本号
        val version = doc.title().substringAfter(" ").substringBefore(" ·")
        // 提取后面的内容
        val content = doc.select("textarea.content").text()
        //println("内容: $content")
        Update(version ?: "正在获取", content ?: "正在获取")
    } catch (e : Exception) {
        Update("正在获取","正在获取")
    }
}