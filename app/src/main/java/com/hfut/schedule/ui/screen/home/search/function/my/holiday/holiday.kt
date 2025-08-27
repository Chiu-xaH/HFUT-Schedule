package com.hfut.schedule.ui.screen.home.search.function.my.holiday

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel

import com.hfut.schedule.ui.screen.home.getHolidays
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.iconElementShare
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Holiday(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.Holiday.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Holiday.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Holiday.icon), contentDescription = null,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = route))
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
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.Holiday.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text("${DateTimeManager.Date_yyyy}年 ${AppNavRoute.Holiday.label}") },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route,AppNavRoute.Holiday.icon)
                    },
                    modifier = Modifier.topBarBlur(hazeState, )
                )
            },
            ) { innerPadding ->
            Column (
                modifier = Modifier.hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                HolidayUI(innerPadding)
            }
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