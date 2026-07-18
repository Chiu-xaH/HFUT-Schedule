package com.hfut.schedule.network.api.model.response.json.github

import com.google.gson.annotations.SerializedName

data class GithubRepoResponse(
    @SerializedName("stargazers_count")
    val stargazersCount : Int
)