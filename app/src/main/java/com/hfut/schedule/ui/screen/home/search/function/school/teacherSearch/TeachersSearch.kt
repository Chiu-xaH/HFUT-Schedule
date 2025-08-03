package com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TeacherSearch(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.TeacherSearch.route }

    TransplantListItem(
        headlineContent = { Text(text = "教师检索") },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.TeacherSearch.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TeacherSearchScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val route = remember { AppNavRoute.TeacherSearch.route }

    var name by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }

    val uiState by vm.teacherSearchData.state.collectAsState()

    val scope = rememberCoroutineScope()
    val refreshNetwork: suspend () -> Unit = {
        vm.teacherSearchData.clear()
        vm.searchTeacher(name,direction)
    }
    LaunchedEffect(Unit) {
        vm.teacherSearchData.emitPrepare()
    }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState, )
                ) {
                    TopAppBar(
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.TeacherSearch.title) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.TeacherSearch.icon)
                        },
                        actions = {
                            FilledTonalIconButton(
                                onClick = { scope.launch { refreshNetwork() } },
                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                            ) {
                                Icon(painterResource(R.drawable.search), contentDescription = "")
                            }
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = APP_HORIZONTAL_DP),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(.5f),
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            label = { Text("姓名" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            modifier = Modifier
                                .weight(.5f),
                            value = direction,
                            onValueChange = {
                                direction = it
                            },
                            label = { Text("研究方向" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                }

            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
                    TeacherListUI(vm,innerPadding)
                }
            }
        }
    }
}

@Composable
fun ApiToTeacherSearch(input : String,vm: NetWorkViewModel,innerPadding : PaddingValues) {
    val uiState by vm.teacherSearchData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        vm.teacherSearchData.clear()
        vm.searchTeacher(input)
    }
    LaunchedEffect(input) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        TeacherListUI(vm, innerPadding)
    }
}