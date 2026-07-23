package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.html.FloorMap
import com.hfut.schedule.network.api.model.response.json.gitee.GiteeReleaseResponse
import com.hfut.schedule.network.api.model.response.json.github.GithubBuildingMapResponse
import com.hfut.schedule.network.api.model.response.json.github.GithubFolderResponse
import com.hfut.schedule.network.api.model.response.json.github.GithubIssueResponse
import com.hfut.schedule.network.api.model.response.json.github.GithubProgramSearchResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramData
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.UiStateHolder

interface GithubRepositoryInf {
    fun getMyApi()
    suspend fun getProgramListInfo(id : Int,campus : CampusRegion,holder : UiStateHolder<UniAppProgramData>)
    suspend fun getProgramList(campus : CampusRegion,holder : UiStateHolder<List<GithubProgramSearchResponse>>)
    suspend fun getStarNum(githubStarsData : UiStateHolder<Int>)
    suspend fun getUpdateContents(holder : UiStateHolder<List<GithubFolderResponse>>)
    fun downloadHoliday()
    suspend fun getUpdateFileSize(fileName : String,holder : UiStateHolder<Double>)
    suspend fun getUpdate(holder : UiStateHolder<GiteeReleaseResponse>)
    suspend fun getIssues(page : Int,holder : UiStateHolder<List<GithubIssueResponse>>)
    suspend fun getBuildingMaps(holder : UiStateHolder<List<GithubBuildingMapResponse>>)
    suspend fun getFloorXml(filename : String,holder : UiStateHolder<FloorMap>)
}