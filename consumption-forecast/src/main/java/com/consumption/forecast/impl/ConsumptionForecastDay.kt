package com.consumption.forecast.impl

import com.consumption.forecast.inf.ConsumptionForecastBase
import com.xah.common.logic.model.HuiXinBill
import com.consumption.forecast.model.result.PredictResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.sqrt

internal class ConsumptionForecastDay(bean: HuiXinBill) : ConsumptionForecastBase(bean) {

    override fun predict(): PredictResult {
        if (map.isEmpty()) {
            return PredictResult(0.0, 0.0)
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dates = map.keys.map {
            LocalDate.parse(it, formatter)
        }
        val amounts = map.values.toList()
        val base = dates.minOrNull()!!
        val daysSinceStart = dates.map {
            ChronoUnit.DAYS.between(base, it).toDouble()
        }

        // 剔除偏离较大点（标准差法）
        val mean = amounts.average()
        val std = sqrt(
            amounts.sumOf {
                (it - mean) * (it - mean)
            } / amounts.size
        )

        val filteredIndices = amounts.mapIndexedNotNull { index, value ->
            if (abs(value - mean) <= 2 * std) {
                index
            } else {
                null
            }
        }

        val xFiltered = filteredIndices.map {
            daysSinceStart[it]
        }
        val yFiltered = filteredIndices.map {
            amounts[it]
        }

        if (xFiltered.isEmpty()) {
            return PredictResult(0.0, 0.0)
        }

        // 线性拟合
        val n = xFiltered.size
        val sumX = xFiltered.sum()
        val sumY = yFiltered.sum()
        val sumXY = xFiltered
            .zip(yFiltered)
            .sumOf { it.first * it.second }
        val sumX2 = xFiltered.sumOf {
            it * it
        }

        val a = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        val b = (sumY - a * sumX) / n

        val nextDay = xFiltered.maxOrNull()!! + 1
        val predicted = a * nextDay + b

        val yHat = xFiltered.map {
            a * it + b
        }
        val ssRes = yFiltered
            .zip(yHat)
            .sumOf { (y, yPred) ->
                (y - yPred) * (y - yPred)
            }
        val ssTot = yFiltered.sumOf {
            (it - yFiltered.average()) * (it - yFiltered.average())
        }
        val r2 = if (ssTot == 0.0) {
            1.0
        } else {
            1 - ssRes / ssTot
        }

        return PredictResult(
            predict = predicted,  // 保留两位
            r2 = r2             // 保留三位
        )
    }
}