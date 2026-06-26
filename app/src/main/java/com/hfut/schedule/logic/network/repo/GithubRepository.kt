package com.hfut.schedule.logic.network.repo


import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.CampusRegion.HEFEI
import com.hfut.schedule.logic.enumeration.CampusRegion.XUANCHENG
import com.hfut.schedule.logic.model.BuildingMapResponseBean
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.model.GithubBean
import com.hfut.schedule.logic.model.GithubFolderBean
import com.hfut.schedule.logic.model.GithubIssueBean
import com.hfut.schedule.logic.model.GithubIssueLabel
import com.hfut.schedule.logic.model.jxglstu.ProgramListBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchResponse
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.network.api.GiteeService
import com.hfut.schedule.network.api.GithubRawService
import com.hfut.schedule.network.api.GithubService
import com.hfut.schedule.network.api.MyService
import com.hfut.schedule.network.impl.GiteeServiceCreator
import com.hfut.schedule.network.impl.GithubRawServiceCreator
import com.hfut.schedule.network.impl.GithubServiceCreator
import com.hfut.schedule.network.impl.MyServiceCreator
import com.hfut.schedule.network.util.GsonInstance
import com.hfut.schedule.ui.screen.home.search.function.other.life.FloorMap
import com.hfut.schedule.ui.screen.home.search.function.other.life.RoomRect
import okhttp3.Headers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GithubRepository {
    private val github = GithubServiceCreator.create(GithubService::class.java)
    private val githubRaw = GithubRawServiceCreator.create(GithubRawService::class.java)
    private val gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val myAPI = MyServiceCreator.create(MyService::class.java)


    fun getMyApi() {
        val call = myAPI.my()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("my", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    suspend fun getProgramListInfo(id : Int,campus : CampusRegion,holder : UiStateHolder<ProgramSearchBean>) =
        launchRequestState(
            holder = holder,
            request = {
                myAPI.getProgram(
                    id,
                    when (campus) {
                        HEFEI -> "hefei"
                        XUANCHENG -> "xuancheng"
                    }
                )
            },
            transformSuccess = { _, json -> parseProgramSearchInfo(json) }
        )
    private fun parseProgramSearchInfo(json : String) : ProgramSearchBean = try {
        GsonInstance.fromJson(json,ProgramSearchResponse::class.java).data
    } catch (e : Exception) { throw e }

    suspend fun getProgramList(campus : CampusRegion,holder : UiStateHolder<List<ProgramListBean>>) =
        launchRequestState(
            holder = holder,
            request = {
                myAPI.getProgramList(
                    when (campus) {
                        HEFEI -> "hefei"
                        XUANCHENG -> "xuancheng"
                    }
                )
            },
            transformSuccess = { _, json -> parseProgramSearch(json) }
        )
    @JvmStatic
    private fun parseProgramSearch(json : String) : List<ProgramListBean> = try {
        val data: List<ProgramListBean> = GsonInstance.fromJson(json,object : TypeToken<List<ProgramListBean>>() {}.type)
        data
    } catch (e : Exception) { throw e }

    suspend fun getStarNum(githubStarsData : UiStateHolder<Int>) = launchRequestState(
        holder = githubStarsData,
        request = { github.getRepoInfo() },
        transformSuccess = { _, json -> parseGithubStarNum(json) }
    )

    @JvmStatic
    private fun parseGithubStarNum(json : String) : Int = try {
        GsonInstance.fromJson(json,GithubBean::class.java).stargazers_count
    } catch (e : Exception) { throw e }

    suspend fun getUpdateContents(holder : UiStateHolder<List<GithubFolderBean>>) =
        launchRequestState(
            holder = holder,
            request = { github.getFolderContent() },
            transformSuccess = { _, json -> parseUpdateContents(json) }
        )

    @JvmStatic
    private fun parseUpdateContents(json : String) : List<GithubFolderBean> = try {
        val listType = object : TypeToken<List<GithubFolderBean>>() {}.type
        val data : List<GithubFolderBean> = GsonInstance.fromJson(json,listType)
        data
    } catch (e : Exception) { throw e }

    fun downloadHoliday()  {
        val call = githubRaw.getYearHoliday(DateTimeManager.Date_yyyy)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("HOLIDAY", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    suspend fun getUpdateFileSize(fileName : String,holder : UiStateHolder<Double>) =
        launchRequestState(
            holder = holder,
            request = { gitee.download(fileName) },
            transformSuccess = { headers -> parseGiteeFileSize(headers) }
        )

    @JvmStatic
    private fun parseGiteeFileSize(headers: Headers): Double = try {
        val contentLength = headers["Content-Length"]?.toLongOrNull() ?: throw Exception("无法获取文件")
        contentLength.toDouble() / (1024 * 1024)
    } catch (e: Exception) { throw e }

    suspend fun getUpdate(holder : UiStateHolder<GiteeReleaseResponse>) = launchRequestState(
        request = { gitee.getUpdate() },
        holder = holder,
        transformSuccess = { _, json -> parseGiteeUpdates(json) }
    )
    @JvmStatic
    private fun parseGiteeUpdates(json : String) : GiteeReleaseResponse = try {
        val listType = object : TypeToken<List<GiteeReleaseResponse>>() {}.type
        val b : List<GiteeReleaseResponse> = GsonInstance.fromJson(json,listType)
        val data = b[0]
        val list = data.assets.filter {
            it.name.endsWith(".apk") || it.name.endsWith(".patch")
        }
        val versionName = data.name.replace("HFUT-Schedule ","")
        GiteeReleaseResponse(versionName,data.body,list)
    } catch (e : Exception) { throw e }

    suspend fun getIssues(page : Int,holder : UiStateHolder<List<GithubIssueBean>>) = launchRequestState(
        request = { github.getIssues(page) },
        holder = holder,
        transformSuccess = { _, json -> parseGithubIssues(json) }
    )
    @JvmStatic
    private fun parseGithubIssues(json : String) : List<GithubIssueBean> = try {
        val listType = object : TypeToken<List<GithubIssueBean>>() {}.type
        val issues : List<GithubIssueBean> = GsonInstance.fromJson(json,listType)
        val flowLabelIds = GithubIssueLabel.entries.map { it.id }.toSet()
        val realIssues = issues.filter { issue ->
            // 过滤 PR
            issue.pr == null &&
            // 过滤 F-DROID
            !issue.title.contains("F-DROID", ignoreCase = true) &&
            // 不包含任何GithubIssueLabel标签非事务
            issue.labels.any { it.id in flowLabelIds }
        }
        realIssues
    } catch (e : Exception) { throw e }


    suspend fun getBuildingMaps(holder : UiStateHolder<List<BuildingMapResponseBean>>) = launchRequestState(
        request = { githubRaw.getBuildingMaps() },
        holder = holder,
        transformSuccess = { _, json -> parseBuildingMaps(json) }
    )
    @JvmStatic
    private fun parseBuildingMaps(json : String) : List<BuildingMapResponseBean> = try {
        val listType = object : TypeToken<List<BuildingMapResponseBean>>() {}.type
        GsonInstance.fromJson(json,listType) as List<BuildingMapResponseBean>
    } catch (e : Exception) { throw e }

    suspend fun getFloorXml(filename : String,holder : UiStateHolder<FloorMap>) = launchRequestState(
        request = { githubRaw.getFloorXml(filename) },
        holder = holder,
        transformSuccess = { _, xml -> parseFloorXml(xml) }
    )
    @JvmStatic
    private fun parseFloorXml(xml : String) : FloorMap = try {
        val doc = Jsoup.parse(xml, "", Parser.xmlParser())

        val width = doc.selectFirst("size > width")?.text()?.toFloatOrNull() ?: throw Exception("解析width失败")
        val height = doc.selectFirst("size > height")?.text()?.toFloatOrNull() ?: throw Exception("解析height失败")

        val rooms = mutableListOf<RoomRect>()

        val objects = doc.select("object")
        for (obj in objects) {
            val id = obj.selectFirst("name")?.text() ?: continue

            val xMin = obj.selectFirst("bndbox > xmin")?.text()?.toFloatOrNull() ?: continue
            val yMin = obj.selectFirst("bndbox > ymin")?.text()?.toFloatOrNull() ?: continue
            val xMax = obj.selectFirst("bndbox > xmax")?.text()?.toFloatOrNull() ?: continue
            val yMax = obj.selectFirst("bndbox > ymax")?.text()?.toFloatOrNull() ?: continue

            rooms += RoomRect(
                id = id,
                left = xMin / width,
                top = yMin / height,
                right = xMax / width,
                bottom = yMax / height
            )
        }

        FloorMap(width, height, rooms)
    } catch (e : Exception) { throw e }
}