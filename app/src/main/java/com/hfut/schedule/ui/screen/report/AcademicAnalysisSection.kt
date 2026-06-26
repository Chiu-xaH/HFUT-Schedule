package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.hfut.schedule.logic.model.uniapp.UniAppCoursesResponse
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.parse.SemesterParser

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.network.util.GsonInstance
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.ui.component.chart.BarChart
import com.xah.common.ui.component.chart.RadarChart
import com.xah.common.ui.component.chart.RadarData
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.JxglstuExam
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamFromCache
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.LogUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private data class CourseAnalysisResult(
    val busiestWeekNum: Int,
    val busiestCourses: List<Pair<String, String>>,
    val avgPerWeek: Double
)

private data class ExamAnalysisResult(
    val busiestMonth: Pair<String, List<JxglstuExam>>?,
    val monthStats: List<Pair<String, Int>>,
    val maxConsecutiveDays: Int,
    val consecutiveStart: LocalDate?,
    val consecutiveEnd: LocalDate?
)

private fun gradeTextToDouble(grade: String): Double? {
    return grade.toDoubleOrNull() ?: when (grade) {
        "优" -> 3.9
        "良" -> 3.0
        "中" -> 2.0
        "及格" -> 1.2
        "不及格" -> 0.0
        else -> null
    }
}

private fun gradeDisplayText(grade: String?, credits: Double): String {
    if (grade == null) return "${credits}学分"
    val numeric = grade.toDoubleOrNull()
    return if (numeric != null) {
        "${numeric.roundOffString(2)}分 / ${credits}学分"
    } else {
        val mapped = gradeTextToDouble(grade)
        if (mapped != null) "$grade (${mapped.roundOffString(2)}) / ${credits}学分"
        else "$grade / ${credits}学分"
    }
}

@Composable
fun AcademicAnalysisSection(vm: NetWorkViewModel, semester: Int, periodLabel: String = "本学期") {
    val uiState by vm.uniAppGradesResp.state.collectAsState()

    val isLatestSemester = remember(semester) {
        SemesterParser.isLatestSemester(semester)
    }

    var courseAnalysis by remember { mutableStateOf<CourseAnalysisResult?>(null) }

    LaunchedEffect(semester, isLatestSemester) {
        courseAnalysis = null

        if (!isLatestSemester) {
            return@LaunchedEffect
        }

        try {
            val json = LargeStringDataManager.read(
                LargeStringDataManager.getUniAppCoursesKey(semester)
            )

            if (json != null) {
                val courses = GsonInstance.fromJson(json, UniAppCoursesResponse::class.java).data
                val weekCount = mutableMapOf<Int, MutableList<Pair<String, String>>>()

                for (item in courses) {
                    for (schedule in item.schedules) {
                        weekCount.getOrPut(schedule.weekIndex) { mutableListOf() }
                            .add(item.course.nameZh to "${schedule.startTime}-${schedule.endTime}")
                    }
                }

                if (weekCount.isNotEmpty()) {
                    val busiest = weekCount.maxByOrNull { it.value.size }!!
                    val avg = weekCount.values.sumOf { it.size }.toDouble() / weekCount.size
                    courseAnalysis = CourseAnalysisResult(busiest.key, busiest.value, avg)
                }
            }
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }

    val examAnalysis by produceState<ExamAnalysisResult?>(
        initialValue = null,
        semester,
        isLatestSemester
    ) {
        value = null

        if (!isLatestSemester) {
            return@produceState
        }

        try {
            val allExams = getExamFromCache()
            val exams = allExams

            if (exams.isNotEmpty()) {
                val monthGroups = exams.groupBy {
                    try {
                        it.dateTime.substring(5, 7).toInt()
                    } catch (e: Exception) {
                        LogUtil.error(e)
                        0
                    }
                }.filterKeys { it != 0 }

                val busiestMonthEntry = monthGroups.maxByOrNull { it.value.size }
                val busiestMonth = busiestMonthEntry?.let { it.key.toString() to it.value }

                val monthStats = monthGroups
                    .map { it.key.toString() to it.value.size }
                    .sortedBy { it.first.toInt() }

                val sortedDates = exams.mapNotNull {
                    try {
                        LocalDate.parse(it.dateTime.substring(0, 10))
                    } catch (e: Exception) {
                        LogUtil.error(e)
                        null
                    }
                }.sorted().distinct()

                var maxCons = 1
                var currentCons = 1
                var maxStart: LocalDate? = sortedDates.firstOrNull()
                var maxEnd: LocalDate? = sortedDates.firstOrNull()
                var currentStart: LocalDate? = sortedDates.firstOrNull()

                for (i in 1 until sortedDates.size) {
                    val prev = sortedDates[i - 1]
                    val curr = sortedDates[i]

                    if (prev.plusDays(1) == curr) {
                        currentCons++
                        if (currentCons > maxCons) {
                            maxCons = currentCons
                            maxStart = currentStart
                            maxEnd = curr
                        }
                    } else {
                        currentCons = 1
                        currentStart = curr
                    }
                }

                value = ExamAnalysisResult(
                    busiestMonth = busiestMonth,
                    monthStats = monthStats,
                    maxConsecutiveDays = maxCons,
                    consecutiveStart = maxStart,
                    consecutiveEnd = maxEnd
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DividerTextExpandedWith("学业分析") {
        CommonNetworkScreen(uiState, onReload = null, isFullScreen = false) {
            Column {
                val gradeMap = (uiState as NetworkUiState.Success).data
                val termInfo = remember(semester) { SemesterParser.parseSemester(semester) }

                val grades = remember(gradeMap, semester) {
                    if (termInfo == null || semester == 0) {
                        gradeMap.values.flatten().filter { it.finalGrade != null }
                    } else {
                        gradeMap.filter { SemesterParser.matchesSemester(it.key, semester) }.values.flatten().filter { it.finalGrade != null }
                    }
                }

                if (grades.isEmpty()) {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(headlineContent = { Text("暂无成绩数据") })
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                } else {
                    // 最佳和最差科目
                    val sorted = remember(grades) { grades.sortedByDescending { it.gp } }
                    val best = sorted.first()
                    val worst = sorted.last()

                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text(SemesterParser.parseSemester(semester) ?: "全部学期") },
                            headlineContent = { Text("科目排行", style = MaterialTheme.typography.titleMedium) }
                        )
                        TransplantListItem(
                            overlineContent = { Text("最佳科目") },
                            headlineContent = { Text(best.courseNameZh, style = MaterialTheme.typography.titleSmall) },
                            trailingContent = { Text(gradeDisplayText(best.finalGrade, best.credits)) }
                        )
                        TransplantListItem(
                            overlineContent = { Text("最差科目") },
                            headlineContent = { Text(worst.courseNameZh, style = MaterialTheme.typography.titleSmall) },
                            trailingContent = { Text(gradeDisplayText(worst.finalGrade, worst.credits)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

                // 课表分析：最繁忙周（仅默认学期可见）
                if (isLatestSemester) {
                    courseAnalysis?.let { (busiestWeekNum, busiestCourses, avgPerWeek) ->
                        val courseNames = busiestCourses.map { it.first }.distinct()

                        CustomCard(color = cardNormalColor()) {
                            TransplantListItem(
                                headlineContent = {
                                    Text(
                                        "最繁忙的一周",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                            TransplantListItem(
                                overlineContent = { Text("第${busiestWeekNum}周") },
                                headlineContent = {
                                    Text(
                                        "共 ${busiestCourses.size} 节课",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                },
                                supportingContent = {
                                    Text("平均每周 ${avgPerWeek.roundOffString(2)} 节")
                                }
                            )
                            val courseListStr = buildString {
                                append(courseNames.take(6).joinToString("\n"))
                                if (courseNames.size > 6) {
                                    append("\n...还有 ${courseNames.size - 6} 门课")
                                }
                            }
                            TransplantListItem(
                                headlineContent = {
                                    Text(courseListStr, style = MaterialTheme.typography.bodyMedium)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    }
                }

                // 考试分析
                // 考试分析（仅最新学期可见）
                if (isLatestSemester) {
                    examAnalysis?.let { result ->
                        CustomCard(color = cardNormalColor()) {
                            TransplantListItem(
                                headlineContent = {
                                    Text(
                                        "考试月分布",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                            result.busiestMonth?.let { (month, exams) ->
                                TransplantListItem(
                                    overlineContent = { Text("考试最多的月份") },
                                    headlineContent = {
                                        Text("${month}月", style = MaterialTheme.typography.headlineMedium)
                                    },
                                    supportingContent = { Text("共 ${exams.size} 门考试") }
                                )
                            }
                            result.monthStats.forEach { (month, count) ->
                                TransplantListItem(
                                    headlineContent = { Text("${month}月") },
                                    trailingContent = { Text("${count}门") }
                                )
                            }
                            if (
                                result.maxConsecutiveDays >= 2 &&
                                result.consecutiveStart != null &&
                                result.consecutiveEnd != null
                            ) {
                                TransplantListItem(
                                    overlineContent = { Text("连续考试") },
                                    headlineContent = {
                                        Text(
                                            "最长连续 ${result.maxConsecutiveDays} 天",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            "${result.consecutiveStart.format(DateTimeFormatter.ofPattern("M月d日"))} ~ " +
                                                    result.consecutiveEnd.format(DateTimeFormatter.ofPattern("M月d日"))
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    }
                }

                if (grades.isNotEmpty()) {
                    // 学分分布 BarChart
                    val creditGroups = remember(grades) {
                        grades.groupBy { it.credits.toInt() }
                            .mapValues { (_, v) -> v.size }
                            .toList().sortedByDescending { it.first }
                    }

                    // GPA分布 BarChart
                    val gpaRanges = remember(grades) {
                        val ranges = listOf(
                            "4.0+" to { g: Double -> g >= 4.0 },
                            "3.5-3.9" to { g: Double -> g in 3.5..3.99 },
                            "3.0-3.4" to { g: Double -> g in 3.0..3.49 },
                            "2.0-2.9" to { g: Double -> g in 2.0..2.99 },
                            "< 2.0" to { g: Double -> g < 2.0 }
                        )
                        ranges.mapNotNull { (label, filter) ->
                            val count = grades.count { filter(it.gp) }
                            if (count > 0) label to count else null
                        }
                    }

                    // 课程类别能力雷达图 (按单科绩点)
                    val radarData = remember(grades) {
                        val targetGrades = if (grades.size > 8) grades.sortedByDescending { it.credits }.take(8) else grades
                        targetGrades.map {
                            val ratio = (it.gp / 4.0f).toFloat().coerceIn(0f, 1f)
                            RadarData(it.courseNameZh.take(4), ratio)
                        }
                    }

                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { Text("学业数据图表", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
                        )

                        if (radarData.size >= 3) {
                            TransplantListItem(headlineContent = { Text("各科绩点能力模型", style = MaterialTheme.typography.titleSmall) })
                            RadarChart(
                                data = radarData,
                                modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (creditGroups.isNotEmpty()) {
                            val barData = creditGroups.associate { "${it.first}学分" to it.second.toFloat() }
                            BarChart(data = barData, showLabel = true, title = "学分分布", modifier = Modifier.padding(bottom = 16.dp))
                        }

                        if (gpaRanges.isNotEmpty()) {
                            val gpaBarData = gpaRanges.associate { it.first to it.second.toFloat() }
                            BarChart(data = gpaBarData, showLabel = true, title = "绩点分布", modifier = Modifier.padding(bottom = 16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                    // 有趣统计
                    val messages = remember(grades) {
                        mutableListOf<String>().apply {
                            val totalCredits = grades.sumOf { it.credits }
                            val avgGp = grades.map { it.gp }.average()
                            val highGpa = grades.count { it.gp >= 4.0 }
                            val perfect = grades.count { val score = it.finalGrade?.toDoubleOrNull(); score != null && score >= 95.0 }
                            val nearFail = grades.count { val score = it.finalGrade?.toDoubleOrNull(); score != null && score in 60.0..65.0 }

                            add("${periodLabel}共修 ${grades.size} 门课程，总计 ${totalCredits.roundOffString(2)} 学分")
                            add("平均绩点 ${avgGp.roundOffString(2)}")
                            if (highGpa > 0) add("其中 $highGpa 门课程 GPA ≥ 4.0，学霸认证！")
                            if (perfect > 0) add("有 $perfect 门课程 ≥ 95 分，接近满分！")
                            if (nearFail > 0) add("有 $nearFail 门课程在 60-65 分之间，险过！")
                            if (avgGp >= 3.5) add("整体表现优秀，继续保持！")
                            else if (avgGp >= 3.0) add("表现不错，还有提升空间~")
                            else add("革命尚未成功，同志仍需努力！")
                        }
                    }

                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { Text("学期总结", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
                        )
                        TransplantListItem(
                            headlineContent = { Text(messages.joinToString("\n"), style = MaterialTheme.typography.bodyMedium) }
                        )
                    }
                }
                }
        }
    }
}
