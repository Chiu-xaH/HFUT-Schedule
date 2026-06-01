package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.model.community.GradeJxglstuDTO
import com.hfut.schedule.logic.model.community.GradeJxglstuResponse
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getGpa
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getScore
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalCredits
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalGpa
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalScore
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.chart.BarChart
import com.xah.common.ui.component.chart.PieChart
import com.xah.common.ui.component.chart.PieChartData
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.logic.safeDiv
import kotlinx.coroutines.flow.first

@Composable
fun AcademicReportSection(vm: NetWorkViewModel, semester: Int, onLatestSemester: (Int) -> Unit) {
    val uiState by vm.uniAppGradesResp.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = m@ {
        if (uiState is UiState.Success) return@m
        var cookie = DataStoreManager.uniAppJwt.first()
        if (cookie.isEmpty()) {
            if (UniAppRepository.login() == false) return@m
            cookie = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppGradesResp.clear()
        vm.getUniAppGrades(cookie)
    }

    LaunchedEffect(Unit) { refreshNetwork() }

    DividerTextExpandedWith("学业报表", false) {
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val gradeMap = (uiState as UiState.Success).data
            val allTermList = remember(gradeMap) {
                gradeMap.toList().sortedByDescending { it.first }.map { (term, items) ->
                    GradeJxglstuDTO(term, items.filter { !(it.passed && it.gp == 0.0) && it.finalGrade != null }
                        .map { GradeJxglstuResponse(it.courseNameZh, it.credits.toString(), it.gp.toString(), it.gradeDetail, it.finalGrade!!, it.lessonCode) })
                }.filter { it.list.isNotEmpty() }
            }

            LaunchedEffect(allTermList) {
                if (allTermList.isNotEmpty()) {
                    latestSemesterFromTerms(allTermList.map { it.term })?.let { onLatestSemester(it) }
                }
            }

            val termInfo = remember(semester) { parseSemesterInt(semester) }
            val list = remember(allTermList, termInfo) {
                if (termInfo == null || semester == 0) allTermList
                else allTermList.filter { termStringToSemesterInt(it.term) == semester }
            }

            val tc = remember(list) { list.fold(0f) { a, b -> a + getTotalCredits(b) } }
            val ag = remember(list, tc) { list.fold(0f) { a, b -> a + getTotalGpa(b) } safeDiv tc }
            val as2 = remember(list, tc) { list.fold(0f) { a, b -> a + getTotalScore(b) } safeDiv tc }
            val total = remember(list) { list.sumOf { it.list.size } }
            val passed = remember(list) { list.sumOf { d -> d.list.count { getScore(it.score)?.let { s -> s >= 60f } ?: false } } }

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text(termInfo?.displayName ?: "全部学期") },
                    headlineContent = { Text("总科目 $total | 通过 $passed | 未通过 ${total - passed}", style = MaterialTheme.typography.titleMedium) }
                )
                TransplantListItem(overlineContent = { Text("加权平均分") }, headlineContent = { Text(formatDecimal(as2.toDouble(), 2), style = MaterialTheme.typography.headlineMedium) })
                TransplantListItem(overlineContent = { Text("加权GPA") }, headlineContent = { Text(formatDecimal(ag.toDouble(), 2), style = MaterialTheme.typography.headlineMedium) })
                TransplantListItem(overlineContent = { Text("总学分") }, headlineContent = { Text(formatDecimal(tc.toDouble(), 1), style = MaterialTheme.typography.headlineMedium) })
            }

            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            if (allTermList.size > 1) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP), contentAlignment = Alignment.Center) {
                    BarChart(data = allTermList.reversed().associate { it.term to getTotalGpa(it) }, showLabel = false, title = "各学期GPA")
                }
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            }

            if (total > 0) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP), contentAlignment = Alignment.Center) {
                    PieChart(data = listOf(PieChartData("通过", passed.toFloat()), PieChartData("未通过", (total - passed).toFloat())), title = "课程通过率")
                }
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            }

            list.reversed().forEach { dto ->
                val ctc = remember { getTotalCredits(dto) }
                val ctg = remember { getTotalGpa(dto) }
                val cts = remember { getTotalScore(dto) }
                val cag = ctg safeDiv ctc
                val cas = cts safeDiv ctc
                val cc = dto.list.size
                val pc = dto.list.count { getScore(it.score)?.let { s -> s >= 60f } ?: false }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text(dto.term) },
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
