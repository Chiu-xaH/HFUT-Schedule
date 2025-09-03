package com.hfut.schedule.ui.screen.welcome

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.UpdateContents
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VersionInfoScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)

    var showBottomSheetUpdate by remember { mutableStateOf(false) }

    if(showBottomSheetUpdate) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheetUpdate = false },
            showBottomSheet = showBottomSheetUpdate,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("历史更新日志")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    UpdateContents(vm)
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.VersionInfo.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            roundShape = MaterialTheme.shapes.medium,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.VersionInfo.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.VersionInfo.icon)
                    },
                    actions = {
                        FilledTonalButton(onClick = { showBottomSheetUpdate = true }, modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                            Text("历史更新日志")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                VersionInfo()
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}