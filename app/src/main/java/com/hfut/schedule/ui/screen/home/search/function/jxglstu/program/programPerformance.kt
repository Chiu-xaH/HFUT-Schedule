package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.model.jxglstu.CourseItem
import com.hfut.schedule.logic.model.jxglstu.ProgramBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompetitionType
import com.hfut.schedule.logic.model.jxglstu.getProgramCompetitionType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.status.LoadingScreen
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.clickableWithScale
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramCompetitionScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.ProgramCompetition.receiveRoute() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        CustomTransitionScaffold (
            roundShape = MaterialTheme.shapes.large,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            route = route,
            
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.ProgramCompetition.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(route, AppNavRoute.ProgramCompetition.icon)
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                ProgramPerformance(vm,ifSaved,innerPadding,navController)
            }
        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun ProgramPerformance(
    vm : NetWorkViewModel,
    ifSaved : Boolean,
    innerPadding : PaddingValues,
    navController : NavHostController,
) {
    val context = LocalContext.current
    val uiState by vm.programPerformanceData.state.collectAsState()
    val data by produceState<ProgramBean?>(initialValue = null) {
        if(!ifSaved || uiState is UiState.Success) {
            onListenStateHolder(vm.programPerformanceData) { data ->
                value = data
            }
        } else {
            val bean = try {
                val json = LargeStringDataManager.read(LargeStringDataManager.PROGRAM_PERFORMANCE)
                if(json == null) {
                    null
                }
                Gson().fromJson(json,ProgramBean::class.java)
            } catch (e : Exception) {
                null
            }
            if(bean != null) {
                vm.programPerformanceData.emitData(bean)
            }
            value = bean
        }
    }
    val loading = data == null

    val refreshNetwork: suspend () -> Unit = s@{
        if(uiState is UiState.Success) {
            return@s
        }
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.programPerformanceData.clear()
            vm.getProgramPerformance(it)
        }
    }
    LaunchedEffect(Unit) {
        if(!ifSaved) {
            refreshNetwork()
        }
    }

    if(loading) {
        LoadingScreen()
    } else {
        if(data == null) return
        val dataList = data!!.moduleList
        val outCourse = data!!.outerCourseList
        LazyColumn {
            item { InnerPaddingHeight(innerPadding,true) }
            dataList.let { it ->
                items(it.size) { index->
                    val item = it[index]
                    val requireInfo = item.requireInfo
                    val summary = item.completionSummary
                    val route = AppNavRoute.ProgramCompetitionDetail.withArgs(item.nameZh,index)
                    DividerTextExpandedWith(text = item.nameZh + " 要求 ${requireInfo.courseNum} 门 ${requireInfo.credits} 学分") {
                        AnimationCustomCard(
                            index = index,
                            containerColor = cardNormalColor(),
                            modifier = Modifier
                                .clickableWithScale() {
                                    navController.navigateForTransition(AppNavRoute.ProgramCompetitionDetail,route)
                                }
                                .containerShare(
//                                sharedTransitionScope,
//                                animatedContentScope=animatedContentScope,
                                route=route,
                                roundShape = MaterialTheme.shapes.medium,
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text(text = "${summary.passedCourseNum} 门 ${summary.passedCredits} 学分") },
                                    overlineContent = { Text(text = "已通过") },
                                    modifier = Modifier.weight(.5f)
                                )
                                TransplantListItem(
                                    headlineContent = { Text(text = "${summary.failedCourseNum} 门 ${summary.failedCredits} 学分") },
                                    overlineContent = { Text(text = "未通过") },
                                    modifier = Modifier.weight(.5f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text(text = "${summary.takingCourseNum} 门 ${summary.takingCredits} 学分") },
                                    overlineContent = { Text(text = "本学期在修") },
                                    modifier = Modifier.weight(1f),
                                    trailingContent = {
                                        Button(
                                            onClick = {
                                                navController.navigateForTransition(AppNavRoute.ProgramCompetitionDetail,route)
                                            },
                                        ) {
                                            Text(text = "查看详情")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            if(outCourse.isNotEmpty()) {
                val summary = data!!.outerCompletionSummary
                item { DividerText(text = "培养方案外课程 (包含转专业废弃课程)") }
                item {
                    val route = AppNavRoute.ProgramCompetitionDetail.withArgs("培养方案外课程",999)
                    AnimationCustomCard(
                        containerColor = cardNormalColor(),
                        modifier = Modifier
                            .clickableWithScale() {
                                navController.navigateForTransition(AppNavRoute.ProgramCompetitionDetail,route)
                            }
                            .containerShare(
//                            sharedTransitionScope,
//                            animatedContentScope=animatedContentScope,
                            route=route,
                            roundShape = MaterialTheme.shapes.medium,
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(text = "${summary.passedCourseNum} 门 ${summary.passedCredits} 学分") },
                                overlineContent = { Text(text = "已通过") },
                                modifier = Modifier.weight(.5f)
                            )
                            TransplantListItem(
                                headlineContent = { Text(text = "${summary.failedCourseNum} 门 ${summary.failedCredits} 学分") },
                                overlineContent = { Text(text = "未通过") },
                                modifier = Modifier.weight(.5f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(text = "${summary.takingCourseNum} 门 ${summary.takingCredits} 学分") },
                                overlineContent = { Text(text = "本学期在修") },
                                modifier = Modifier.weight(1f),
                                trailingContent = {
                                    Button(
                                        onClick = {
                                            navController.navigateForTransition(AppNavRoute.ProgramCompetitionDetail,route)
                                        },
                                    ) {
                                        Text(text = "查看详情")
                                    }
                                }
                            )
                        }
                    }
                }
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }
}



@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramCompetitionDetailScreen(
    vm: NetWorkViewModel,
    title : String,
    moduleIndex : Int,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var input by remember { mutableStateOf("") }
    val route = remember { AppNavRoute.ProgramCompetitionDetail.withArgs(title,moduleIndex) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        CustomTransitionScaffold (
            roundShape = MaterialTheme.shapes.medium,
            route = route,
            
            navHostController = navController,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(title) },
                        navigationIcon = {
                            TopBarNavigationIcon()
                        },
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = APP_HORIZONTAL_DP),
                            value = input,
                            onValueChange = {
                                input = it
                            },
                            label = { Text("课程代码 或 课程名") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        // TODO
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = "description"
                                    )
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant()
                        )
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
//                    .padding(innerPadding)
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                PerformanceInfo(vm,moduleIndex,hazeState,innerPadding,input)
            }
        }
//    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerformanceInfo(vm: NetWorkViewModel,moduleIndex : Int, hazeState: HazeState,innerPadding: PaddingValues,input : String) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val uiState by vm.programPerformanceData.state.collectAsState()
    var itemForInfo by remember { mutableStateOf(CourseItem("","详情",0.0, listOf(""),"",null,null,null)) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(itemForInfo.nameZh, isPaddingStatusBar = false)
            ProgramInfoItem(itemForInfo)
            Spacer(modifier = Modifier.height(45.dp))
        }
    }

    CommonNetworkScreen(uiState, onReload = {}) {
        val bean = (uiState as UiState.Success).data
        if(moduleIndex != 999) {
            val dataList = bean.moduleList[moduleIndex]
            val allCourse = dataList.allCourseList
            val filteredList = mutableListOf<CourseItem>()
            allCourse.forEach { i ->
                if(i.nameZh.contains(input) || i.code.contains(input)) {
                    filteredList.add(i)
                }
            }
            filteredList.sortBy { it.resultType }
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
//                item { Spacer(modifier = Modifier.height(CARD_NORMAL_DP)) }
                if(filteredList.isNotEmpty()) {
                    items(filteredList.size) { index ->
                        val item = filteredList[index]
                        val term = transferTerm(item.terms)
                        val type = getProgramCompetitionType(item.resultType)
                        AnimationCardListItem(
                            headlineContent = { Text(text = item.nameZh) },
                            supportingContent = {
                                if(type == ProgramCompetitionType.FAILED || type == ProgramCompetitionType.PASSED) {
                                    Text(text =
                                        "均分 ${item.score} 绩点 ${item.gp} " +
                                                if(item.rank != null) "等级 ${item.rank}" else ""
                                    )
                                }
                            },
                            trailingContent = {
                                Text(text = type?.description ?: item.resultType)
                            },
                            overlineContent = {
                                var text = ""
                                if (term != null) {
                                    for(i in term.indices) {
                                        text = text + term[i] + " "
                                    }
                                }
                                Text(text = "第${text.replace(" ",",").dropLast(1)}学期" + " | 学分 ${item.credits}")
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(type?.icon ?: R.drawable.question_mark),
                                    null,
                                    tint = if(type == ProgramCompetitionType.FAILED) MaterialTheme.colorScheme.error
                                    else  LocalContentColor. current
                                )
                            },
                            modifier = Modifier.clickable {
                                itemForInfo = item
                                showBottomSheet = true
                            },
                            index = index
                        )
                    }
                }

                item { InnerPaddingHeight(innerPadding,false) }

            }
        } else {
            val outerCourse = bean.outerCourseList
            val filteredList = mutableListOf<CourseItem>()
            outerCourse.forEach { i ->
                if(i.nameZh.contains(input) || i.code.contains(input)) {
                    filteredList.add(i)
                }
            }
            filteredList.sortBy { it.resultType }
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
//                item { Spacer(modifier = Modifier.height(CARD_NORMAL_DP)) }
                if(filteredList.isNotEmpty()) {
                    items(filteredList.size) { index ->
                        val item = filteredList[index]
                        val type = getProgramCompetitionType(item.resultType)
                        AnimationCardListItem(
                            headlineContent = { Text(text = item.nameZh) },
                            supportingContent = {
                                if(type == ProgramCompetitionType.PASSED || type == ProgramCompetitionType.FAILED) {
                                    Text(text =
                                        "均分 ${item.score} 绩点 ${item.gp} " +
                                                if(item.rank != null) "等级 ${item.rank}" else ""
                                    )
                                }
                            },
                            trailingContent = {
                                Text(text = type?.description ?: item.resultType)
                            },
                            overlineContent = {
                                Text(text = "学分 ${item.credits}")
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(type?.icon ?: R.drawable.question_mark),
                                    null,
                                    tint = if(type == ProgramCompetitionType.FAILED) MaterialTheme.colorScheme.error
                                    else  LocalContentColor. current
                                )
                            },
                            modifier = Modifier.clickable {
                                itemForInfo = item
                                showBottomSheet = true
                            },
                            index = index
                        )
                    }
                }

                item { InnerPaddingHeight(innerPadding,false) }

            }
        }
    }
}

fun transferTerm(term : List<String>) : List<String>? {
    return if(term.isNotEmpty()) {
        val newList = mutableListOf<String>()
        term.forEach { item->
            newList.add(item.substringAfter("_"))
        }
        newList
    } else {
        null
    }
}

@Composable
fun ProgramInfoItem(item : CourseItem) {
    val term = transferTerm(item.terms)
    val type = getProgramCompetitionType(item.resultType)
    var text = ""
    if (term != null) {
        for(i in term.indices) {
            text = text + term[i] + " "
        }
    }

    LargeCard(
        title = type?.description ?: item.resultType
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TransplantListItem(
                headlineContent = { Text(text = item.code) },
                overlineContent = { ScrollText(text = "代号") },
                leadingContent = {
                    Icon(painterResource(id = R.drawable.tag), contentDescription = null)
                },
                modifier = Modifier.weight(.5f)
            )
            TransplantListItem(
                headlineContent = { Text(text = item.credits.toString()) },
                overlineContent = { Text(text = "学分") },
                modifier = Modifier.weight(.5f),
                leadingContent = {
                    Icon(painterResource(id = R.drawable.filter_vintage), contentDescription = null)
                },
            )
        }

        if(type == ProgramCompetitionType.PASSED || type == ProgramCompetitionType.FAILED) {
            TransplantListItem(
                headlineContent = {
                    Text(
                        text =
                        "均分 ${item.score} 绩点 ${item.gp} " +
                                if (item.rank != null) "等级 ${item.rank}" else ""
                    )
                },
                overlineContent = { Text(text = "成绩") },
                leadingContent = {
                    Icon(painterResource(id = R.drawable.article), contentDescription = null)
                }
            )
        }
    }
}