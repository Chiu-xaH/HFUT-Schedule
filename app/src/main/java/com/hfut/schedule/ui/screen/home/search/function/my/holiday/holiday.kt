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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem

import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.component.button.LiquidButton

import com.hfut.schedule.ui.screen.home.getHolidays
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor

import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.destination.HolidayDestination
import com.hfut.schedule.ui.destination.NewsApiDestination
import com.hfut.schedule.ui.screen.news.home.TotalNewsScreen
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.container.container.SharedContainer
import com.xah.mirror.util.rememberShaderState

import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Holiday() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Holiday.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Holiday.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(HolidayDestination)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HolidayScreen(
//    navController : NavHostController,
) {
    val navController = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val backdrop = rememberLayerBackdrop()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = { Text("${DateTimeManager.Date_yyyy}年 ${stringResource(AppNavRoute.Holiday.label)}") },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    val dest = NewsApiDestination(
                        AppNavRoute.NewsApi.Keyword.HOLIDAY_SCHEDULE.keyword
                    )
                    SharedContainer(
                        key = dest.key,
                        shape = CircleShape,
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        LiquidButton(
                            backdrop = backdrop,
                            shape = RoundedCornerShape(0.dp),
                            onClick = { navController.push(dest) }
                        ) {
                            Text("调休通知")
                        }
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
//    navController : NavHostController,
    vm : NetWorkViewModel,
    keyword : String
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(keyword) },
                    navigationIcon = {
                        TopBarNavigationIcon()
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
