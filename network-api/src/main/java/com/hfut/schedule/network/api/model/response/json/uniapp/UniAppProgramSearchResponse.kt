package com.hfut.schedule.network.api.model.response.json.uniapp

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppProgramSearchResponse(
    val data : UniAppProgramSearchList
)

data class UniAppProgramSearchList(
    val data : List<UniAppProgramSearchData>
)

data class UniAppProgramSearchData(
    val id : Int,
    val nameZh: String,
    val grade : String,
    val department : MultiLanguageBaseData,
    val major : MultiLanguageBaseData,
)