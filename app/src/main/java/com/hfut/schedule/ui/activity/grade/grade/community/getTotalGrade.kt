package com.hfut.schedule.ui.activity.grade.grade.community

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.community.GradeResult
import com.hfut.schedule.logic.utils.data.SharePrefs

fun getTotalGrade(): GradeResult? {
    val json = SharePrefs.prefs.getString("Grade", "")
    return try {
        Gson().fromJson(json,com.hfut.schedule.logic.beans.community.GradeResponse::class.java).result
    } catch (e : Exception) {
        null
    }
}