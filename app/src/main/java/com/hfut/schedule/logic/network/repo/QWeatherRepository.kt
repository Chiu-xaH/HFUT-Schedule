package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.model.QWeatherResponse
import com.hfut.schedule.logic.model.QWeatherWarnBean
import com.hfut.schedule.logic.model.QWeatherWarnResponse
import com.hfut.schedule.logic.network.StatusCode
import com.hfut.schedule.logic.network.api.QWeatherService
import com.hfut.schedule.logic.network.servicecreator.QWeatherServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.ui.screen.home.search.function.other.life.getLocation
import retrofit2.awaitResponse

object QWeatherRepository {
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)

    suspend fun getWeatherWarn(campus: CampusRegion, weatherWarningData : StateHolder<List<QWeatherWarnBean>>) = launchRequestSimple(
        holder = weatherWarningData,
        request = { qWeather.getWeatherWarn(locationID = getLocation(campus)).awaitResponse() },
        transformSuccess = { _,json -> parseWeatherWarn(json) }
    )

    @JvmStatic
    private fun parseWeatherWarn(json : String) : List<QWeatherWarnBean> = try {
        Gson().fromJson(json, QWeatherWarnResponse::class.java).warning
    } catch (e : Exception) { throw e }

    suspend fun getWeather(campus: CampusRegion, qWeatherResult : StateHolder<QWeatherNowBean>) = launchRequestSimple(
        holder = qWeatherResult,
        request = { qWeather.getWeather(locationID = getLocation(campus)).awaitResponse() },
        transformSuccess = { _, json -> parseWeatherNow(json) }
    )


    @JvmStatic
    private fun parseWeatherNow(json : String) : QWeatherNowBean = try {
        if(json.contains(StatusCode.OK.code.toString()))
            Gson().fromJson(json, QWeatherResponse::class.java).now
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }


}