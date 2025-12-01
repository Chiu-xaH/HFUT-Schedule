package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.jxglstu.MyApplyModels
import com.hfut.schedule.logic.model.jxglstu.TransferData
import com.hfut.schedule.logic.model.jxglstu.NameZh


fun getEventCampus() : EventCampus = when(getCampusRegion()) {
    CampusRegion.HEFEI -> EventCampus.HEFEI
    CampusRegion.XUANCHENG -> EventCampus.XUANCHENG
}


enum class EventCampus  {
    HEFEI,XUANCHENG,DEFAULT
}

fun getMyTransfer(list : List<MyApplyModels>?,index : Int) : TransferData {
    val n = TransferData(null,0,null, NameZh(""), NameZh(""),0,0)
    return try {
        list?.get(index)?.changeMajorSubmit ?: n
    } catch (e : Exception) {
        n
    }
}


fun getApplyStatus(list : List<MyApplyModels>?, index : Int) : Boolean? {
    return try {
       val data = list?.get(index)?.applyStatus ?: ""
        data == "ACCEPTED"
    } catch (_:Exception) {
        null
    }
}


data class ChangeMajorInfo(val title: String, val batchId: String, val applicationDate: String, val admissionDate: String)
