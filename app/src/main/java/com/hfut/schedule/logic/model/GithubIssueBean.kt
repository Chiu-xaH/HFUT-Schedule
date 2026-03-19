package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class GithubIssueBean(
    val number : Long,
    @SerializedName("html_url")
    val url : String,
    val title : String,
    val labels : List<GithubIssueLabelBean>,
    @SerializedName("created_at")
    val createTime : String,
    @SerializedName("updated_at")
    val updatedTime : String,
)

data class GithubIssueLabelBean(
    val description : String
)