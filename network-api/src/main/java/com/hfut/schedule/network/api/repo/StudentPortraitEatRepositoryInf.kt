package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatData
import com.xah.common.logic.state.UiStateHolder

interface StudentPortraitEatRepositoryInf {
    suspend fun getReport(
        authorization: String,
        holder: UiStateHolder<StudentPortraitEatData>
    )

    suspend fun loginEhall(code: String)
}
