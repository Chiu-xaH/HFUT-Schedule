package com.hfut.schedule.ui.screen.grade.grade

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.community.GradeResponse
import com.hfut.schedule.logic.model.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.model.community.scoreInfoDTOList
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import org.jsoup.Jsoup

fun getGrade(vm: NetWorkViewModel) :  List<scoreInfoDTOList> {
    try {
        val result = Gson().fromJson(vm.gradeFromCommunityResponse.value,GradeResponse::class.java).result
        return result.scoreInfoDTOList
    } catch (e:Exception) {
        return emptyList()
    }
}