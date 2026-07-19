package com.consumption.forecast.inf

import com.consumption.forecast.model.result.AnalyzeResult
import com.consumption.forecast.model.result.Result
import com.xah.common.logic.model.HuiXinBill
import com.consumption.forecast.helper.toDto

internal abstract class ConsumptionForecastBase(val bean : HuiXinBill) : ConsumptionForecast {

    lateinit var map : Map<String, Double>

    init {
        wash()
    }

    // 洗刷数据
    override fun wash() {
        val list = bean.toDto()
        // 不记录电费，电费基本是一个寝室共用，无法代表个人消费
        val washedData = list.filter {
            !it.merchant.contains("电")
        }
        map = washedData
            .groupBy { it.date }
            .mapValues {
                it.value.sumOf {
                    it.amount.toDouble()
                }
            }
    }

    override fun analyze() : AnalyzeResult {
        // 计算平均值 并返回map
        if (!::map.isInitialized || map.isEmpty()) {
            return AnalyzeResult(0.0, emptyMap())
        }

        val average = map.values.average()

        return AnalyzeResult(
            average = average,
            statisticalData = map
        )
    }
    // 预测
    override fun getResult(): Result = Result(predict(), analyze())
}