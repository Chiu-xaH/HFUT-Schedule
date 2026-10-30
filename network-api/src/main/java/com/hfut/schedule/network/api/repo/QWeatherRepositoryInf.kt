package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNowResponse
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherWarnResponse
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.UiStateHolder

// iOS端实现这个接口
interface QWeatherRepositoryInf {
    suspend fun getWeatherWarn(campus: CampusRegion, weatherWarningData : UiStateHolder<QWeatherWarnResponse>)
    suspend fun getWeather(campus: CampusRegion, qWeatherResult : UiStateHolder<QWeatherNowResponse>)
}