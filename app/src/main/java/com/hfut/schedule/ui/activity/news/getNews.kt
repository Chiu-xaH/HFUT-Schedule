package com.hfut.schedule.ui.activity.news

import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.NewsResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun getNews(vm : NetWorkViewModel): MutableList<NewsResponse> {
    var newsList = mutableListOf<NewsResponse>()
    try {
        val html= vm.NewsData.value
        val doc: Document = Jsoup.parse(html)
        val newsItems = doc.select("ul.list li")

        for (item in newsItems) {
            val date = item.select("i.timefontstyle252631").text()
            val title = item.select("p.titlefontstyle252631").text()
            val link = item.select("a").attr("href")
            newsList.add(NewsResponse(title, date, link))
        }
        return newsList
    } catch (e : Exception) {
        return newsList
    }
}

