package com.hfut.schedule.logic.beans.zjgd

data class PayStep1Response(val data : PayStep1Data)

data class PayStep2Response(val data : PayStep2Data)
data class PayStep3Response(val msg : String)
data class PayStep1Data(val orderid : String)
data class PayStep2Data(val passwordMap : Map<String,String>)