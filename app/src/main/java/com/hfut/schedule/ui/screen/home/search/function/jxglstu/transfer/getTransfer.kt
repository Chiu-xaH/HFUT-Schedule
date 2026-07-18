package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorMyApply
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorData
import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData


fun getEventCampus() : EventCampus = when(getCampusRegion()) {
    CampusRegion.HEFEI -> EventCampus.HEFEI
    CampusRegion.XUANCHENG -> EventCampus.XUANCHENG
}


enum class EventCampus  {
    HEFEI,XUANCHENG,DEFAULT
}

fun getMyTransfer(list : List<JxglstuTransferMajorMyApply>?, index : Int) : JxglstuTransferMajorData {
    val n = JxglstuTransferMajorData(null,0,null, MultiLanguageBaseData(""), MultiLanguageBaseData(""),0,0)
    return try {
        list?.get(index)?.changeMajorSubmit ?: n
    } catch (e : Exception) {
        n
    }
}


fun getApplyStatus(list : List<JxglstuTransferMajorMyApply>?, index : Int) : Boolean? {
    return try {
       val data = list?.get(index)?.applyStatus ?: ""
        data == "ACCEPTED"
    } catch (_:Exception) {
        null
    }
}


data class ChangeMajorInfo(val title: String, val batchId: String, val applicationDate: String, val admissionDate: String)
