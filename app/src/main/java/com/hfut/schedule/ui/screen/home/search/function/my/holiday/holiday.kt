package com.hfut.schedule.ui.screen.home.search.function.my.holiday

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.CampusRegion.HEFEI
import com.hfut.schedule.logic.enumeration.CampusRegion.XUANCHENG
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.button.LiquidButton

import com.hfut.schedule.ui.screen.home.getHolidays
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.autoWebVpnForNews
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie
import com.hfut.schedule.ui.screen.news.home.NewsApiScreen
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Holiday(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Holiday.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Holiday.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Holiday.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Holiday,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HolidayScreen(
    navController : NavHostController,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Holiday.route }
    val backdrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = { Text("${DateTimeManager.Date_yyyy}年 ${AppNavRoute.Holiday.label}") },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route,AppNavRoute.Holiday.icon)
                },
                actions = {
                    LiquidButton(
                        backdrop = backdrop,
                        modifier = Modifier
                            .containerShare(
                                AppNavRoute.HolidaySchedule.route,
                                MaterialTheme.shapes.large
                            )
                            .padding(horizontal = APP_HORIZONTAL_DP)
                        ,
                        onClick = {
                            navController.navigateForTransition(AppNavRoute.HolidaySchedule,AppNavRoute.HolidaySchedule.route)
                        },) {
                        Text(AppNavRoute.HolidaySchedule.label)
                    }
                },
                modifier = Modifier.topBarBlur(hazeState)
            )
        },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            HolidayUI(innerPadding)
        }
    }
}

@Composable
fun HolidayUI(innerPadding : PaddingValues) {
    val list by remember { mutableStateOf(getHolidays()) }
    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp)) {
        items(2) { InnerPaddingHeight(innerPadding,true) }
        items(list.size) { index->
            val item = list[index]
            val isOffDay = item.isOffDay
            SmallCard(
                modifier = Modifier.padding(CARD_NORMAL_DP),
                color = if(!isOffDay) MaterialTheme.colorScheme.errorContainer else null
            ) {
                TransplantListItem(
                    headlineContent = { ScrollText(item.name) },
                    overlineContent = { Text(item.date) },
                    trailingContent = { Text( if(isOffDay) "休" else "班" ) },
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) })  {
            BottomTip("数据来源:国务院")
        }
        items(2) { InnerPaddingHeight(innerPadding,false) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayScheduleScreen(
    navController : NavHostController,
    vm : NetWorkViewModel,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.HolidaySchedule.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    CustomTransitionScaffold (
        route = route,
        roundShape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.HolidaySchedule.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.HolidaySchedule.icon)
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            NewsApiScreen(vm,"放假安排",innerPadding)
        }
    }
}
