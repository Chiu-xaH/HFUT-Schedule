package com.hfut.schedule.ui.screen.report

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.component.screen.PartyPlace
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.component.status.LoadingScreen
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.logic.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val REPORT_PREPARATION_TIMEOUT_MILLIS = 30_000L

private val REPORT_DATA_USAGE_TEXT = """
    为生成学期报告，本功能会在你主动开始后，使用应用内已经保存的登录状态，从以下数据源获取与你相关的数据：

    1. 教务系统、合工大教务：成绩、学分和绩点；
    2. 智慧社区：成绩排名、宿舍信息和卫生评分；
    3. 慧新易校：校园卡消费、消费预测，以及适用校区的本月校园网数据；
    4. 图书馆：借阅概览和已加载的借阅信息；
    5. 校园网自服务接口：学期或全部学期的校园网使用情况。

    报告还会读取应用内已有的课表和考试记录进行统计。实际请求的数据源会根据你的校区、登录状态和报告类型确定。获取的数据仅用于生成、展示和按你主动操作导出学期报告；未登录或请求失败的数据源可能导致对应板块内容缺失。
""".trimIndent()

private fun NetworkUiState<*>.toPreparationStatus(): ReportPreparationStatus = when (this) {
    is NetworkUiState.Success -> ReportPreparationStatus.COMPLETED
    is NetworkUiState.Error -> ReportPreparationStatus.FAILED
    is NetworkUiState.Loading,
    is NetworkUiState.Prepare -> ReportPreparationStatus.LOADING
}

private fun aggregatePreparationStatus(
    requests: List<ReportPreparationDetail>
): ReportPreparationStatus = when {
    requests.isEmpty() -> ReportPreparationStatus.UNAVAILABLE
    requests.any { it.status == ReportPreparationStatus.LOADING } ->
        ReportPreparationStatus.LOADING
    requests.any { it.status == ReportPreparationStatus.COMPLETED } &&
        requests.any {
            it.status == ReportPreparationStatus.FAILED ||
                it.status == ReportPreparationStatus.UNAVAILABLE
        } -> ReportPreparationStatus.PARTIAL
    requests.any { it.status == ReportPreparationStatus.FAILED } ->
        ReportPreparationStatus.FAILED
    requests.all { it.status == ReportPreparationStatus.UNAVAILABLE } ->
        ReportPreparationStatus.UNAVAILABLE
    else -> ReportPreparationStatus.COMPLETED
}

@Composable
private fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartReport: (isGraduating: Boolean) -> Unit
) {
    var isGraduating by remember { mutableStateOf(false) }
    var showAgreement by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED),
        label = "报告首页透明度"
    )
    val contentOffset by animateDpAsState(
        targetValue = if (contentVisible) 0.dp else 16.dp,
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED),
        label = "报告首页位移"
    )

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    if (showAgreement) {
        LittleDialog(
            onDismissRequest = { showAgreement = false },
            onConfirmation = { showAgreement = false },
            dialogTitle = "学期报告数据使用说明",
            dialogText = REPORT_DATA_USAGE_TEXT,
            conformText = "知道了",
            dismissText = "关闭"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Party(
            show = isGraduating,
            timeSecond = 1L,
            count = 200,
            place = PartyPlace.TOP_CENTER
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .offset(y = contentOffset)
                .alpha(contentAlpha)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(56.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.celebration),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                AnimatedContent(
                    targetState = isGraduating,
                    transitionSpec = {
                        AppAnimationManager.fadeAnimation.enter togetherWith
                            AppAnimationManager.fadeAnimation.exit
                    },
                    label = "报告类型标题",
                    modifier = Modifier.weight(1f)
                ) { graduating ->
                    Column {
                        Text(
                            text = if (graduating) "回顾你的大学时光" else "回顾你的校园时光",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (graduating) {
                                "汇总全部可用学期，生成毕业报告"
                            } else {
                                "整合本学期的学习与校园生活数据"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Text(
                text = "报告范围",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
            )
            Spacer(Modifier.height(6.dp))
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = {
                        Text(
                            text = "我是毕业生",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    supportingContent = {
                        AnimatedContent(
                            targetState = isGraduating,
                            transitionSpec = {
                                AppAnimationManager.fadeAnimation.enter togetherWith
                                    AppAnimationManager.fadeAnimation.exit
                            },
                            label = "报告范围说明"
                        ) { graduating ->
                            Text(
                                text = if (graduating) {
                                    "将统计全部可用学期的数据"
                                } else {
                                    "默认生成当前学期的报告"
                                }
                            )
                        }
                    },
                    trailingContent = {
                        Switch(
                            checked = isGraduating,
                            onCheckedChange = { isGraduating = it }
                        )
                    },
                    modifier = Modifier.clickable {
                        isGraduating = !isGraduating
                    }
                )
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "数据说明",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
            )
            Spacer(Modifier.height(6.dp))
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = {
                        Text(
                            text = "报告数据来源",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    supportingContent = {
                        Text("教务、慧新易校、图书馆、智慧社区及校园网")
                    }
                )
                PaddingHorizontalDivider()
                TextButton(
                    onClick = { showAgreement = true },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp)
                ) {
                    Text("查看详细说明")
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "生成前建议连接校园网，并刷新各数据源的登录状态。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onStartReport(isGraduating) },
                modifier = Modifier
                    .padding(horizontal = APP_HORIZONTAL_DP)
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                AnimatedContent(
                    targetState = isGraduating,
                    transitionSpec = {
                        AppAnimationManager.fadeAnimation.enter togetherWith
                            AppAnimationManager.fadeAnimation.exit
                    },
                    label = "开始生成报告按钮"
                ) { graduating ->
                    Text(if (graduating) "开始生成毕业报告" else "开始生成学期报告")
                }
            }
            Spacer(Modifier.height(APP_HORIZONTAL_DP))
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
            allSemesters = if (isGraduating) allSemesters else emptyList(),
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
    var showPreparation by remember { mutableStateOf(false) }
    var preparationAttempt by remember { mutableIntStateOf(0) }
    var preparationTimedOut by remember { mutableStateOf(false) }
    var preparationReady by remember { mutableStateOf(false) }
    var isGraduating by remember { mutableStateOf(false) }
    val uniAppGradeState by vm.uniAppGradesResp.state.collectAsState()
    val jxglstuGradeState by vm.jxglstuGradeData.state.collectAsState()
    val communityGradeState by vm.gradeFromCommunityResponse.state.collectAsState()
    val allCommunityRankingsState by vm.allSemestersRankingsFromCommunityResponse.state.collectAsState()
    val billState by vm.huiXinBillResult.state.collectAsState()
    val predictedState by vm.cardPredictedResponse.state.collectAsState()
    val libraryState by vm.libraryStatusResp.state.collectAsState()
    val dormitoryState by vm.dormitoryFromCommunityResp.state.collectAsState()
    val dormitoryUsersState by vm.dormitoryInfoFromCommunityResp.state.collectAsState()
    val dormitoryScoresState by vm.allDormitoryScoresResp.state.collectAsState()
    val huiXinSchoolNetState by vm.huiXinSchoolNetInfoResp.state.collectAsState()
    val schoolNetState by vm.schoolNetSemesterUsageResp.state.collectAsState()
    val uniAppGrades = (uniAppGradeState as? NetworkUiState.Success)?.data
    val jxglstuGrades = (jxglstuGradeState as? NetworkUiState.Success)?.data
    val allSemesters = remember(uniAppGradeState, jxglstuGradeState) {
        val terms = when {
            hasUniAppGradeData(uniAppGrades) ->
                uniAppGrades!!.filterValues { it.isNotEmpty() }.keys
            hasJxglstuGradeData(jxglstuGrades) ->
                jxglstuGrades!!.filter { it.list.isNotEmpty() }.map { it.term }
            else -> emptyList()
        }
        terms.mapNotNull(SemesterParser::parseSemester).distinct()
    }
    val communityToken = remember(preparationAttempt) {
        prefs.getString("TOKEN", "").orEmpty()
    }
    val huiXinAuth = remember(preparationAttempt) {
        prefs.getString("auth", "").orEmpty()
    }
    val libraryToken = remember(preparationAttempt) {
        prefs.getString(SharedPrefs.LIBRARY_TOKEN, "").orEmpty()
    }
    val storedJxglstuCookie = remember(preparationAttempt) {
        prefs.getString("redirect", "").orEmpty()
    }
    val webVpnCookie by DataStoreManager.webVpnCookies.collectAsState(initial = "")
    val hasJxglstuCredential = if (GlobalUiStateHolder.webVpn) {
        webVpnCookie.isNotEmpty()
    } else {
        storedJxglstuCookie.isNotEmpty()
    }
    val isXuanCheng = remember { getCampusRegion() == CampusRegion.XUANCHENG }

    val uniAppGradeRequest = ReportPreparationDetail(
        title = "合工大教务成绩",
        status = when {
            hasUniAppGradeData(uniAppGrades) -> ReportPreparationStatus.COMPLETED
            uniAppGradeState is NetworkUiState.Loading ||
                uniAppGradeState is NetworkUiState.Prepare -> ReportPreparationStatus.LOADING
            else -> ReportPreparationStatus.FAILED
        }
    )
    val jxglstuGradeRequest = ReportPreparationDetail(
        title = "教务系统成绩",
        status = when {
            hasJxglstuGradeData(jxglstuGrades) -> ReportPreparationStatus.COMPLETED
            !hasJxglstuCredential -> ReportPreparationStatus.UNAVAILABLE
            jxglstuGradeState is NetworkUiState.Loading ||
                jxglstuGradeState is NetworkUiState.Prepare -> ReportPreparationStatus.LOADING
            else -> ReportPreparationStatus.FAILED
        }
    )
    val academicCoreStatus = when {
        uniAppGradeRequest.status == ReportPreparationStatus.COMPLETED ||
            jxglstuGradeRequest.status == ReportPreparationStatus.COMPLETED ->
            ReportPreparationStatus.COMPLETED
        uniAppGradeRequest.status == ReportPreparationStatus.LOADING ||
            jxglstuGradeRequest.status == ReportPreparationStatus.LOADING ->
            ReportPreparationStatus.LOADING
        else -> ReportPreparationStatus.FAILED
    }
    val communityRankingRequest = ReportPreparationDetail(
        title = if (isGraduating) "智慧社区各学期排名" else "智慧社区排名",
        status = if (communityToken.isEmpty()) {
            ReportPreparationStatus.UNAVAILABLE
        } else if (isGraduating) {
            when (val state = allCommunityRankingsState) {
                is NetworkUiState.Success -> when {
                    allSemesters.isEmpty() -> ReportPreparationStatus.LOADING
                    state.data.keys.containsAll(allSemesters) ->
                        ReportPreparationStatus.COMPLETED
                    state.data.isNotEmpty() -> ReportPreparationStatus.PARTIAL
                    else -> ReportPreparationStatus.FAILED
                }
                is NetworkUiState.Error -> ReportPreparationStatus.FAILED
                is NetworkUiState.Loading,
                is NetworkUiState.Prepare -> ReportPreparationStatus.LOADING
            }
        } else {
            communityGradeState.toPreparationStatus()
        }
    )
    val academicStatus = when {
        academicCoreStatus == ReportPreparationStatus.LOADING ||
            communityRankingRequest.status == ReportPreparationStatus.LOADING ->
            ReportPreparationStatus.LOADING
        academicCoreStatus == ReportPreparationStatus.FAILED ->
            ReportPreparationStatus.FAILED
        communityRankingRequest.status == ReportPreparationStatus.PARTIAL ||
            communityRankingRequest.status == ReportPreparationStatus.FAILED ||
            communityRankingRequest.status == ReportPreparationStatus.UNAVAILABLE ->
            ReportPreparationStatus.PARTIAL
        else -> ReportPreparationStatus.COMPLETED
    }
    val academicCoreRequests = buildList {
        if (academicCoreStatus == ReportPreparationStatus.COMPLETED) {
            add(ReportPreparationDetail("成绩数据", ReportPreparationStatus.COMPLETED))
        } else {
            add(uniAppGradeRequest)
            add(jxglstuGradeRequest)
        }
    }
    val academicRequests = buildList {
        addAll(academicCoreRequests)
        add(communityRankingRequest)
    }

    val expenseRequests = if (huiXinAuth.isEmpty()) {
        listOf(
            ReportPreparationDetail(
                title = "慧新易校消费数据",
                status = ReportPreparationStatus.UNAVAILABLE
            )
        )
    } else {
        listOf(
            ReportPreparationDetail("慧新易校消费明细", billState.toPreparationStatus()),
            ReportPreparationDetail("慧新易校消费预测", predictedState.toPreparationStatus())
        )
    }
    val expenseStatus = aggregatePreparationStatus(expenseRequests)

    val libraryRequests = if (libraryToken.isEmpty()) {
        listOf(
            ReportPreparationDetail(
                title = "图书馆借阅概览",
                status = ReportPreparationStatus.UNAVAILABLE
            )
        )
    } else {
        listOf(
            ReportPreparationDetail("图书馆借阅概览", libraryState.toPreparationStatus())
        )
    }
    val libraryStatus = aggregatePreparationStatus(libraryRequests)

    val lifeRequests = buildList {
        if (communityToken.isEmpty()) {
            add(
                ReportPreparationDetail(
                    title = "智慧社区生活数据",
                    status = ReportPreparationStatus.UNAVAILABLE
                )
            )
        } else {
            add(ReportPreparationDetail("智慧社区宿舍信息", dormitoryState.toPreparationStatus()))
            add(ReportPreparationDetail("智慧社区室友信息", dormitoryUsersState.toPreparationStatus()))
            add(ReportPreparationDetail("智慧社区卫生评分", dormitoryScoresState.toPreparationStatus()))
        }
        add(ReportPreparationDetail("校园网自服务使用记录", schoolNetState.toPreparationStatus()))
        if (isXuanCheng) {
            add(
                ReportPreparationDetail(
                    title = "慧新易校本月校园网",
                    status = if (huiXinAuth.isEmpty()) {
                        ReportPreparationStatus.UNAVAILABLE
                    } else {
                        huiXinSchoolNetState.toPreparationStatus()
                    }
                )
            )
        }
    }
    val lifeStatus = aggregatePreparationStatus(lifeRequests)
    val rawPreparationItems = listOf(
        ReportPreparationItem(
            title = "学业报表",
            status = academicStatus,
            details = academicRequests,
            suggestion = "建议登录或者刷新对应教务来源、智慧社区后重试"
        ),
        ReportPreparationItem(
            title = "学业分析",
            status = academicCoreStatus,
            details = academicCoreRequests,
            suggestion = "建议刷新教务系统或合工大教务登录状态后重试"
        ),
        ReportPreparationItem(
            title = "消费分析",
            status = expenseStatus,
            details = expenseRequests,
            suggestion = "建议登录或刷新慧新易校，确认网络连接后重试"
        ),
        ReportPreparationItem(
            title = "图书馆报表",
            status = libraryStatus,
            details = libraryRequests,
            suggestion = "建议通过 CAS 重新登录图书馆后重试"
        ),
        ReportPreparationItem(
            title = "生活报表",
            status = lifeStatus,
            details = lifeRequests,
            suggestion = "建议确认校园网连接，并登录或刷新对应数据源后重试"
        )
    )
    val allPreparationRequestsFinished = rawPreparationItems.none {
        it.status == ReportPreparationStatus.LOADING
    }
    val preparationItems = rawPreparationItems.map { item ->
        if (preparationTimedOut && item.status == ReportPreparationStatus.LOADING) {
            item.copy(
                status = ReportPreparationStatus.TIMED_OUT,
                details = item.details.map { detail ->
                    if (detail.status == ReportPreparationStatus.LOADING) {
                        detail.copy(status = ReportPreparationStatus.TIMED_OUT)
                    } else {
                        detail
                    }
                }
            )
        } else {
            item
        }
    }
    val backdrop = rememberLayerBackdrop()

    LaunchedEffect(Unit) {
        initialSemester = SemesterParser.getSemester()
        semester = initialSemester
        exportSemester = initialSemester
    }

    LaunchedEffect(
        preparationAttempt,
        showPreparation,
        allPreparationRequestsFinished
    ) {
        if (!showPreparation) return@LaunchedEffect

        if (allPreparationRequestsFinished) {
            delay(300L)
            preparationReady = true
        } else {
            preparationReady = false
            delay(REPORT_PREPARATION_TIMEOUT_MILLIS)
            preparationTimedOut = true
            preparationReady = true
        }
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
                        val activity = LocalActivity.current
                        FilledTonalButton(
                            enabled = selectedModules.isNotEmpty() && !exporting && activity!= null,
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    exporting = true
                                    try {
                                        exportTermReport(
                                            activity = activity!!,
                                            vm = vm,
                                            semester = exportSemester,
                                            modules = selectedModules,
                                            action = TermReportExportAction.SAVE_TO_GALLERY,
                                            isGraduating = isGraduating,
                                            allSemesters = allSemesters
                                        )
                                        showToast( "已保存到相册/HFUT-Schedule")
                                    } catch (e: Exception) {
                                        showToast("导出失败")
                                        LogUtil.error(e)
                                    }
                                    exporting = false
                                    showExportSheet = false
                                }
                            }
                        ) {
                            Text(if (exporting) "导出中" else "保存图库")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            enabled = selectedModules.isNotEmpty() && !exporting && activity != null,
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    exporting = true
                                    try {
                                        exportTermReport(
                                            activity = activity!!,
                                            vm = vm,
                                            semester = exportSemester,
                                            modules = selectedModules,
                                            action = TermReportExportAction.SHARE,
                                            isGraduating = isGraduating,
                                            allSemesters = allSemesters
                                        )
                                    } catch (e: Exception) {
                                        showToast("分享失败")
                                        LogUtil.error(e)
                                    }
                                    exporting = false
                                    showExportSheet = false
                                }
                            }
                        ) {
                            Text(if (exporting) "导出中" else "分享")
                        }
                    }

                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
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
                    if (!showWelcome && !showPreparation) {
                        LiquidButton(
                            enabled = semester != null && !exporting,
                            onClick = {
                                exportSemester = semester ?: initialSemester
                                showExportSheet = true
                            },
                            backdrop = backdrop,
                            isCircle = true,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
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
                    preparationAttempt++
                    preparationTimedOut = false
                    preparationReady = false
                    showPreparation = true
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
                    .backDropSource(backdrop)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = !showPreparation
                ) {
                    item { InnerPaddingHeight(innerPadding, true) }

                    item {
                        key(preparationAttempt) {
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
                    }

                    if (!isGraduating) {
                        item { PaddingForPageControllerButton() }
                    }
                    item { InnerPaddingHeight(innerPadding, false) }
                }

                if (!isGraduating && !showPreparation) {
                    PageController(
                        modifier = Modifier
                            .padding(innerPadding)
                            .zIndex(2f)
                        ,
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

                if (showPreparation) {
                    TermReportPreparationScreen(
                        items = preparationItems,
                        canEnter = preparationReady,
                        isGraduating = isGraduating,
                        contentPadding = innerPadding,
                        onRetry = {
                            preparationAttempt++
                            preparationTimedOut = false
                            preparationReady = false
                        },
                        onEnter = { showPreparation = false },
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(3f)
                    )
                }
            }
        }
    }
}
