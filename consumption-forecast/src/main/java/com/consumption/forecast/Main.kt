package com.consumption.forecast

import com.consumption.forecast.impl.ConsumptionForecastDay
import com.consumption.forecast.impl.ConsumptionForecastMonth
import com.xah.common.logic.model.HuiXinBill
import com.xah.common.logic.model.HuiXinBillRecord
import com.consumption.forecast.model.result.TotalResult

internal fun main() {
    val list = listOf(
        HuiXinBillRecord(
            tranamt = 1300,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-04-01 19:10:05",
            effectdateStr = "2025-04-01 11:26:33",
            orderId = "1"
        ),
        HuiXinBillRecord(
            tranamt = 1200,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-04-01 19:10:05",
            effectdateStr = "2025-04-01 11:26:33",
            orderId = "1"
        ),
        HuiXinBillRecord(
            tranamt = 1000,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-04-02 19:10:05",
            effectdateStr = "2025-04-02 11:26:33",
            orderId = "1"
        ),
        HuiXinBillRecord(
            tranamt = 1500,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-05-02 19:10:05",
            effectdateStr = "2025-05-02 11:26:33",
            orderId = "1"
        ),
        HuiXinBillRecord(
            tranamt = 700,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-05-03 19:10:05",
            effectdateStr = "2025-05-03 11:26:33",
            orderId = "1"
        ),
        HuiXinBillRecord(
            tranamt = 1100,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-06-03 19:10:05",
            effectdateStr = "2025-06-03 11:26:33",
            orderId = "1"
        ),
        HuiXinBillRecord(
            tranamt = 500,
            resume = "安徽天和餐饮管理有限公司-持卡人消费",
            fromAccount = "24XXX",
            turnoverType = "消费",
            jndatetimeStr = "2025-06-03 19:10:05",
            effectdateStr = "2025-06-03 11:26:33",
            orderId = "1"
        ),

    )
    val bean = HuiXinBill(list,list.size,list.size)

    println(ConsumptionForecastMonth(bean).getResult())
}

fun getConsumptionResult(bean : HuiXinBill) : TotalResult = TotalResult(
    ConsumptionForecastDay(bean).getResult(),
    ConsumptionForecastMonth(bean).getResult()
)