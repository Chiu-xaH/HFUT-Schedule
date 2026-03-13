package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.destination.AllProgramsDestination
import com.hfut.schedule.ui.destination.ProgramCompetitionDestination
import com.hfut.schedule.ui.destination.ProgramDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.container.container.SharedContainer
import com.xah.container.container.sharedContainer
import com.xah.mirror.util.rememberShaderState
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
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
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Program.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Program.icon), contentDescription = null)
        },
        trailingContent = {
            SharedContainer(
                key = dest.key,
                shape = CircleShape,
                containerColor = IconButtonDefaults.filledTonalIconButtonColors().containerColor
            ) {
                NoPadding {
                    FilledTonalIconButton(
                        shape = RoundedCornerShape(0.dp),
                        onClick = {
                            navController.push(dest)
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
                    }
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


private const val PAGE_COMPETITION = 0
private const val PAGE_PROGRAM = 1
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
)
@Composable
fun ProgramScreen(
    vm: NetWorkViewModel,
    ifSaved: Boolean,
) {
    val navController = LocalNavController.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val titles = remember { listOf("完成情况","教学计划") }
    val pageState = rememberPagerState(
        initialPage = if(prefs.getString("PROGRAM_COMPETITION","") != null) PAGE_COMPETITION else PAGE_PROGRAM
    ) { titles.size }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val backDrop = rememberLayerBackdrop()
    val scope = rememberCoroutineScope()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            val dest = ProgramCompetitionDestination(ifSaved)
            AnimatedVisibility(
                visible = pageState.currentPage == PAGE_COMPETITION,
                exit = AppAnimationManager.toBottomAnimation.exit,
                enter = AppAnimationManager.toBottomAnimation.enter
            ) {
                Column (modifier = Modifier.bottomBarBlur(hazeState).navigationBarsPadding()) {
                    NoPadding {
                        LargeButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(APP_HORIZONTAL_DP)
                                .sharedContainer(
                                    dest.key,
                                    MaterialTheme.shapes.large,
                                    MaterialTheme.colorScheme.surfaceVariant
                                ),
                            onClick = {
                                scope.launch {
                                    val json = LargeStringDataManager.read(LargeStringDataManager.PROGRAM_PERFORMANCE)
                                    if(json?.contains("children") == true || !ifSaved) navController.push(dest)
                                    else refreshLogin(context)
                                }
                            },
                            icon = AppNavRoute.ProgramCompetition.icon,
                            text = stringResource(AppNavRoute.ProgramCompetition.label),
                            shape = RoundedCornerShape(0.dp),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.Program.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        val dest = AllProgramsDestination(ifSaved)
                        SharedContainer(
                            key = dest.key,
                            shape = CircleShape,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            LiquidButton (
                                shape = RoundedCornerShape(0.dp),
                                backdrop = backDrop,
                                onClick = {
                                    navController.push(dest)
                                },
                            ) {
                                Text("全校培养方案")
                            }
                        }
                    }
                )
                CustomTabRow(pageState,titles)
            }
        },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            HorizontalPager(state = pageState) { page ->
                Column(
                ) {
                    when(page) {
                        PAGE_PROGRAM -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ProgramScreenMini(vm,ifSaved,hazeState,innerPadding)
                            }
                        }
                        PAGE_COMPETITION -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ProgramCompetitionScreenMini(vm,ifSaved,innerPadding)
                            }
                        }
                    }
                }
            }
        }
    }
}


