package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatAmount
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatAnnualRecord
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatData
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatFrequencyRecord
import com.hfut.schedule.network.api.model.response.json.studentportrait.StudentPortraitEatYearAverage
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.ui.component.status.LoadingUI

private fun Double?.formatAmount(): String = this?.roundOffString(2) ?: "-"

private fun academicYearLabel(year: String?, enrollmentYear: Int?): String {
    val academicYear = year?.take(4)?.toIntOrNull()
    val grade = if (academicYear != null && enrollmentYear != null) {
        academicYear - enrollmentYear + 1
    } else {
        null
    }
    val gradeLabel = when (grade) {
        1 -> "大一"
        2 -> "大二"
        3 -> "大三"
        4 -> "大四"
        else -> null
    }
    return when {
        gradeLabel != null && year != null -> "$gradeLabel（${year}学年）"
        gradeLabel != null -> gradeLabel
        year != null -> "${year}学年"
        else -> "未知学年"
    }
}

private fun annualLabel(record: StudentPortraitEatAnnualRecord, enrollmentYear: Int?): String =
    academicYearLabel(record.academicYear, enrollmentYear)

private fun yearAverageLabel(record: StudentPortraitEatYearAverage, enrollmentYear: Int?): String =
    academicYearLabel(record.academicYear, enrollmentYear)

@Composable
private fun AmountTop3Column(
    title: String,
    items: List<StudentPortraitEatAmount>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        items.ifEmpty {
            listOf(StudentPortraitEatAmount(null, null))
        }.forEach { item ->
            TransplantListItem(
                overlineContent = { Text(item.amount.formatAmount()) },
                headlineContent = { Text(item.place ?: "暂无记录") },
                usePadding = false,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun FrequencyColumn(
    title: String,
    items: List<StudentPortraitEatFrequencyRecord>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        items.ifEmpty {
            listOf(StudentPortraitEatFrequencyRecord(null, null))
        }.forEach { item ->
            TransplantListItem(
                overlineContent = { Text(item.count?.let { "$it 次" } ?: "-") },
                headlineContent = { Text(item.place ?: "暂无记录") },
                usePadding = false,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun Top3Card(data: StudentPortraitEatData) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("消费金额 Top3") },
            headlineContent = { Text("个人与全体学生对比", style = MaterialTheme.typography.titleMedium) }
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            AmountTop3Column("个人", data.top3.myData.orEmpty(), Modifier.weight(1f))
            AmountTop3Column("全体学生", data.top3.allData.orEmpty(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun AnnualCard(data: StudentPortraitEatData) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("年消费情况") },
            headlineContent = { Text("按学年消费金额对比", style = MaterialTheme.typography.titleMedium) }
        )
        if (data.annual.isEmpty()) {
            TransplantListItem(headlineContent = { Text("暂无年度消费数据") })
        } else {
            data.annual.forEach { item ->
                TransplantListItem(
                    overlineContent = { Text(annualLabel(item, data.enrollmentYear)) },
                    headlineContent = { Text("个人 ￥${item.myAmount.formatAmount()}") },
                    trailingContent = { Text("平均 ￥${item.averageAmount.formatAmount()}") }
                )
            }
        }
    }
}

@Composable
private fun FrequencyCard(data: StudentPortraitEatData) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("消费频次") },
            headlineContent = { Text("常去消费地点 Top3", style = MaterialTheme.typography.titleMedium) }
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            FrequencyColumn("个人", data.frequency.my.orEmpty(), Modifier.weight(1f))
            FrequencyColumn("全体学生", data.frequency.all.orEmpty(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun OneDayCard(data: StudentPortraitEatData) {
    val oneDay = data.oneDay
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("第一餐对比") },
            headlineContent = { Text("首笔消费时间与地点", style = MaterialTheme.typography.titleMedium) }
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            TransplantListItem(
                overlineContent = { Text("个人") },
                headlineContent = { Text(oneDay.myFirstTime ?: "-") },
                supportingContent = {
                    Text("${oneDay.myFirstPlace ?: "未知地点"} · ${oneDay.myCount ?: "-"} 次")
                },
                modifier = Modifier.weight(1f)
            )
            TransplantListItem(
                overlineContent = { Text("全体学生") },
                headlineContent = { Text(oneDay.allFirstTime ?: "-") },
                supportingContent = {
                    Text("${oneDay.allFirstPlace ?: "未知地点"} · ${oneDay.allCount ?: "-"} 次")
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AverageCard(data: StudentPortraitEatData) {
    val average = data.average
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("消费均值对比") },
            headlineContent = { Text("个人与全体学生", style = MaterialTheme.typography.titleMedium) }
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            TransplantListItem(
                overlineContent = { Text("个人") },
                headlineContent = { Text("年均 ￥${average.myYearAverage.formatAmount()}") },
                supportingContent = {
                    Text("月均 ￥${average.myMonthAverage.formatAmount()} · 日均 ￥${average.myDayAverage.formatAmount()}")
                },
                modifier = Modifier.weight(1f)
            )
            TransplantListItem(
                overlineContent = { Text("全体学生") },
                headlineContent = { Text("年均 ￥${average.allYearAverage.formatAmount()}") },
                supportingContent = {
                    Text("月均 ￥${average.allMonthAverage.formatAmount()} · 日均 ￥${average.allDayAverage.formatAmount()}")
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun YearAverageCard(data: StudentPortraitEatData) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("分年消费均值") },
            headlineContent = { Text("各学年消费对比", style = MaterialTheme.typography.titleMedium) }
        )
        if (data.yearAverages.isEmpty()) {
            TransplantListItem(headlineContent = { Text("暂无分年消费均值") })
        } else {
            data.yearAverages.forEach { item ->
                val average = item.average
                TransplantListItem(
                    overlineContent = { Text(yearAverageLabel(item, data.enrollmentYear)) },
                    headlineContent = {
                        Text("个人年均 ￥${average.myYearAverage.formatAmount()}")
                    },
                    supportingContent = {
                        Text(
                            "全体年均 ￥${average.allYearAverage.formatAmount()} · " +
                                "个人月均 ￥${average.myMonthAverage.formatAmount()} / " +
                                "全体月均 ￥${average.allMonthAverage.formatAmount()}"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StudentPortraitEatReportSection(vm: NetWorkViewModel) {
    val state by vm.studentPortraitEatResp.state.collectAsState()

    DividerTextExpandedWith("信息门户消费画像") {
        when (state) {
            is NetworkUiState.Success -> {
                val data = (state as NetworkUiState.Success).data
                Top3Card(data)
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                AnnualCard(data)
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                FrequencyCard(data)
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                OneDayCard(data)
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                AverageCard(data)
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                YearAverageCard(data)
                ReportDataSourceText("信息门户学生画像")
            }

            is NetworkUiState.Loading,
            is NetworkUiState.Prepare -> {
                CustomCard(color = cardNormalColor()) {
                    LoadingUI()
                }
            }

            is NetworkUiState.Error -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("暂无信息门户消费画像") },
                        supportingContent = { Text("请登录信息门户后重新准备报告") }
                    )
                }
            }
        }
    }
}
