package com.hfut.schedule.logic.datamodel.zjgd

data class BillResponse(val data : data5)

data class data5(val records : List<records>,
                 val pages : Int,
                 val msg : String?)

data class records(val tranamt : Int,
                   val resume : String,
                   val fromAccount : String,
                   val turnoverType : String,
                   val jndatetimeStr : String,
                   val effectdateStr : String)
