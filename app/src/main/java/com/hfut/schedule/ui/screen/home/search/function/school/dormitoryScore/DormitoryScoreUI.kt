package com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DormitoryScoreScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.DormitoryScore.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val dormitoryUiState by vm.dormitoryFromCommunityResp.state.collectAsState()
    val dormitoryInfoUiState by vm.dormitoryInfoFromCommunityResp.state.collectAsState()
    val dormitoryScoreUiState by vm.dormitoryScoreResp.state.collectAsState()
    val semester by produceState<Int?>(initialValue = null) {
        value = SemesterParser.getSemester()
    }
    var week by remember { mutableStateOf<Int?>(null) }

    val refreshNetworkScore  : suspend () -> Unit = m@ {
        if(semester == null) {
            showToast("学期为空")
            return@m
        }
        prefs.getString("TOKEN","")?.let {
            vm.dormitoryScoreResp.clear()
            vm.getDormitoryScore(it,week, semester?.let { s -> SemesterParser.parseSemesterForDormitory(s)})
        }
    }
    val scrollState = rememberScrollState()

    LaunchedEffect(week,semester) {
        if(semester == null) {
            return@LaunchedEffect
        }
        refreshNetworkScore()
    }

    val refreshNetwork : suspend () -> Unit = m@ {
        if(dormitoryInfoUiState is UiState.Success) {
            return@m
        }
        prefs.getString("TOKEN","")?.let {
            vm.dormitoryFromCommunityResp.clear()
            vm.getDormitory(it)
            if(vm.dormitoryFromCommunityResp.state.first() is UiState.Success) {
                vm.getDormitoryInfo(it)
            }
        }
    }
    LaunchedEffect(Unit) {
        launch {
            week = DateTimeManager.weeksBetweenJxglstu.toInt()
        }
        launch {
            refreshNetwork()
        }
    }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.DormitoryScore.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.DormitoryScore.icon)
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            week?.let { page ->
                PageController(
                    scrollState = scrollState,
                    currentPage = page,
                    onNextPage = { week = it },
                    onPreviousPage = { week = it },
                    range = Pair(1, MyApplication.MAX_WEEK),
                    resetPage = DateTimeManager.weeksBetweenJxglstu.toInt(),
                    text = "第${page}周",
                )
            }
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                DividerTextExpandedWith("寝室信息") {
                    CommonNetworkScreen(dormitoryUiState,isFullScreen = false, onReload = refreshNetwork) {
                        val data = (dormitoryUiState as UiState.Success).data
                        CustomCard(color = cardNormalColor()) {
                            Column {
                                TransplantListItem(
                                    headlineContent = { Text( data.dormitory + " " + data.room)},
                                    overlineContent = { Text("所在寝室") },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.bed),null)
                                    }
                                )
                                PaddingHorizontalDivider()
                                CommonNetworkScreen(dormitoryInfoUiState,isFullScreen = false, onReload = refreshNetwork) {
                                    val data2 = (dormitoryInfoUiState as UiState.Success).data
                                    Column {
                                        for(i in data2) {
                                            TransplantListItem(
                                                headlineContent = {
                                                    Text(i.realname + " | " + i.username)
                                                },
                                                modifier = Modifier.clickable {
                                                    ClipBoardHelper.copy(i.username)
                                                },
                                                overlineContent = { Text("寝室成员") },
                                                leadingContent = {
                                                    Icon(painterResource(R.drawable.person),null)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                DividerTextExpandedWith("卫生评分") {
                    CommonNetworkScreen(dormitoryScoreUiState,isFullScreen = false, onReload = refreshNetworkScore) {
                        val data = (dormitoryScoreUiState as UiState.Success).data
                        Column {
                            if(data.isEmpty()) {
                                EmptyIcon("未查询到评分")
                            } else {
                                CustomCard(color = cardNormalColor())  {
                                    for(index in data.indices step 2) {
                                        val item = data[index]
                                        RowHorizontal {
                                            TransplantListItem(
                                                headlineContent = { Text(item.title) },
                                                supportingContent = { Text(item.value) },
                                                modifier = Modifier.weight(.5f)
                                            )
                                            if(index + 1 < data.size) {
                                                val item2 = data[index+1]
                                                TransplantListItem(
                                                    headlineContent = { Text(item2.title) },
                                                    supportingContent = { Text(item2.value) },
                                                    modifier = Modifier.weight(.5f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            semester?.let {
                                val text = SemesterParser.parseSemester(it)
                                BottomTip(text)
                            }
                        }
                    }
                }
                PaddingForPageControllerButton()
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}