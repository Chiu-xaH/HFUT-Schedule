package com.xah.shared.forecast

import com.xah.shared.model.AnalyzeResult
import com.xah.shared.model.BillBean
import com.xah.shared.model.PredictResult
import com.xah.shared.model.Result
import kotlin.math.round


class ConsumptionForecastMonth(bean: BillBean) : ConsumptionForecastBase(bean) {

    override fun wash() {
        super.wash()
        // 按月归类map类似 {2025-05=100.00}
        map = map
            .entries
            .groupBy { it.key.substring(0, 7) }  // yyyy-MM
            .mapValues { entry -> entry.value.sumOf { it.value } }
    }

    override fun analyze(): AnalyzeResult = super.analyze()

    override fun getResult(): Result = super.getResult()

    override fun predict(): PredictResult {
        if (map.isEmpty()) return PredictResult(0.0, 0.0)


        val months = map.keys.sorted() // 按时间排序
        val amounts = months.map { map[it]!! }

        // 按月编号 x = 0, 1, 2...
        val x = months.indices.map { it.toDouble() }
        val y = amounts

        // --- 2. 剔除异常点（标准差法） ---
        val mean = y.average()
        val std = kotlin.math.sqrt(y.sumOf { (it - mean) * (it - mean) } / y.size)
        val filteredIndices = y.mapIndexedNotNull { index, value ->
            if (kotlin.math.abs(value - mean) <= 2 * std) index else null
        }
        val xFiltered = filteredIndices.map { x[it] }
        val yFiltered = filteredIndices.map { y[it] }

        if (xFiltered.isEmpty()) return PredictResult(0.0, 0.0)

        // --- 3. 线性拟合 ---
        val n = xFiltered.size
        val sumX = xFiltered.sum()
        val sumY = yFiltered.sum()
        val sumXY = xFiltered.zip(yFiltered).sumOf { it.first * it.second }
        val sumX2 = xFiltered.sumOf { it * it }

        val a = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        val b = (sumY - a * sumX) / n

        // --- 4. 预测下一月 ---
        val nextMonth = xFiltered.maxOrNull()!! + 1
        val predicted = a * nextMonth + b

        // --- 5. 计算 R² ---
        val yHat = xFiltered.map { a * it + b }
        val ssRes = yFiltered.zip(yHat).sumOf { (yVal, yPred) -> (yVal - yPred) * (yVal - yPred) }
        val ssTot = yFiltered.sumOf { (it - yFiltered.average()) * (it - yFiltered.average()) }
        val r2 = if (ssTot == 0.0) 1.0 else 1 - ssRes / ssTot

        return PredictResult(
            predict = predicted, // 保留两位
            r2 = r2           // R² 保留三位
        )
    }
}


