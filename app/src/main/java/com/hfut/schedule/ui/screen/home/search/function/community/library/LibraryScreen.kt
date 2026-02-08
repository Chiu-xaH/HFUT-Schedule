package com.hfut.schedule.ui.screen.home.search.function.community.library

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.model.library.BorrowedStatus
import com.hfut.schedule.logic.model.library.LibrarySearchPositionBean
import com.hfut.schedule.logic.model.library.LibraryStatus
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigation.navigateForBottomBar
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

enum class LibraryBarItems(val page : Int) {
    SEARCH(0),MINE(2)
}

private val items = listOf(
    NavigationBarItemData(
        LibraryBarItems.MINE.name,
        "我",
        R.drawable.person,
        R.drawable.person_filled,
    ),
    NavigationBarItemData(
        LibraryBarItems.SEARCH.name,
        "搜索",
        R.drawable.search,
        R.drawable.search_filled,
    )
)

private const val TAB_PAGE_COMMUNITY = 0
private const val TAB_PAGE_LIBRARY = 1

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
) {
    val libraryNavController = rememberNavController()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Library.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(libraryNavController.currentRouteWithoutArgs()) {
        LibraryBarItems.SEARCH.name ->LibraryBarItems.SEARCH
//        LibraryBarItems.SEARCH_BOOK.name -> LibraryBarItems.SEARCH_BOOK
        else -> LibraryBarItems.MINE
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }
    var inputKeyword by remember { mutableStateOf("") }
    val backDrop = rememberLayerBackdrop()
    val scope = rememberCoroutineScope()
    var pageState = rememberPagerState { 2 }
    val titles = remember { listOf("智慧社区","斛兵知搜") }
    val refreshNetworkCommunity : suspend (Int) -> Unit = { page ->
        prefs.getString("TOKEN","")?.let {
            vm.libraryData.clear()
            vm.searchBooks(it,inputKeyword,page)
        }
    }
    val refreshNetworkLibrary : suspend (Int) -> Unit = { page ->
        vm.librarySearchResp.clear()
        vm.searchLibrary(inputKeyword,page)
    }
    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.Library.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon(route, AppNavRoute.Library.icon)
                    },
                )
                if(targetPage != LibraryBarItems.MINE) {
                    CustomTabRow(pageState,titles)
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backDrop, MaterialTheme.shapes.medium),
                        input = inputKeyword,
                        label = { Text(
                            when(pageState.currentPage) {
                                TAB_PAGE_LIBRARY -> {
                                    "搜索馆藏与电子书"
                                }
                                else -> {
                                    "搜索馆藏"
                                }
                            }
                        ) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        when(pageState.currentPage) {
                                            TAB_PAGE_COMMUNITY -> {
                                                refreshNetworkCommunity(1)
                                            }
                                            TAB_PAGE_LIBRARY -> {
                                                refreshNetworkLibrary(1)
                                            }
                                        }
                                    }
                                }) {
                                Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                            }
                        },
                    ) {
                        inputKeyword = it
                    }
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                }
            }
        },
        bottomBar = {
            HazeBottomBar(hazeState, items,libraryNavController)
        }
    ) { innerPadding ->
        val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

        NavHost(navController = libraryNavController,
            startDestination = LibraryBarItems.MINE.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
                .hazeSource(state = hazeState)
                .backDropSource(backDrop)
        ) {
            composable(LibraryBarItems.SEARCH.name) {
                Column (modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(pageState) { pager ->
                        when(pager) {
                            TAB_PAGE_COMMUNITY -> SearchScreenCommunity(vm,hazeState,innerPadding,refreshNetworkCommunity)
                            TAB_PAGE_LIBRARY -> SearchScreenLibrary(vm,innerPadding,hazeState,refreshNetworkLibrary)
                        }
                    }
                }
            }
            composable(LibraryBarItems.MINE.name) {
                Column (modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)) {
                    LibraryMineUI(
                        vm,
                        innerPadding,
                        libraryNavController,
                        navController
                    )
                }
            }
        }
    }
}
private const val seatUrl = MyApplication.LIBRARY_SEAT + "home/web/f_second"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenCommunity(
    vm: NetWorkViewModel,
    hazeState: HazeState,
    innerPadding : PaddingValues,
    refreshNetwork : suspend (Int) -> Unit
) {
    var startUse by rememberSaveable { mutableStateOf(false) }
    var title by remember { mutableStateOf("地点") }
    var callNum by remember { mutableStateOf<String>("") }
    var page by remember { mutableIntStateOf(1) }
    val uiState by vm.libraryData.state.collectAsState()

    LaunchedEffect(page) {
        if(startUse == false) {
            vm.libraryData.emitPrepare()
            startUse = true
        } else if(uiState !is UiState.Success) {
            refreshNetwork(page)
        }
    }

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

    CommonNetworkScreen(uiState, onReload = { refreshNetwork(page) }, prepareContent = { PrepareSearchIcon() }) {
        val books = (uiState as UiState.Success).data
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                item { InnerPaddingHeight(innerPadding,true) }
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
                            supportingContent = {Text(text = "${item.year}  ${item.publisher}" ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.person),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                }
                item { InnerPaddingHeight(innerPadding,false) }
                item { PaddingForPageControllerButton() }
            }
            PageController(listState,page,onNextPage = { page = it }, onPreviousPage = { page = it }, modifier = Modifier.padding(innerPadding), paddingBottom = false)
        }
    }
}


@Composable
private fun DetailBookUI(vm: NetWorkViewModel, callNo : String) {
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

// 加入下拉刷新
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterialApi::class)
@Composable
fun LibraryMineUI(
    vm: NetWorkViewModel,
    innerPadding: PaddingValues,
    libraryNavController : NavHostController,
    navController: NavHostController,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var libraryStatusCode by rememberSaveable { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        if(libraryStatusCode == null) {
            libraryStatusCode = vm.checkLibraryNetwork()
        }
    }
    val uiState by vm.libraryStatusResp.state.collectAsState()

    val refreshNetwork : suspend(Boolean) -> Unit =  m@ { skip : Boolean ->
        if(skip && uiState is UiState.Success) {
            return@m
        }
        val token = prefs.getString(LIBRARY_TOKEN,"")
        token?.let {
            vm.libraryStatusResp.clear()
            vm.getLibraryStatus(it)
            val pageSize = (vm.libraryStatusResp.state.value as? UiState.Success)?.data?.borrowCount ?: return@m
            vm.libraryBorrowedResp.clear()
            vm.getBorrowed(token,1,null,pageSize)
        }
    }
    val uiStateBorrowed by vm.libraryBorrowedResp.state.collectAsState()
    val list = (uiStateBorrowed as? UiState.Success)?.data?.sortedByDescending { it.createdTime }
    val analysis = list?.map { l ->
        BorrowedStatus.entries.find { e ->
            e.status == l.status
        }
    }

    val overdueCount = analysis?.filter { it == BorrowedStatus.OVERDUE }?.size
    val borrowingCount = analysis?.filter { it == BorrowedStatus.BORROWING }?.size
    val latestReturnedData = list
        ?.filter { it.returnTime != null }
        ?.minByOrNull { it.returnTime!! }

    LaunchedEffect(Unit) {
        if(uiState is UiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork(false)
    }

    val loading = uiState !is UiState.Success
    val response = (uiState as? UiState.Success)?.data ?: LibraryStatus()
    val refreshing = uiState is UiState.Loading
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork(false)
        }
    })
    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        RefreshIndicator(refreshing, pullRefreshState, Modifier
            .align(Alignment.TopCenter)
            .zIndex(1f)
            .padding(innerPadding))
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            InnerPaddingHeight(innerPadding,true)
            DividerTextExpandedWith("状态",openBlurAnimation = false) {
                LoadingLargeCard (
                    loading = loading,
                    prepare = false,
                    title =
                           when {
                                overdueCount == null || borrowingCount == null -> "待归还 -本"
                                overdueCount == 0 && borrowingCount == 0 -> "无待归还书籍"
                                overdueCount == 0 && borrowingCount != 0 -> "借阅中 ${borrowingCount}本"
                                overdueCount != 0 && borrowingCount == 0 -> "逾期 ${overdueCount}本"
                                else -> "待归还 ${overdueCount + borrowingCount}本"
                           },
                    rightTop = {
                        latestReturnedData?.returnTime?.substringBefore(" ")?.let { Text("${DateTimeManager.daysBetween(it)}天后") }
                    }
                ) {
                    Row() {
                        TransplantListItem(
                            overlineContent = { Text("借阅") },
                            headlineContent = { Text("${response.borrowCount}本") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.book_5),null, modifier = Modifier.iconElementShare(AppNavRoute.LibraryBorrowed.route))
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable {
                                    if (!loading) {
                                        navController.navigateForTransition(
                                            AppNavRoute.LibraryBorrowed,
                                            AppNavRoute.LibraryBorrowed.route,
                                            transplantBackground = true
                                        )
                                    }
                                }
                        )
                        if(latestReturnedData != null){
                            TransplantListItem(
                                overlineContent = { Text(latestReturnedData.libraryDetail.detail.title, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                                headlineContent = { ScrollText("${latestReturnedData.returnTime?.substringBefore(" ")}") },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.send_money),null)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .clickable {
                                        if (!loading) {
                                            navController.navigateForTransition(
                                                AppNavRoute.LibraryBorrowed,
                                                AppNavRoute.LibraryBorrowed.route,
                                                transplantBackground = true
                                            )
                                        }
                                    }
                            )
                        } else {
                            TransplantListItem(
                                overlineContent = { Text("预约") },
                                headlineContent = { Text("${response.reserveCount}本") },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.schedule),null)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .clickable {
                                        if (!loading) {
                                            showToast("正在开发")
                                        }
                                    }
                            )
                        }
                    }
                    Row {
                        TransplantListItem(
                            overlineContent = { Text("收藏") },
                            headlineContent = { Text("${response.collectCount}条") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.bookmark),null)
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable {
                                    if (!loading) {
                                        showToast("正在开发")
                                    }
                                }
                        )
                        TransplantListItem(
                            overlineContent = { Text("书架") },
                            headlineContent = { Text("${response.bookShelfCount}本") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.newsstand),null)
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .clickable {
                                    if (!loading) {
                                        showToast("正在开发")
                                    }
                                }
                        )
                    }
                }

            }
            DividerTextExpandedWith("选项") {
                CustomCard(color = MaterialTheme.colorScheme.surface) {
                    TransplantListItem(
                        supportingContent = {
                            Text("搜索图书馆中的纸质书本")
                        },
                        headlineContent = {
                            Text("搜索")
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.search),null)
                        },
                        modifier = Modifier.clickable {
                            libraryNavController.navigateForBottomBar(LibraryBarItems.SEARCH.name)
                        }
                    )
//                    PaddingHorizontalDivider()
//                    TransplantListItem(
//                        headlineContent = {
//                            Text("斛兵知搜")
//                        },
//                        supportingContent = {
//                            Text("搜索电子图书馆中的所有资料")
//                        },
//                        leadingContent = {
//                            Icon(painterResource(R.drawable.search),null)
//                        },
//                        modifier = Modifier.clickable {
//                            libraryNavController.navigateForBottomBar(LibraryBarItems.SEARCH_ALL.name)
//                        }
//                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("更多")
                        },
                        supportingContent = {
                            Text("新图书馆官网(有时需校园网)")
                        },
                        colors = MaterialTheme.colorScheme.surface,
                        leadingContent = {
                            Icon(painterResource(R.drawable.globe_book),null, modifier = Modifier.iconElementShare(AppNavRoute.WebView.shareRoute(MyApplication.NEW_LIBRARY_URL)))
                        },
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    Starter.startWebView(
                                        navController,
                                        url = MyApplication.NEW_LIBRARY_URL,
                                        title = "图书馆",
                                        icon = R.drawable.globe_book,
                                    )
                                }
                            }
                            .containerShare(AppNavRoute.WebView.shareRoute(MyApplication.NEW_LIBRARY_URL))
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("续借、预约等服务")
                        },
                        supportingContent = {
                            Text("旧图书馆官网(需校园网)")
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.net),null, modifier = Modifier.iconElementShare(AppNavRoute.WebView.shareRoute(MyApplication.OLD_LIBRARY_URL)))
                        },
                        colors = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    Starter.startWebView(
                                        navController,
                                        url = MyApplication.OLD_LIBRARY_URL,
                                        title = "图书馆",
                                        icon = R.drawable.net,
                                    )
                                }
                            }
                            .containerShare(AppNavRoute.WebView.shareRoute(MyApplication.OLD_LIBRARY_URL))
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("座位预约")
                        },
                        supportingContent = {
                            Text("合肥校区(需校园网)")
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.table_restaurant),null, modifier = Modifier.iconElementShare(AppNavRoute.WebView.shareRoute(seatUrl)))
                        },
                        colors = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    Starter.startWebView(
                                        navController,
                                        url = seatUrl,
                                        title = "座位预约",
                                        icon = R.drawable.table_restaurant
                                    )
                                }
                            }
                            .containerShare(AppNavRoute.WebView.shareRoute(seatUrl))
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("研讨间预约")
                        },
                        supportingContent = {
                            Text("合肥&宣城校区(需校园网)(应前往图书馆线下预约)")
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.meeting_room),null, modifier = Modifier.iconElementShare(AppNavRoute.WebView.shareRoute(MyApplication.MEETING_ROOM_URL)))
                        },
                        colors = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    Starter.startWebView(
                                        navController,
                                        url = MyApplication.MEETING_ROOM_URL,
                                        title = "研讨间预约",
                                        icon = R.drawable.meeting_room
                                    )
                                }
                            }
                            .containerShare(AppNavRoute.WebView.shareRoute(MyApplication.MEETING_ROOM_URL))
                    )
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}

// TODO 加入书架、收藏
@Composable
private fun SearchScreenLibrary(
    vm: NetWorkViewModel,
    innerPadding: PaddingValues,
    hazeState: HazeState,
    refreshNetwork : suspend (Int) -> Unit
) {
    var startUse by remember { mutableStateOf(false) }
    var page by remember { mutableIntStateOf(1) }
    val uiState by vm.librarySearchResp.state.collectAsState()

    LaunchedEffect(page) {
        if(startUse == false) {
            vm.librarySearchResp.emitPrepare()
            startUse = true
        } else if(uiState !is UiState.Success) {
            refreshNetwork(page)
        }
    }
    var detailBean: List<LibrarySearchPositionBean>? by remember { mutableStateOf(null) }
    var title by remember { mutableStateOf("详情") }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet && detailBean != null) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            autoShape = false
        ) {
            Column {
                HazeBottomSheetTopBar(title, isPaddingStatusBar = false)
                LazyColumn {
                    items(detailBean!!.size,key = { it }) { index ->
                        val item = detailBean!![index]
                        CardListItem(
                            headlineContent = {
                                Text(item.`in`)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.near_me),null)
                            },
                            supportingContent = {
                                Text("索书号 ${item.cp}")
                            },
                            trailingContent = {
                                item.js?.let { Text(it) }
                            }
                        )
                    }
                    item {
                        Spacer(Modifier
                            .height(APP_HORIZONTAL_DP)
                            .navigationBarsPadding())
                    }
                }
            }
        }
    }

    CommonNetworkScreen(uiState, onReload = { refreshNetwork(page) }, prepareContent = { PrepareSearchIcon() }) {
        val books = (uiState as UiState.Success).data
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                item { InnerPaddingHeight(innerPadding,true) }
                items (books.size){ index ->
                    val item = books[index]

                    CustomCard(
                        color = cardNormalColor(),
                        modifier = Modifier.clickable {
                            detailBean = item.gc
                            showBottomSheet = true
                        }
                    ) {
                        Column {
                            TransplantListItem(
                                headlineContent = {
                                    Text(item.title.removeHtmlTags())
                                },
                                supportingContent = {
                                    Text("ISBN ${item.isbn}")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.book_5),null)
                                }
                            )
                            PaddingHorizontalDivider()
                            TransplantListItem(
                                headlineContent = {
                                    Text(item.author.joinToString(","))
                                },
                                supportingContent = {
                                    Text("${item.year}" + (item.publishers?.let { " $it" } ?: ""))
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.person),null)
                                }
                            )
                            PaddingHorizontalDivider()
                            TransplantListItem(
                                headlineContent = {
                                    Text("${item.ds?.joinToString(","){ it.tName }}")
                                },
                                supportingContent = {
                                    item.abstract?.removeHtmlTags()?.let { Text(it) }
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.info),null)
                                }
                            )
                            CardBottomButtons(listOf(
                                CardBottomButton(
                                    show = true,
                                    text = "加入书架",
                                    clickable = {
                                        showDevelopingToast()
                                    }
                                ),
                                CardBottomButton(
                                    show = true,
                                    text = "收藏",
                                    clickable = {
                                        showDevelopingToast()
                                    }
                                ),
                                CardBottomButton(
                                    show = true,
                                    text = "阅读",
                                    clickable = {
                                        showDevelopingToast()
                                    }
                                ),
                                CardBottomButton(
                                    show = true,
                                    text = "预约",
                                    clickable = {
                                        showDevelopingToast()
                                    }
                                ),
                                CardBottomButton(
                                    show = true,
                                    text = "详情",
                                    clickable = {
                                        detailBean = item.gc
                                        showBottomSheet = true
                                    }
                                ),
                            ))
                        }
                    }
                }
                item { InnerPaddingHeight(innerPadding,false) }
                item { PaddingForPageControllerButton() }
            }
            PageController(listState,page,onNextPage = { page = it }, onPreviousPage = { page = it }, modifier = Modifier.padding(innerPadding), paddingBottom = false)
        }
    }
}


fun String.removeHtmlTags(): String = this.replace("<[^>]*>".toRegex() , "")
