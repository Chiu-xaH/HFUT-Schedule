package com.hfut.schedule.logic.beans

data class PayResponse(val data : PayData)
data class PayData(val total : String,
                   val dstjf : String,
                   val zsf : String,
                   val xf : String,
                   val dsjxf : String)
