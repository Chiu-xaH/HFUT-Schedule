package com.hfut.schedule.ui.screen.grade.grade.jxglstu

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.ScoreGrade
import com.hfut.schedule.logic.model.ScoreWithGPALevel
import com.hfut.schedule.logic.model.community.GradeJxglstuDTO
import com.hfut.schedule.logic.model.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.model.scoreWithGPA
import com.hfut.schedule.logic.network.repo.hfut.JxglstuRepository.parseJxglstuGrade
import com.hfut.schedule.logic.network.repo.hfut.UniAppRepository
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyUI
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.containerShare
import com.xah.uicommon.component.chart.RadarChart
import com.xah.uicommon.component.chart.RadarData
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.set

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemJxglstuUI(
    navController: NavHostController,
    innerPadding: PaddingValues,
    vm: NetWorkViewModel,
    input : String,
    hazeState: HazeState,
    ifSaved : Boolean,
    displayCompactly : Boolean
) {
    val context = LocalContext.current
    var showBottomSheet_Survey by remember { mutableStateOf(false) }
    var surveyCode by remember { mutableStateOf("") }
    if (showBottomSheet_Survey) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Survey = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Survey
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("评教 $surveyCode")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SurveyUI(vm,hazeState,true,innerPadding,surveyCode)
                }
            }
        }
    }


    val itemUI = @Composable { grade : GradeResponseJXGLSTU ->
        val isFailed = grade.gpa.toFloatOrNull() == 0f
        val needSurvey = grade.score.contains("评教")

        CustomCard(
            color = if(needSurvey) MaterialTheme.colorScheme.secondaryContainer  else cardNormalColor(),
            modifier = Modifier
                .containerShare(AppNavRoute.GradeDetail.shareRoute(grade))
                .clickable {
                if(needSurvey) {
                    if(ifSaved) {
                        showToast("未评教，请登录后评教")
                        Starter.refreshLogin(context)
                    } else {
                        surveyCode = grade.lessonCode
                        showToast("请为本课程的所有老师评教，下拉刷新以查看成绩")
                        showBottomSheet_Survey = true
                    }
                } else {
                    navController.navigateForTransition(AppNavRoute.GradeDetail, AppNavRoute.GradeDetail.withArgs(grade))
                }
            }
        ) {
            TransplantListItem(
                headlineContent = {  Text(grade.courseName) },
                overlineContent = { Text(
                    if(!needSurvey)
                        "分数 "+ grade.detail + " | 绩点 " + grade.gpa +  " | 学分 " + grade.credits
                    else grade.lessonCode
                ) },
                leadingContent = {
                    Icon(
                        painterResource(
                            if(isFailed) {
                                R.drawable.error
                            } else {
                                R.drawable.check_circle
                            }
                        ),
                        contentDescription = "Localized description",
                        tint = if(isFailed) MaterialTheme.colorScheme.error else LocalContentColor.current
                    ) },
                supportingContent = {
                    if(needSurvey) {
                        Text("点击跳转评教")
                    } else if(displayCompactly) {
                        Text(grade.score)
                    }
                },
            )

            if(!displayCompactly && !needSurvey) {
                val list = remember { grade.score.split(" ") }
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).padding(bottom = APP_HORIZONTAL_DP - CARD_NORMAL_DP*3),
                    ) {
                        list.forEach {
                            val item = it.split(":")
                            val value = try {
                                item[1]
                            } catch (e : Exception) {
                                LogUtil.error(e)
                                null
                            }
                            val key = try {
                                item[0]
                            } catch (e : Exception) {
                                LogUtil.error(e)
                                it
                            }
                            AssistChip(
                                onClick = {  },
                                border = null,
                                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                label = { Text(key) },
                                trailingIcon = {
                                    value?.let { text -> Text(text) }
                                },
                                modifier = Modifier.padding(end = CARD_NORMAL_DP*3).padding(bottom = CARD_NORMAL_DP*3)
                            )
                        }
                    }
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    val expandedMap = remember {
        mutableStateMapOf<String, Boolean>()
    }

    val ui = @Composable { gradeList : List<GradeJxglstuDTO> ->
        Column {
            if(gradeList.isEmpty()) {
                CenterScreen {
                    EmptyIcon()
                }
            }
            else {
                LazyColumn {
                    item {
                        InnerPaddingHeight(innerPadding,true)
                    }
                    gradeList.forEach { term ->
                        val termKey = term.term
                        var subList = term.list.filter {
                            it.courseName.contains(input) || it.lessonCode.contains(input)
                        }
                        item(key = termKey) {
                            DividerText(termKey) {
                                // 收起列表
                                expandedMap[termKey] = !(expandedMap[termKey] ?: true)
                            }
                        }

                        if (expandedMap[termKey] ?: true) {
                            items(
                                subList.size,
                                key = { subList[it].lessonCode }
                            ) { index ->
                                val subItem = subList[index]
                                itemUI(subItem)
                            }
                        }

                    }
                    item {
                        InnerPaddingHeight(innerPadding,false)
                    }
                }
            }
        }
    }

    if(ifSaved) {
        val gradeList by produceState<List<GradeJxglstuDTO>?>(initialValue = null) {
            scope.launch(Dispatchers.IO) {
                LargeStringDataManager.read(LargeStringDataManager.GRADE)?.let {
                    value = parseJxglstuGrade(it)
                }
            }
        }

        gradeList?.let { ui(it) }
        return
    }

    val uiState by vm.jxglstuGradeData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.jxglstuGradeData.clear()
            vm.getGradeFromJxglstu(cookie,null)
        }
    }
    val refreshing = uiState is UiState.Loading

    LaunchedEffect(Unit) {
        if(refreshing) {
            refreshNetwork()
        }
    }

    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch { refreshNetwork() }
    })

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(pullRefreshState)){
        RefreshIndicator(refreshing, pullRefreshState, Modifier
            .padding(innerPadding)
            .align(Alignment.TopCenter))
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val gradeList = (uiState as UiState.Success).data
            ui(gradeList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIUniApp(
    navController: NavHostController,
    innerPadding: PaddingValues,
    vm: NetWorkViewModel,
    input : String,
    displayCompactly : Boolean
) {
    val scope = rememberCoroutineScope()
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

    val refreshing = uiState is UiState.Loading

    LaunchedEffect(Unit) {
        if(refreshing) {
            refreshNetwork()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshNetwork()
            }
        }
    )

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(pullRefreshState)
    ){
        RefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier
                .padding(innerPadding)
                .align(Alignment.TopCenter)
        )
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val gradeList = (uiState as UiState.Success).data.toList().sortedByDescending { it.first }
            Column {
                if(gradeList.isEmpty()) {
                    CenterScreen {
                        EmptyIcon()
                    }
                }
                else {
                    val expandedMap = remember {
                        mutableStateMapOf<String, Boolean>()
                    }
                    LazyColumn {
                        item { InnerPaddingHeight(innerPadding,true) }
                        gradeList.forEach { term ->
                            val termKey = term.first
                            var subList = term.second.filter {
                                it.courseNameZh.contains(input) || it.lessonCode.contains(input)
                            }
                            item(key = term.first) {
                                DividerText(term.first) {
                                    // 收起列表
                                    expandedMap[termKey] = !(expandedMap[termKey] ?: true)
                                }
                            }

                            if (expandedMap[termKey] ?: true) {
                                items(
                                    subList.size,
                                    key = { subList[it].lessonCode }
                                ) { index ->
                                    val subItem = subList[index]
                                    val isFailed = !subItem.passed

                                    val finalGpaStr = (if(!isFailed && subItem.gp == 0.0) "--" else subItem.gp).toString()

                                    val bean = GradeResponseJXGLSTU(
                                        courseName = subItem.courseNameZh,
                                        credits = subItem.credits.toString(),
                                        gpa = finalGpaStr,
                                        score = subItem.gradeDetail,
                                        detail = subItem.finalGrade.toString(),
                                        lessonCode = subItem.lessonCode
                                    )
                                    CustomCard(
                                        color = cardNormalColor(),
                                        modifier = Modifier
                                            .containerShare(AppNavRoute.GradeDetail.shareRoute(bean))
                                            .clickable {
                                                navController.navigateForTransition(AppNavRoute.GradeDetail, AppNavRoute.GradeDetail.withArgs(bean))
                                            }
                                    ) {
                                        TransplantListItem(
                                            headlineContent = {  Text(subItem.courseNameZh) },
                                            overlineContent = { Text("分数 "+ subItem.finalGrade + " | 绩点 " + finalGpaStr +  " | 学分 " + subItem.credits) },
                                            leadingContent = {
                                                Icon(
                                                    painterResource(
                                                        if(isFailed) {
                                                            R.drawable.error
                                                        } else {
                                                            R.drawable.check_circle
                                                        }
                                                    ),
                                                    contentDescription = "Localized description",
                                                    tint = if(isFailed) MaterialTheme.colorScheme.error else LocalContentColor.current
                                                )
                                            },
                                            supportingContent = {
                                                if(displayCompactly) {
                                                    Text(subItem.gradeDetail)
                                                }
                                            }
                                        )

                                        if(!displayCompactly) {
                                            val list = remember { subItem.gradeDetail.split(" ") }
                                            CompositionLocalProvider(
                                                LocalMinimumInteractiveComponentSize provides 0.dp
                                            ) {
                                                FlowRow(
                                                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).padding(bottom = APP_HORIZONTAL_DP - CARD_NORMAL_DP*3),
                                                ) {
                                                    list.forEach {
                                                        val item = it.split(":")
                                                        val value = try {
                                                            item[1]
                                                        } catch (e : Exception) {
                                                            LogUtil.error(e)
                                                            null
                                                        }
                                                        val key = try {
                                                            item[0]
                                                        } catch (e : Exception) {
                                                            LogUtil.error(e)
                                                            it
                                                        }
                                                        AssistChip(
                                                            onClick = {  },
                                                            border = null,
                                                            colors = AssistChipDefaults.assistChipColors(
                                                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                            ),
                                                            label = { Text(key) },
                                                            trailingIcon = {
                                                                value?.let { text -> Text(text) }
                                                            },
                                                            modifier = Modifier.padding(end = CARD_NORMAL_DP*3).padding(bottom = CARD_NORMAL_DP*3)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeDetailScreen(
    bean : GradeResponseJXGLSTU,
    navController: NavHostController
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.GradeDetail.shareRoute(bean) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var party by remember { mutableStateOf(false) }
    val isFailed = remember { bean.gpa.toFloatOrNull() == 0f }

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { showDialog = false },
            dialogText = "平时因数=除去期末成绩各项平均分/期末分数,可大致反映最终成绩平时分占比\n越接近1则平衡,越>1则表明最终成绩可能更靠平时分,越<1表明最终成绩可能因平时分拖后腿",
            conformText = "好",
            dismissText = "关闭",
            hazeState = hazeState
        )
    }

    val list = remember { bean.score.split(" ") }
    val radarList by produceState(initialValue = emptyList<RadarData>()) {
        value = list.map { item ->
            val label = item.substringBefore(":")
            val scoreText = item.substringAfter(":")
            val scoreF = scoreText.toFloatOrNull()
            // 五星制
            val score = scoreF ?: (ScoreGrade.entries.find { it.label == scoreText }?.score?.toFloat() ?: 0f)
            RadarData(label, score/100f)
        }
    }


    var avgPingshi by remember { mutableStateOf(0f) }
    var examScore by remember { mutableStateOf(0f) }
    var count by remember { mutableStateOf(0) }

    LaunchedEffect(radarList) {
        if(radarList.isEmpty()) {
            return@LaunchedEffect
        }
        radarList.forEach { item ->
            if (!item.label.contains("期末")) {
                avgPingshi += item.value
                count++
            } else {
                examScore = item.value
            }
        }
        if (count != 0)
            avgPingshi /= count
        else avgPingshi = 0f
        if (examScore != 0f)
            avgPingshi /= examScore
        else avgPingshi = 0f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Party(show = party)
        CustomTransitionScaffold (
            route = route,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            navHostController = navController,
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(bean.courseName) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController)
                        }
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                if(radarList.size > 1) {
                    DividerTextExpandedWith(text = "雷达图") {
                        Spacer(modifier = Modifier.height(35.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            RadarChart(
                                data = radarList,
                                modifier = Modifier.size(200.dp),
                                primaryColor = if(isFailed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                onPrimaryColor = if(isFailed) MaterialTheme.colorScheme.error.copy(.25f) else MaterialTheme. colorScheme. inversePrimary
                            )
                        }
                        val bottomPadding = if(radarList.isEmpty()) {
                            0
                        } else if(radarList.size == 1) {
                            0
                        } else if(radarList.size == 3) {
                            0
                        } else {
                            25
                        }
                        Spacer(modifier = Modifier.height(bottomPadding.dp))
                    }
                }

                DividerTextExpandedWith(text = "详情",false) {
                    LargeCard("分数 ${bean.detail} 绩点 ${bean.gpa}") {
                        for (i in list.indices step 2) {
                            Row {
                                val l1 = list[i]
                                val t1 = l1.substringBefore(":")
                                val score1 = l1.substringAfter(":")

                                TransplantListItem(
                                    headlineContent = {
                                        Text(
                                            text = score1,
                                            fontWeight = if(t1.contains("期末考试") || t1.contains("期中考试")) FontWeight.Bold else FontWeight.Normal,
                                            textDecoration = if(t1.contains("期末考试") || t1.contains("期中考试")) TextDecoration.Underline else TextDecoration.None
                                        ) },
                                    overlineContent = { Text(t1) },
                                    modifier = Modifier.weight(.5f),
                                    leadingContent = { GradeIcons(t1) }
                                )
                                if (i + 1 < list.size) {
                                    val l2 = list[i+1]
                                    val t2 = l2.substringBefore(":")
                                    val score2 = l2.substringAfter(":")
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(
                                                text = score2,
                                                fontWeight = if(t2.contains("期末考试") || t2.contains("期中考试")) FontWeight.Bold else FontWeight.Normal,
                                                textDecoration = if(t2.contains("期末考试") || t2.contains("期中考试")) TextDecoration.Underline else TextDecoration.None
                                            ) },
                                        overlineContent = { Text(t2) },
                                        modifier = Modifier.weight(.5f),
                                        leadingContent = { GradeIcons(t2) }
                                    )
                                }
                            }
                        }
                    }
                }
                DividerTextExpandedWith("其他信息") {
                    CustomCard(color = cardNormalColor()) {
                        Row {
                            TransplantListItem(
                                leadingContent = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.filter_vintage),
                                        contentDescription = ""
                                    )
                                },
                                headlineContent = {
                                    ScrollText(
                                        text =  bean.credits
                                    )
                                },
                                overlineContent = {
                                    Text("学分")
                                },
                                modifier = Modifier.weight(.5f)
                            )
                            TransplantListItem(
                                leadingContent = {
                                    Icon(painter = painterResource(R.drawable.percent), contentDescription = "")
                                },
                                headlineContent = {
                                    ScrollText(
                                        text = if (avgPingshi != 0f) formatDecimal(
                                            avgPingshi.toDouble(),
                                            2
                                        ) else "未知"
                                    )
                                },
                                overlineContent = {
                                    Text("平时因数")
                                },
                                modifier = Modifier
                                    .weight(.5f)
                                    .clickable {
                                        showDialog = true
                                    }
                            )
                        }
                        TransplantListItem(
                            leadingContent = {
                                Icon(
                                    painter = painterResource(id = R.drawable.tag),
                                    contentDescription = ""
                                )
                            },
                            headlineContent = {
                                ScrollText(
                                    text =  bean.lessonCode
                                )
                            },
                            overlineContent = {
                                Text("代码")
                            }
                        )
                        with(bean) {
                            if(gpa.toFloatOrNull() != null) {
                                TransplantListItem(
                                    headlineContent = {
                                        GPAStarGroup(detail,gpa) {
                                            party = it
                                        }
                                    },
                                    leadingContent = {
                                        Icon(painterResource(if(party) R.drawable.thumb_up else R.drawable.stairs),null)
                                    },
                                    overlineContent = {
                                        getGradeNextLevel(detail,gpa)?.let { target ->
                                            detail.toDoubleOrNull().let { current ->
                                                if(target.score == null) {
                                                    Text(" 下一绩点为${target.gpa}")
                                                } else if(isFailed) {
                                                    Text("挂科")
                                                } else {
                                                    Text(" 距离下一绩点${target.gpa}需要${target.score.min - (current ?: 0.0)}分")
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                }
                DividerTextExpandedWith("绩点与分数的关系") {
                    GPAWithScore()
                }
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}


@Composable
fun GPAWithScore() {
    CustomCard(color = cardNormalColor()) {
        for(i in 0..scoreWithGPA.size-1 step 3) {
            val item1 = scoreWithGPA[i]
            val item2 = scoreWithGPA[i+1]
            val item3 = scoreWithGPA[i+2]

            Row {
                with(item1) {
                    TransplantListItem(
                        supportingContent = {
                            Text("${score.min}-${score.max}")
                        },
                        headlineContent = { Text(gpa.toString()) },
                        modifier = Modifier.weight(1/3f)
                    )
                }
                with(item2) {
                    TransplantListItem(
                        supportingContent = {
                            Text("${score.min}-${score.max}")
                        },
                        headlineContent = { Text(gpa.toString()) },
                        modifier = Modifier.weight(1/3f)
                    )
                }
                with(item3) {
                    TransplantListItem(
                        supportingContent = {
                            Text("${score.min}-${score.max}")
                        },
                        headlineContent = { Text(gpa.toString()) },
                        modifier = Modifier.weight(1/3f)
                    )
                }
            }
        }
    }
    CustomCard(color = cardNormalColor()) {
//        var boldIndex =
//             if(index?.second == ScoreGrade.entries.size - 1) {
//                index.first
//            } else {
//                null
//            }
        val item1 = ScoreGrade.A
        val item2 = ScoreGrade.B
        val item3 = ScoreGrade.C
        val item4 = ScoreGrade.D
        val item5 = ScoreGrade.F
        Row {
            with(item1) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            with(item2) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            with(item3) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
        }
        Row {
            with(item4) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            with(item5) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            TransplantListItem(
                supportingContent = {
                    Text("五星制")
                },
                headlineContent = { Text("GPA") },
                modifier = Modifier.weight(1/3f)
            )
        }
    }
}

// 传入分数，判定分级
fun getGradeLevel(score : String,gpa : String) : Pair<Int, Int>? {
    val gpaLevels = scoreWithGPA.reversed()
    try {
        return when(score) {
            ScoreGrade.A.label -> Pair(4,4)
            ScoreGrade.B.label -> Pair(3,4)
            ScoreGrade.C.label -> Pair(2,4)
            ScoreGrade.D.label -> Pair(1,4)
            ScoreGrade.F.label -> Pair(0,4)
            else -> {
                // gpa等于gpaLevels列表某个值，对应后返回Pair(在列表的索引值,列表总长度-1)
                val index = gpaLevels.indexOfFirst { it.gpa == gpa.toFloat() }
                if(index == -1) {
                    // 未找到
                    null
                }
                Pair(index, gpaLevels.size - 1)
            }
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        return null
    }
}

// 获取距离下一个级别需要的分数
fun getGradeNextLevel(score : String,gpa : String) : ScoreWithGPALevel? {
    val gpaLevels = scoreWithGPA.reversed()
    try {
        when(score) {
            ScoreGrade.A.label -> return null
            ScoreGrade.B.label -> return ScoreWithGPALevel(ScoreGrade.A.gpa,null)
            ScoreGrade.C.label -> return ScoreWithGPALevel(ScoreGrade.B.gpa,null)
            ScoreGrade.D.label -> return ScoreWithGPALevel(ScoreGrade.C.gpa,null)
            ScoreGrade.F.label -> return ScoreWithGPALevel(ScoreGrade.D.gpa,null)
            else -> {
                val gpaF = gpa.toFloat()
                if(gpaF == gpaLevels.last().gpa) {
                    return null
                }
                // gpa等于gpaLevels列表某个值，对应后返回Pair(在列表的索引值,列表总长度-1)
                val index = gpaLevels.indexOfFirst { it.gpa == gpaF }
                val bean = gpaLevels[index+1]
                return ScoreWithGPALevel(bean.gpa,bean.score)
            }
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        return null
    }
}

// 等级星星
@Composable
fun StarGroup(level: Pair<Int, Int>,onParty : (Boolean) -> Unit) {
    val totalNum = level.second
    val onNum = level.first
    // 挂科
    val color = if(onNum == 0) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.secondary
    }

    // 控制每个星星是否点亮
    val starStates = remember { List(totalNum) { mutableStateOf(false) } }

    // 控制最后一个星星的缩放动画
    val starScales = remember(level) { List(totalNum) { Animatable(1f) } }

    // 启动点亮动画
    LaunchedEffect(Unit) {
        delay(AppAnimationManager.ANIMATION_SPEED*1L)
        for (i in 0 until onNum) {
            delay(75L) // 每颗星延迟出现
            starStates[i].value = true
            // 同步启动缩放动画
            launch {
                starScales[i].animateTo(
                    1.15f,
                    animationSpec = tween(AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing)
                )
                starScales[i].animateTo(
                    1f,
                    animationSpec = tween(AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    // 满绩
    if(totalNum == onNum) {
        onParty(true)
        Text("满绩")
    } else {
        onParty(false)
        Row {
            for (i in 0 until totalNum) {
                val isFilled = starStates[i].value
                val painter = if (isFilled) {
                    if(i+1 == onNum) R.drawable.family_star else R.drawable.kid_star_filled
                } else R.drawable.kid_star

                val scale = starScales[i].value
                Icon(
                    painter = painterResource(id = painter),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }
    }
}

@Composable
fun GPAStarGroup(score : String,gpa : String,onParty: (Boolean) -> Unit) = getGradeLevel(score,gpa)?.let { StarGroup(it,onParty) }

@Composable
fun GradeIcons(text : String) {
    if(text.contains("补考")) {
        Icon(painterResource(R.drawable.draw),null)
    } else if(text.contains("考试")) {
        Icon(painterResource(R.drawable.draw),null)
    } else if(text.contains("报告")) {
        Icon(painterResource(R.drawable.description),null)
    } else if(text.contains("日记")) {
        Icon(painterResource(R.drawable.book_5),null)
    } else if(text.contains("预习")) {
        Icon(painterResource(R.drawable.azm),null)
    } else if(text.contains("操作")) {
        Icon(painterResource(R.drawable.massage),null)
    } else if(text.contains("实验")) {
        Icon(painterResource(R.drawable.body_fat),null)
    } else if(text.contains("作业")) {
        Icon(painterResource(R.drawable.ink_pen),null)
    } else if(text.contains("实习")) {
        Icon(painterResource(R.drawable.work),null)
    } else if(text.contains("测试")) {
        Icon(painterResource(R.drawable.tactic),null)
    } else if(text.contains("平时")) {
        Icon(painterResource(R.drawable.kid_star),null)
    } else if(text.contains("演讲")) {
        Icon(painterResource(R.drawable.groups),null)
    } else if(text.contains("讨论")) {
        Icon(painterResource(R.drawable.forum),null)
    } else if(text.contains("口试")) {
        Icon(painterResource(R.drawable.voice_selection),null)
    } else if(text.contains("上机")) {
        Icon(painterResource(R.drawable.desktop_windows),null)
    } else if(text.contains("论文")) {
        Icon(painterResource(R.drawable.newsstand),null)
    } else if(text.contains("验收")) {
        Icon(painterResource(R.drawable.check_circle),null)
    } else if(text.contains("出勤")) {
        Icon(painterResource(R.drawable.meeting_room),null)
    } else {
        Icon(painterResource(R.drawable.category),null)
    }
}