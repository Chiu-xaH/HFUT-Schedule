package com.hfut.schedule.network.api.model.response.json.huixin

import com.google.gson.annotations.SerializedName

data class HuiXinCardResponse(
    val data : HuiXinCardData
)

data class HuiXinCardData(
    val card : List<HuiXinCard>
)

data class HuiXinCard(
    @SerializedName("db_balance")
    val balance : Int,
    @SerializedName("unsettle_amount")
    val unsettleAmount : Int,
    @SerializedName("autotrans_limite")
    val autoTransLimit : Int,
    @SerializedName("autotrans_amt")
    val autoTransAmt : Int,
    val name : String,
    val account : String
)