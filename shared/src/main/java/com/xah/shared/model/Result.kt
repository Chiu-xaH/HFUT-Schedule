package com.xah.shared.model

data class Result(
    val predictData : PredictResult,
    val analyzeData : AnalyzeResult,
)


data class TotalResult(
    val day : Result,
    val month : Result
)