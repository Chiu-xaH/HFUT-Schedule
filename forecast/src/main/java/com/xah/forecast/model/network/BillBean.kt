package com.xah.forecast.model.network

import com.xah.forecast.model.network.VercelForecastRequestBody

data class BillBean(val records : List<BillRecordBean>, val total : Int, val size : Int) {
    fun toVercelForecastRequestBody() : List<VercelForecastRequestBody> = records.mapNotNull { record ->
        if(record.turnoverType == "消费") {
            val r = (record.tranamt ?: 0 ) / 100.0
            VercelForecastRequestBody(
                date = record.jndatetimeStr.substringBefore(" "),
                amount = r.toString(),
                merchant = record.resume
            )
        } else {
            null
        }
    }
}