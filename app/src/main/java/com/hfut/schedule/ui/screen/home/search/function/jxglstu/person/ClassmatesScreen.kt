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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.URLImage
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.screen.HazeBlurLevel
import com.hfut.schedule.ui.screen.home.getWxAuth
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassmatesScreen(
    vm: NetWorkViewModel,
    navTopController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.Classmates.route }
    var nameSort by remember { mutableStateOf(true) }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            roundShape = MaterialTheme.shapes.medium,
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navTopController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(getPersonInfo().classes ?: AppNavRoute.Classmates.label) },
                    navigationIcon = {
                        TopBarNavigateIcon(
                            navTopController,
                            animatedContentScope,
                            route,
                            AppNavRoute.Classmates.icon
                        )
                    },
                    actions = {
                        FilledTonalButton(
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                            onClick = {
                                nameSort = !nameSort
                            }
                        ) {
                            Text(if(nameSort)"按姓名" else "按学号")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
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
                                    StyleCardListItem(
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
//                                    trailingContent = {
//                                        photoUrl?.let {
//                                            if(isValidWebUrl(it)) URLImage(it)
//                                        }
//                                    },
                                        leadingContent = { Text((index+1).toString())}
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
    }
}


