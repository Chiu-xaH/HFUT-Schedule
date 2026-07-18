package com.hfut.schedule.network.api.model.response.json.gitee

import com.google.gson.annotations.SerializedName

data class GiteeReleaseResponse(
    val name : String,
    val body : String,
    val assets : List<GiteeRelease>
)

data class GiteeRelease(
    val name : String,
    @SerializedName("browser_download_url")
    val url : String
)