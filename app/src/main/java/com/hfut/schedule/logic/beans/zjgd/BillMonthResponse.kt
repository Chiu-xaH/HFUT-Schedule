package com.hfut.schedule.logic.beans.zjgd

data class BillMonthResponse(val data: Map<String, Double>)

data class BillMonth(val date : String, val balance : Double)