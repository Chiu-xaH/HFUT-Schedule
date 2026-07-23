package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNow
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherWarning
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.UiStateHolder

// iOS端实现这个接口
interface QWeatherRepositoryInf {
    suspend fun getWeatherWarn(campus: CampusRegion, weatherWarningData : UiStateHolder<List<QWeatherWarning>>)
    suspend fun getWeather(campus: CampusRegion, qWeatherResult : UiStateHolder<QWeatherNow>)
}