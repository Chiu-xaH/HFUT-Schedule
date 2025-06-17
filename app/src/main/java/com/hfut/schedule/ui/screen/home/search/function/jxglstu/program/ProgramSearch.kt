package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.PlanCoursesSearch
import com.hfut.schedule.logic.model.jxglstu.ProgramListBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StatusUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.HEFEI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramSearch(vm : NetWorkViewModel, ifSaved: Boolean, hazeState: HazeState) {
    var campus by remember { mutableStateOf( getCampus() ) }
    val uiState by vm.programList.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.programList.clear()
        vm.getProgramList(campus)
    }
    LaunchedEffect(campus) {
        refreshNetwork()
    }

    var item by remember { mutableStateOf(ProgramListBean(0,"","培养方案详情","","")) }
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(item.name)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ProgramSearchInfo(vm,item,campus, ifSaved, hazeState =hazeState )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("全校培养方案") {
                FilledTonalButton(
                    onClick = {
                        campus = when(campus) {
                            HEFEI -> XUANCHENG
                            XUANCHENG -> HEFEI
                        }
                    },
                ) {
                    Text(
                        when(campus) {
                            HEFEI -> "合肥"
                            XUANCHENG -> "宣城"
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            var input by remember { mutableStateOf("") }
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
                    label = { Text("搜索") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {}) {
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

            CommonNetworkScreen(uiState, onReload = refreshNetwork, loadingText = "若加载过长 请搭外网") {
                val programList = (uiState as UiState.Success).data
                val searchList = programList.filter {
                    it.name.contains(input) || it.department.contains(input) || it.major.contains(input) || it.grade.contains(input)
                }
                if(searchList.isNotEmpty()) {
                    LazyColumn {
                        items(searchList.size, key = { it }) { index ->
                            val data = searchList[index]
                            var department = data.department
                            val name = data.name
                            department = department.substringBefore("（")
                            AnimationCardListItem(
                                headlineContent = { Text(name) },
                                overlineContent = { Text(data.grade + "级 " + department + " " + data.major) },
                                leadingContent = { DepartmentIcons(department) },
                                modifier = Modifier.clickable {
                                    item = data
                                    showBottomSheet = true
                                },
                                index = index
                            )
                        }
                    }
                } else {
                    if(campus == HEFEI && programList.isEmpty()) {
                        Column {
                            EmptyUI("需合肥校区在读生贡献数据源")
                            Spacer(Modifier.height(5.dp))
                            RowHorizontal {
                                Button(
                                    onClick = {
                                        Starter.startWebUrl("https://github.com/${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}/blob/main/tools/All-Programs-Get-Python/README.md")
                                    }
                                ) {
                                    Text("接入指南(Github)")
                                }
                            }
                        }
                    } else {
                        EmptyUI()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramSearchInfo(vm: NetWorkViewModel, item: ProgramListBean, campus: Campus, ifSaved: Boolean, hazeState: HazeState) {
    val uiState by vm.programSearchData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.programSearchData.clear()
        vm.getProgramListInfo(item.id,campus)
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork, loadingText = "培养方案较大 加载中") {
        val bean = (uiState as UiState.Success).data
        ProgramSearchChildrenUI(bean,hazeState,vm,ifSaved)
    }
}


@Composable
fun ProgramSearchChildrenUI(entity : ProgramSearchBean?, hazeState : HazeState,vm: NetWorkViewModel,ifSaved : Boolean) {
    if(entity == null) return
    val children = entity.children
    val planCourses = entity.planCourses.sortedBy { it.terms.let { if(it.isNotEmpty()) it[0] else null } }

    var showBottomSheet_Program by remember { mutableStateOf(false) }

    if(children.isNotEmpty()) {
        var bean by remember { mutableStateOf<ProgramSearchBean?>(null) }
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
                            ProgramSearchChildrenUI(it, hazeState = hazeState,vm,ifSaved)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }


        LazyColumn {
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
        var input by remember { mutableStateOf("") }

        var courseInfo by remember { mutableStateOf<PlanCoursesSearch?>(null) }
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
        val searchList = mutableListOf<PlanCoursesSearch>()
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
        LazyColumn {
            items(searchList.size, key = { it }) {item ->
                val listItem = searchList[item]
                val course = listItem.course
                val name = course.nameZh
                val department = listItem.openDepartment.nameZh.substringBefore("（")
                val term = listItem.terms.let { if(it.isNotEmpty()) it[0] else null }?.substringAfter("_")?.toIntOrNull()
                AnimationCardListItem(
                    headlineContent = { Text(text = name) },
                    supportingContent = { Text(text = department) },
                    overlineContent = { Text(text = term?.let { "第" + it + "学期  " }+ course.credits?.let { "| 学分 $it" })},
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
