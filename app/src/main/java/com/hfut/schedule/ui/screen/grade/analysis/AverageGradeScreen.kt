package com.hfut.schedule.ui.screen.grade.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.network.api.model.response.html.JxglstuTermGrade
import com.hfut.schedule.network.api.model.response.html.JxglstuGrade
import com.hfut.schedule.logic.network.repo.JxglstuRepository.parseJxglstuGrade
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.xah.common.logic.state.NetworkUiState

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.AverageGradeDestination
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getGpa
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalCredits
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalGpa
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.getTotalScore
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.component.chart.BarChart
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.logic.util.safeDiv
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AverageGradeScreen(
    vm: NetWorkViewModel,
    useUniAppData : Boolean,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val backDrop = rememberLayerBackdrop()
    var roundCount by rememberSaveable() { mutableIntStateOf(2) }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AverageGradeDestination.TITLE.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                    ) {
                        LiquidButton(
                            enabled = roundCount > 2,
                            onClick = {
                                roundCount--
                            },
                            backdrop = backDrop,
                            isCircle = true,
                        ) {
                            Icon(painterResource(R.drawable.keyboard_arrow_left),null)
                        }
                        Spacer(Modifier.width(BUTTON_PADDING))
                        LiquidButton(
                            onClick = {
                                roundCount = 2
                            },
                            backdrop = backDrop,
                            isCircle = false,
                        ) {
                            Text("${roundCount}位小数")
                        }
                        Spacer(Modifier.width(BUTTON_PADDING))
                        LiquidButton(
                            onClick = {
                                // 即使两位数字一致，也一定要分出高下吗，你这家伙
                                roundCount++
                            },
                            backdrop = backDrop,
                            isCircle = true,
                        ) {
                            Icon(painterResource(R.drawable.keyboard_arrow_right),null)
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            if(useUniAppData) {
                val uiState by vm.uniAppGradesResp.state.collectAsState()
                val refreshNetwork: suspend () -> Unit = m@ {
                    var cookie = DataStoreManager.uniAppJwt.first()
                    if(cookie.isEmpty() || cookie.isEmpty()) {
                        val loginResult = UniAppRepository.login()
                        if(loginResult == false) {
                            return@m
                        }
                        cookie = DataStoreManager.uniAppJwt.first()
                    }
                    vm.uniAppGradesResp.clear()
                    vm.getUniAppGrades(cookie)
                }
                CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                    val gradeList = (uiState as NetworkUiState.Success).data.toList().sortedByDescending { it.first }
                    val safelyList = remember(gradeList) {
                        gradeList
                            .associate { it.first to it.second }
                            .map { (term, items) ->
                                JxglstuTermGrade(
                                    term,
                                    items.filter {
                                        !(it.passed && it.gp == 0.0) && it.finalGrade != null
                                    }.map {
                                        JxglstuGrade(
                                            it.courseNameZh,
                                            it.credits.toString(),
                                            it.gp.toString(),
                                            it.gradeDetail,
                                            it.finalGrade!!,
                                            it.lessonCode
                                        )
                                    }
                                )
                            }
                    }


                    val allTotalCredits by produceState(initialValue = 0f) {
                        value = safelyList.fold(0f) { init,acc -> init + getTotalCredits(acc) }
                    }

                    val allAvgGpa by produceState(initialValue = 0f, key1 = allTotalCredits) {
                        value = safelyList.fold(0f) { init,acc -> init + getTotalGpa(acc) } safeDiv allTotalCredits
                    }

                    val allAvgScore by produceState(initialValue = 0f, key1 = allTotalCredits) {
                        value = safelyList.fold(0f) { init,acc -> init + getTotalScore(acc) } safeDiv allTotalCredits
                    }

                    LazyColumn {
                        item {
                            InnerPaddingHeight(innerPadding,true)
                        }
                        item(key = "total") {
                            DividerTextExpandedWith("平均成绩",false) {
                                LargeCard(
                                    title = "分数 ${allAvgScore.roundOffString(roundCount)} 绩点 ${allAvgGpa.roundOffString(roundCount)}",
                                ) {
                                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                                    BarChart(safelyList.reversed().associate { it.term to getTotalGpa(it) })

                                    safelyList.reversed().forEach {
                                        val totalCredits = remember { getTotalCredits(it) }
                                        val totalGpa = remember { getTotalGpa(it) }
                                        val totalScore = remember { getTotalScore(it) }
                                        val avgGpa = totalGpa safeDiv totalCredits
                                        val avgScore = totalScore safeDiv totalCredits

                                        TransplantListItem(
                                            trailingContent = {
                                                Text("占比${(totalCredits/allTotalCredits*100).roundOffString(roundCount)}%")
                                            },
                                            headlineContent = {
                                                ScrollText("分数 ${avgScore.roundOffString(roundCount)} | 绩点 ${avgGpa.roundOffString(roundCount)} | 学分 $totalCredits")
                                            },
                                            overlineContent = { Text(it.term) }
                                        )
                                    }
                                }
                                BottomTip("数据由本地计算，实际以校务行为准")
                            }
                        }
                        item {
                            DividerText("不参与计算的项目")
                        }
                        item {
                            DevelopingIcon()
                        }
                        // TODO
//                        items {
//                            safelyList
//                        }
                        item {
                            InnerPaddingHeight(innerPadding,false)
                        }
                    }
                }
            } else {
                val gradeList by produceState<List<JxglstuTermGrade>?>(initialValue = null) {
                    scope.launch(Dispatchers.IO) {
                        LargeStringDataManager.read(LargeStringDataManager.GRADE)?.let {
                            value = parseJxglstuGrade(it)
                        }
                    }
                }
                if(gradeList != null) {
                    val safelyList = remember(gradeList) {
                        gradeList!!
                            .associate { it.term to it.list }
                            .map { (term, items) ->
                                JxglstuTermGrade(
                                    term,
                                    items.filter { getGpa(it.gpa) != null }
                                )
                            }
                    }
                    val allTotalCredits by produceState(initialValue = 0f) {
                        value = safelyList.fold(0f) { init,acc -> init + getTotalCredits(acc) }
                    }

                    val allAvgGpa by produceState(initialValue = 0f, key1 = allTotalCredits) {
                        value = safelyList.fold(0f) { init,acc -> init + getTotalGpa(acc) } safeDiv allTotalCredits
                    }

                    val allAvgScore by produceState(initialValue = 0f, key1 = allTotalCredits) {
                        value = safelyList.fold(0f) { init,acc -> init + getTotalScore(acc) } safeDiv allTotalCredits
                    }

                    LazyColumn {
                        item {
                            InnerPaddingHeight(innerPadding,true)
                        }
                        item(key = "total") {
                            DividerTextExpandedWith("平均成绩",false) {
                                LargeCard(
                                    title = "分数 ${allAvgScore.roundOffString(roundCount)} 绩点 ${allAvgGpa.roundOffString(roundCount)}",
                                ) {
                                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                                    BarChart(safelyList.reversed().associate { it.term to getTotalGpa(it) })

                                    safelyList.reversed().forEach {
                                        val totalCredits = remember { getTotalCredits(it) }
                                        val totalGpa = remember { getTotalGpa(it) }
                                        val totalScore = remember { getTotalScore(it) }
                                        val avgGpa = totalGpa safeDiv totalCredits
                                        val avgScore = totalScore safeDiv totalCredits

                                        TransplantListItem(
                                            trailingContent = {
                                                Text("占比${(totalCredits/allTotalCredits*100).roundOffString(0)}%")
                                            },
                                            headlineContent = {
                                                ScrollText("分数 ${avgScore.roundOffString(roundCount)} | 绩点 ${avgGpa.roundOffString(roundCount)} | 学分 $totalCredits")
                                            },
                                            overlineContent = { Text(it.term) }
                                        )
                                    }
                                }
                                BottomTip("数据由本地计算，实际以校务行为准")
                            }
//                            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                        }
                        item {
                            DividerText("不参与计算的项目")
                        }
                        item {
                            DevelopingIcon()
                        }
                        // TODO
//                        items {
//                            safelyList
//                        }
                        item {
                            InnerPaddingHeight(innerPadding,false)
                        }
                    }
                }
            }
        }
    }
}