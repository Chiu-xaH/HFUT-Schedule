package com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.next.JxglstuCourseTableUINext
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NextCourse(
    ifSaved : Boolean,
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    var showDialogN by remember { mutableStateOf(false) }
    val route = remember { AppNavRoute.NextCourse.receiveRoute() }

    val cookie by produceState(initialValue = "") {
        value = getJxglstuCookie(vm) ?: ""
    }
    WebDialog(
        showDialogN,
        { showDialogN = false },
        url = if(vm.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table",
        title = "教务系统",
        cookie = cookie
    )

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.NextCourse.title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.NextCourse.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            if (isNextOpen()) {
                if(ifSaved) {
                    if(prefs.getInt("FIRST",0) != 0)
                        navController.navigateAndSaveForTransition(AppNavRoute.NextCourse.withArgs(ifSaved))
                    else refreshLogin()
                } else navController.navigateAndSaveForTransition(AppNavRoute.NextCourse.withArgs(ifSaved))
            } else {
                if(!ifSaved) {
                    showDialogN = true
                } else {
                    showToast("入口暂未开放")
                }
            }
        }
    )
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NextCourseScreen(
    vm: NetWorkViewModel,
    vmUI: UIViewModel,
    ifSaved : Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    var showAll by rememberSaveable { mutableStateOf(false) }
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var next by remember { mutableStateOf(isNextOpen()) }
    val route = remember { AppNavRoute.NextCourse.receiveRoute() }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState,useTry = true),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.NextCourse.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.NextCourse.icon)
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            FilledTonalIconButton (onClick = { showAll = !showAll }) {
                                Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                            }
                            CourseTotalForApi(vm=vm, next=next, onNextChange = { next = !next}, hazeState = hazeState, ifSaved = ifSaved)
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                JxglstuCourseTableUINext(showAll,vm,vmUI,hazeState,navController,sharedTransitionScope,animatedContentScope,innerPadding)
            }
        }
    }
}