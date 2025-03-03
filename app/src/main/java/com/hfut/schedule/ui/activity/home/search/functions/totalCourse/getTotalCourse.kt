package com.hfut.schedule.ui.activity.home.search.functions.totalCourse

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.community.CourseTotalResponse
import com.hfut.schedule.logic.beans.community.courseBasicInfoDTOList
import com.hfut.schedule.logic.beans.community.courseDetailDTOList
import com.hfut.schedule.logic.utils.data.SharePrefs

fun getCourse(): List<courseBasicInfoDTOList>  {
    val json = SharePrefs.prefs.getString("Course", MyApplication.NullTotal)
    return try {
        Gson().fromJson(json, CourseTotalResponse::class.java).result.courseBasicInfoDTOList
    } catch (e:Exception) {
        emptyList()
    }
}


fun getDetailCourse(item : Int,name : String) : List<courseDetailDTOList> {
    val json = SharePrefs.prefs.getString("Course", MyApplication.NullTotal)
    try {
        val result = Gson().fromJson(json, CourseTotalResponse::class.java).result
        return result.courseBasicInfoDTOList[item].courseDetailDTOList
    } catch (e:Exception) {
        return emptyList()
    }
}
