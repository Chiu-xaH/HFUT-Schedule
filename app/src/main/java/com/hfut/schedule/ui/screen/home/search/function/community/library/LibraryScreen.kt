package com.hfut.schedule.ui.screen.home.search.function.community.library

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PagingController
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.Library.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Library.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.Library.icon)
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            FilledTonalIconButton(
                                onClick = {
                                    navController.navigateForTransition(
                                        AppNavRoute.WebView,
                                        AppNavRoute.WebView.withArgs(
                                        url = MyApplication.NEW_LIBRARY_URL,
                                        title = "图书馆",
                                        icon = R.drawable.net,
                                    ),transplantBackground = true)
                                }
                            ) {
                                Icon(
                                    painterResource(R.drawable.net),
                                    contentDescription = null,
                                    modifier = Modifier.iconElementShare(
                                        sharedTransitionScope,
                                        animatedContentScope = animatedContentScope,
                                        route = AppNavRoute.WebView.shareRoute(MyApplication.NEW_LIBRARY_URL)
                                    )
                                )
                            }
                            Spacer(Modifier.width(CARD_NORMAL_DP))
                            val seatTitle = remember { "座位预约" }
                            val seatUrl = remember { MyApplication.LIBRARY_SEAT + "home/web/f_second" }
                            FilledTonalButton(
                                onClick = {
                                    navController.navigateForTransition(
                                        AppNavRoute.WebView,
                                        AppNavRoute.WebView.withArgs(
                                            url = seatUrl,
                                            title = seatTitle,
                                        ))
                                },
                                modifier = Modifier.containerShare(sharedTransitionScope,animatedContentScope=animatedContentScope, route = AppNavRoute.WebView.shareRoute(seatUrl))
                            ) {
                                Text(text = seatTitle)
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                BooksUI(vm,hazeState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksUI(vm: NetWorkViewModel, hazeState: HazeState) {
    var startUse by remember { mutableStateOf(false) }

    var input by remember { mutableStateOf( "") }
    var title by remember { mutableStateOf("地点") }
    var callNum by remember { mutableStateOf<String>("") }
    var page by remember { mutableIntStateOf(1) }
    val uiState by vm.libraryData.state.collectAsState()
    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.libraryData.clear()
            vm.searchBooks(it,input,page)
            startUse = true
        }
    }

    LaunchedEffect(Unit) {
        vm.libraryData.emitPrepare()
    }
    LaunchedEffect(page) {
        if(startUse)
            refreshNetwork()
    }
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            autoShape = false
        ) {
            Column {
                HazeBottomSheetTopBar(title, isPaddingStatusBar = false)
                DetailBookUI(vm,callNum)
            }
        }
    }

    Column {
        LibraryChips(vm,hazeState)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = APP_HORIZONTAL_DP),
                value = input,
                onValueChange = {
                    input = it
                },
                label = { Text("搜索图书" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            scope.launch { refreshNetwork() }
                        }) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
            val books = (uiState as UiState.Success).data
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    items (books.size){ index ->
                        val item = books[index]
                        val name = item.name
                        val callNo = item.callNumber
                        AnimationCustomCard(
                            containerColor = cardNormalColor(),
                            index = index,
                            modifier = Modifier.clickable {
                                title = name
                                callNum = callNo
                                showBottomSheet = true
                            }
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(text = name,fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(text = "索书号 $callNo") },
                                leadingContent = {
                                    Icon(
                                        painterResource(AppNavRoute.Library.icon),
                                        contentDescription = "Localized description",
                                    )
                                }
                            )
                            PaddingHorizontalDivider()
                            TransplantListItem(
                                headlineContent = { item.author?.let { Text(text = it) } },
                                supportingContent = {Text(text = item.year +  "  " + item.publisher) },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.person),
                                        contentDescription = "Localized description",
                                    )
                                }
                            )
                        }
                    }
                    item { PaddingForPageControllerButton() }
                }
                PagingController(listState,page,nextPage = { page = it }, previousPage = { page = it })
            }
        }
    }
}


@Composable
fun DetailBookUI(vm: NetWorkViewModel, callNo : String) {
    val uiState by vm.bookPositionData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.bookPositionData.clear()
            vm.getBookPosition(it,callNo)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    DividerTextExpandedWith("索书号 $callNo") {
        CommonNetworkScreen(uiState, onReload = refreshNetwork,isFullScreen = false) {
            val list = (uiState as UiState.Success).data
            LazyColumn {
                items(list.size) { index ->
                    val item = list[index]
                    val status = item.status_dictText
                    CardListItem(
                        headlineContent = {
                            Text(item.place)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.near_me),null)
                        },
                        trailingContent = {
                            Text(status)
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}