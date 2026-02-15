package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.model.jxglstu.PlanCoursesSearch
import com.hfut.schedule.logic.model.jxglstu.ProgramListBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon

import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramSearchScreen(
    vm : NetWorkViewModel,
    ifSaved: Boolean,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var input by remember { mutableStateOf("") }
    val backdrop = rememberLayerBackdrop()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.AllPrograms.receiveRoute() }
    val jwt by DataStoreManager.uniAppJwt.collectAsState(initial = null)
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork: suspend () -> Unit = m@ {
        if(jwt == null || jwt!!.isEmpty() || jwt!!.isBlank()) {
            return@m
        }
        vm.searchProgramsResp.clear()
        vm.searchPrograms(jwt!!,page,input)
    }
    val uiState by vm.searchProgramsResp.state.collectAsState()

    LaunchedEffect(page,jwt) {
        refreshNetwork()
    }
    var id by remember { mutableIntStateOf(-1) }
    var title by remember { mutableStateOf("培养方案详情") }
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
                    HazeBottomSheetTopBar(title)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ProgramSearchInfo(vm,id, ifSaved,hazeState)
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.AllPrograms.label)) },
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
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                            .weight(1f)
                            ,
                        value = input,
                        onValueChange = {
                            input = it
                        },
                        label = { Text("搜索") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch{
                                        refreshNetwork()
                                    }
                                }
                            ) {
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
            }

        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            Column {
                CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchIcon() }) {
                    val programList = (uiState as UiState.Success).data
                    val listState = rememberLazyListState()

                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(state = listState) {
                            item { InnerPaddingHeight(innerPadding,true) }
                            item { Spacer(Modifier.height(CARD_NORMAL_DP))}
                            items(programList.size, key = { it }) { index ->
                                val data = programList[index]
                                var department = data.department.nameZh
                                val name = data.nameZh
                                department = department.substringBefore("（")
                                CardListItem(
                                    headlineContent = { Text(name) },
                                    overlineContent = { Text(data.grade + "级 " + department + " " + data.major.nameZh) },
                                    leadingContent = { DepartmentIcons(department) },
                                    modifier = Modifier.clickable {
                                        title = data.nameZh
                                        id = data.id
                                        showBottomSheet = true
                                    },
                                )
                            }
                            item { InnerPaddingHeight(innerPadding,false) }
                            item { PaddingForPageControllerButton() }
                        }
                        PageController(listState,page,onNextPage = { page = it }, onPreviousPage = { page = it })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramSearchInfo(vm: NetWorkViewModel, id : Int, ifSaved: Boolean, hazeState: HazeState) {
    val uiState by vm.getProgramByIdResp.state.collectAsState()
    val jwt by DataStoreManager.uniAppJwt.collectAsState(initial = null)
    val refreshNetwork: suspend () -> Unit = m@ {
        if(jwt == null || jwt!!.isEmpty() || jwt!!.isBlank()) {
            return@m
        }
        vm.getProgramByIdResp.clear()
        vm.getProgramById(id, jwt!!)
    }

    LaunchedEffect(jwt) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork, loadingText = "培养方案较大 加载中") {
        val bean = (uiState as UiState.Success).data
        ProgramSearchChildrenUI(bean,hazeState,vm,ifSaved)
    }
}


@Composable
private fun ProgramSearchChildrenUI(entity : ProgramSearchBean?, hazeState : HazeState,vm: NetWorkViewModel,ifSaved : Boolean) {
    if(entity == null) return
    val children = entity.children
    val planCourses = entity.planCourses.sortedBy {
        it.terms.let {
            if(it.isNotEmpty())
                it[0]
            else
                null
        }
    }

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
                CardListItem(
                    headlineContent = { Text(text = dataItem.type?.nameZh + dataItem.requireInfo?.requiredCredits.let { if(it != 0.0)" (要求" + it + "学分)" else "" }) },
                    supportingContent = { dataItem.remark?.let { Text(it) } },
                    modifier = Modifier.clickable {
                        showBottomSheet_Program = true
                        bean = dataItem
                    }
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
                CardListItem(
                    headlineContent = { Text(text = name) },
                    supportingContent = { Text(text = department) },
                    overlineContent = { Text(text = term?.let { "第" + it + "学期  " }+ course.credits?.let { "| 学分 $it" })},
                    leadingContent = { DepartmentIcons(name = department) },
                    trailingContent = if(!listItem.compulsory){{ Text("选修") }} else null,
                    modifier = Modifier.clickable {
                        courseInfo = listItem
                        showInfo = true
                    },
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



