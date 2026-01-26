package com.hfut.schedule.ui.screen.home.search.function.my.holiday

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.component.button.LiquidButton

import com.hfut.schedule.ui.screen.home.getHolidays
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.news.home.TotalNewsScreen
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

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
                    val toRoute = remember { AppNavRoute.NewsApi.withArgs(AppNavRoute.NewsApi.Keyword.HOLIDAY_SCHEDULE.keyword) }

                    LiquidButton(
                        backdrop = backdrop,
                        modifier = Modifier
                            .containerShare(
                                toRoute,
                                MaterialTheme.shapes.large
                            )
                            .padding(horizontal = APP_HORIZONTAL_DP)
                        ,
                        onClick = {
                            navController.navigateForTransition(AppNavRoute.NewsApi,toRoute)
                        },) {
                        Text("调休通知", maxLines = 1)
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
fun NewsApiScreen(
    navController : NavHostController,
    vm : NetWorkViewModel,
    keyword : String
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.NewsApi.withArgs(keyword) }
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
                    title = { Text(keyword) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.NewsApi.icon)
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
            TotalNewsScreen(vm,keyword,innerPadding)
        }
    }
}
