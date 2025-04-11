package com.hfut.schedule.ui.screen.home.search.function.teacherSearch

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.TeacherBean
import com.hfut.schedule.logic.model.TeacherResponse

fun getTeacherList(vm: NetWorkViewModel) : List<TeacherBean?> {
    val json = vm.teacherSearchData.value
    return try {
        Gson().fromJson(json, TeacherResponse::class.java).teacherData
    } catch (e: Exception) {
        emptyList()
    }
}