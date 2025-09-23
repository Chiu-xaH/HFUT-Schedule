package com.hfut.schedule.ui.screen.home.search.function.community.library.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageIndicator
import com.hfut.schedule.ui.component.screen.pager.PagingController
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryBorrowedScreen(
    vm: NetWorkViewModel,
    navController : NavHostController
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.LibraryBorrowed.route }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.LibraryBorrowed.label) },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route, AppNavRoute.LibraryBorrowed.icon)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            BorrowUI(vm,innerPadding)
        }
    }
}


@Composable
private fun BorrowUI(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues
) {
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork = suspend m@ {
        val token = prefs.getString(LIBRARY_TOKEN,"") ?: return@m
        vm.libraryBorrowedResp.clear()
        vm.getBorrowed(token,page)
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }
    val uiState by vm.libraryBorrowedResp.state.collectAsState()
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        val listState = rememberLazyListState()
        Box() {
            LazyColumn(state = listState) {
                item { InnerPaddingHeight(innerPadding,true) }
                items(list.size, key = { it }) { index ->
                    val item = list[index]
                    with(item) {
                        val detail = libraryDetail.detail
                        CustomCard(color = cardNormalColor(), modifier = Modifier.clickable {

                        }) {
                            TransplantListItem(
                                headlineContent = { Text(detail.title, fontWeight = FontWeight.Bold) },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.book_5), null)
                                },
                                supportingContent = {
                                    Text(location)
                                },
                                overlineContent = {
                                    Text("借于 $createdTime\n还于 $realReturnTime")
                                },
                            )
                            PaddingHorizontalDivider()
                            with(detail) {
                                TransplantListItem(
                                    headlineContent = { Text(publishers + " (${year})") },
                                    overlineContent = { Text("ISBN $isbn\n索书号 $callNo") },
                                    supportingContent = { Text(authors) },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.person), null)
                                    },
                                )
                            }
                        }
                    }
                }
                item { PaddingForPageControllerButton() }
                item { InnerPaddingHeight(innerPadding,false) }
            }
            PagingController(listState,page,false,{ page = it },{ page = it }, modifier = Modifier.padding(innerPadding))
        }
    }
}