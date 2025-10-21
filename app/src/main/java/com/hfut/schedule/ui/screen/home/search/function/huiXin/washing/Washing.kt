package com.hfut.schedule.ui.screen.home.search.function.huiXin.washing

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Washing(
    vm: NetWorkViewModel,
    hazeState: HazeState,
    navController : NavHostController,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val route = remember { AppNavRoute.HaiLeWashing.route }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            WashingUI(vm,hazeState)
        }
    }

    TransplantListItem(
        headlineContent = { ScrollText(text = "洗衣机") },
        leadingContent = {
            Icon(painterResource(id = R.drawable.local_laundry_service), contentDescription = "")
        },
        trailingContent = {
                FilledTonalIconButton(
                    modifier = Modifier.size(30.dp).containerShare(route),
                    onClick = {
                        navController.navigateForTransition(AppNavRoute.HaiLeWashing,route)
                    },
                ) { Icon( painterResource(R.drawable.search), contentDescription = null, modifier = Modifier.size(20.dp)) }
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun HaiLeWashingScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.HaiLeWashing.route }
        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            roundShape = MaterialTheme.shapes.extraExtraLarge,
            route = route,
            
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.HaiLeWashing.label) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController)
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize().padding(innerPadding)
            ) {
                HaiLeScreen(vm,hazeState)
            }
        }
//    }
}


private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WashingUI(vm : NetWorkViewModel,hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(AppNavRoute.HaiLeWashing.label)
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    HaiLeScreen(vm,hazeState)
                }
            }
        }
    }
    val titles = remember { listOf("合肥","宣城") }

    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampusRegion()) {
            CampusRegion.XUANCHENG -> XUANCHENG_TAB
            CampusRegion.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")
    val route = remember { AppNavRoute.HaiLeWashing.route }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    //布局///////////////////////////////////////////////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("洗衣机", isPaddingStatusBar = false) {
            FilledTonalButton(onClick = {
                showBottomSheet = true
            }) {
                Text(AppNavRoute.HaiLeWashing.label)
            }
        }
        CustomTabRow(pagerState,titles)
        HorizontalPager(state = pagerState) { page ->
            Column() {
                when(page) {
                    HEFEI_TAB -> {
                        CardListItem(
                            headlineContent = { Text("官方充值查询入口") },
                            modifier = Modifier.clickable {
                               scope.launch {
                                   Starter.startWebView(context,url = MyApplication.HUI_XIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.WASHING_HEFEI.code}&name=pays&paymentUrl=${MyApplication.HUI_XIN_URL}plat&token=" + auth, title = "慧新易校")
                               }
                            },
                            trailingContent = {
                                Icon(Icons.Default.ArrowForward,null)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.search),null)
                            },
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    XUANCHENG_TAB -> {
                        EmptyUI("请使用${AppNavRoute.HaiLeWashing.label}")
                    }
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP*3))
            }
        }
    }
}



