package com.xah.forecast.inf

import com.xah.forecast.model.result.AnalyzeResult
import com.xah.forecast.model.result.PredictResult
import com.xah.forecast.model.result.Result

interface ConsumptionForecast {
    fun wash()
    fun predict() : PredictResult
    fun analyze() : AnalyzeResult
    fun getResult() : Result
}