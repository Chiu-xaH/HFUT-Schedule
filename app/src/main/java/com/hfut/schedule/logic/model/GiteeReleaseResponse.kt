package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class GiteeReleaseResponse(
    val name : String,
    val body : String,
    val assets : List<GiteeReleaseBean>
)
data class GiteeReleaseBean(
    val name : String,
    @SerializedName("browser_download_url")
    val url : String
)