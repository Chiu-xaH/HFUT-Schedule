package com.hfut.schedule.logic.datamodel.ZJGD

data class LiushuiResponse(val data : data5)

data class data5(val records : List<record>,
                 val pages : Int)

data class record(val tranamt : Int,
                  val resume : String,
                  val turnoverType : String,
                  val jndatetimeStr : String,
                  val effectdateStr : String)
