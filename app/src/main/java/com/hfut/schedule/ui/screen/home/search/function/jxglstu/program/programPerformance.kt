package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program


import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.CourseItem
import com.hfut.schedule.logic.model.jxglstu.PlanCourses
import com.hfut.schedule.logic.model.jxglstu.ProgramBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompetitionType
import com.hfut.schedule.logic.model.jxglstu.ProgramModule
import com.hfut.schedule.logic.model.jxglstu.ProgramPerformanceDetailItem
import com.hfut.schedule.logic.model.jxglstu.getProgramCompetitionType
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.network.helper.GsonInstance
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.status.CustomLineProgressIndicator
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.ProgramCompetitionDestination
import com.hfut.schedule.ui.nav.destination.ProgramCompetitionDetailDestination
import com.hfut.schedule.ui.nav.window.ProgramRemarkWindow
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.logic.util.safeDiv
import com.xah.common.ui.component.status.LoadingScreen
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.ColumnVertical
import com.xah.common.ui.style.clickableWithScale
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.SharedContainer
import com.xah.container.component.base.sharedContainer
import com.sharednav.common.helper.NoneRoundShape
import com.xah.floating.util.LocalFloatingController
import com.xah.navigation.util.LocalNavController
import com.xah.common.logic.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramCompetitionScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(ProgramCompetitionDestination.TITLE.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.hazeSource(hazeState).fillMaxSize()
        ) {
            ProgramPerformance(vm,ifSaved,innerPadding)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun ProgramPerformanceCustom(
    innerPadding : PaddingValues,
    data : ProgramBean,
    programCourseMap : Map<String, PlanCourses>,
    programTypeMap : Map<Long,String?>
) {
    val navController = LocalNavController.current
    val dataList = data.moduleList
    val outCourse = data.outerCourseList
    LazyColumn {
        item { InnerPaddingHeight(innerPadding,true) }
        items(dataList.size, key = { dataList[it].moduleId }) { index ->
            val item = dataList[index]
            InnerItem(item,programTypeMap[item.moduleId],programCourseMap,programTypeMap)
        }
        if(outCourse.isNotEmpty()) {
            val summary = data.outerCompletionSummary
            item { DividerText(text = "培养方案外课程") }
            item {
                val dest = ProgramCompetitionDetailDestination(ProgramPerformanceDetailItem.Outer(outCourse),programCourseMap,programTypeMap)
                CustomCard(
                    shape = NoneRoundShape,
                    color = cardNormalColor(),
                    modifier = Modifier
                        .clickableWithScale {
                            navController.push(dest)
                        }
                        .sharedContainer(
                            key = dest.key,
                            MaterialTheme.shapes.medium,
                            cardNormalColor()
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TransplantListItem(
                            headlineContent = { Text(text = "${summary.passedCredits} 学分") },
                            overlineContent = { Text(text = "通过") },
                            modifier = Modifier.weight(.5f)
                        )
                        TransplantListItem(
                            headlineContent = { Text(text = "${summary.failedCredits} 学分") },
                            overlineContent = { Text(text = "挂科") },
                            modifier = Modifier.weight(.5f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TransplantListItem(
                            headlineContent = { Text(text = "${summary.takingCredits} 学分") },
                            overlineContent = { Text(text = "本学期在修") },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item {
                BottomTip("含转专业废弃课程、跨专业选修课程")
            }
            item {
                BottomTip("转专业废弃课程如在跨专业选修课名单中，则可当作跨专业选修课；跨专业选修课的学分是否算作专业选修内尚存在争议，请咨询各学院教务处")
            }
        }
        item { InnerPaddingHeight(innerPadding,false) }
    }
}

@Composable
private fun InnerItem(
    item : ProgramModule,
    remark : String?,
    programCourseMap : Map<String, PlanCourses>,
    programTypeMap : Map<Long, String?>
) {
    val navController = LocalNavController.current
    val requireInfo = item.requireInfo
    val summary = item.completionSummary
    val dest = ProgramCompetitionDetailDestination(ProgramPerformanceDetailItem.Inner(item),programCourseMap,programTypeMap)


    DividerTextExpandedWith(text = item.nameZh) {
        CustomCard(
            shape = NoneRoundShape,
            color = cardNormalColor(),
            modifier = Modifier
                .clickableWithScale {
                    navController.push(dest)
                }
                .sharedContainer(
                    key = dest.key,
                    MaterialTheme.shapes.medium,
                    cardNormalColor()
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransplantListItem(
                    headlineContent = { Text(text = "${summary.passedCredits} 学分") },
                    overlineContent = { Text(text = "通过") },
                    modifier = Modifier.weight(1/3f)
                )
                TransplantListItem(
                    headlineContent = { Text(text = "${summary.failedCredits} 学分") },
                    overlineContent = { Text(text = "挂科") },
                    modifier = Modifier.weight(1/3f)
                )
                TransplantListItem(
                    headlineContent = { Text(text = "${summary.skipCredits} 学分") },
                    overlineContent = { Text(text = "跳过") },
                    modifier = Modifier.weight(1/3f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransplantListItem(
                    headlineContent = { Text(text = "${summary.takingCredits} 学分") },
                    overlineContent = { Text(text = "本学期在修") },
                    modifier = Modifier.weight(.5f),
                )
                TransplantListItem(
                    headlineContent = { Text(text = "${requireInfo.credits} 学分") },
                    overlineContent = { Text(text = "要求") },
                    modifier = Modifier.weight(.5f),
                )
            }

            if(requireInfo.credits != 0.0) {
                val progress = summary.passedCredits safeDiv requireInfo.credits
                CustomLineProgressIndicator(
                    progress.toFloat(),
                    text = "${summary.passedCredits}/${requireInfo.credits}"
                )
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
            }
        }
        remark?.let {
            BottomTip(it)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun ProgramPerformance(
    vm : NetWorkViewModel,
    ifSaved : Boolean,
    innerPadding : PaddingValues,
) {
    val uiState by vm.programPerformanceData.state.collectAsState()
    val data by produceState<ProgramBean?>(initialValue = null) {
        if(!ifSaved || uiState is NetworkUiState.Success) {
            onListenStateHolder(vm.programPerformanceData) { data ->
                value = data
            }
        } else {
            val bean = try {
                val json = LargeStringDataManager.read(LargeStringDataManager.PROGRAM_PERFORMANCE)
                GsonInstance.fromJson(json,ProgramBean::class.java)
            } catch (e : Exception) {
                LogUtil.error(e)
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
        if(uiState is NetworkUiState.Success) {
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

    val programTypeMap by produceState(initialValue = emptyMap()) {
        value = createProgramRemarkMap()
    }

    val programCourseMap by produceState(initialValue = emptyMap()) {
        value = createProgramMap()
    }

    if(loading) {
        LoadingScreen()
    } else {
//        CompositionLocalProvider(
//            LocalProgramRemarkMap provides programTypeMap,
//            LocalProgramMap provides programCourseMap
//        ) {
            data?.let {
                ProgramPerformanceCustom(innerPadding,it,programCourseMap,programTypeMap)
            }
//        }
    }
}

//
//private val LocalProgramRemarkMap = staticCompositionLocalOf { mutableMapOf<Long, String?>() }
//private val LocalProgramMap = staticCompositionLocalOf { mutableMapOf<String, PlanCourses>() }
//

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramCompetitionDetailScreen(
    bean : ProgramPerformanceDetailItem,
    programCourseMap : Map<String, PlanCourses>,
    programTypeMap : Map<Long,String?>
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var input by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()

    /*
    item {
                        programTypeMap[dataList.moduleId]?.let {
                            BottomTip(it)
                        }
                    }
     */

    val isCourses = bean is ProgramPerformanceDetailItem.Inner && bean.bean.allModuleList.isEmpty()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(
                        when(bean) {
                            is ProgramPerformanceDetailItem.Inner -> {
                                bean.bean.nameZh
                            }
                            is ProgramPerformanceDetailItem.Outer -> {
                                "培养方案外课程"
                            }
                        }
                    ) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        if(bean is ProgramPerformanceDetailItem.Inner) {
                            val beanModule = bean.bean
                            val remark = programTypeMap[beanModule.moduleId]
                            if(remark != null) {
                                val controller = LocalFloatingController.current
                                val window = ProgramRemarkWindow(remark)
                                SharedContainer(
                                    key = window.key,
                                    shape = CircleShape,
                                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    LiquidButton (
                                        shape = NoneRoundShape,
                                        backdrop = backDrop,
                                        isCircle = true,
                                        onClick = {
                                            controller.push(window)
                                        },
                                    ) {
                                        Icon(painterResource(R.drawable.info),null)
                                    }
                                }
                            }
                        }
                    }
                )
                if(
                    bean is ProgramPerformanceDetailItem.Outer ||
                    isCourses
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = APP_HORIZONTAL_DP)
                                .containerBackDrop(backDrop, MaterialTheme.shapes.medium)
                            ,
                            value = input,
                            onValueChange = {
                                input = it
                            },
                            label = { Text("课程名、代码或备注") },
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
                            colors = textFiledAllTransplant(),
                        )
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .backDropSource(backDrop)
                .fillMaxSize()
        ) {
            PerformanceInfo(bean,innerPadding,input, programCourseMap,programTypeMap)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerformanceInfo(
    bean : ProgramPerformanceDetailItem,
    innerPadding: PaddingValues,
    input : String,
    programCourseMap : Map<String, PlanCourses>,
    programTypeMap : Map<Long, String?>
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var itemForInfo by remember { mutableStateOf(CourseItem("","详情",0.0, listOf(""),true,"",null,null,null)) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
        ) {
            HazeBottomSheetTopBar(itemForInfo.nameZh, isPaddingStatusBar = false)
            ProgramInfoItem(itemForInfo)
            Spacer(modifier = Modifier.height(45.dp))
        }
    }

    when(bean) {
        is ProgramPerformanceDetailItem.Inner -> {
            val dataList = bean.bean
            val allModules = dataList.allModuleList.sortedByDescending { it.nameZh }
            if(allModules.isEmpty()) {
                val allCourse = dataList.allCourseList
                val filteredList = mutableListOf<CourseItem>()
                allCourse.forEach { i ->
                    if(i.nameZh.contains(input) || i.code.contains(input) || programCourseMap[i.code]?.remark?.contains(input) == true) {
                        filteredList.add(i)
                    }
                }
                filteredList.sortBy { it.resultType }

                // 如果清一色compulsory（布尔值）一样则返回false，否则返回true
                val displayCompulsory = filteredList.any { it.compulsory } && filteredList.any { !it.compulsory }

                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    if(filteredList.isNotEmpty()) {
                        items(filteredList.size) { index ->
                            val item = filteredList[index]
                            val term = transferTerm(item.terms)
                            val type = getProgramCompetitionType(item.resultType)
                            val warning = displayCompulsory && item.compulsory
                            val programDetail = programCourseMap[item.code]
                            CustomCard(
                                color = cardNormalColor(),
                                modifier = Modifier.clickable {
                                    itemForInfo = item
                                    showBottomSheet = true
                                }
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text(text = item.nameZh.replace("&nbsp;","") ) },
                                    supportingContent = {
                                        if(type == ProgramCompetitionType.FAILED || type == ProgramCompetitionType.PASSED) {
                                            Text(text =
                                                "均分 ${item.score ?: "--"} 绩点 ${item.gp ?: "--"} " +
                                                        if(item.rank != null) "等级 ${item.rank ?: "--"}" else ""
                                            )
                                        }
                                    },
                                    trailingContent = {
                                        ColumnVertical() {
                                            if(warning) {
                                                Text("必修", color = MaterialTheme.colorScheme.error)
                                            }
                                            Text(text = type?.description ?: item.resultType)
                                        }
                                    },
                                    overlineContent = {
                                        val termText = if(term != null) {
                                            if(term.size >= 8) {
                                                "每学期"
                                            } else {
                                                "第${term.joinToString(",")}学期"
                                            }
                                        } else {
                                            "未知学期"
                                        }
                                        Text(text = termText + " | 学分 ${item.credits}")
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(type?.icon ?: R.drawable.help),
                                            null,
                                            tint = (
                                                    if(type == ProgramCompetitionType.FAILED) MaterialTheme.colorScheme.error
                                                    else  LocalContentColor.current
                                                    ).copy(
                                                    if(type == ProgramCompetitionType.UNREPAIRED && !item.compulsory) {
                                                        .5f
                                                    } else {
                                                        1f
                                                    }
                                                )
                                        )
                                    },
                                )
                                programDetail?.remark?.let { remark ->
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text(remark) },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.info),null)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            } else {
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(allModules.size, key = { allModules[it].moduleId }) { index ->
                        val item = allModules[index]
                        InnerItem(item,programTypeMap[item.moduleId],programCourseMap,programTypeMap)
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
        is ProgramPerformanceDetailItem.Outer -> {
            val outerCourse = bean.list
            val filteredList = mutableListOf<CourseItem>()
            outerCourse.forEach { i ->
                if(i.nameZh.contains(input) || i.code.contains(input)) {
                    filteredList.add(i)
                }
            }
            filteredList.sortBy { it.resultType }
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
                if(filteredList.isNotEmpty()) {
                    items(filteredList.size) { index ->
                        val item = filteredList[index]
                        val type = getProgramCompetitionType(item.resultType)
                        CardListItem(
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
                                    painterResource(type?.icon ?: R.drawable.help),
                                    null,
                                    tint = (
                                            if(type == ProgramCompetitionType.FAILED) MaterialTheme.colorScheme.error
                                            else  LocalContentColor. current
                                            )
                                )
                            },
                            modifier = Modifier.clickable {
                                itemForInfo = item
                                showBottomSheet = true
                            },
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