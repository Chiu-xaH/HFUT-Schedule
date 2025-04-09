package com.hfut.schedule.ui.activity.grade.grade.community

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.community.GradeResult
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.viewmodel.NetWorkViewModel

fun getTotalGrade(vm : NetWorkViewModel): GradeResult? {
    return try {
        Gson().fromJson(vm.GradeData.value,com.hfut.schedule.logic.beans.community.GradeResponse::class.java).result
    } catch (e : Exception) {
        null
    }
}