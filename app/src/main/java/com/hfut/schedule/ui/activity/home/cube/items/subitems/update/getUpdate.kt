package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.App.MyApplication.Companion.context
import com.hfut.schedule.logic.beans.Update
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import org.jsoup.Jsoup
import java.io.File


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
        Update(version, content ?: "正在获取")
    } catch (e : Exception) {
        Update("正在获取","正在获取")
    }
}

data class Patch(val oldVersion : String,val newVersion : String)

fun getPatchVersions() : List<Patch> {
    val html = prefs.getString("versions","")
    val e = ".patch"
    return try {
        // 解析 HTML
        val document = Jsoup.parse(html)

        // 获取所有 <a> 标签，并提取它们的文本内容
        val versions = document.select("a[href]").mapNotNull { element ->
            // 只提取补丁包
            val text = element.text().trim()
            if (text.endsWith(e)) {
                val str = text.substringBefore(e)
                val old = str.substringBefore("_to_")
                val new = str.substringAfter("_to_")
                Patch(old,new)
            } else {
                null
            }
        }
        versions
    } catch (e : Exception) {
        emptyList()
    }
}