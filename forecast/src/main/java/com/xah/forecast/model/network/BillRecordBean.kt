package com.xah.forecast.model.network

data class BillRecordBean(
    val tranamt : Int?,
    val resume : String,
    val fromAccount : String,
    val turnoverType : String,
    val orderId : String,
    val jndatetimeStr : String,
    val effectdateStr : String
)