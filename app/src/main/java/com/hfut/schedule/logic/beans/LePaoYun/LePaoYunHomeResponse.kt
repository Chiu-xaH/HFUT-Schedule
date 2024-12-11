package com.hfut.schedule.logic.beans.LePaoYun

data class LePaoYunHomeResponse(val msg : String, val data : distance)

data class distance(val distance: String,val cralist : List<cralist>)

data class cralist(val raName: String,
                   val dayStartTime : String,
                   val dayEndTime : String,
                   val raStartTime : String,
                   val raEndTime : String,
    val raSingleMileageMin : Int,
    val raSingleMileageMax : Int,
    val raCadenceMin : Int,
    val raCadenceMax : Int,
    val raPaceMin : Int,
    val raPaceMax : Int,
    val points : String,
    val fence : String
    )
