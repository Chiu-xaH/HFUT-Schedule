package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.PlanCourses
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.item
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch


@Composable
fun sProgramScreen(vm: NetWorkViewModel, ifSaved: Boolean, hazeState: HazeState) {

    var showBottomSheet_Performance by remember { mutableStateOf(false) }

    val uiState by vm.programCompletionData.state.collectAsState()
    var loadingCard = uiState !is UiState.Success
    val scale2 = animateFloatAsState(
        targetValue = if (loadingCard) 0.97f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val programData by produceState<ProgramResponse?>(initialValue = null) {
        if(!ifSaved) {
            onListenStateHolder(vm.programData) { data ->
                value = data
            }
        } else {
            value = try {
                Gson().fromJson(prefs.getString("program",""), ProgramResponse::class.java)
            } catch (e : Exception) {
                null
            }
        }
    }


    var completion by remember { mutableStateOf(
        item("培养方案课程",0.0,0.0).let { nilItem -> ProgramCompletionResponse(nilItem, listOf(nilItem,nilItem,nilItem)) }
    ) }

    LaunchedEffect(Unit) {
        if(!ifSaved) {
            val cookie = getJxglstuCookie(vm)
            cookie?.let {
                launch {
                    vm.programData.clear()
                    vm.getProgram(it)
                }
                launch {
                    vm.programCompletionData.clear()
                    vm.getProgramCompletion(it)
                }
            }
        }
    }

    if (showBottomSheet_Performance ) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_Performance = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Performance
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("培养方案 完成情况")
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramPerformance(vm,hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }


    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val response = (uiState as UiState.Success).data
            response.let {
                completion = it
            }
        }
    }

    DividerTextExpandedWith(text = if(ifSaved)"完成情况(登录后可查看)" else "完成情况",openBlurAnimation = false) {
        LoadingLargeCard(
            title = "已修 ${completion.total.actual}/${completion.total.full}",
            rightTop = {
                val res = completion.total.actual/completion.total.full * 100.0
                Text(text = "${formatDecimal(res,1)} %")
            },
            loading = loadingCard
        ) {
            for(i in 0 until completion.other.size step 2)
                Row {
                    TransplantListItem(
                        headlineContent = { Text(text = completion.other[i].name) },
                        overlineContent = {Text(text = "${completion.other[i].actual}/${completion.other[i].full}", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(.5f)
                    )
                    if(i+1 < completion.other.size)
                        TransplantListItem(
                            headlineContent = { Text(text = completion.other[i+1].name) },
                            overlineContent = {Text(text = "${completion.other[i+1].actual}/${completion.other[i+1].full}",fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(.5f)
                        )
                }
        }
        LargeButton(
            onClick = {
                if(ifSaved) refreshLogin()
                else showBottomSheet_Performance = true
            },
            icon = R.drawable.monitoring,
            text = "培养方案完成情况",
            modifier = Modifier
                .fillMaxWidth().scale(scale2.value)
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        )
    }


    val loading = programData == null
    DividerTextExpandedWith(text = getPersonInfo().program ?: "方案安排") {
        Box {
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    LoadingUI()
                }
            }
            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ProgramChildrenUI(programData,hazeState,vm,ifSaved)
            }
        }
    }
}

@Composable
fun ProgramChildrenUI(entity : ProgramResponse?, hazeState : HazeState,vm: NetWorkViewModel,ifSaved : Boolean) {
    if(entity == null) return

    val children = entity.children
    val planCourses = entity.planCourses.sortedBy { it.readableTerms.let { if(it.isNotEmpty()) it[0] else null } }

    var showBottomSheet_Program by remember { mutableStateOf(false) }


    if(children.isNotEmpty()) {
        var bean by remember { mutableStateOf<ProgramResponse?>(null) }
        bean?.let {
            if (showBottomSheet_Program) {
                HazeBottomSheet (
                    onDismissRequest = { showBottomSheet_Program = false },
                    hazeState = hazeState,
                    showBottomSheet = showBottomSheet_Program
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        topBar = {
                            HazeBottomSheetTopBar(it.type?.nameZh ?: "培养方案")
                        },) {innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        ){
                            ProgramChildrenUI(it, hazeState = hazeState,vm,ifSaved)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }




        LazyColumn() {

            items(children.size, key = { it }) { item ->
                val dataItem = children[item]
                AnimationCardListItem(
                    headlineContent = { Text(text = dataItem.type?.nameZh + dataItem.requireInfo?.requiredCredits.let { if(it != 0.0)" (要求" + it + "学分)" else "" }) },
                    supportingContent = { dataItem.remark?.let { Text(it) } },
                    modifier = Modifier.clickable {
                        showBottomSheet_Program = true
                        bean = dataItem
                    },
                    index = item
                )
            }
            entity.requireInfo?.let {
                if(it.requiredCredits == 0.0 && it.requiredCourseNum == 0) {
                    return@let
                }
                item {
                    BottomTip(
                        "要求 " +
                                it.requiredCredits.let { num ->
                                    if(num == 0.0) "" else "" + num + "学分"
                                }
                                +
                                it.requiredCourseNum.let { num ->
                                    if(num == 0) "" else " " + num + "门"
                                }
                    )
                }
            }
            entity.remark?.let { item { BottomTip(str = it) } }

        }
    }
    if(planCourses.isNotEmpty()) {

        val state = rememberLazyListState()

        var input by remember { mutableStateOf("") }

        var courseInfo by remember { mutableStateOf<PlanCourses?>(null) }
        var showInfo by remember { mutableStateOf(false) }
        if(showInfo) {
            courseInfo?.let {
                planCoursesTransform(it)?.let { b ->
                    ProgramDetailInfo(courseInfo = b,vm, hazeState, ifSaved){ showInfo = false }
                }
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
                label = { Text("搜索课程、类型或代码" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
            )
        }
        val searchList = mutableListOf<PlanCourses>()
        planCourses.forEach { item ->
            val has =
                item.course.nameZh.contains(input,ignoreCase = true) ||
                        item.course.courseType.nameZh.contains(input) ||
                        item.course.code.contains(input,ignoreCase = true) ||
                        item.remark?.contains(input) == true ||
                        item.openDepartment.nameZh.contains(input)
            if(has) {
                searchList.add(item)
            }
        }

        Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
        LazyColumn(state = state) {
            items(searchList.size, key = { it }) {item ->
                val listItem = searchList[item]
                val course = listItem.course
                val name = course.nameZh
                val department = listItem.openDepartment.nameZh.substringBefore("（")
                val term = listItem.readableTerms.let { if(it.isNotEmpty()) it[0] else null }
                AnimationCardListItem(
                    headlineContent = { Text(text = name) },
                    supportingContent = { Text(text = department) },
                    overlineContent = { Text(text = term?.let { "第" + it + "学期  " }+ course.credits?.let { "| 学分 $it" } )},
                    leadingContent = { DepartmentIcons(name = department) },
                    trailingContent = if(!listItem.compulsory){{ Text("选修") }} else null,
                    modifier = Modifier.clickable {
                        courseInfo = listItem
                        showInfo = true
                    },
                    index = item
                )
            }
            entity.requireInfo?.let {
                if(it.requiredCredits == 0.0 && it.requiredCourseNum == 0) {
                    return@let
                }
                item {
                    BottomTip(
                        "要求 " +
                                it.requiredCredits.let { num ->
                                    if(num == 0.0) "" else "" + num + "学分"
                                }
                                +
                                it.requiredCourseNum.let { num ->
                                    if(num == 0) "" else " " + num + "门"
                                }
                    )
                }
            }
            entity.remark?.let { item { BottomTip(str = it) } }
        }
    }
}

@Composable
fun ProgramDetailInfo(courseInfo : ProgramPartThree, vm: NetWorkViewModel, hazeState: HazeState, ifSaved: Boolean, onDismissRequest : () -> Unit) {
    var showBottomSheet_Search by remember { mutableStateOf(false) }

    ApiForCourseSearch(vm,null, courseInfo.code,showBottomSheet_Search, hazeState = hazeState) {
        showBottomSheet_Search = false
    }
    HazeBottomSheet(
        showBottomSheet = true,
        isFullExpand = true,
        autoShape = false,
        hazeState = hazeState,
        onDismissRequest = onDismissRequest
    ){
        Column(modifier = Modifier.navigationBarsPadding()) {
            HazeBottomSheetTopBar(courseInfo.name , isPaddingStatusBar = false) {
                FilledTonalButton(
                    onClick = {
                        if(!ifSaved) {
                            showBottomSheet_Search = true
                        } else {
                            showToast("登录教务后可查询开课")
                            refreshLogin()
                        }
                    }
                ) {
                    Text("开课查询")
                }
            }
            Row {
                val type = courseInfo.courseType
                val isCompulsory = courseInfo.isCompulsory
                val specially = isCompulsory && type.contains("选修")
                TransplantListItem(
                    headlineContent = { Text(type) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.hotel_class),null)
                    },
                    overlineContent = { Text("类型") },
                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { Text(if(courseInfo.isCompulsory) "必修" else "选修") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.verified),null)
                    },
                    colors = if(specially) MaterialTheme.colorScheme.errorContainer else null,
                    overlineContent = { Text("真实选修性") },
                    modifier = Modifier.weight(.5f).clickable {
                        if(specially)
                            showToast("虽然类型为$type,但是培养方案标注为必修")
                    }
                )
            }
            Row {
                TransplantListItem(
                    headlineContent = { Text(courseInfo!!.credit.toString()) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.filter_vintage),null)
                    },
                    overlineContent = { Text("学分") },

                    modifier = Modifier.weight(.5f)
                )
                courseInfo.code.let {
                    TransplantListItem(
                        headlineContent = { Text(it) },
                        leadingContent = {
                            Icon(painterResource(R.drawable.tag),null)
                        },
                        overlineContent = { Text("课程代码") },
                        modifier = Modifier.weight(.5f).clickable {
                            ClipBoardUtils.copy(it)
                        }
                    )
                }
            }
            Row {
                TransplantListItem(
                    headlineContent = { Text("第" + courseInfo.term +"学期") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.schedule),null)
                    },
                    overlineContent = { Text("类型") },

                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { Text("共"+courseInfo.week+"周") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.calendar),null)
                    },
                    overlineContent = { Text("上课周数") },

                    modifier = Modifier.weight(.5f)
                )
            }

            courseInfo.depart.let {
                TransplantListItem(
                    headlineContent = { Text(it) },
                    leadingContent = {
                        DepartmentIcons(it)
                    },
                    overlineContent = { Text("开设学院") },
                )
            }
            courseInfo.remark?.let {
                TransplantListItem(
                    headlineContent = { Text(it) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.info),null)
                    },
                    overlineContent = { Text("备注") },
                )
            }
            Spacer(Modifier.height(APP_HORIZONTAL_DP))
        }
    }

}
