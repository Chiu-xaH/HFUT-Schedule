package com.consumption.forecast.helper

import com.consumption.forecast.model.ForecastDto
import com.xah.common.logic.model.HuiXinBill

internal fun HuiXinBill.toDto() : List<ForecastDto> = records.mapNotNull { record ->
    if(record.turnoverType == "消费") {
        val r = (record.tranamt ?: 0 ) / 100.0
        ForecastDto(
            date = record.jndatetimeStr.substringBefore(" "),
            amount = r.toString(),
            merchant = record.resume
        )
    } else {
        null
    }
}