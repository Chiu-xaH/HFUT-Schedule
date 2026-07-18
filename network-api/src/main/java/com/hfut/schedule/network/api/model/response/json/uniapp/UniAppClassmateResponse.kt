package com.hfut.schedule.network.api.model.response.json.uniapp

import com.google.gson.annotations.SerializedName

data class UniAppClassmateResponse(
    val data : List<UniAppClassmate>?
)

data class UniAppClassmate(
    val code : String,
    val nameZh : String,
    @SerializedName("adminclass")
    val className : String,
    val gender : String,
    val telephone : String?,
)