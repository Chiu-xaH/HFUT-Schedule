package com.xah.shared.forecast

import com.xah.shared.model.AnalyzeResult
import com.xah.shared.model.BillBean
import com.xah.shared.model.Result

abstract class ConsumptionForecastBase(val bean : BillBean) : ConsumptionForecast {
    lateinit var map : Map<String, Double>

    init {
        wash()
    }

    // 洗刷数据
    override fun wash() {
        val list = bean.toVercelForecastRequestBody()
        // 不记录电费，电费基本是一个寝室共用，无法代表个人消费
        val washedData = list.filter { !it.merchant.contains("电") }
        map = washedData
            .groupBy { it.date }
            .mapValues { it.value.sumOf { it.amount.toDouble() } }
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
    override fun getResult(): Result = Result(predict(),analyze())
}
