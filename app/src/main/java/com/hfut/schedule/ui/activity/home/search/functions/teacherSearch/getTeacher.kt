package com.hfut.schedule.ui.activity.home.search.functions.teacherSearch

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.TeacherBean
import com.hfut.schedule.logic.beans.TeacherResponse

fun getTeacherList(vm: NetWorkViewModel) : List<TeacherBean?> {
    val json = vm.teacherSearchData.value
    return try {
        Gson().fromJson(json, TeacherResponse::class.java).teacherData
    } catch (e: Exception) {
        emptyList()
    }
}