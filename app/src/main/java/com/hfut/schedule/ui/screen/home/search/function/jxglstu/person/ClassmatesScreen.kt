package com.hfut.schedule.ui.screen.home.search.function.jxglstu.person

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.component.button.LiquidButton

import com.hfut.schedule.ui.screen.home.getWxAuth
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.style.special.backDropSource
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassmatesScreen(
    vm: NetWorkViewModel,
    navTopController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Classmates.route }
    var nameSort by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()

    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        roundShape = MaterialTheme.shapes.medium,
        route = route,

        navHostController = navTopController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(getPersonInfo().className ?: AppNavRoute.Classmates.label) },
                navigationIcon = {
                    TopBarNavigationIcon(
                        navTopController,
                        route,
                        AppNavRoute.Classmates.icon
                    )
                },
                actions = {
                    LiquidButton(
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                        onClick = {
                            nameSort = !nameSort
                        },
                        backdrop = backdrop
                    ) {
                        Text(
                            if(nameSort)"按姓名" else "按学号"
                        )
                    }
//                    FilledTonalButton(
//                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
//                        onClick = {
//                            nameSort = !nameSort
//                        }
//                    ) {
//                        Text(if(nameSort)"按姓名" else "按学号")
//                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            val uiState by vm.wxClassmatesResponse.state.collectAsState()
            val refreshNetwork = suspend m@ {
                val auth = getWxAuth() ?: return@m
                vm.wxClassmatesResponse.clear()
                vm.wxGetClassmates(auth)
            }
            LaunchedEffect(Unit) {
                if(uiState is UiState.Success) {
                    return@LaunchedEffect
                }
                refreshNetwork()
            }
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val bean = (uiState as UiState.Success).data
                val list = bean.records.sortedBy {
                    if(nameSort){
                        null
                    } else {
                        it.id
                    }
                }
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { list[it].id }) { index ->
                        val item = list[index]
                        with(item) {
                            Box(modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)) {
                                CardListItem(
                                    headlineContent = { Text(name) },
                                    overlineContent = { Text(id) },
                                    supportingContent = {
                                        phone?.let {
                                            if(it.contains("1")) {
                                                Text("电话 $it")
                                            }
                                        }
                                        email?.let {
                                            if (it.contains("@")) {
                                                Text("邮件 $it")
                                            }
                                        }
                                    },
                                    leadingContent = {
                                        FilledTonalIconButton(
                                            onClick = {},
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            if(photoUrl != null && isValidWebUrl(photoUrl)) {
                                                UrlImage(photoUrl)
                                            } else {
                                                Text(name.substring(0,1))
                                            }
                                        }
                                    }
                                )
                            }

                        }
                    }
                    item {
                        BottomTip("共 ${bean.total} 位")
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
//    }
}


