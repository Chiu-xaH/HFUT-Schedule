package com.hfut.schedule.network.api.model.response.json.xiaowuxing

import com.google.gson.annotations.SerializedName

data class XiaoWuXingDocPreviewResponse(
    @SerializedName("errcode")
    val code : String,
    val result : XiaoWuXingDocPreview
)

data class XiaoWuXingDocPreview(
    @SerializedName("smallImageList")
    val imageBase64String : String
)
