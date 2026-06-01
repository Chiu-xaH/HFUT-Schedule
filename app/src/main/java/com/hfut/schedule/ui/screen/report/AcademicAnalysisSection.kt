package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.logic.model.uniapp.UniAppCoursesResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.JxglstuExam
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamFromCache
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
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

@Composable
fun AcademicAnalysisSection(vm: NetWorkViewModel, semester: Int) {
    val uiState by vm.uniAppGradesResp.state.collectAsState()

    var courseAnalysis by remember { mutableStateOf<CourseAnalysisResult?>(null) }
    var examAnalysis by remember { mutableStateOf<ExamAnalysisResult?>(null) }

    LaunchedEffect(semester) {
        courseAnalysis = null
        try {
            val targetSemester = if (semester == 0) SemesterParser.getSemester() else semester
            val json = LargeStringDataManager.read(LargeStringDataManager.getUniAppCoursesKey(targetSemester))
            if (json != null) {
                val courses = Gson().fromJson(json, UniAppCoursesResponse::class.java).data
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
        } catch (_: Exception) {}
    }

    LaunchedEffect(semester) {
        examAnalysis = null
        try {
            val allExams = getExamFromCache()
            val termInfo = parseSemesterInt(semester)
            val exams = if (termInfo == null || semester == 0) allExams else {
                val start = termInfo.dateRangeStart
                val end = termInfo.dateRangeEnd
                allExams.filter { exam ->
                    try {
                        val date = exam.dateTime.substring(0, 10)
                        date >= start && date < end
                    } catch (_: Exception) { false }
                }
            }
            
            if (exams.isNotEmpty()) {
                val monthGroups = exams.groupBy { exam ->
                    try { exam.dateTime.substring(0, 7) } catch (_: Exception) { "未知" }
                }
                val dates = exams.mapNotNull { exam ->
                    try { LocalDate.parse(exam.dateTime.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE) }
                    catch (_: Exception) { null }
                }.sorted()

                var maxCons = 1; var curCons = 1
                var maxStart = dates.firstOrNull(); var maxEnd = dates.firstOrNull()
                var tempStart = dates.firstOrNull()
                for (i in 1 until dates.size) {
                    if (dates[i].toEpochDay() - dates[i - 1].toEpochDay() == 1L) {
                        curCons++
                    } else {
                        if (curCons > maxCons) { maxCons = curCons; maxStart = tempStart; maxEnd = dates[i - 1] }
                        curCons = 1; tempStart = dates[i]
                    }
                }
                if (curCons > maxCons) { maxCons = curCons; maxStart = tempStart; maxEnd = dates.last() }

                val busiestMonthEntry = monthGroups.maxByOrNull { it.value.size }
                val busiestMonth = busiestMonthEntry?.let { it.key to it.value }
                val monthStats = monthGroups.map { (m, l) -> m to l.size }.sortedByDescending { it.second }
                examAnalysis = ExamAnalysisResult(busiestMonth, monthStats, maxCons, maxStart, maxEnd)
            } else {
                examAnalysis = null
            }
        } catch (_: Exception) {}
    }

    DividerTextExpandedWith("学业分析", false) {
        CommonNetworkScreen(uiState, onReload = null) {
            Column {
                val gradeMap = (uiState as UiState.Success).data
                val termInfo = parseSemesterInt(semester)

            val grades = remember(gradeMap, semester) {
                if (termInfo == null || semester == 0) {
                    gradeMap.values.flatten().filter { it.finalGrade != null }
                } else {
                    gradeMap.filter { termStringToSemesterInt(it.key) == semester }.values.flatten().filter { it.finalGrade != null }
                }
            }

            if (grades.isEmpty()) {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("暂无成绩数据") })
                }
                return@CommonNetworkScreen
            }

            // 最佳和最差科目
            val sorted = remember(grades) { grades.filter { it.finalGrade != null }.sortedByDescending { it.finalGrade!!.toDoubleOrNull() ?: 0.0 } }
            val best = sorted.first()
            val worst = sorted.last()

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text(termInfo?.displayName ?: "全部学期") },
                    headlineContent = { Text("科目排行", style = MaterialTheme.typography.titleMedium) }
                )
                TransplantListItem(
                    overlineContent = { Text("最佳科目 ${formatDecimal(best.finalGrade!!.toDoubleOrNull() ?: 0.0, 1)}分") },
                    headlineContent = { Text(best.courseNameZh, style = MaterialTheme.typography.titleSmall) },
                    trailingContent = { Text("${best.credits}学分") }
                )
                TransplantListItem(
                    overlineContent = { Text("最差科目 ${formatDecimal(worst.finalGrade!!.toDoubleOrNull() ?: 0.0, 1)}分") },
                    headlineContent = { Text(worst.courseNameZh, style = MaterialTheme.typography.titleSmall) },
                    trailingContent = { Text("${worst.credits}学分") }
                )
            }

            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            // 课表分析：最繁忙周
            courseAnalysis?.let { (busiestWeekNum, busiestCourses, avgPerWeek) ->
                val courseNames = busiestCourses.map { it.first }.distinct()

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("课表分析") },
                        headlineContent = { Text("最繁忙的一周", style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(
                        overlineContent = { Text("第${busiestWeekNum}周") },
                        headlineContent = { Text("共 ${busiestCourses.size} 节课", style = MaterialTheme.typography.headlineMedium) },
                        supportingContent = { Text("平均每周 ${formatDecimal(avgPerWeek, 1)} 节") }
                    )
                    val courseListStr = buildString {
                        append(courseNames.take(6).joinToString("\n"))
                        if (courseNames.size > 6) {
                            append("\n...还有 ${courseNames.size - 6} 门课")
                        }
                    }
                    TransplantListItem(headlineContent = { Text(courseListStr, style = MaterialTheme.typography.bodyMedium) })
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            }

            // 考试分析
            examAnalysis?.let { result ->
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("考试分析") },
                        headlineContent = { Text("考试月份分布", style = MaterialTheme.typography.titleMedium) }
                    )
                    result.busiestMonth?.let { (month, exams) ->
                        TransplantListItem(
                            overlineContent = { Text("考试最多的月份") },
                            headlineContent = { Text("${month}月", style = MaterialTheme.typography.headlineMedium) },
                            supportingContent = { Text("共 ${exams.size} 门考试") }
                        )
                    }
                    result.monthStats.forEach { (month, count) ->
                        TransplantListItem(
                            headlineContent = { Text("${month}月") },
                            trailingContent = { Text("${count}门") }
                        )
                    }
                    if (result.maxConsecutiveDays >= 2 && result.consecutiveStart != null && result.consecutiveEnd != null) {
                        TransplantListItem(
                            overlineContent = { Text("连续考试") },
                            headlineContent = { Text("最长连续 ${result.maxConsecutiveDays} 天", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = {
                                Text("${result.consecutiveStart.format(DateTimeFormatter.ofPattern("M月d日"))} ~ ${result.consecutiveEnd.format(DateTimeFormatter.ofPattern("M月d日"))}")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            }

            // 学分分布
            val creditGroups = remember(grades) {
                grades.groupBy { it.credits.toInt() }
                    .mapValues { (_, v) -> v.size }
                    .toList().sortedByDescending { it.first }
            }

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text("学分分布") },
                    headlineContent = { Text("各学分课程数量", style = MaterialTheme.typography.titleMedium) }
                )
                creditGroups.forEach { (credit, count) ->
                    if (credit > 0) {
                        TransplantListItem(
                            headlineContent = { Text("${credit}学分课程") },
                            trailingContent = { Text("${count}门") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            // GPA分布
            val gpaRanges = remember(grades) {
                val ranges = listOf(
                    "4.0+" to { g: Double -> g >= 4.0 },
                    "3.5-4.0" to { g: Double -> g in 3.5..3.99 },
                    "3.0-3.5" to { g: Double -> g in 3.0..3.49 },
                    "2.0-3.0" to { g: Double -> g in 2.0..2.99 },
                    "2.0以下" to { g: Double -> g < 2.0 }
                )
                ranges.mapNotNull { (label, filter) ->
                    val count = grades.count { (it.gp).let { g -> filter(g) } }
                    if (count > 0) label to count else null
                }
            }

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text("绩点分布") },
                    headlineContent = { Text("GPA 分布统计", style = MaterialTheme.typography.titleMedium) }
                )
                gpaRanges.forEach { (range, count) ->
                    TransplantListItem(
                        headlineContent = { Text("GPA $range") },
                        trailingContent = { Text("${count}门") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            // 有趣统计
            val messages = remember(grades) {
                mutableListOf<String>().apply {
                    val totalCredits = grades.sumOf { it.credits }
                    val avgGp = grades.map { it.gp }.average()
                    val highGpa = grades.count { it.gp >= 4.0 }
                    val perfect = grades.count { (it.finalGrade?.toDoubleOrNull() ?: 0.0) >= 95.0 }
                    val nearFail = grades.count { (it.finalGrade?.toDoubleOrNull() ?: 0.0) in 60.0..65.0 }

                    add("本学期共修 ${grades.size} 门课程，总计 ${formatDecimal(totalCredits, 1)} 学分")
                    add("平均绩点 ${formatDecimal(avgGp, 2)}")
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
                    overlineContent = { Text("学期总结") },
                    headlineContent = { Text(messages.joinToString("\n"), style = MaterialTheme.typography.bodyMedium) }
                )
            }
            }
        }
    }
}
