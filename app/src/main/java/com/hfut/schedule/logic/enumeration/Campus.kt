package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo


enum class CampusRegion(val description : String) {
    HEFEI("合肥"),XUANCHENG("宣城")
}

enum class Campus(val description : String) {
    TXL("屯溪路"),FCH("翡翠湖"),XC("宣城")
}

fun getCampusRegion() : CampusRegion {
    return if(getPersonInfo().campus?.contains("宣城") == true) {
        CampusRegion.XUANCHENG
    } else {
        CampusRegion.HEFEI
    }
}


fun getCampus() : Campus? {
    val campusText = getPersonInfo().campus ?: return null
    return if(campusText.contains(Campus.XC.description) == true) {
        Campus.XC
    } else if(campusText.contains(Campus.FCH.description) == true) {
        Campus.FCH
    } else if(campusText.contains(Campus.TXL.description) == true) {
        Campus.TXL
    } else {
        null
    }
}
