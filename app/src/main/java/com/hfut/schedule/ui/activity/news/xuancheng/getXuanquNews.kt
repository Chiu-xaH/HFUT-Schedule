package com.hfut.schedule.ui.activity.news.xuancheng

import com.hfut.schedule.logic.beans.XuanquNewsItem
import com.hfut.schedule.viewmodel.NetWorkViewModel
import org.jsoup.Jsoup


fun getXuanquNews(vm : NetWorkViewModel): List<XuanquNewsItem> {
    val html = vm.NewsXuanChengData.value
    try {
        val document = Jsoup.parse(html)
        return document.select("ul.news_list > li").map { element ->
            val titleElement = element.selectFirst("span.news_title a")
            val title = titleElement?.attr("title") ?: "未知标题"
            val url = titleElement?.attr("href") ?: "未知URL"
            val date = element.selectFirst("span.news_meta")?.text() ?: "未知日期"

            XuanquNewsItem(title, date, url)
        }
    } catch (e: Exception) {
        return emptyList()
    }
}
