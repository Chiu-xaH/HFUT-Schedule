package com.hfut.schedule.network.api.model.response.json.huixin

import com.google.gson.annotations.SerializedName

data class HuiXinPayStep1Response(
    val data : HuiXinPayStep1Data
)

data class HuiXinPayStep2Response(
    val data : HuiXinPayStep2Data
)

data class HuiXinPayStep3Response(
    val msg : String
)

data class HuiXinPayStep1Data(
    @SerializedName("orderid")
    val orderId : String
)

data class HuiXinPayStep2Data(
    val passwordMap : Map<String,String>
)