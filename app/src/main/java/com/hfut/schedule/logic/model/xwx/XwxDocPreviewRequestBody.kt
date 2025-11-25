package com.hfut.schedule.logic.model.xwx

data class XwxDocPreviewRequestBody(
    val schoolCode : Long,
    val userId : String,
    val filePropertyType : Int,
    val fileProperty : String,
    val pm : String = "1",
)


