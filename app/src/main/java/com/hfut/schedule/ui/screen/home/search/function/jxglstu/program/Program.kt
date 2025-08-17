package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.screen.HazeBlurLevel
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.style.containerBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
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
        headlineContent = { ScrollText(text = AppNavRoute.Program.label) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.Program.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        trailingContent = {
            with(sharedTransitionScope) {
                FilledTonalIconButton(
                    onClick = {
                        navController.navigateForTransition(AppNavRoute.ProgramSearch,AppNavRoute.ProgramSearch.withArgs(ifSaved))
                    },
                    modifier = containerShare(Modifier.size(30.dp),animatedContentScope,iconRoute)
                ) {
                    Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
                }
            }
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                navController.navigateForTransition(AppNavRoute.Program,AppNavRoute.Program.withArgs(ifSaved))
            }
            else refreshLogin()
        }
    )
}


private const val PAGE_COMPETITION = 0
private const val PAGE_PROGRAM = 1
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ProgramScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.Program.receiveRoute() }
    val titles = remember { listOf("完成情况","教学计划") }
    val pageState = rememberPagerState(
        initialPage = if(prefs.getString("PROGRAM_COMPETITION","") != null) PAGE_COMPETITION else PAGE_PROGRAM
    ) { titles.size }
    val competitionRoute = remember { AppNavRoute.ProgramCompetition.receiveRoute() }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            roundShape = MaterialTheme.shapes.extraExtraLarge,
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            bottomBar = {
                AnimatedVisibility(
                    visible = pageState.currentPage == PAGE_COMPETITION,
                    exit = AppAnimationManager.toBottomAnimation.exit,
                    enter = AppAnimationManager.toBottomAnimation.enter
                ) {
                    Column (modifier = Modifier.bottomBarBlur(hazeState).navigationBarsPadding()) {
                        with(sharedTransitionScope) {
                            LargeButton(
                                iconModifier = iconElementShare(animatedContentScope=animatedContentScope, route = competitionRoute),
                                onClick = {
                                    if(prefs.getString("PROGRAM_PERFORMANCE","")?.contains("children") == true || !ifSaved) navController.navigateForTransition(AppNavRoute.ProgramCompetition,AppNavRoute.ProgramCompetition.withArgs(ifSaved))
                                    else refreshLogin()
                                },
                                icon = AppNavRoute.ProgramCompetition.icon,
                                text = AppNavRoute.ProgramCompetition.label,
                                shape = MaterialTheme.shapes.large,
                                modifier = containerShare(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(APP_HORIZONTAL_DP),
                                    animatedContentScope,
                                    competitionRoute,
                                    roundShape = MaterialTheme.shapes.large,
                                ),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.75f),
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            },
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    TopAppBar(
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.Program.label) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Program.icon)
                        },
                        actions = {
                            FilledTonalButton(
                                onClick = {
                                    navController.navigateForTransition(AppNavRoute.ProgramSearch,AppNavRoute.ProgramSearch.withArgs(ifSaved))
                                },
                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                            ) {
                                Text("全校培养方案")
                            }
                        }
                    )
                    CustomTabRow(pageState,titles)
                }
            },
        ) { innerPadding ->
            HorizontalPager(state = pageState) { page ->
                Column(
                    modifier = Modifier
                        .hazeSource(hazeState)
                        .fillMaxSize()
                ) {
                    when(page) {
                        PAGE_PROGRAM -> {
                            ProgramScreenMini(vm,ifSaved,hazeState,innerPadding)
                        }
                        PAGE_COMPETITION -> {
                            ProgramCompetitionScreenMini(vm,ifSaved,innerPadding)
                        }
                    }
                }
            }
        }
    }
}


