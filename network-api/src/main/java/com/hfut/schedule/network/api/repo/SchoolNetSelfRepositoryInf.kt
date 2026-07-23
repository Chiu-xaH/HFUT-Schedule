package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.html.SchoolNetMonthPayResult
import com.hfut.schedule.network.api.model.response.html.SchoolNetSemesterUsageResult
import com.xah.common.logic.state.UiStateHolder

interface SchoolNetSelfRepositoryInf {
    suspend fun loginAndGetMonthPay(
        year: Int,
        holder: UiStateHolder<SchoolNetMonthPayResult>
    )
    suspend fun getMonthPayAfterLogin(
        year: Int,
        holder: UiStateHolder<SchoolNetMonthPayResult>
    )
    suspend fun loginAndGetSemesterUsage(
        semester: Int,
        holder: UiStateHolder<SchoolNetSemesterUsageResult>
    )
    suspend fun loginAndGetAllSemestersUsage(
        allSemesters: List<Int>,
        holder: UiStateHolder<SchoolNetSemesterUsageResult>
    )
}