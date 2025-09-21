package com.hfut.schedule.logic.network.repo.hfut

import com.hfut.schedule.logic.model.AcademicNewsResponse
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.network.api.AcademicService
import com.hfut.schedule.logic.network.api.AcademicXCService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.network.servicecreator.AcademicServiceCreator
import com.hfut.schedule.logic.network.servicecreator.AcademicXCServiceCreator
import com.hfut.schedule.logic.network.servicecreator.NewsServiceCreator
import com.hfut.schedule.logic.network.servicecreator.XuanChengServiceCreator
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.ui.screen.news.home.transferToPostData
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

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
    suspend fun getXuanChengNews(page: Int,newsXuanChengResult : StateHolder<List<NewsResponse>>) =
        launchRequestSimple(
            holder = newsXuanChengResult,
            request = {
                xuanCheng.getNotications(page = page.let { if (it <= 1) "" else it.toString() })
                    .awaitResponse()
            },
            transformSuccess = { _, html -> parseNewsXuanCheng(html) }
        )

    @JvmStatic
    private fun parseNewsXuanCheng(html : String) : List<NewsResponse> = try {
        val document = Jsoup.parse(html)
        document.select("ul.news_list > li").map { element ->
            val titleElement = element.selectFirst("span.news_title a")
            val title = titleElement?.attr("title") ?: "未知标题"
            val url = titleElement?.attr("href") ?: "未知URL"
            val date = element.selectFirst("span.news_meta")?.text() ?: "未知日期"

            NewsResponse(title, date, url)
        }
    } catch (e : Exception) { throw e }

    suspend fun getAcademicXC(type: AcademicXCType, page: Int = 1, holder : StateHolder<List<NewsResponse>>) =
        launchRequestSimple(
            holder = holder,
            request = { academicXC.getNews(type.type, page).awaitResponse() },
            transformSuccess = { _, json -> parseAcademicNewsXC(json) },
        )
    @JvmStatic
    private fun parseAcademicNewsXC(html : String) : List<NewsResponse> = try {
        val document = Jsoup.parse(html)
        val newsList = mutableListOf<NewsResponse>()

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

        newsList
    } catch (e : Exception) { throw e }

    suspend fun getAcademic(type: AcademicType, totalPage : Int? = null, page: Int = 1, holder : StateHolder<AcademicNewsResponse>) =
        launchRequestSimple(
            holder = holder,
            request = {
                if (totalPage == null || totalPage == page) {
                    academic.getNews("${type.type}.htm").awaitResponse()
                } else {
                    academic.getNews("${type.type}/${totalPage - page + 1}.htm").awaitResponse()
                }
            },
            transformSuccess = { _, json -> parseAcademicNews(json) },
        )
    @JvmStatic
    private fun parseAcademicNews(html : String) : AcademicNewsResponse = try {
        val document: Document = Jsoup.parse(html)

        // 提取新闻列表
        val newsList = mutableListOf<NewsResponse>()
        val newsElements = document.select("a.l3-news--item")

        for (element in newsElements) {
            val link = element.attr("href")  // 相对链接，可拼接 baseUrl
            val title = element.selectFirst("div.l3-news--title")?.text() ?: ""
            val date = element.selectFirst("div.l3-news--month")?.text() ?: ""

            newsList.add(NewsResponse(title = title, date = date, link = link))
        }

        // 提取总页数，例如最后的“110”
        val pageText = document.select("span.p_no a").map { it.text() }
        val maxPage = pageText.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 1

        AcademicNewsResponse(news = newsList, totalPage = maxPage)
    } catch (e : Exception) { throw e }

    suspend fun searchNews(title : String,page: Int = 1,newsResult : StateHolder<List<NewsResponse>>) =
        launchRequestSimple(
            holder = newsResult,
            request = { news.searchNews(Crypto.encodeToBase64(title), page).awaitResponse() },
            transformSuccess = { _, html -> parseNews(html) }
        )

    @JvmStatic
    private fun parseNews(html : String) : List<NewsResponse> = try {
        var newsList = mutableListOf<NewsResponse>()
        val doc: Document = Jsoup.parse(html)
        val newsItems = doc.select("ul.list li")

        for (item in newsItems) {
            val date = item.select("i.timefontstyle252631").text()
            val title = item.select("p.titlefontstyle252631").text()
            val link = item.select("a").attr("href")
            if(title.isEmpty() || title.isBlank()) {
                break
            }
            newsList.add(NewsResponse(title, date, link))
        }
        // 去重
        newsList = newsList.distinctBy { it.title + it.link + it.date }.toMutableList()
        newsList
    } catch (e : Exception) { throw e }

}