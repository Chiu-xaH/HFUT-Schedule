package com.hfut.schedule.logic.network.repo


import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNow
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNowResponse
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherWarning
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherWarnResponse
import com.hfut.schedule.network.api.inf.QWeatherService
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.network.api.impl.QWeatherServiceCreator
import com.hfut.schedule.network.api.repo.QWeatherRepositoryInf
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.ui.screen.home.search.function.other.life.getLocation

object QWeatherRepository : QWeatherRepositoryInf {
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)

    override suspend fun getWeatherWarn(campus: CampusRegion, weatherWarningData : UiStateHolder<List<QWeatherWarning>>) =
        launchRequestState(
            holder = weatherWarningData,
            request = { qWeather.getWeatherWarn(locationID = getLocation(campus)) },
            transformSuccess = { _, json -> parseWeatherWarn(json) }
        )

    @JvmStatic
    private fun parseWeatherWarn(json : String) : List<QWeatherWarning> = try {
        GsonInstance.fromJson(json, QWeatherWarnResponse::class.java).warning
    } catch (e : Exception) { throw e }

    override suspend fun getWeather(campus: CampusRegion, qWeatherResult : UiStateHolder<QWeatherNow>) =
        launchRequestState(
            holder = qWeatherResult,
            request = { qWeather.getWeather(locationID = getLocation(campus)) },
            transformSuccess = { _, json -> parseWeatherNow(json) }
        )

    @JvmStatic
    private fun parseWeatherNow(json : String) : QWeatherNow = try {
        if(json.contains(StatusCode.OK.code.toString()))
            GsonInstance.fromJson(json, QWeatherNowResponse::class.java).now
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

}