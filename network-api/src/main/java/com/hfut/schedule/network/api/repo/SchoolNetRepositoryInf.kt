package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.dto.SchoolNetInfo
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.UiStateHolder

interface SchoolNetRepositoryInf {
    suspend fun loginSchoolNet(campus: CampusRegion, loginSchoolNetResponse : UiStateHolder<Boolean>) : Any?
    suspend fun logoutSchoolNet(campus: CampusRegion, loginSchoolNetResponse : UiStateHolder<Boolean>) : Any?
    suspend fun getWebInfo(infoWebValue : UiStateHolder<SchoolNetInfo>)
    suspend fun getWebInfo2(infoWebValue : UiStateHolder<SchoolNetInfo>)
}