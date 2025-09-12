package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.model.GithubBean
import com.hfut.schedule.logic.model.GithubFolderBean
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GithubRawService
import com.hfut.schedule.logic.network.api.GithubService
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubRawServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
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


    suspend fun getStarNum(githubStarsData : StateHolder<Int>) = launchRequestSimple(
        holder = githubStarsData,
        request = { github.getRepoInfo().awaitResponse() },
        transformSuccess = { _,json -> parseGithubStarNum(json) }
    )

    @JvmStatic
    private fun parseGithubStarNum(json : String) : Int = try {
        Gson().fromJson(json,GithubBean::class.java).stargazers_count
    } catch (e : Exception) { throw e }

    suspend fun getUpdateContents(holder : StateHolder<List<GithubFolderBean>>) = launchRequestSimple(
        holder = holder,
        request = { github.getFolderContent().awaitResponse() },
        transformSuccess = { _,json -> parseUpdateContents(json) }
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


    suspend fun getUpdateFileSize(fileName : String,holder : StateHolder<Double>) = launchRequestSimple(
        holder = holder,
        request = { gitee.download(fileName).awaitResponse() },
        transformSuccess = { headers -> parseGiteeFileSize(headers) }
    )

    @JvmStatic
    private fun parseGiteeFileSize(headers: Headers): Double = try {
        val contentLength = headers["Content-Length"]?.toLongOrNull() ?: throw Exception("无法获取文件")
        contentLength.toDouble() / (1024 * 1024)
    } catch (e: Exception) { throw e }

    suspend fun getUpdate(holder : StateHolder<GiteeReleaseResponse>) = launchRequestSimple(
        request = { gitee.getUpdate().awaitResponse() },
        holder = holder,
        transformSuccess = { _,json -> parseGiteeUpdates(json) }
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