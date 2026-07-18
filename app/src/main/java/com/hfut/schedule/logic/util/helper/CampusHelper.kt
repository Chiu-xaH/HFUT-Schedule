package com.hfut.schedule.logic.util.helper

import com.xah.common.logic.model.Campus
import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo

fun getCampusRegion() : CampusRegion {
    return if(getPersonInfo().campus?.contains("宣城") == true) {
        CampusRegion.XUANCHENG
    } else {
        CampusRegion.HEFEI
    }
}

fun getCampus() : Campus? {
    val campusText = getPersonInfo().campus ?: return null
    return if(campusText.contains(Campus.XC.description)) {
        Campus.XC
    } else if(campusText.contains(Campus.FCH.description)) {
        Campus.FCH
    } else if(campusText.contains(Campus.TXL.description)) {
        Campus.TXL
    } else {
        null
    }
}