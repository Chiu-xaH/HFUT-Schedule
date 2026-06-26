package com.hfut.schedule.logic.model.community

import android.content.Context

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.network.util.GsonInstance
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers

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
fun getTodayCampusApps(context: Context) : List<TodayCampusAppLargeBean> {
    try {
        val json = with(Dispatchers.IO) {
            context.assets.open("stu.json").bufferedReader().use { it.readText() }
        }
        return with(Dispatchers.Default) {
            GsonInstance.fromJson(json, TodayCampusAppsResponse::class.java).datas
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        return emptyList()
    }
}