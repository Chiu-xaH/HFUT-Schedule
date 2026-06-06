package com.hfut.schedule.ui.screen.report

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.component.screen.PartyPlace
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.status.LoadingScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
private fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartReport: (isGraduating: Boolean) -> Unit
) {
    var isGraduating by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Party(show = true, timeSecond = 1L, count = 200, place = PartyPlace.TOP_CENTER)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.celebration),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = if (isGraduating) "毕业快乐" else "学期报告",
                style = MaterialTheme.typography.headlineLarge,
                color = if (isGraduating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (isGraduating) "四年时光，每一节课、每一次考试、每一个深夜的图书馆，都是你青春的注脚。感谢你选择工大，愿前程似锦，未来可期。"
                       else "在这里回顾你的校园时光",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(48.dp))
            FilterChip(
                selected = isGraduating,
                onClick = { isGraduating = !isGraduating },
                label = {
                    Column {
                        Text(
                            text = "我是毕业生",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "建议连接校园网刷新登录状态后再次打开此界面",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { onStartReport(isGraduating) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("开始查看报告")
            }
        }
    }
}

@Composable
private fun GraduationWelcomeCard(graduationInfo: GraduationInfo) {
    CustomCard(color = cardNormalColor()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "🎓 毕业快乐",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${graduationInfo.startYear}~${graduationInfo.graduationYear}届毕业生",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "四年时光，从${graduationInfo.startYear}年秋天到${graduationInfo.graduationYear}年夏天，" +
                        "你在这里度过了${graduationInfo.totalSemesters}个学期。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "每一节课、每一次考试、每一个深夜的图书馆，都是你青春的注脚。" +
                        "感谢你选择工大，愿前程似锦，未来可期。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun TermReportContent(
    vm: NetWorkViewModel,
    semester: Int,
    isGraduating: Boolean = false,
    allSemesters: List<Int> = emptyList(),
    onLatestSemester: (Int) -> Unit = {}
) {
    val graduationInfo = remember(allSemesters) {
        if (allSemesters.size >= 7) detectGraduation(allSemesters) else null
    }

    val periodLabel = if (isGraduating) "四年" else "本学期"

    Column {
        if (isGraduating && graduationInfo != null) {
            GraduationWelcomeCard(graduationInfo)
        }
        AcademicReportSection(
            vm = vm,
            semester = semester,
            onLatestSemester = onLatestSemester
        )
        AcademicAnalysisSection(vm, semester, periodLabel)
        ExpenseAnalysisSection(vm, semester, periodLabel)
        LibraryReportSection(vm, periodLabel)
        LifeReportSection(vm, semester, allSemesters = if (isGraduating) allSemesters else emptyList())
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TermReportScreen(vm: NetWorkViewModel) {
    val hazeState = rememberHazeState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exporting by remember { mutableStateOf(false) }

    var semester by remember { mutableStateOf<Int?>(null) }
    var initialSemester by remember { mutableIntStateOf(-1) }

    var showExportSheet by remember { mutableStateOf(false) }
    var selectedModules by remember {
        mutableStateOf(TermReportExportModule.entries.toSet())
    }
    var exportSemester by remember { mutableIntStateOf(0) }
    var showWelcome by remember { mutableStateOf(true) }
    var isGraduating by remember { mutableStateOf(false) }
    val gradeState by vm.uniAppGradesResp.state.collectAsState()
    val allSemesters = remember(gradeState) {
        if (gradeState is UiState.Success) {
            (gradeState as UiState.Success).data.keys.mapNotNull {
                SemesterParser.parseSemester(it)
            }
        } else emptyList()
    }

    LaunchedEffect(Unit) {
        initialSemester = SemesterParser.getSemester()
        semester = initialSemester
        exportSemester = initialSemester
    }

    if (showExportSheet) {
        HazeBottomSheet(
            onDismissRequest = { showExportSheet = false },
            showBottomSheet = showExportSheet
        ) {
            Column(modifier = Modifier.padding(CARD_NORMAL_DP)) {
                HazeBottomSheetTopBar(
                    title = "导出学期报表",
                    isPaddingStatusBar = false
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(16.dp))

                    if (isGraduating) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(R.drawable.celebration),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "四年（全部学期）",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    exportSemester = SemesterParser.subSemester(exportSemester)
                                }) {
                                    Icon(painterResource(R.drawable.arrow_back), contentDescription = "上一学期")
                                }
                                Text(
                                    text = SemesterParser.parseSemester(exportSemester) ?: "未知学期",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = {
                                    exportSemester = SemesterParser.plusSemester(exportSemester)
                                }) {
                                    Icon(painterResource(R.drawable.arrow_forward), contentDescription = "下一学期")
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "选择导出内容",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row {
                            TextButton(onClick = {
                                selectedModules = TermReportExportModule.entries.toSet()
                            }) {
                                Text("全选")
                            }
                            TextButton(onClick = { selectedModules = emptySet() }) {
                                Text("清空")
                            }
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TermReportExportModule.entries.forEach { module ->
                            val selected = module in selectedModules
                            FilterChip(
                                border = null,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                selected = selected,
                                onClick = {
                                    selectedModules = if (selected) {
                                        selectedModules - module
                                    } else {
                                        selectedModules + module
                                    }
                                },
                                label = { Text(module.title) }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "请先滑动查看各模块确保数据已加载，再导出",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showExportSheet = false }
                        ) {
                            Text("取消")
                        }
                        Spacer(Modifier.width(8.dp))
                        FilledTonalButton(
                            enabled = selectedModules.isNotEmpty() && !exporting,
                            onClick = {
                                val activity = context.findActivity()
                                if (activity == null) {
                                    Toast.makeText(context, "导出失败：无法获取 Activity", Toast.LENGTH_SHORT).show()
                                    return@FilledTonalButton
                                }
                                showExportSheet = false
                                scope.launch {
                                    exporting = true
                                    try {
                                        exportTermReport(
                                            activity = activity,
                                            vm = vm,
                                            semester = exportSemester,
                                            modules = selectedModules,
                                            action = TermReportExportAction.SAVE_TO_GALLERY,
                                            isGraduating = isGraduating,
                                            allSemesters = allSemesters
                                        )
                                        Toast.makeText(context, "已保存到相册/HFUT-Schedule", Toast.LENGTH_SHORT).show()
                                    } catch (e: Throwable) {
                                        Toast.makeText(context, e.message ?: "导出失败", Toast.LENGTH_LONG).show()
                                    } finally {
                                        exporting = false
                                    }
                                }
                            }
                        ) {
                            Text(if (exporting) "导出中" else "保存图库")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            enabled = selectedModules.isNotEmpty() && !exporting,
                            onClick = {
                                val activity = context.findActivity()
                                if (activity == null) {
                                    Toast.makeText(context, "导出失败：无法获取 Activity", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                showExportSheet = false
                                scope.launch {
                                    exporting = true
                                    try {
                                        exportTermReport(
                                            activity = activity,
                                            vm = vm,
                                            semester = exportSemester,
                                            modules = selectedModules,
                                            action = TermReportExportAction.SHARE,
                                            isGraduating = isGraduating,
                                            allSemesters = allSemesters
                                        )
                                    } catch (e: Throwable) {
                                        Toast.makeText(context, e.message ?: "分享失败", Toast.LENGTH_LONG).show()
                                    } finally {
                                        exporting = false
                                    }
                                }
                            }
                        ) {
                            Text(if (exporting) "导出中" else "分享")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(TermReportDestination.title.asString()) },
                navigationIcon = { TopBarNavigationIcon() },
                actions = {
                    if (!showWelcome) {
                        IconButton(
                            enabled = semester != null && !exporting,
                            onClick = {
                                exportSemester = semester ?: initialSemester
                                showExportSheet = true
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.ios_share),
                                contentDescription = "导出"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showWelcome) {
            WelcomeScreen(
                modifier = Modifier.padding(innerPadding),
                onStartReport = { graduating ->
                    isGraduating = graduating
                    showWelcome = false
                }
            )
        } else if(semester == null) {
            LoadingScreen()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    item { InnerPaddingHeight(innerPadding, true) }

                    item {
                        TermReportContent(
                            vm = vm,
                            semester = if (isGraduating) 0 else semester!!,
                            isGraduating = isGraduating,
                            allSemesters = allSemesters,
                            onLatestSemester = { latestSemester ->
                                semester = latestSemester
                            }
                        )
                    }

                    if (!isGraduating) {
                        item { PaddingForPageControllerButton() }
                    }
                    item { InnerPaddingHeight(innerPadding, false) }
                }

                if (!isGraduating) {
                    PageController(
                        modifier = Modifier.padding(innerPadding),
                        listState = listState,
                        currentPage = semester!!,
                        onNextPage = { semester = it },
                        onPreviousPage = { semester = it },
                        gap = 20,
                        text = SemesterParser.parseSemester(semester!!) ?: "未知学期",
                        paddingBottom = false,
                        resetPage = initialSemester
                    )
                }
            }
        }
    }
}
