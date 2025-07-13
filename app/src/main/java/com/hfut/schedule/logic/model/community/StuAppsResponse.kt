package com.hfut.schedule.logic.model.community

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication

data class StuAppsResponse(
    val result : List<StuAppLargeBean>
)

data class StuAppLargeBean(
    val category : String,
    val subList : List<StuAppBean>
)

data class StuAppBean(
    val name : String,
    val logo : String,
    val url : String?
)
data class TodayCampusAppsResponse(val datas : List<TodayCampusAppLargeBean>)
data class TodayCampusAppLargeBean(
    val categoryName : String,
    val apps : List<TodayCampusAppBean>
)
data class TodayCampusAppBean(
    val name : String,
    val iconUrl : String,
    val openUrl : String
)
fun getTodayCampusApps() : List<TodayCampusAppLargeBean> {
    try {
        val json = MyApplication.context.assets.open("stu.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(json, TodayCampusAppsResponse::class.java).datas
    } catch (e : Exception) {
        return emptyList()
    }
}