package com.hfut.schedule.logic.datamodel.zjgd

data class FeeResponse(val map : FeeMap)
data class FeeMap(val showData : Map<String,String>)
enum class FeeType {
    WEB,ELECTRIC
}