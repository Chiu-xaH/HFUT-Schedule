package com.hfut.schedule.ui.screen.home.search.function.school

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun SecondClass(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.SecondClass.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.SecondClass.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.SecondClass.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.SecondClass,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecondClassScreen(
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.SecondClass.route }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.SecondClass.label) },
                navigationIcon = {
                    TopBarNavigationIcon(route, AppNavRoute.SecondClass.icon)
                },
            )
        },
    ) { innerPadding ->
        CenterScreen {
            DevelopingIcon()
        }
    }
}