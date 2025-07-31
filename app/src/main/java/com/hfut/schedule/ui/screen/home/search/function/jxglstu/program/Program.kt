package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Program(
    ifSaved : Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val iconRoute = remember { AppNavRoute.ProgramSearch.receiveRoute() }
    val route = remember { AppNavRoute.Program.receiveRoute() }


    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Program.title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.Program.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        trailingContent = {
            with(sharedTransitionScope) {
                FilledTonalIconButton(
                    onClick = {
                        navController.navigateAndSaveForTransition(AppNavRoute.ProgramSearch.withArgs(ifSaved))
                    },
                    modifier = containerShare(Modifier.size(30.dp),animatedContentScope,iconRoute)
                ) {
                    Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
                }
            }
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                navController.navigateAndSaveForTransition(AppNavRoute.Program.withArgs(ifSaved))
            }
            else refreshLogin()
        }
    )
}



@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProgramScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val route = remember { AppNavRoute.Program.receiveRoute() }
    with(sharedTransitionScope) {
        TransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState,useTry = true),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Program.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Program.icon)
                    },
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                navController.navigateAndSaveForTransition(AppNavRoute.ProgramSearch.withArgs(ifSaved))
                            },
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        ) {
                            Text("全校培养方案")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                sProgramScreen(vm,ifSaved,hazeState)
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}