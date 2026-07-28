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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalCredits
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalGpa
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalScore
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.parse.SemesterParser

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.safeDiv
import com.xah.common.ui.component.chart.BarChart
import com.xah.common.ui.component.chart.LineChart
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.flow.first

private fun parseCommunityRanking(ranking: String): Float? =
    Regex("""\d+(?:\.\d+)?""").find(ranking)?.value?.toFloatOrNull()

@Composable
fun AcademicReportSection(
    vm: NetWorkViewModel,
    semester: Int,
    allSemesters: List<Int> = emptyList(),
    onLatestSemester: (Int) -> Unit
) {
    val uniAppState by vm.uniAppGradesResp.state.collectAsState()
    val jxglstuState by vm.jxglstuGradeData.state.collectAsState()
    val communityGradeState by vm.gradeFromCommunityResponse.state.collectAsState()
    val allCommunityGradesState by vm.allSemestersGradesFromCommunityResponse.state.collectAsState()
    var initialSemesterSet by remember { mutableStateOf(false) }
    val communityToken = prefs.getString("TOKEN", "").orEmpty()

    val uniAppGrades = (uniAppState as? NetworkUiState.Success)?.data
    val jxglstuGrades = (jxglstuState as? NetworkUiState.Success)?.data

    val useUniApp = hasUniAppGradeData(uniAppGrades)
    val uiState = if (useUniApp) uniAppState else jxglstuState

    val allTermList = remember(uniAppState, jxglstuState) {
        selectReportGrades(uniAppGrades, jxglstuGrades)
            .filter { it.finalGrade != null && !(it.passed && it.gp == 0.0) }
            .toReportTerms()
    }

    val refreshNetwork: suspend () -> Unit = m@ {
        getJxglstuCookie()?.let { cookie ->
            vm.jxglstuGradeData.clear()
            vm.getGradeFromJxglstu(cookie, null)
        }
        if (!hasUniAppGradeData(uniAppGrades)) {
            var cookie = DataStoreManager.uniAppJwt.first()
            if (cookie.isEmpty()) {
                if (!UniAppRepository.login()) return@m
                cookie = DataStoreManager.uniAppJwt.first()
            }
            vm.uniAppGradesResp.clear()
            vm.getUniAppGrades(cookie)
        }
    }

    LaunchedEffect(Unit) { refreshNetwork() }

    LaunchedEffect(semester, communityToken) {
        if (semester <= 0) return@LaunchedEffect
        vm.gradeFromCommunityResponse.clear()
        if (communityToken.isNotEmpty()) {
            SemesterParser.parseSemesterForCommunity(semester)?.let { (year, term) ->
                vm.getGrade(communityToken, year, term)
            }
        }
    }

    LaunchedEffect(allSemesters, communityToken) {
        if (allSemesters.isEmpty()) return@LaunchedEffect
        if (communityToken.isEmpty()) {
            vm.clearAllSemestersGradesFromCommunity()
            return@LaunchedEffect
        }
        vm.getAllSemestersGrades(communityToken, allSemesters)
    }

    DividerTextExpandedWith("学业报表") {
        CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
            Column {
            LaunchedEffect(allTermList, semester) {
                if (
                    !initialSemesterSet &&
                    semester == 0 &&
                    allTermList.isNotEmpty()
                ) {
                    SemesterParser.parseLatestSemesterFromTerms(allTermList.map { it.dto.term })?.let { latest ->
                        onLatestSemester(latest)
                    }
                    initialSemesterSet = true
                }
            }

            val termInfo = remember(semester) { SemesterParser.parseSemester(semester) }
            val list = remember(allTermList, termInfo) {
                if (semester == 0) allTermList
                    else allTermList.filter { SemesterParser.matchesSemester(it.dto.term, semester) }
            }

            val tc = remember(list) { list.fold(0f) { a, b -> a + getTotalCredits(b.dto) } }
            val ag = remember(list, tc) {
                list.fold(0f) { a, b -> a + getTotalGpa(b.dto) } safeDiv tc
            }
            val as2 = remember(list, tc) {
                list.fold(0f) { a, b -> a + getTotalScore(b.dto) } safeDiv tc
            }
            val total = remember(list) { list.sumOf { it.dto.list.size } }
            val passed = remember(list) { list.sumOf { it.passFlags.count { p -> p } } }

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text(SemesterParser.parseSemester(semester) ?: "全部学期") },
                    headlineContent = { Text("总科目 $total | 通过 $passed | 未通过 ${total - passed}", style = MaterialTheme.typography.titleMedium) }
                )
                TransplantListItem(overlineContent = { Text("加权平均分") }, headlineContent = { Text(as2.roundOffString(2), style = MaterialTheme.typography.headlineMedium) })
                TransplantListItem(overlineContent = { Text("加权GPA") }, headlineContent = { Text(ag.roundOffString(2), style = MaterialTheme.typography.headlineMedium) })
                TransplantListItem(overlineContent = { Text("总学分") }, headlineContent = { Text(tc.roundOffString(2), style = MaterialTheme.typography.headlineMedium) })
            }

            val communityGrade = (communityGradeState as? NetworkUiState.Success)?.data
            if (semester > 0 && communityGrade != null) {
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("智慧社区排名") },
                        headlineContent = { Text("班级 ${communityGrade.classRanking}", style = MaterialTheme.typography.headlineMedium) }
                    )
                    TransplantListItem(
                        headlineContent = { Text("专业 ${communityGrade.majorRanking}", style = MaterialTheme.typography.headlineMedium) }
                    )
                    TransplantListItem(
                        overlineContent = { Text("GPA") },
                        headlineContent = { Text(communityGrade.gpa.roundOffString(2), style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(
                        headlineContent = { Text("智慧社区成绩更新可能不及时，排名不准确", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                }
            }

            val communityRankingMap =
                (allCommunityGradesState as? NetworkUiState.Success)
                    ?.data
                    .orEmpty()
                    .filterKeys { it in allSemesters }
            if (semester == 0 && communityRankingMap.isNotEmpty()) {
                val classRankingData = remember(communityRankingMap) {
                    communityRankingMap.toSortedMap().mapNotNull { (sem, result) ->
                        val label = SemesterParser.parseSemesterSimply(sem)
                        parseCommunityRanking(result.classRanking)?.let { label to it }
                    }.toMap()
                }
                val majorRankingData = remember(communityRankingMap) {
                    communityRankingMap.toSortedMap().mapNotNull { (sem, result) ->
                        val label = SemesterParser.parseSemesterSimply(sem)
                        parseCommunityRanking(result.majorRanking)?.let { label to it }
                    }.toMap()
                }

                if (classRankingData.size >= 2 || majorRankingData.size >= 2) {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("智慧社区排名趋势") },
                            headlineContent = { Text("共 ${communityRankingMap.size} 个学期数据", style = MaterialTheme.typography.titleMedium) }
                        )
                        if (classRankingData.size >= 2) {
                            TransplantListItem(headlineContent = { Text("班级排名", style = MaterialTheme.typography.titleSmall) })
                            LineChart(
                                data = classRankingData,
                                showLabel = true,
                                modifier = Modifier
                                    .padding(horizontal = APP_HORIZONTAL_DP)
                                    .height(180.dp),
                                xLabelSpacing = if (classRankingData.size > 8) 2 else 1
                            )
                        }
                        if (majorRankingData.size >= 2) {
                            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                            TransplantListItem(headlineContent = { Text("专业排名", style = MaterialTheme.typography.titleSmall) })
                            LineChart(
                                data = majorRankingData,
                                showLabel = true,
                                modifier = Modifier
                                    .padding(horizontal = APP_HORIZONTAL_DP)
                                    .height(180.dp),
                                xLabelSpacing = if (majorRankingData.size > 8) 2 else 1
                            )
                        }
                        TransplantListItem(
                            headlineContent = { Text("智慧社区源更新可能不及时，排名不准确", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            if (allTermList.size > 1) {
                val gpaData = remember(allTermList) {
                    allTermList.reversed().associate { tg ->
                        tg.dto.term to getTotalGpa(tg.dto)
                    }
                }
                CustomCard(color = cardNormalColor()) {
                    BarChart(data = gpaData, showLabel = false, title = "各学期GPA", modifier = Modifier.padding(APP_HORIZONTAL_DP))
                }
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            }

            list.reversed().forEach { tg ->
                val ctc = getTotalCredits(tg.dto)
                val cag = getTotalGpa(tg.dto) safeDiv ctc
                val cas = getTotalScore(tg.dto) safeDiv ctc
                val cc = tg.dto.list.size
                val pc = tg.passFlags.count { it }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text(tg.dto.term) },
                        headlineContent = { Text("分数 ${cas.roundOffString(2)} | 绩点 ${cag.roundOffString(2)}", style = MaterialTheme.typography.titleSmall) },
                        trailingContent = { Text("${cc}科") }
                    )
                    Row {
                        TransplantListItem(overlineContent = { Text("学分") }, headlineContent = { Text(ctc.roundOffString(2)) }, modifier = Modifier.weight(1f))
                        TransplantListItem(overlineContent = { Text("通过") }, headlineContent = { Text("$pc/$cc") }, modifier = Modifier.weight(1f))
                    }
                }
                }

            ReportDataSourceText(
                buildList {
                    when {
                        useUniApp -> add("合工大教务")
                        hasJxglstuGradeData(jxglstuGrades) -> add("教务系统")
                    }
                    if (
                        (semester > 0 && communityGrade != null) ||
                        (semester == 0 && communityRankingMap.isNotEmpty())
                    ) {
                        add("智慧社区")
                    }
                }
            )
            }
        }
    }
}
