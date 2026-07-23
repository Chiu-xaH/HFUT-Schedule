package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.one.OneBuilding
import com.hfut.schedule.network.api.model.response.json.one.OneClassroomRecord
import com.hfut.schedule.network.api.model.response.json.one.OneFeeData
import com.hfut.schedule.network.api.model.response.json.one.OneSchoolEmailResponse
import com.xah.common.logic.model.Campus
import com.xah.common.logic.state.UiStateHolder

interface OneRepositoryInf {
    suspend fun getPay(holder : UiStateHolder<OneFeeData>)
    suspend fun getMailURL(token : String,holder : UiStateHolder<OneSchoolEmailResponse>)
    suspend fun getClassroomInfo(code : String,token : String,holder : UiStateHolder<List<OneClassroomRecord>>)
    suspend fun getBuildings(campus : Campus, token : String, holder: UiStateHolder<Pair<Campus, List<OneBuilding>>>)
    suspend fun checkOneLogin(token : String,holder : UiStateHolder<Boolean>)
    fun loginOne(code : String)
}