package com.hfut.schedule.ui.screen.other

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.xah.transition.component.TopBarNavigateIcon
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NavigationExceptionScreen(
    exception: String,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Exception.receiveRoute() }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Exception.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Exception.icon)
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).hazeSource(hazeState).fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                Text(exception)
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}