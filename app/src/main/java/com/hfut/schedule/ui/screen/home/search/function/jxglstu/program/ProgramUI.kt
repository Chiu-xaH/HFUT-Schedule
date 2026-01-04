package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.PlanCourses
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.item
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.status.CustomLineProgressIndicator
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch




@Composable
fun ProgramScreenMini(vm: NetWorkViewModel, ifSaved: Boolean, hazeState: HazeState,innerPadding : PaddingValues) {
    val context = LocalContext.current
    val programData by produceState<ProgramResponse?>(initialValue = null) {
        if(!ifSaved) {
            onListenStateHolder(vm.programData) { data ->
                value = data
            }
        } else {
            value = try {
                val content = LargeStringDataManager.read(LargeStringDataManager.PROGRAM)
                if(content == null) {
                    null
                }
                Gson().fromJson(content, ProgramResponse::class.java)
            } catch (e : Exception) {
                null
            }
        }
    }


    LaunchedEffect(Unit) {
        if(!ifSaved) {
            val cookie = getJxglstuCookie()
            cookie?.let {
                launch {
                    vm.programData.clear()
                    vm.getProgram(it)
                }
            }
        }
    }

    val loading = programData == null
    Column(modifier = Modifier.padding(innerPadding)) {
        DividerTextExpandedWith(text = getPersonInfo().program ?: "方案安排") {
            Box {
                androidx.compose.animation.AnimatedVisibility (
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
                androidx.compose.animation.AnimatedVisibility(
                    visible = !loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ProgramChildrenUI(programData,hazeState,vm,ifSaved)
                }
            }
        }
    }
}

@Composable
fun ProgramCompetitionScreenMini(vm: NetWorkViewModel,ifSaved: Boolean,innerPadding : PaddingValues) {
    val emptyData = remember {  item("培养方案课程",0.0,1.0).let { nilItem -> ProgramCompletionResponse(nilItem, listOf(nilItem,nilItem,nilItem)) } }
    val completion by produceState(initialValue = emptyData) {
        if(!ifSaved) {
            onListenStateHolder(vm.programCompletionData) { data ->
                value = data
            }
        } else {
            value = try {
                val listType = object : TypeToken<List<ProgramCompletionResponse>>() {}.type
                val data : List<ProgramCompletionResponse> = Gson().fromJson(prefs.getString("PROGRAM_COMPETITION",""), listType)
                data[0]
            } catch (e : Exception) {
                emptyData
            }
        }
    }

    val loading = completion == emptyData

    LaunchedEffect(Unit) {
        if(!ifSaved) {
            val cookie = getJxglstuCookie()
            cookie?.let {
                launch {
                    vm.programCompletionData.clear()
                    vm.getProgramCompletion(it)
                }
            }
        }
    }


    val state = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(state)
    ) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("详情",openBlurAnimation = false) {
            completion.let {
                LoadingLargeCard(
                    prepare = false,
                    title = "已修 ${it.total.actual}/${it.total.full}",
                    rightTop = {
                        val res = it.total.actual/it.total.full * 100.0
                        Text(text = "${formatDecimal(res,1)} %")
                    },
                    loading = loading
                ) {
                    for(i in 0 until it.other.size step 2)
                        Row {
                            TransplantListItem(
                                headlineContent = { Text(text = it.other[i].name) },
                                overlineContent = {Text(text = "${it.other[i].actual}/${it.other[i].full}", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(.5f)
                            )
                            if(i+1 < it.other.size)
                                TransplantListItem(
                                    headlineContent = { Text(text = it.other[i+1].name) },
                                    overlineContent = {Text(text = "${it.other[i+1].actual}/${it.other[i+1].full}",fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.weight(.5f)
                                )
                        }
                }
            }
            Spacer(Modifier.height(APP_HORIZONTAL_DP))

            CustomLineProgressIndicator(
                (completion.total.actual/completion.total.full).toFloat(),
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(CARD_NORMAL_DP*2))
            for (i in 0 until completion.other.size) {
                CustomLineProgressIndicator((completion.other[i].actual/completion.other[i].full).toFloat())
                if(i != completion.other.size-1) {
                    Spacer(Modifier.height(CARD_NORMAL_DP*2))
                }
            }
        }
        InnerPaddingHeight(innerPadding,false)
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
    val context = LocalContext.current

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
                            refreshLogin(context)
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
                        Icon(painterResource(R.drawable.kid_star),null)
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
                    modifier = Modifier
                        .weight(.5f)
                        .clickable {
                            if (specially)
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
                        modifier = Modifier
                            .weight(.5f)
                            .clickable {
                                ClipBoardHelper.copy(it)
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
