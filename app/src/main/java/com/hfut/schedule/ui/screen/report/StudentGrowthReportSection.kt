package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormHonor
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormGpaRanking
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormStudentAchievementData
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormVolunteerActivity
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.ui.component.status.LoadingUI

private data class SecondClassScore(
    val name: String,
    val value: String
)

private fun String?.nonBlank(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

private fun normalizeAcademicYear(value: String?): String? {
    val text = value.nonBlank() ?: return null
    val range = Regex("""(\d{4})\s*[-~～/]\s*(\d{4})""").find(text)
    if (range != null) {
        return "${range.groupValues[1]}-${range.groupValues[2]}"
    }

    val year = Regex("""\d{4}""").find(text)?.value?.toIntOrNull() ?: return null
    return "$year-${year + 1}"
}

private fun secondClassScores(data: OneFormStudentAchievementData): List<SecondClassScore> {
    val scores = buildList {
        data.secondClassXxScores.orEmpty().forEach { item ->
            add("公益服务" to item.publicService)
            add("社会实践" to item.socialPractice)
            add("创业活动" to item.entrepreneurship)
            add("文艺活动" to item.culturalActivity)
            add("技能项目" to item.skills)
        }
        data.secondClassBxScores.orEmpty().forEach { item ->
            add("素质学习" to item.qualityStudy)
            add("科技创新" to item.technologyInnovation)
            add("体育竞赛" to item.sports)
            add("劳动实践" to item.laborPractice)
        }
    }.mapNotNull { (name, value) ->
        value.nonBlank()?.let { name to it }
    }

    return scores
        .groupBy({ it.first }, { it.second })
        .map { (name, values) ->
            SecondClassScore(name, values.distinct().joinToString("、"))
        }
}

private fun formatRanking(value: String?): String = value.nonBlank()?.let {
    if (it.toIntOrNull() != null) "专业第${it}名" else "专业排名 $it"
} ?: "暂无专业排名"

private fun rankingDetails(item: OneFormGpaRanking): String = buildList {
    item.totalGpa.nonBlank()?.let { add("总绩点 $it") }
}.joinToString(" · ")

private fun honorDetails(item: OneFormHonor): String = buildList {
    normalizeAcademicYear(item.year)?.let { add("${it}学年") }
    item.scope.nonBlank()?.let(::add)
    item.level.nonBlank()?.let(::add)
    item.amount.nonBlank()?.let { add("奖金 ${it} 元") }
}.joinToString(" · ")

private fun volunteerDetails(item: OneFormVolunteerActivity): String = buildList {
    item.activityTime.nonBlank()?.let(::add)
    item.organizer.nonBlank()?.let(::add)
}.joinToString(" · ")

private fun formatHours(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else "%.2f".format(value).trimEnd('0').trimEnd('.')

@Composable
private fun RankingCard(rankings: List<OneFormGpaRanking>) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("学年排名") },
            headlineContent = { Text("${rankings.size}条记录", style = MaterialTheme.typography.titleMedium) }
        )
        rankings.forEach { item ->
            val details = rankingDetails(item)
            TransplantListItem(
                overlineContent = {
                    Text(normalizeAcademicYear(item.academicYear)?.let { "${it}学年" } ?: "未知学年")
                },
                headlineContent = { Text(formatRanking(item.majorRank)) },
                supportingContent = details.takeIf(String::isNotEmpty)?.let { text ->
                    { Text(text) }
                }
            )
        }
    }
}

@Composable
private fun HonorsCard(honors: List<OneFormHonor>) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("奖惩情况") },
            headlineContent = { Text("${honors.size}项记录", style = MaterialTheme.typography.titleMedium) }
        )
        honors.forEach { item ->
            val details = honorDetails(item)
            TransplantListItem(
                headlineContent = { Text(item.name.nonBlank() ?: "未命名记录") },
                supportingContent = details.takeIf(String::isNotEmpty)?.let { text ->
                    { Text(text) }
                }
            )
        }
    }
}

@Composable
private fun SecondClassCard(scores: List<SecondClassScore>) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("第二课堂累计") },
            headlineContent = { Text("${scores.size}类成绩", style = MaterialTheme.typography.titleMedium) },
            supportingContent = { Text("以下数据为累计结果") }
        )
        scores.forEach { item ->
            TransplantListItem(
                headlineContent = { Text(item.name) },
                trailingContent = { Text(item.value) }
            )
        }
    }
}

@Composable
private fun VolunteerActivityItem(item: OneFormVolunteerActivity) {
    val details = volunteerDetails(item)
    TransplantListItem(
        headlineContent = { Text(item.activityName.nonBlank() ?: "未命名活动") },
        supportingContent = details.takeIf(String::isNotEmpty)?.let { text ->
            { Text(text) }
        },
        trailingContent = item.serviceHours.nonBlank()?.let { hours ->
            { Text("${hours} 小时") }
        }
    )
}

@Composable
private fun VolunteerCard(activities: List<OneFormVolunteerActivity>) {
    var showDetails by remember { mutableStateOf(false) }
    val totalHours = activities.sumOf { it.serviceHours?.toDoubleOrNull() ?: 0.0 }

    if (showDetails) {
        HazeBottomSheet(
            onDismissRequest = { showDetails = false },
            showBottomSheet = showDetails
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                HazeBottomSheetTopBar(
                    title = "公益/志愿活动详情",
                    isPaddingStatusBar = false
                )
                TransplantListItem(
                    overlineContent = { Text("公益/志愿活动累计") },
                    headlineContent = { Text("${activities.size}次活动") },
                    supportingContent = { Text("累计服务 ${formatHours(totalHours)} 小时") }
                )
                activities.forEach { item ->
                    VolunteerActivityItem(item)
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))
            }
        }
    }

    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("公益/志愿活动累计") },
            headlineContent = { Text("${activities.size}次活动", style = MaterialTheme.typography.titleMedium) },
            supportingContent = { Text("累计服务 ${formatHours(totalHours)} 小时") },
            trailingContent = {
                TextButton(onClick = { showDetails = true }) {
                    Text("查看详情")
                }
            }
        )
    }
}

@Composable
fun StudentGrowthReportSection(
    vm: NetWorkViewModel
) {
    val state by vm.oneFormGradesResp.state.collectAsState()
    val storedBearer by DataStoreManager.oneBearer.collectAsState(initial = "")
    val hasOneFormCredential = storedBearer.isNotBlank() ||
        prefs.getString("bearer", "").orEmpty().isNotBlank()

    DividerTextExpandedWith("综合素质报表") {
        if (!hasOneFormCredential) {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text("暂无综合素质数据") },
                    supportingContent = { Text("请登录信息门户后重新准备报告") }
                )
            }
        } else when (state) {
            is NetworkUiState.Success -> {
                val data = (state as NetworkUiState.Success).data
                val rankings = data.gpaRankings.orEmpty()
                    .sortedBy { it.sort ?: Int.MAX_VALUE }
                val honors = data.honors.orEmpty()
                    .sortedBy { it.sort ?: Int.MAX_VALUE }
                val secondClassScores = secondClassScores(data)
                val volunteerActivities = data.volunteerActivities.orEmpty()
                    .sortedWith(compareBy({ it.sort ?: Int.MAX_VALUE }, { it.activityName.orEmpty() }))

                if (rankings.isNotEmpty()) {
                    RankingCard(rankings)
                } else {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("学年排名") },
                            headlineContent = { Text("暂无学年排名") }
                        )
                    }
                }

                if (honors.isNotEmpty()) {
                    HonorsCard(honors)
                } else {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("奖惩情况") },
                            headlineContent = { Text("暂无奖惩记录") }
                        )
                    }
                }

                if (secondClassScores.isNotEmpty()) {
                    SecondClassCard(secondClassScores)
                }
                if (volunteerActivities.isNotEmpty()) {
                    VolunteerCard(volunteerActivities)
                }

                ReportDataSourceText("信息门户综合报表")
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
                        headlineContent = { Text("暂无综合素质数据") },
                        supportingContent = { Text("请登录信息门户后重新准备报告") }
                    )
                }
            }
        }
    }
}
