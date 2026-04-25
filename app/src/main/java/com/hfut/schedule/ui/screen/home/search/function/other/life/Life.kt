package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.destination.LifeDestination
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.nav2Composable
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Life() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = LifeDestination.title.asString()) },
        leadingContent = {
            Icon(painterResource(LifeDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(LifeDestination)
        }
    )
}


enum class LifeBarItems(val page : Int) {
    WEATHER(0),
    CAMPUS(1),
    STARTER(2)
}

private val items = listOf(
    NavigationBarItemData(
        LifeBarItems.CAMPUS.name,
        "校园",
        R.drawable.search,
        R.drawable.search_filled,
    ),
    NavigationBarItemData(
        LifeBarItems.WEATHER.name,
        "天气",
        R.drawable.person,
        R.drawable.person_filled,
    ),
    NavigationBarItemData(
        LifeBarItems.STARTER.name,
        "外部",
        R.drawable.search,
        R.drawable.search_filled,
    )
)


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LifeScreen(
    vm : NetWorkViewModel,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lifeNavController = rememberNavController()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState, ),
                colors = topBarTransplantColor(),
                title = { Text(LifeDestination.title.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
            )
        },
        bottomBar = {
            HazeBottomBar(
                hazeState,
                items,
                lifeNavController,
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = lifeNavController,
            startDestination = LifeBarItems.CAMPUS.name,
            enterTransition = {
                AppAnimationManager.centerAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.centerAnimation.exit
            },
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            nav2Composable(LifeBarItems.CAMPUS.name) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        InnerPaddingHeight(innerPadding,true)
                        CampusMapScreen(vm)
                        InnerPaddingHeight(innerPadding,false)
                    }
                }
            }
            nav2Composable(LifeBarItems.WEATHER.name) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        InnerPaddingHeight(innerPadding,true)
                        WeatherScreen(vm)
                        InnerPaddingHeight(innerPadding,false)
                    }
                }
            }
            nav2Composable(LifeBarItems.STARTER.name) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        InnerPaddingHeight(innerPadding,true)
                        StarterScreen()
                        InnerPaddingHeight(innerPadding,false)
                    }
                }
            }
        }
    }
}