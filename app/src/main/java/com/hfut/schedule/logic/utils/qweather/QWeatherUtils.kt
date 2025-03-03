package com.hfut.schedule.logic.utils.qweather

import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo

object QWeatherUtils {
    fun getLocation() : String {
        val campus = getPersonInfo().school
        return if (campus != null) {
            if(campus.contains("宣城") ) "101221401"
            else "101220101"
        } else {
            "101220101"
        }
    }
}