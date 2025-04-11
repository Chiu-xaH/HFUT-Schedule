package com.hfut.schedule.ui.screen.shower.bill

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.logic.model.guagua.GuaguaBills
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse

fun getGuaguaBills(vm: GuaGuaViewModel) : List<GuaguaBills> {
    return try {
        Gson().fromJson(vm.billsResult.value, GuaguaBillsResponse::class.java).data
    } catch (_:Exception) {
        val list = mutableListOf<GuaguaBills>()
        list
    }
}