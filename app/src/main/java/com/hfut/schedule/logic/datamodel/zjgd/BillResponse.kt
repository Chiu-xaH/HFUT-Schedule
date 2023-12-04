package com.hfut.schedule.logic.datamodel.zjgd

data class BillResponse(val data : data5)

data class data5(val records : List<records>,
                 val pages : Int)

data class records(val tranamt : Int,
                   val resume : String,
                   val turnoverType : String,
                   val jndatetimeStr : String,
                   val effectdateStr : String)
