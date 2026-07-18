package com.hfut.schedule.network.api.model.request.xiaowuxing

data class XiaoWuXingDocPreviewRequest(
    val schoolCode : Long,
    val userId : String,
    val filePropertyType : Int,
    val fileProperty : String,
    val pm : String = "1",
)