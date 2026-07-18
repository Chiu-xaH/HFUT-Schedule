package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.network.api.model.response.html.news.AcademicNewsList
import com.hfut.schedule.network.api.model.response.html.news.AcademicNewsType
import com.hfut.schedule.network.api.model.response.html.news.AcademicNewsXuanChengType
import com.hfut.schedule.network.api.model.response.html.news.News
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.network.api.impl.AcademicServiceCreator
import com.hfut.schedule.network.api.impl.AcademicXCServiceCreator
import com.hfut.schedule.network.api.impl.NewsServiceCreator
import com.hfut.schedule.network.api.impl.XuanChengServiceCreator
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.network.api.inf.AcademicService
import com.hfut.schedule.network.api.inf.AcademicXCService
import com.hfut.schedule.network.api.inf.NewsService
import com.hfut.schedule.network.api.inf.XuanChengService
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.util.CryptoUtil
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.screen.news.home.transferToPostData
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NewsRepository {
    private val news = NewsServiceCreator.create(NewsService::class.java)
    private val academic = AcademicServiceCreator.create(AcademicService::class.java)
    private val academicXC = AcademicXCServiceCreator.create(AcademicXCService::class.java)
    private val xuanCheng = XuanChengServiceCreator.create(XuanChengService::class.java)

    fun searchXuanChengNews(title : String, page: Int = 1) {

        val postData = transferToPostData(title, page)
        val call = xuanCheng.searchNotications(postData)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                newsXuanChengResult.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    suspend fun getXuanChengNews(page: Int,newsXuanChengResult : UiStateHolder<List<News>>) =
        launchRequestState(
            holder = newsXuanChengResult,
            request = {
                xuanCheng.getNotications(page = page.let { if (it <= 1) "" else it.toString() })

            },
            transformSuccess = { _, html -> parseNewsXuanCheng(html) }
        )

    @JvmStatic
    private fun parseNewsXuanCheng(html : String) : List<News> = try {
        val document = Jsoup.parse(html)
        document.select("ul.news_list > li").map { element ->
            val titleElement = element.selectFirst("span.news_title a")
            val title = titleElement?.attr("title") ?: "未知标题"
            val url = titleElement?.attr("href") ?: "未知URL"
            val date = element.selectFirst("span.news_meta")?.text() ?: "未知日期"

            News(title, date, url)
        }
    } catch (e : Exception) { throw e }

    suspend fun getAcademicXC(type: AcademicNewsXuanChengType, page: Int = 1, holder : UiStateHolder<List<News>>) =
        launchRequestState(
            holder = holder,
            request = { academicXC.getNews(type.type, page) },
            transformSuccess = { _, json -> parseAcademicNewsXC(json) },
        )
    @JvmStatic
    private fun parseAcademicNewsXC(html : String) : List<News> = try {
        val document = Jsoup.parse(html)
        val newsList = mutableListOf<News>()

        // by Claude 这里接口变了，重新写解析函数
        val links = document.select("td[align=left] > a[target=_blank]")

        for (a in links) {
            val title = a.attr("title").trim()
            val link = a.attr("href")

            val date = a
                .parent()
                ?.parent()
                ?.selectFirst("td[align=right]")
                ?.text()
                ?.trim()
                ?: continue

            if (title.isNotEmpty() && link.isNotEmpty()) {
                newsList.add(News(title, date, link))
            }
        }
        /*
        // 找到所有<tr class="articlelist2_tr">
        val rows = document.select("tr.articlelist2_tr")
        for (row in rows) {
            val aTag = row.selectFirst("a.articlelist1_a_title")
            val dateTd = row.selectFirst("td[align=right]")

            if (aTag != null && dateTd != null) {
                val title = aTag.attr("title").replace("\u00a0", " ") // 替换不间断空格
                val link = aTag.attr("href")
                val date = dateTd.text()

                newsList.add(NewsResponse(title, date, link))
            }
        }
         */

        newsList
    } catch (e : Exception) { throw e }

    suspend fun getAcademic(type: AcademicNewsType, totalPage : Int? = null, page: Int = 1, holder : UiStateHolder<AcademicNewsList>) =
        launchRequestState(
            holder = holder,
            request = {
                if (totalPage == null || totalPage == page) {
                    academic.getNews("${type.type}.htm")
                } else {
                    academic.getNews("${type.type}/${totalPage - page + 1}.htm")
                }
            },
            transformSuccess = { _, json -> parseAcademicNews(json) },
        )
    @JvmStatic
    private fun parseAcademicNews(html : String) : AcademicNewsList = try {
        val document: Document = Jsoup.parse(html)

        // 提取新闻列表
        val newsList = mutableListOf<News>()
        val newsElements = document.select("a.l3-news--item")

        for (element in newsElements) {
            val link = element.attr("href")  // 相对链接，可拼接 baseUrl
            val title = element.selectFirst("div.l3-news--title")?.text() ?: ""
            val date = element.selectFirst("div.l3-news--month")?.text() ?: ""

            newsList.add(News(title = title, date = date, link = link))
        }

        // 提取总页数，例如最后的“110”
        val pageText = document.select("span.p_no a").map { it.text() }
        val maxPage = pageText.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 1

        AcademicNewsList(news = newsList, totalPage = maxPage)
    } catch (e : Exception) { throw e }

    suspend fun searchNews(title : String,page: Int = 1,newsResult : UiStateHolder<List<News>>) =
        launchRequestState(
            holder = newsResult,
            request = { news.searchNews(CryptoUtil.encodeToBase64(title), page) },
            transformSuccess = { _, html -> parseNews(html) }
        )

    @JvmStatic
    private fun parseNews(html : String) : List<News> = try {
        var newsList = mutableListOf<News>()
        val doc: Document = Jsoup.parse(html)
        val newsItems = doc.select("ul.list li")

        for (item in newsItems) {
            val date = item.select("i.timefontstyle252631").text()
            val title = item.select("p.titlefontstyle252631").text()
            val link = item.select("a").attr("href")
            if(title.isEmpty() || title.isBlank()) {
                break
            }
            val links = if(isValidWebUrl(link)) {
                link
            } else {
                Constant.NEWS_URL + link
            }
            newsList.add(News(title, date, links))
        }
        // 去重
        newsList = newsList.distinctBy { it.title + it.link + it.date }.toMutableList()
        newsList
    } catch (e : Exception) { throw e }

}