package com.hfut.schedule.ui.Activity.success.search.Search.Teachers

import com.google.gson.Gson
import com.hfut.schedule.ViewModel.NetWorkViewModel
import com.hfut.schedule.logic.datamodel.TeacherBean
import com.hfut.schedule.logic.datamodel.TeacherResponse

fun getTeacherList(vm: NetWorkViewModel) : List<TeacherBean?> {
    val json = vm.teacherSearchData.value
    try {
        val data = Gson().fromJson(json, TeacherResponse::class.java).teacherData
        return data
    } catch (e: Exception) {
        val lists = mutableListOf<TeacherBean>()
        return lists
    }
}