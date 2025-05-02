package com.hfut.schedule.ui.screen.home.search.function.work

import com.google.gson.Gson
import com.hfut.schedule.logic.model.WorkSearchResponse

fun parseWorkResponse(resp : String): WorkSearchResponse? = try {
    // 去掉前缀，提取 JSON 部分
    val jsonStr = resp.removePrefix("var __result = ").removeSuffix(";").trim()
    Gson().fromJson(jsonStr,WorkSearchResponse::class.java)
} catch (e : Exception) {
    null
}