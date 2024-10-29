package com.hfut.schedule.ui.Activity.shower.bills

import com.google.gson.Gson
import com.hfut.schedule.ViewModel.GuaGuaViewModel
import com.hfut.schedule.logic.datamodel.guaGua.GuaguaBills
import com.hfut.schedule.logic.datamodel.guaGua.GuaguaBillsResponse

fun getGuaguaBills(vm: GuaGuaViewModel) : List<GuaguaBills> {
    return try {
        Gson().fromJson(vm.billsResult.value, GuaguaBillsResponse::class.java).data
    } catch (_:Exception) {
        val list = mutableListOf<GuaguaBills>()
        list
    }
}