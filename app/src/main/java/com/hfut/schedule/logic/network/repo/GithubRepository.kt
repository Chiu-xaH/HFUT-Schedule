package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.CampusRegion.HEFEI
import com.hfut.schedule.logic.enumeration.CampusRegion.XUANCHENG
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.model.GithubBean
import com.hfut.schedule.logic.model.GithubFolderBean
import com.hfut.schedule.logic.model.jxglstu.ProgramListBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchResponse
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GithubRawService
import com.hfut.schedule.logic.network.api.GithubService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubRawServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

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

    suspend fun getProgramListInfo(id : Int,campus : CampusRegion,holder : StateHolder<ProgramSearchBean>) =
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
        Gson().fromJson(json,ProgramSearchResponse::class.java).data
    } catch (e : Exception) { throw e }

    suspend fun getProgramList(campus : CampusRegion,holder : StateHolder<List<ProgramListBean>>) =
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
        val data: List<ProgramListBean> = Gson().fromJson(json,object : TypeToken<List<ProgramListBean>>() {}.type)
        data
    } catch (e : Exception) { throw e }

    suspend fun getStarNum(githubStarsData : StateHolder<Int>) = launchRequestState(
        holder = githubStarsData,
        request = { github.getRepoInfo() },
        transformSuccess = { _, json -> parseGithubStarNum(json) }
    )

    @JvmStatic
    private fun parseGithubStarNum(json : String) : Int = try {
        Gson().fromJson(json,GithubBean::class.java).stargazers_count
    } catch (e : Exception) { throw e }

    suspend fun getUpdateContents(holder : StateHolder<List<GithubFolderBean>>) =
        launchRequestState(
            holder = holder,
            request = { github.getFolderContent() },
            transformSuccess = { _, json -> parseUpdateContents(json) }
        )

    @JvmStatic
    private fun parseUpdateContents(json : String) : List<GithubFolderBean> = try {
        val listType = object : TypeToken<List<GithubFolderBean>>() {}.type
        val data : List<GithubFolderBean> = Gson().fromJson(json,listType)
        data
    } catch (e : Exception) { throw e }

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


    suspend fun getUpdateFileSize(fileName : String,holder : StateHolder<Double>) =
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

    suspend fun getUpdate(holder : StateHolder<GiteeReleaseResponse>) = launchRequestState(
        request = { gitee.getUpdate() },
        holder = holder,
        transformSuccess = { _, json -> parseGiteeUpdates(json) }
    )
    @JvmStatic
    private fun parseGiteeUpdates(json : String) : GiteeReleaseResponse = try {
        val listType = object : TypeToken<List<GiteeReleaseResponse>>() {}.type
        val b : List<GiteeReleaseResponse> = Gson().fromJson(json,listType)
        val data = b[0]
        val list = data.assets.filter {
            it.name.endsWith(".apk") || it.name.endsWith(".patch")
        }
        val versionName = data.name.replace("HFUT-Schedule ","")
        GiteeReleaseResponse(versionName,data.body,list)
    } catch (e : Exception) { throw e }
}