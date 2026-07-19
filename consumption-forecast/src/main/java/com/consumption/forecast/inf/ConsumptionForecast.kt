package com.consumption.forecast.inf

import com.consumption.forecast.model.result.AnalyzeResult
import com.consumption.forecast.model.result.PredictResult
import com.consumption.forecast.model.result.Result

internal interface ConsumptionForecast {
    fun wash()
    fun predict() : PredictResult
    fun analyze() : AnalyzeResult
    fun getResult() : Result
}