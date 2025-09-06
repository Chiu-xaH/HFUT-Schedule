package com.xah.shared.forecast

import com.xah.shared.model.AnalyzeResult
import com.xah.shared.model.PredictResult
import com.xah.shared.model.Result

interface ConsumptionForecast {
    fun wash()
    fun predict() : PredictResult
    fun analyze() : AnalyzeResult
    fun getResult() : Result
}

