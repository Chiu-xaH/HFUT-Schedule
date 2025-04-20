package com.hfut.schedule.ui.screen.grade.analysis

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.community.AvgResult
import com.hfut.schedule.logic.model.community.GradeAvgResponse
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import java.lang.Exception

fun getAvg(vm : NetWorkViewModel): AvgResult = try {
         Gson().fromJson(vm.avgData.value, GradeAvgResponse::class.java).result
    } catch (e : Exception) {
        AvgResult(null,null,null,null,null,null)
    }