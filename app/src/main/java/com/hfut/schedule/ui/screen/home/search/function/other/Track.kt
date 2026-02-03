package com.hfut.schedule.ui.screen.home.search.function.other

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.rememberHazeState

/*
标题 不可空
描述 可空
链接 可空
阶段 评估->设计->开发->测试->发布
负责人 不可空（可多位，用逗号隔开，github用户名）
 */
@Composable
fun Track(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Track.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Track.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Track.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Track,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.Track.route }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(stringResource(AppNavRoute.Track.label)) },
                navigationIcon = {
                    TopBarNavigationIcon(route, AppNavRoute.Track.icon)
                },
            )
        },
    ) { innerPadding ->
        CenterScreen {
            DevelopingIcon()
        }
    }
}