package com.hfut.schedule.logic.network.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.GithubBean
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.model.QWeatherResponse
import com.hfut.schedule.logic.model.QWeatherWarnBean
import com.hfut.schedule.logic.model.QWeatherWarnResponse
import com.hfut.schedule.logic.model.SearchEleResponse
import com.hfut.schedule.logic.model.TeacherResponse
import com.hfut.schedule.logic.model.WorkSearchResponse
import com.hfut.schedule.logic.model.XuanquNewsItem
import com.hfut.schedule.logic.model.XuanquResponse
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.model.guagua.UseCodeResponse
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.FWDTService
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GithubRawService
import com.hfut.schedule.logic.network.api.GithubService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.QWeatherService
import com.hfut.schedule.logic.network.api.TeachersService
import com.hfut.schedule.logic.network.api.WorkService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubRawServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.servicecreator.NewsServiceCreator
import com.hfut.schedule.logic.network.servicecreator.QWeatherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SearchEleServiceCreator
import com.hfut.schedule.logic.network.servicecreator.TeacherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.WorkServiceCreator
import com.hfut.schedule.logic.network.servicecreator.XuanChengServiceCreator
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.network.StateHolder
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.screen.home.search.function.other.life.getLocation
import com.hfut.schedule.ui.screen.news.home.transferToPostData
import com.hfut.schedule.ui.screen.shower.home.function.StatusMsgResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.util.Locale
// Repo迁移计划
object Repository {
    private val loginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val xuanCheng = XuanChengServiceCreator.create(XuanChengService::class.java)
    private val workSearch = WorkServiceCreator.create(WorkService::class.java)
    private val news = NewsServiceCreator.create(NewsService::class.java)
    private val searchEle = SearchEleServiceCreator.create(FWDTService::class.java)
    private val xuanChengDormitory = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)
    private val github = GithubServiceCreator.create(GithubService::class.java)
    private val gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val githubRaw = GithubRawServiceCreator.create(GithubRawService::class.java)
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
//    private val lePaoYun = LePaoYunServiceCreator.create(LePaoYunService::class.java)

    //引入接口
    // 通用的网络请求方法，支持自定义的操作
    @JvmStatic
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: (MutableLiveData<T>)? = null,
        onSuccess: ((Response<ResponseBody>) -> Unit)? = null
    ) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && liveData != null) {
                    val responseBody = response.body()?.string()
                    val result: T? = parseResponse(responseBody)
                    liveData.value = result
                }

                // 执行自定义操作
                onSuccess?.invoke(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // 通用方法用于解析响应（根据需要进行调整）
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }

    @JvmStatic
    suspend fun <T> launchRequestSimple(
        holder: StateHolder<T>,
        request: suspend () -> Response<ResponseBody>,
        transformSuccess: (Headers, String) -> T,
        transformRedirect: ((Headers) -> T)? = null
    ) = try {
        holder.setLoading()
        val response = request()
        val headers = response.headers()
        val bodyString = response.body()?.string().orEmpty()

        if (response.isSuccessful) {
            // 成功
            val result = try {
                transformSuccess(headers, bodyString)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else if(response.code() == 302){
//             重定向 特殊处理
            val result = try {
                transformRedirect!!(headers)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else {
            // 承接错误解析 可选
            holder.emitError(HttpException(response), response.code())
        }
    } catch (e: Exception) {
        holder.emitError(e,null)
    }

    suspend fun loginSchoolNet(campus: Campus = getCampus(), loginSchoolNetResponse : StateHolder<Boolean>) =
        withContext(Dispatchers.IO) {
            getPersonInfo().username?.let { uid ->
                getCardPsk()?.let { pwd ->
                    when (campus) {
                        Campus.HEFEI -> {
                            showToast("暂未支持")
                            return@withContext
                        }

                        Campus.XUANCHENG -> {
                            val location = "宣州Login"
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = {
                                        loginWeb.loginWeb(uid, pwd, location).awaitResponse()
                                    },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = {
                                        loginWeb2.loginWeb(uid, pwd, location).awaitResponse()
                                    },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                        }
                    }
                }
            }
        }
    suspend fun logoutSchoolNet(campus: Campus = getCampus(), loginSchoolNetResponse : StateHolder<Boolean>) =
        withContext(Dispatchers.IO) {
            getPersonInfo().username?.let { uid ->
                getCardPsk()?.let { pwd ->
                    when (campus) {
                        Campus.HEFEI -> {
                            showToast("暂未支持")
                            return@withContext
                        }

                        Campus.XUANCHENG -> {
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = { loginWeb.logoutWeb().awaitResponse() },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = { loginWeb2.logoutWeb().awaitResponse() },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                        }
                    }
                }
            }
        }
    // 目前仅适配了宣区
    @JvmStatic
    private fun parseLoginSchoolNet(result : String) : Boolean = try {
        if(result.contains("登录成功") && !result.contains("已使用")) {
            true
        } else if(result.contains("已使用")) {
            false
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    suspend fun getWebInfo(infoWebValue : StateHolder<WebInfo>) = launchRequestSimple(
        holder = infoWebValue,
        request = { loginWeb.getInfo().awaitResponse() },
        transformSuccess = { _,json -> parseWebInfo(json) }
    )

    suspend fun getWebInfo2(infoWebValue : StateHolder<WebInfo>) = launchRequestSimple(
        holder = infoWebValue,
        request = { loginWeb2.getInfo().awaitResponse() },
        transformSuccess = { _,json -> parseWebInfo(json) }
    )
    @JvmStatic
    private fun parseWebInfo(html : String) : WebInfo = try {
        //本段照搬前端
        val flow = html.substringAfter("flow").substringBefore(" ").substringAfter("'").toDouble()
        val fee = html.substringAfter("fee").substringBefore(" ").substringAfter("'").toDouble()
        var flow0 = flow % 1024
        val flow1 = flow - flow0
        flow0 *= 1000
        flow0 -= flow0 % 1024
        var fee1 = fee - fee % 100
        var flow3 = "."
        if (flow0 / 1024 < 10) flow3 = ".00"
        else { if (flow0 / 1024 < 100) flow3 = ".0"; }
        val resultFee = (fee1 / 10000).toString()
        val resultFlow : String = ((flow1 / 1024).toString() + flow3 + (flow0 / 1024)).substringBefore(".")
        val result = WebInfo(resultFee,resultFlow)
//        vmUI.webValue.value = result
        saveString("memoryWeb", result.flow)
        result
    } catch (e : Exception) { throw e }

    suspend fun searchTeacher(name: String = "", direction: String = "",teacherSearchData : StateHolder<TeacherResponse>) = launchRequestSimple(
        holder = teacherSearchData,
        request = { teacher.searchTeacher(name=name, direction = direction, size = prefs.getString("TeacherSearchRequest",MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE.toString() ).awaitResponse() },
        transformSuccess = { _,json -> parseTeacherSearch(json) }
    )

    @JvmStatic
    private fun parseTeacherSearch(json : String) : TeacherResponse = try {
        Gson().fromJson(json, TeacherResponse::class.java)
    } catch (e : Exception) { throw e }

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
    suspend fun getXuanChengNews(page: Int,newsXuanChengResult : StateHolder<List<XuanquNewsItem>>) = launchRequestSimple(
        holder = newsXuanChengResult,
        request = { xuanCheng.getNotications(page = page.let { if(it <= 1)  ""  else  it.toString()  }).awaitResponse() },
        transformSuccess = { _,html -> parseNewsXuanCheng(html) }
    )

    @JvmStatic
    private fun parseNewsXuanCheng(html : String) : List<XuanquNewsItem> = try {
        val document = Jsoup.parse(html)
        document.select("ul.news_list > li").map { element ->
            val titleElement = element.selectFirst("span.news_title a")
            val title = titleElement?.attr("title") ?: "未知标题"
            val url = titleElement?.attr("href") ?: "未知URL"
            val date = element.selectFirst("span.news_meta")?.text() ?: "未知日期"

            XuanquNewsItem(title, date, url)
        }
    } catch (e : Exception) { throw e }

    suspend fun searchWorks(keyword: String?, page: Int = 1,type: Int,campus: Campus,workSearchResult : StateHolder<WorkSearchResponse>) = launchRequestSimple(
        holder = workSearchResult,
        request = {
            workSearch.search(
                keyword = keyword,
                page = page,
                pageSize = prefs.getString("WorkSearchRequest",MyApplication.PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.PAGE_SIZE,
                type = type.let { if(it == 0) null else it },
                token = "yxqqnn1700000" + if(campus == Campus.XUANCHENG) "119" else "002"
            ).awaitResponse() },
        transformSuccess = { _, json -> parseWorkResponse(json) },
    )

    @JvmStatic
    private fun parseWorkResponse(resp : String): WorkSearchResponse = try {
        // 去掉前缀，提取 JSON 部分
        val jsonStr = resp.removePrefix("var __result = ").removeSuffix(";").trim()
        Gson().fromJson(jsonStr,WorkSearchResponse::class.java)
    } catch (e : Exception) { throw e }

    suspend fun searchNews(title : String,page: Int = 1,newsResult : StateHolder<List<NewsResponse>>) = launchRequestSimple(
        holder = newsResult,
        request = { news.searchNews(Encrypt.encodeToBase64(title),page).awaitResponse() },
        transformSuccess = { _,html -> parseNews(html) }
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

    suspend fun searchEle(json : String,electricOldData : StateHolder<String>) = launchRequestSimple(
        holder = electricOldData,
        request = { searchEle.searchEle(json,"synjones.onecard.query.elec.roominfo",true).awaitResponse() },
        transformSuccess = { _,json -> parseElectric(json) }
    )

    @JvmStatic
    private fun parseElectric(result : String) : String = try {
        if (result.contains("query_elec_roominfo")) {
            var msg = Gson().fromJson(result, SearchEleResponse::class.java).query_elec_roominfo.errmsg

            if(msg.contains("剩余金额"))
                formatDecimal(msg.substringAfter("剩余金额").substringAfter(":").toDouble(),2)
            else
                throw Exception(msg)
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchDormitoryXuanCheng(code : String,dormitoryResult : StateHolder<List<XuanquResponse>>) = launchRequestSimple(
        holder = dormitoryResult,
        request = { xuanChengDormitory.search(code).awaitResponse() },
        transformSuccess = { _,html -> parseDormitoryXuanCheng(html) }
    )

    @JvmStatic
    private fun parseDormitoryXuanCheng(html : String) : List<XuanquResponse> = try {
        // 定义一个正则表达式来匹配HTML标签
        val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

        val data = html.let {
            regex.findAll(it).map {
                XuanquResponse(score = it.groupValues[2].toInt(), date = it.groupValues[5])
            }.toList()
        }
        data
    }  catch (e : Exception) { throw e }

    suspend fun getWeatherWarn(campus: Campus,weatherWarningData : StateHolder<List<QWeatherWarnBean>>) = launchRequestSimple(
        holder = weatherWarningData,
        request = { qWeather.getWeatherWarn(locationID = getLocation(campus)).awaitResponse() },
        transformSuccess = { _,json -> parseWeatherWarn(json) }
    )

    @JvmStatic
    private fun parseWeatherWarn(json : String) : List<QWeatherWarnBean> = try {
        Gson().fromJson(json, QWeatherWarnResponse::class.java).warning
    } catch (e : Exception) { throw e }

    suspend fun getWeather(campus: Campus,qWeatherResult : StateHolder<QWeatherNowBean>) = launchRequestSimple(
        holder = qWeatherResult,
        request = { qWeather.getWeather(locationID = getLocation(campus)).awaitResponse() },
        transformSuccess = { _, json -> parseWeatherNow(json) }
    )

    @JvmStatic
    private fun parseWeatherNow(json : String) : QWeatherNowBean = try {
        if(json.contains("200"))
            Gson().fromJson(json, QWeatherResponse::class.java).now
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getStarNum(githubStarsData : StateHolder<Int>) = launchRequestSimple(
        holder = githubStarsData,
        request = { github.getRepoInfo().awaitResponse() },
        transformSuccess = { _,json -> parseGithubStarNum(json) }
    )

    @JvmStatic
    private fun parseGithubStarNum(json : String) : Int = try {
        Gson().fromJson(json,GithubBean::class.java).stargazers_count
    } catch (e : Exception) { throw e }

    fun getUpdate() {
        val call = gitee.getUpdate()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("versions",response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun downloadHoliday()  {
        val call = githubRaw.getYearHoliday()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("HOLIDAY", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    suspend fun guaGuaLogin(phoneNumber : String, password : String,loginResult : StateHolder<GuaGuaLoginResponse>) = launchRequestSimple(
        holder = loginResult,
        request = { guaGua.login(phoneNumber,password).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaLogin(json) },
    )

    suspend fun guaGuaStartShower(phoneNumber: String, macLocation : String, loginCode : String,startShowerResult : StateHolder<String>) = launchRequestSimple(
        holder = startShowerResult,
        request = { guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaStartShower(json) },
    )

    suspend fun guaGuaGetBills(billsResult : StateHolder<GuaguaBillsResponse>) = launchRequestSimple(
        holder = billsResult,
        request = { guaGua.getBills(phoneNumber= prefs.getString("PHONENUM","") ?: "", loginCode= prefs.getString("loginCode","") ?: "").awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaBills(json) },
    )

    suspend fun guaGuaGetUseCode(useCodeResult : StateHolder<String>) = launchRequestSimple(
        holder = useCodeResult,
        request = { guaGua.getUseCode(phoneNumber= prefs.getString("PHONENUM","") ?: "",loginCode= prefs.getString("loginCode","") ?: "").awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaUseCode(json) }
    )

    suspend fun guaGuaReSetUseCode(newCode : String,reSetCodeResult : StateHolder<String>) = launchRequestSimple(
        holder = reSetCodeResult,
        request = {
            val psk = prefs.getString("GuaGuaPsk","") ?: ""
            val encrypted = Encrypt.md5Hash(psk).uppercase(Locale.ROOT)
            guaGua.reSetUseCode(phoneNumber= prefs.getString("PHONENUM","") ?: "", encrypted, loginCode= prefs.getString("loginCode","") ?: "", newCode).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaReSetUseCode(json) }
    )

    @JvmStatic
    private fun parseGuaGuaLogin(result: String): GuaGuaLoginResponse = try {
        val data = Gson().fromJson(result, GuaGuaLoginResponse::class.java)
        if(data.message.contains("成功")) {
            saveString("GuaGuaPersonInfo",result)
            saveString("loginCode",data.data?.loginCode)
        }
        data
    } catch (e: Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaStartShower(result: String): String = try {
        Gson().fromJson(result, StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaBills(result: String) : GuaguaBillsResponse = try {
        Gson().fromJson(result, GuaguaBillsResponse::class.java)
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaUseCode(result: String) : String = try {
        if(result.contains("成功"))
            Gson().fromJson(result,UseCodeResponse::class.java).data.randomCode
        else throw Exception("解析错误")
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaReSetUseCode(result: String) : String = try {
        Gson().fromJson(result,StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }
}