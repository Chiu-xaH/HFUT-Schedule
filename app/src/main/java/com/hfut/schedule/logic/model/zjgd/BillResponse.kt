package com.hfut.schedule.logic.model.zjgd

data class BillResponse(val data : BillDatas)

data class BillDatas(val records : List<records>,
                     val pages : Int,
                     val msg : String?)

data class records(val tranamt : Int?,
                   val resume : String,
                   val fromAccount : String,
                   val turnoverType : String,
                   val jndatetimeStr : String,
                   val effectdateStr : String)
