package com.hfut.schedule.ui.screen.home.search.function.selectCourse

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.jxglstu.SelectCourse
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo

fun getSelectCourseList(vm : NetWorkViewModel) : List<SelectCourse> {
    val json = vm.selectCourseData.value
    return try {
        val courses: List<SelectCourse> = Gson().fromJson(json, object : TypeToken<List<SelectCourse>>() {}.type)
        courses
    } catch (_: Exception) {
        emptyList()
    }
}

fun getSelectedCourse(vm : NetWorkViewModel) : List<SelectCourseInfo>  {
    val json = vm.selectedData.value
    return try {
        val courses: List<SelectCourseInfo> = Gson().fromJson(json, object : TypeToken<List<SelectCourseInfo>>() {}.type)
        courses
    } catch (_: Exception) {
        emptyList()
    }
}