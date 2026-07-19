package com.xah.common.logic.model

data class HuiXinBillResponse(
    val data : HuiXinBill
)

data class HuiXinBill(
    val records : List<HuiXinBillRecord>,
    val total : Int,
    val size : Int
)

data class HuiXinBillRecord(
    val tranamt : Int?,
    val resume : String,
    val fromAccount : String,
    val turnoverType : String,
    val orderId : String,
    val jndatetimeStr : String,
    val effectdateStr : String
)