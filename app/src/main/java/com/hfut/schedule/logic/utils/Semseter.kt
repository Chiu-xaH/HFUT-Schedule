package com.hfut.schedule.logic.utils

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.MyAPIResponse

object Semseter {
    fun getSemseter(semster : Int) : String {
        val codes = (semster - 4) / 10
        val year = 2017
        val code = 3

        var upordown = 0
        if(codes % 4 == 1) {
            upordown = 2
        } else if(codes % 4 == 3) {
            upordown = 1
        }

        val years= (year + (codes - code) / 4) + 1
        return years.toString() +  "~" + (years + 1).toString() + "年第" +  upordown + "学期"
    }
    fun getSemseterCloud() : Int {
        return Gson().fromJson(SharePrefs.prefs.getString("my", MyApplication.NullMy), MyAPIResponse::class.java).semesterId.toInt()
    }
}