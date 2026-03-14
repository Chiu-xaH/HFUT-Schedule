package com.xah.forecast.model.result

data class AnalyzeResult(
    val average : Double,
    val statisticalData : Map<String, Double>,
)