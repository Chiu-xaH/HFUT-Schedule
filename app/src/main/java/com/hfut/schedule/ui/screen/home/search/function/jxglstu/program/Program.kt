package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBarV2
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.model.NavigationBarItemData
import com.hfut.schedule.ui.nav.destination.AllProgramsDestination
import com.hfut.schedule.ui.nav.destination.ProgramConfirmationDestination
import com.hfut.schedule.ui.nav.destination.ProgramDestination
import com.hfut.schedule.ui.screen.home.search.QuickButtonForSearchScreen
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.nav2Composable
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.currentRouteWithoutArgs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Program(
    ifSaved : Boolean,
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dest = AllProgramsDestination(ifSaved)

    TransplantListItem(
        headlineContent = { ScrollText(text = ProgramDestination.TITLE.asString()) },
        leadingContent = {
            Icon(painterResource(ProgramDestination.ICON), contentDescription = null)
        },
        trailingContent = {
            SharedContainer(
                key = dest.key,
                shape = CircleShape,
                containerColor = IconButtonDefaults.filledTonalIconButtonColors().containerColor
            ) {
                QuickButtonForSearchScreen(
                    shape = NoneRoundShape,
                    onClick = {
                        navController.push(dest)
                    }
                ) {
                    Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
                }
            }
        },
        modifier = Modifier.clickable {
            scope.launch {
                val json = LargeStringDataManager.read(LargeStringDataManager.PROGRAM)

                if (json?.contains("children") == true || !ifSaved) {
                    navController.push(ProgramDestination(ifSaved))
                }
                else refreshLogin(context)
            }
        }
    )
}

private enum class ProgramBarItems(val page : Int) {
    COMPETITION(0),PROGRAM(1)
}

private val items = listOf(
    NavigationBarItemData(
        ProgramBarItems.COMPETITION.name,
        "完成情况",
        R.drawable.leaderboard,
        R.drawable.leaderboard_filled,
    ),
    NavigationBarItemData(
        ProgramBarItems.PROGRAM.name,
        "教学计划",
        R.drawable.conversion_path,
        R.drawable.conversion_path,
    )
)


private const val PAGE_JXGLSTU = 0
private const val PAGE_UNI_APP = 1
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
)
@Composable
fun ProgramScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
) {
    val hazeState = rememberHazeBlur()
    val titles = remember { listOf("教务系统","合工大教务") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pageState = rememberPagerState(
        initialPage = if(ifSaved) PAGE_UNI_APP else PAGE_JXGLSTU
    ) {
        titles.size
    }
    val backDrop = rememberLayerBackdrop()
    val programNavController = rememberNavController()
    val targetPage = when(programNavController.currentRouteWithoutArgs()) {
        ProgramBarItems.COMPETITION.name -> ProgramBarItems.COMPETITION
        else -> ProgramBarItems.PROGRAM
    }
    val navController = LocalNavController.current
    val context = LocalContext.current

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            HazeBottomBarV2(hazeState,backDrop,items,programNavController)
        },
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(ProgramDestination.TITLE.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        when(targetPage) {
                            ProgramBarItems.PROGRAM -> {
                                val dest = AllProgramsDestination(ifSaved)
                                SharedContainer(
                                    key = dest.key,
                                    shape = CircleShape,
                                    modifier = Modifier.padding(end = BUTTON_END_PADDING),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    LiquidButton(
                                        backdrop = backDrop,
                                        shape = NoneRoundShape,
                                        onClick = {
                                            navController.push(dest)
                                        }
                                    ) {
                                        Text(dest.title.asString())
                                    }
                                }
                            }
                            ProgramBarItems.COMPETITION -> {
                                SharedContainer(
                                    key = ProgramConfirmationDestination.key,
                                    shape = CircleShape,
                                    modifier = Modifier.padding(end = BUTTON_END_PADDING),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    LiquidButton(
                                        backdrop = backDrop,
                                        shape = NoneRoundShape,
                                        onClick = {
                                            if (!ifSaved) {
                                                navController.push(ProgramConfirmationDestination)
                                            } else {
                                                refreshLogin(context)
                                            }
                                        }
                                    ) {
                                        Text(ProgramConfirmationDestination.title.asString())
                                    }
                                }
                            }
                        }
                    }
                )
                CustomTabRow(pageState,titles)
            }
        },
    ) { innerPadding ->
        HorizontalPager(state = pageState) { page ->
            Box(
                modifier = Modifier
                    .hazeSource(state = hazeState)
                    .backDropSource(backDrop)
                    .fillMaxSize()
            ) {
                when(page) {
                    PAGE_JXGLSTU -> {
                        NavHost(
                            navController = programNavController,
                            startDestination = ProgramBarItems.COMPETITION.name,
                            enterTransition = {
                                AppAnimationManager.centerAnimation.enter
                            },
                            exitTransition = {
                                AppAnimationManager.centerAnimation.exit
                            }
                        ) {
                            nav2Composable(ProgramBarItems.COMPETITION.name) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    ProgramCompetitionScreenMini(vm,ifSaved,innerPadding)
                                }
                            }
                            nav2Composable(ProgramBarItems.PROGRAM.name) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    ProgramScreenMini(vm,ifSaved,hazeState,innerPadding)
                                }
                            }
                        }
                    }
                    PAGE_UNI_APP -> {
                        NavHost(
                            navController = programNavController,
                            startDestination = ProgramBarItems.COMPETITION.name,
                            enterTransition = {
                                AppAnimationManager.centerAnimation.enter
                            },
                            exitTransition = {
                                AppAnimationManager.centerAnimation.exit
                            }
                        ) {
                            nav2Composable(ProgramBarItems.COMPETITION.name) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    CenterScreen {
                                        DevelopingIcon()
                                    }
                                }
                            }
                            nav2Composable(ProgramBarItems.PROGRAM.name) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    CenterScreen {
                                        DevelopingIcon()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


