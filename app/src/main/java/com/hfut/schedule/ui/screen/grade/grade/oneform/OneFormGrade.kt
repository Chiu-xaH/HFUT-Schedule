package com.hfut.schedule.ui.screen.grade.grade.oneform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.MIN_PASS_SCORE
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormCourseGrade
import com.hfut.schedule.network.api.model.response.json.oneform.OneFormGpaRanking
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getScore
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.padding.InnerPaddingHeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OneFormGradeUI(
    innerPadding: PaddingValues,
    vm: NetWorkViewModel,
    input: String,
    displayCompactly: Boolean
) {
    val uiState by vm.oneFormGradesResp.state.collectAsState()
    val scope = rememberCoroutineScope()
    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    val refreshNetwork: suspend () -> Unit = {
        withContext(Dispatchers.IO) {
            vm.oneFormGradesResp.clear()
            val bearer = DataStoreManager.getOneFormBearer()
            if (bearer.isBlank()) {
                vm.oneFormGradesResp.emitError(
                    IllegalStateException("一表通登录状态失效"),
                    StatusCode.UNAUTHORIZED.code
                )
            } else {
                vm.getOneFormGrades(bearer)
            }
        }
    }

    val refreshing = uiState is NetworkUiState.Loading

    LaunchedEffect(Unit) {
        if (refreshing) {
            refreshNetwork()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { scope.launch { refreshNetwork() } }
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .pullRefresh(pullRefreshState)
    ) {
        RefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier
                .padding(innerPadding)
                .align(Alignment.TopCenter)
        )
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val responseData = (uiState as NetworkUiState.Success).data
            val rankings = responseData.gpaRankings.orEmpty()
                .sortedBy { it.sort ?: Int.MAX_VALUE }
            val grades = responseData.courseGrades.orEmpty().filter { grade ->
                input.isBlank() || grade.courseName.orEmpty().contains(input, ignoreCase = true)
            }
            val termGrades = grades
                .sortedWith(
                    compareByDescending<OneFormCourseGrade> { it.academicYear.orEmpty() }
                        .thenByDescending { it.semester.orEmpty() }
                )
                .groupBy { grade ->
                    val academicYear = grade.academicYear?.takeIf { it.isNotBlank() }
                    val semester = grade.semester?.takeIf { it.isNotBlank() }
                    when {
                        semester == null -> academicYear ?: "未知学期"
                        academicYear == null || semester.contains(academicYear) -> semester
                        else -> "$academicYear $semester"
                    }
                }
                .toList()

            if (rankings.isEmpty() && termGrades.isEmpty()) {
                CenterScreen { EmptyIcon() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { InnerPaddingHeight(innerPadding, true) }
                    if (rankings.isNotEmpty()) {
                        item(key = "gpa-ranking-title") {
                            DividerText("学年绩点排名") {
                                expandedMap["gpa-ranking"] = !(expandedMap["gpa-ranking"] ?: true)
                            }
                        }

                        if (expandedMap["gpa-ranking"] ?: true) {
                            items(
                                rankings.size,
                                key = { index ->
                                    val ranking = rankings[index]
                                    "gpa:${ranking.academicYear}:$index"
                                }
                            ) { index ->
                                OneFormGpaRankingItem(rankings[index])
                            }
                        }
                    }
                    termGrades.forEach { (term, termList) ->
                        item(key = "term:$term") {
                            DividerText(term) {
                                expandedMap[term] = !(expandedMap[term] ?: true)
                            }
                        }

                        if (expandedMap[term] ?: true) {
                            items(
                                termList.size,
                                key = { index ->
                                    val grade = termList[index]
                                    "grade:$term:${grade.courseName}:${grade.score}:$index"
                                }
                            ) { index ->
                                OneFormGradeItem(termList[index], displayCompactly)
                            }
                        }
                    }
                    if (termGrades.isEmpty()) {
                        item { CenterScreen { EmptyIcon() } }
                    }
                    if (termGrades.isNotEmpty()) {
                        item {
                            Text(
                                text = "课程成绩（注：只展示课程成绩在85分及以上或“良”及以上）",
                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    item { InnerPaddingHeight(innerPadding, false) }
                }
            }
        }
    }
}

@Composable
private fun OneFormGpaRankingItem(ranking: OneFormGpaRanking) {
    val academicYear = ranking.academicYear.orEmpty().ifBlank { "未知学年" }
    val totalGpa = ranking.totalGpa.orEmpty().ifBlank { "--" }
    val majorRank = ranking.majorRank.orEmpty().ifBlank { "--" }

    CustomCard(
        shape = NoneRoundShape,
        color = cardNormalColor()
    ) {
        TransplantListItem(
            headlineContent = { Text(academicYear) },
            overlineContent = { Text("总绩点 $totalGpa") },
            supportingContent = { Text("专业排名 $majorRank") }
        )
    }
}

@Composable
private fun OneFormGradeItem(grade: OneFormCourseGrade, displayCompactly: Boolean) {
    val score = grade.score.orEmpty().ifBlank { "--" }
    val credit = grade.credit.orEmpty().ifBlank { "--" }
    val majorRank = grade.majorRank.orEmpty().ifBlank { "--" }
    val isFailed = getScore(grade.score.orEmpty())?.let { it < MIN_PASS_SCORE }
        ?: grade.score?.contains("不及格") == true
    val details = listOf(
        "成绩:$score",
        "专业排名:$majorRank",
        "学分:$credit"
    ) + listOfNotNull(
        grade.courseType?.takeIf { it.isNotBlank() }?.let { "课程类型:$it" },
        grade.gradingRule?.takeIf { it.isNotBlank() }?.let { "评分规则:$it" }
    )

    CustomCard(
        shape = NoneRoundShape,
        color = cardNormalColor()
    ) {
        TransplantListItem(
            headlineContent = { Text(grade.courseName ?: "未知课程") },
            overlineContent = {
                Text(
                    if (displayCompactly) {
                        "成绩 $score | 学分 $credit"
                    } else {
                        "成绩 $score | 专业排名 $majorRank | 学分 $credit"
                    }
                )
            },
            supportingContent = if (displayCompactly) {
                { Text("专业排名 $majorRank") }
            } else {
                null
            },
            leadingContent = {
                Icon(
                    painter = painterResource(
                        if (isFailed) R.drawable.error else R.drawable.check_circle
                    ),
                    contentDescription = null,
                    tint = if (isFailed) MaterialTheme.colorScheme.error else LocalContentColor.current
                )
            }
        )
        if (!displayCompactly) {
            NoPadding {
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .padding(bottom = APP_HORIZONTAL_DP - CARD_NORMAL_DP * 3)
                ) {
                    details.forEach { detail ->
                        val item = detail.split(":", limit = 2)
                        AssistChip(
                            onClick = {},
                            border = null,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            label = { Text(item[0]) },
                            trailingIcon = {
                                if (item.size > 1) Text(item[1])
                            },
                            modifier = Modifier.padding(
                                end = CARD_NORMAL_DP * 3,
                                bottom = CARD_NORMAL_DP * 3
                            )
                        )
                    }
                }
            }
        }
    }
}
