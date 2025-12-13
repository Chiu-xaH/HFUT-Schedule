package com.hfut.schedule.ui.screen.home.search.function.community.failRate

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FailRate(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.FailRate.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.FailRate.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.FailRate.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.FailRate,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FailRateScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            Column (
                modifier = Modifier.topBarBlur(hazeState),
            ){
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.FailRate.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.FailRate.icon)
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                        ,
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
            modifier = Modifier
                .hazeSource(hazeState)
                .backDropSource(backdrop)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchIcon() }) {
                FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it },innerPadding,hazeState)
            }
        }
    }
//    }
}

var permit = 1
@Composable
fun ApiToFailRate(
    input : String,
    vm: NetWorkViewModel,
    hazeState: HazeState,
    innerPadding : PaddingValues
) {
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