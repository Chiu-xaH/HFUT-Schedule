package com.hfut.schedule.logic.model.zjgd

data class BillResponse(val data : BillBean)

data class BillBean(val records : List<records>, val total : Int,val size : Int)

data class records(val tranamt : Int?,
                   val resume : String,
                   val fromAccount : String,
                   val turnoverType : String,
                   val jndatetimeStr : String,
                   val effectdateStr : String)
