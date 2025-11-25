package com.hfut.schedule.logic.model.xwx

import com.google.gson.annotations.SerializedName

data class XwxDocPreviewResponseBody(
    @SerializedName("errcode")
    val code : String,
    val result : XwxDocPreviewsBean
)

data class XwxDocPreviewsBean(
    @SerializedName("smallImageList")
    val imageBase64String : String
)
