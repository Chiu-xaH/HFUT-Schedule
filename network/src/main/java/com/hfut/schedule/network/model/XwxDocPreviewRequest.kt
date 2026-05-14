package com.hfut.schedule.network.model

data class XwxDocPreviewRequest(
    val schoolCode : Long,
    val userId : String,
    val filePropertyType : Int,
    val fileProperty : String,
    val pm : String = "1",
)