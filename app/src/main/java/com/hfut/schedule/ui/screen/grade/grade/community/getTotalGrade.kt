package com.hfut.schedule.ui.screen.grade.grade.community

import com.google.gson.Gson
import com.hfut.schedule.logic.model.community.GradeResult
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

fun getTotalGrade(vm : NetWorkViewModel): GradeResult? {
    return try {
        Gson().fromJson(vm.gradeFromCommunityResponse.value,com.hfut.schedule.logic.model.community.GradeResponse::class.java).result
    } catch (e : Exception) {
        null
    }
}