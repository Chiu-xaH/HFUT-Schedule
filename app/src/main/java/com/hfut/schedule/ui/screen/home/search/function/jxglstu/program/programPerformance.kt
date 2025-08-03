package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.CourseItem
import com.hfut.schedule.logic.model.jxglstu.ProgramBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
 
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.LoadingScreen
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramCompetitionScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.ProgramCompetition.receiveRoute() }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.ProgramCompetition.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.ProgramCompetition.icon)
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                ProgramPerformance(vm,hazeState,ifSaved,innerPadding)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgramPerformance(vm : NetWorkViewModel, hazeState: HazeState,ifSaved : Boolean,innerPadding : PaddingValues) {
    val data by produceState<ProgramBean?>(initialValue = null) {
        if(!ifSaved) {
            onListenStateHolder(vm.programPerformanceData) { data ->
                value = data
            }
        } else {
            value = try {
                Gson().fromJson(prefs.getString("PROGRAM_PERFORMANCE",""),ProgramBean::class.java)
            } catch (e : Exception) {
                null
            }
        }
    }
    val loading = data == null

    var showBottomSheet by remember { mutableStateOf(false) }
    var moduleIndex by remember { mutableIntStateOf(-1) }
    var title by remember { mutableStateOf("完成情况") }
    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            // 禁用每次加载 特殊 数据较大 省流量
            vm.programPerformanceData.clear()
            vm.getProgramPerformance(it)
        }
    }
    LaunchedEffect(Unit) {
        if(!ifSaved) {
            refreshNetwork()
        }
    }

    if (showBottomSheet && data != null) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(title)
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    PerformanceInfo(data!!, moduleIndex,hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
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
                    DividerTextExpandedWith(text = item.nameZh + " 要求 ${requireInfo.courseNum} 门 ${requireInfo.credits} 学分") {
                        AnimationCustomCard(index = index,containerColor = cardNormalColor()) {
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
                                                moduleIndex = index
                                                title = item.nameZh
                                                showBottomSheet = true
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
                    AnimationCustomCard(containerColor = cardNormalColor()) {
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
                                            moduleIndex = 999
                                            title = "培养方案外课程"
                                            showBottomSheet = true
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerformanceInfo(bean : ProgramBean, moduleIndex : Int, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    var input by remember { mutableStateOf("") }
    var itemForInfo by remember { mutableStateOf(CourseItem("","详情",0.0, listOf(""),"",null,null,null)) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(itemForInfo.nameZh)
            ProgramInfoItem(itemForInfo)
            Spacer(modifier = Modifier.height(45.dp))
        }
    }
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
            if(filteredList.isNotEmpty()) {
                items(filteredList.size) { index ->
                    val item = filteredList[index]
                    val term = transferTerm(item.terms)
                    val type = when(item.resultType) {
                        "PASSED" -> "已修"
                        "TAKING" -> "在修"
                        "UNREPAIRED" -> "未修"
                        else -> item.resultType
                    }
                        AnimationCardListItem(
                            headlineContent = { Text(text = item.nameZh) },
                            supportingContent = {
                                if(type == "通过") {
                                    Text(text =
                                    "均分 ${item.score} 绩点 ${item.gp} " +
                                            if(item.rank != null) "等级 ${item.rank}" else ""
                                    )
                                }
                            },
                            trailingContent = {
                                Text(text = type)
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
                            leadingContent = { PerfermanceIcons(item.resultType) },
                            modifier = Modifier.clickable {
                                itemForInfo = item
                                showBottomSheet = true
                            },
                            index = index
                        )
                }
            }
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
            if(filteredList.isNotEmpty()) {
                items(filteredList.size) { index ->
                    val item = filteredList[index]
                    val type = when(item.resultType) {
                        "PASSED" -> "已修"
                        "TAKING" -> "在修"
                        "UNREPAIRED" -> "未修"
                        else -> item.resultType
                    }
                        AnimationCardListItem(
                            headlineContent = { Text(text = item.nameZh) },
                            supportingContent = {
                                if(type == "通过") {
                                    Text(text =
                                    "均分 ${item.score} 绩点 ${item.gp} " +
                                            if(item.rank != null) "等级 ${item.rank}" else ""
                                    )
                                }
                            },
                            trailingContent = {
                                Text(text = type)
                            },
                            overlineContent = {
                                Text(text = "学分 ${item.credits}")
                            },
                            leadingContent = { PerfermanceIcons(item.resultType) },
                            modifier = Modifier.clickable {
                                itemForInfo = item
                                showBottomSheet = true
                            },
                            index = index
                        )
                }
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
fun PerfermanceIcons(type : String) {
    when(type) {
        "PASSED" -> Icon(painterResource(id = R.drawable.star_filled), contentDescription = null)
        "TAKING" -> Icon(painterResource(id = R.drawable.star_half), contentDescription = null)
        "UNREPAIRED" -> Icon(painterResource(id = R.drawable.star), contentDescription = null)
        else -> Icon(painterResource(id = R.drawable.hotel_class), contentDescription = null)
    }
}

@Composable
fun ProgramInfoItem(item : CourseItem) {
    val term = transferTerm(item.terms)
    val type = when(item.resultType) {
        "PASSED" -> "已修"
        "TAKING" -> "在修"
        "UNREPAIRED" -> "未修"
        else -> item.resultType
    }
    var text = ""
    if (term != null) {
        for(i in term.indices) {
            text = text + term[i] + " "
        }
    }

    LargeCard(
        title = type
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TransplantListItem(
                headlineContent = { Text(text = item.code) },
                overlineContent = { ScrollText(text = item.nameZh) },
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

        if(type == "已通过") {
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