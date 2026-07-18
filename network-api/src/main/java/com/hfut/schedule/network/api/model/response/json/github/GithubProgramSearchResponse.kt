package com.hfut.schedule.network.api.model.response.json.github

data class GithubProgramSearchResponse(
    val id : Int,
    val grade : String,
    val name : String,
    val department : String,
    val major : String
)