package com.hfut.schedule.ui.screen.home.search.function.community.failRate

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FailRate(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.FailRate.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.FailRate.title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.FailRate.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FailRateScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.FailRate.route }
    var input by remember { mutableStateOf( "") }


    LaunchedEffect(Unit) {
        vm.failRateData.emitPrepare()
    }

    val uiState by vm.failRateData.state.collectAsState()
    var firstUse by remember { mutableStateOf(true) }
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit = {
        SharedPrefs.prefs.getString("TOKEN","")?.let {
            vm.failRateData.clear()
            vm.searchFailRate(it,input,page)
            firstUse = false
        }
    }
    LaunchedEffect(page) {
        if(!firstUse) {
            refreshNetwork()
        }
    }
    val scope = rememberCoroutineScope()

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column (
                    modifier = Modifier.topBarBlur(hazeState),
                ){
                    TopAppBar(
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.FailRate.title) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.FailRate.icon)
                        }
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
                            label = { Text("输入科目名称" ) },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        scope.launch { refreshNetwork() }
                                    }) {
                                    Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
                    FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it },innerPadding,hazeState)
                }
            }
        }
    }
}

var permit = 1
@Composable
fun ApiToFailRate(input : String, vm: NetWorkViewModel, hazeState: HazeState,innerPadding : PaddingValues) {
    val uiState by vm.failRateData.state.collectAsState()
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit = {
        SharedPrefs.prefs.getString("TOKEN","")?.let {
            vm.failRateData.clear()
            vm.searchFailRate(it,input,page)
        }
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it },innerPadding,hazeState)
    }
}