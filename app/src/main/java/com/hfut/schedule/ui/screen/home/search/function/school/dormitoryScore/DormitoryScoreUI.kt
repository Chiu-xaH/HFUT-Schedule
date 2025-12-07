package com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
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
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val dormitoryUiState by vm.dormitoryFromCommunityResp.state.collectAsState()
    val dormitoryInfoUiState by vm.dormitoryInfoFromCommunityResp.state.collectAsState()
    val dormitoryScoreUiState by vm.dormitoryScoreResp.state.collectAsState()
    var semester by remember { mutableStateOf<Int?>(null) }
    var week by remember { mutableStateOf<Int?>(null) }

    val refreshNetworkScore  : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.dormitoryScoreResp.clear()
            vm.getDormitoryScore(it,week, semester?.let { s -> SemesterParser.parseSemesterForDormitory(s)})
        }
    }

    LaunchedEffect(week,semester) {
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
    val weekText = if(week == null) {
        "当前周"
    } else {
        "第${week}周"
    }
    val termText = if(semester == null) {
        "当前学期"
    } else {
        SemesterParser.parseSemesterForDormitory(semester!!)
    }

    LaunchedEffect(Unit) {
        launch {
            week = DateTimeManager.weeksBetweenJxglstu.toInt()
        }
        launch {
            semester = SemesterParser.getSemester()
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
        bottomBar = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(APP_HORIZONTAL_DP)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if(semester != null) {
                        FloatingActionButton(
                            onClick = { week = week!! - 1 },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                            ,
                        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }


                        ExtendedFloatingActionButton(
                            onClick = { scope.launch {
                                week = DateTimeManager.weeksBetweenJxglstu.toInt()
                            } },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) { Text(text = weekText) }

                        FloatingActionButton(
                            onClick = { week = week!! + 1 },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                        ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                    }
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
                Box(modifier = Modifier.fillMaxWidth())  {
                    if(week != null) {
                        FloatingActionButton(
                            onClick = { semester = semester!! - 20 },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                            ,
                        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }


                        ExtendedFloatingActionButton(
                            onClick = { scope.launch {
                                semester = SemesterParser.getSemester()
                            } },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) { Text(text = termText) }

                        FloatingActionButton(
                            onClick = { semester = semester!! + 20 },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                        ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)
            DividerTextExpandedWith("卫生评分") {
                CommonNetworkScreen(dormitoryScoreUiState,isFullScreen = false, onReload = refreshNetworkScore) {
                    val data = (dormitoryScoreUiState as UiState.Success).data
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
            }
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
                                                ClipBoardUtils.copy(i.username)
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
            InnerPaddingHeight(innerPadding,false)
        }
    }
}