package com.xah.forecast

import com.xah.forecast.impl.ConsumptionForecastDay
import com.xah.forecast.impl.ConsumptionForecastMonth
import com.xah.forecast.model.network.BillBean
import com.xah.forecast.model.network.BillRecordBean
import com.xah.forecast.model.result.TotalResult

fun main() {
    val list = listOf<BillRecordBean>(
        BillRecordBean(
            tranamt = 1300,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-04-01 19:10:05",
            effectdateStr = "2025-04-01 11:26:33",
            orderId = "1"
        ),
        BillRecordBean(
            tranamt = 1200,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-04-01 19:10:05",
            effectdateStr = "2025-04-01 11:26:33",
            orderId = "1"
        ),
        BillRecordBean(
            tranamt = 1000,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-04-02 19:10:05",
            effectdateStr = "2025-04-02 11:26:33",
            orderId = "1"
        ),
        BillRecordBean(
            tranamt = 1500,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-05-02 19:10:05",
            effectdateStr = "2025-05-02 11:26:33",
            orderId = "1"
        ),
        BillRecordBean(
            tranamt = 700,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-05-03 19:10:05",
            effectdateStr = "2025-05-03 11:26:33",
            orderId = "1"
        ),
        BillRecordBean(
            tranamt = 1100,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-06-03 19:10:05",
            effectdateStr = "2025-06-03 11:26:33",
            orderId = "1"
        ),
        BillRecordBean(
            tranamt = 500,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-06-03 19:10:05",
            effectdateStr = "2025-06-03 11:26:33",
            orderId = "1"
        ),

    )
    val bean = BillBean(list,list.size,list.size)

    println(ConsumptionForecastMonth(bean).getResult())
}


fun getConsumptionResult(bean : BillBean) : TotalResult = TotalResult(
    ConsumptionForecastDay(bean).getResult(),
    ConsumptionForecastMonth(bean).getResult()
)


