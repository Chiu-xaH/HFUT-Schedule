package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.hfut.schedule.logic.model.community.GradeJxglstuResponse
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.safeDiv
import com.xah.common.ui.component.chart.BarChart
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.flow.first

private data class TermGrades(val term: String, val grades: List<GradeJxglstuResponse>, val passFlags: List<Boolean>)

@Composable
fun AcademicReportSection(vm: NetWorkViewModel, semester: Int, onLatestSemester: (Int) -> Unit) {
    val uiState by vm.uniAppGradesResp.state.collectAsState()
    var initialSemesterSet by remember { mutableStateOf(false) }

    val refreshNetwork: suspend () -> Unit = m@ {
        if (uiState is UiState.Success) return@m
        var cookie = DataStoreManager.uniAppJwt.first()
        if (cookie.isEmpty()) {
            if (!UniAppRepository.login()) return@m
            cookie = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppGradesResp.clear()
        vm.getUniAppGrades(cookie)
    }

    LaunchedEffect(Unit) { refreshNetwork() }

    DividerTextExpandedWith("学业报表") {
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            Column {
                val gradeMap = (uiState as UiState.Success).data
                val allTermList = remember(gradeMap) {
                    gradeMap.toList().sortedByDescending { it.first }.mapNotNull { (term, items) ->
                        val filtered = items.filter { it.finalGrade != null }
                        if (filtered.isEmpty()) return@mapNotNull null
                        TermGrades(
                            term = term,
                            grades = filtered.map { GradeJxglstuResponse(it.courseNameZh, it.credits.toString(), it.gp.toString(), it.gradeDetail, it.finalGrade!!, it.lessonCode) },
                            passFlags = filtered.map { it.passed }
                        )
                    }
                }

            LaunchedEffect(allTermList, semester) {
                if (
                    !initialSemesterSet &&
                    semester == 0 &&
                    allTermList.isNotEmpty()
                ) {
                    SemesterParser.parseLatestSemesterFromTerms(allTermList.map { it.term })?.let { latest ->
                        onLatestSemester(latest)
                    }
                    initialSemesterSet = true
                }
            }

            val termInfo = remember(semester) { SemesterParser.parseSemester(semester) }
            val list = remember(allTermList, termInfo) {
                if (semester == 0) allTermList
                else allTermList.filter { SemesterParser.matchesSemester(it.term, semester) }
            }

            val tc = remember(list) { list.fold(0f) { a, b -> a + b.grades.let { g -> g.indices.sumOf { g[it].credits.toFloatOrNull()?.toDouble() ?: 0.0 } }.toFloat() } }
            val ag = remember(list, tc) {
                val totalGp = list.sumOf { b -> b.grades.indices.sumOf { i -> (b.grades[i].gpa.toFloatOrNull()?.toDouble() ?: 0.0) * (b.grades[i].credits.toFloatOrNull() ?: 0f) } }.toFloat()
                totalGp safeDiv tc
            }
            val as2 = remember(list, tc) {
                val totalScore = list.sumOf { b -> b.grades.indices.sumOf { i -> (b.grades[i].detail.toFloatOrNull()?.toDouble() ?: 0.0) * (b.grades[i].credits.toFloatOrNull() ?: 0f) } }.toFloat()
                totalScore safeDiv tc
            }
            val total = remember(list) { list.sumOf { it.grades.size } }
            val passed = remember(list) { list.sumOf { it.passFlags.count { p -> p } } }

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text(SemesterParser.parseSemester(semester) ?: "全部学期") },
                    headlineContent = { Text("总科目 $total | 通过 $passed | 未通过 ${total - passed}", style = MaterialTheme.typography.titleMedium) }
                )
                TransplantListItem(overlineContent = { Text("加权平均分") }, headlineContent = { Text(formatDecimal(as2.toDouble(), 2), style = MaterialTheme.typography.headlineMedium) })
                TransplantListItem(overlineContent = { Text("加权GPA") }, headlineContent = { Text(formatDecimal(ag.toDouble(), 2), style = MaterialTheme.typography.headlineMedium) })
                TransplantListItem(overlineContent = { Text("总学分") }, headlineContent = { Text(formatDecimal(tc.toDouble(), 1), style = MaterialTheme.typography.headlineMedium) })
            }

            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            if (allTermList.size > 1) {
                val gpaData = remember(allTermList) {
                    allTermList.reversed().associate { tg ->
                        val credits = tg.grades.sumOf { it.credits.toFloatOrNull()?.toDouble() ?: 0.0 }.toFloat()
                        val gp = tg.grades.indices.sumOf { i -> (tg.grades[i].gpa.toFloatOrNull()?.toDouble() ?: 0.0) * (tg.grades[i].credits.toFloatOrNull() ?: 0f) }.toFloat()
                        tg.term to (gp safeDiv credits)
                    }
                }
                CustomCard(color = cardNormalColor()) {
                    BarChart(data = gpaData, showLabel = false, title = "各学期GPA", modifier = Modifier.padding(APP_HORIZONTAL_DP))
                }
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            }

            list.reversed().forEach { tg ->
                val ctc = tg.grades.let { g -> g.indices.sumOf { g[it].credits.toFloatOrNull()?.toDouble() ?: 0.0 } }.toFloat()
                val ctg = tg.grades.indices.sumOf { i -> (tg.grades[i].gpa.toFloatOrNull()?.toDouble() ?: 0.0) * (tg.grades[i].credits.toFloatOrNull() ?: 0f) }.toFloat()
                val cts = tg.grades.indices.sumOf { i -> (tg.grades[i].detail.toFloatOrNull()?.toDouble() ?: 0.0) * (tg.grades[i].credits.toFloatOrNull() ?: 0f) }.toFloat()
                val cag = ctg safeDiv ctc
                val cas = cts safeDiv ctc
                val cc = tg.grades.size
                val pc = tg.passFlags.count { it }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text(tg.term) },
                        headlineContent = { Text("分数 ${formatDecimal(cas.toDouble(), 2)} | 绩点 ${formatDecimal(cag.toDouble(), 2)}", style = MaterialTheme.typography.titleSmall) },
                        trailingContent = { Text("${cc}科") }
                    )
                    Row {
                        TransplantListItem(overlineContent = { Text("学分") }, headlineContent = { Text(formatDecimal(ctc.toDouble(), 1)) }, modifier = Modifier.weight(1f))
                        TransplantListItem(overlineContent = { Text("通过") }, headlineContent = { Text("$pc/$cc") }, modifier = Modifier.weight(1f))
                    }
                }
                }
            }
        }
    }
}



