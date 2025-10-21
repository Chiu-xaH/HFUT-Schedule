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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.JxglstuCourseTableUINext
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NextCourse(
    ifSaved : Boolean,
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.NextCourse.receiveRoute() }

    val cookie by produceState(initialValue = "") {
        value = getJxglstuCookie() ?: ""
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.NextCourse.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.NextCourse.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            if (isNextOpen()) {
                if(ifSaved) {
                    if(prefs.getInt("FIRST",0) != 0)
                        navController.navigateForTransition(AppNavRoute.NextCourse,AppNavRoute.NextCourse.withArgs(ifSaved))
                    else refreshLogin(context)
                } else navController.navigateForTransition(AppNavRoute.NextCourse,AppNavRoute.NextCourse.withArgs(ifSaved))
            } else {
                if(!ifSaved) {
                    scope.launch {
                        Starter.startWebView(
                            context,
                            url = if(GlobalUIStateHolder.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table",
                            title = "教务系统",
                            cookie = cookie,
                            icon = AppNavRoute.NextCourse.icon
                        )
                    }
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
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showAll by rememberSaveable { mutableStateOf(false) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var next by remember { mutableStateOf(isNextOpen()) }
    val route = remember { AppNavRoute.NextCourse.receiveRoute() }
        CustomTransitionScaffold (
            route = route,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.NextCourse.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.NextCourse.icon)
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
                JxglstuCourseTableUINext(showAll,vm,vmUI,hazeState,navController,innerPadding,null)
            }
        }
//    }
}