package com.hfut.schedule.ui.activity.shower.bills

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.logic.beans.guagua.GuaguaBills
import com.hfut.schedule.logic.beans.guagua.GuaguaBillsResponse

fun getGuaguaBills(vm: GuaGuaViewModel) : List<GuaguaBills> {
    return try {
        Gson().fromJson(vm.billsResult.value, GuaguaBillsResponse::class.java).data
    } catch (_:Exception) {
        val list = mutableListOf<GuaguaBills>()
        list
    }
}