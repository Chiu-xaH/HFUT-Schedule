package com.hfut.schedule.ui.screen.news.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.NewsBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.xah.uicommon.style.padding.NavigationBarSpacer
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.autoWebVpnForNews
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie
import com.hfut.schedule.ui.screen.news.academic.AcademicTotalScreen
import com.hfut.schedule.ui.screen.news.academic.AcademicXCScreen
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.screen.news.xuancheng.XuanquNewsUI
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigation.navigateForBottomBar
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.util.currentRouteWithoutArgs
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import org.json.JSONArray
private val items = listOf(
    NavigationBarItemData(
        NewsBarItems.News.name,"通知公告", R.drawable.star,R.drawable.star_filled
    ),
    NavigationBarItemData(
        NewsBarItems.Academic.name,"教务处", R.drawable.star_half,R.drawable.star_filled
    ),
    NavigationBarItemData(
        NewsBarItems.School.name,"各级学院", R.drawable.school,R.drawable.school_filled
    )
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    vm: NetWorkViewModel,
    navTopController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.News.route }

    val navController = rememberNavController()
    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        NewsBarItems.News.name -> NewsBarItems.News
        NewsBarItems.Academic.name -> NewsBarItems.Academic
        NewsBarItems.School.name -> NewsBarItems.School
        else -> NewsBarItems.News
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val newsTitles = listOf("总","宣城校区")
    val newsPagerState = rememberPagerState(pageCount = { newsTitles.size })
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet ) {
        val cookies by produceState<String?>(initialValue = null) {
            value = getWebVpnCookie(vm)
        }
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            isFullExpand = true,
            autoShape = false,
            showBottomSheet = showBottomSheet
        ) {
            Column(){
                HazeBottomSheetTopBar("选择校区", isPaddingStatusBar = false)

                CardListItem(
                    headlineContent = {
                        Text("宣城校区教务处")
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            autoWebVpnForNews(context,MyApplication.XC_ACADEMIC_URL, title = "宣城校区教务处", cookie = cookies)
                        }
                    }
                )
                CardListItem(
                    headlineContent = {
                        Text("总教务处")
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebView(context,MyApplication.ACADEMIC_URL, title = "总教务处", cookie = cookies)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        CustomTransitionScaffold (
            route = route,
            
            navHostController = navTopController,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(
                    Modifier.topBarBlur(hazeState)
                ) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.News.label) },
                        navigationIcon = {
                            TopBarNavigationIcon(
                                navTopController,
                                route,
                                AppNavRoute.News.icon
                            )
                        },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                if (targetPage == NewsBarItems.Academic) {
                                    FilledTonalIconButton(
                                        onClick = {
                                            showBottomSheet = true
                                        }
                                    ) {
                                        Icon(painterResource(R.drawable.net), null)
                                    }
                                }
                            }
                        }
                    )
                    if (targetPage != NewsBarItems.School) {
                        CustomTabRow(
                            pagerState = newsPagerState,
                            titles = newsTitles
                        )
                    }
                }
            },
            bottomBar = {
                HazeBottomBar(hazeState,items,navController)
            }
        ) { innerPadding ->
            val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

            NavHost(navController = navController,
                startDestination = NewsBarItems.News.name,
                enterTransition = { animation.enter },
                exitTransition = { animation.exit },
                modifier = Modifier.hazeSource(state = hazeState)
            ) {
                composable(NewsBarItems.News.name) {
                    NewsScreenMini(innerPadding,vm,newsPagerState)
                }
                composable(NewsBarItems.Academic.name) {
                    AcademicScreen(innerPadding,vm,newsPagerState)
                }
                composable(NewsBarItems.School.name) {
                    SchoolsUI(innerPadding)
                }
            }
        }
//    }
}

private const val TAB_TOTAL = 0
private const val TAB_XC = 1
@Composable
fun NewsScreenMini(innerPadding : PaddingValues,vm : NetWorkViewModel,pagerState : PagerState) {
    HorizontalPager(state = pagerState) { page ->
        when(page) {
            TAB_TOTAL -> NewsUI(innerPadding,vm)
            TAB_XC -> XuanquNewsUI(innerPadding, vm)
        }
    }
}
@Composable
fun AcademicScreen(innerPadding : PaddingValues,vm : NetWorkViewModel,pagerState : PagerState) {
    HorizontalPager(state = pagerState) { page ->
        when(page) {
            TAB_TOTAL -> AcademicTotalScreen(innerPadding,vm)
            TAB_XC -> AcademicXCScreen(innerPadding,vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsUI(innerPadding : PaddingValues,vm : NetWorkViewModel) {
    val cookies by produceState<String?>(initialValue = null) {
        value = getWebVpnCookie(vm)
    }
    val context = LocalContext.current

    val words = remember { listOf(
        "放假","转专业","周考试安排","选课"
    ) }
    var input by remember { mutableStateOf( words[0]) }
    var page by remember { mutableIntStateOf(1) }

    val uiState by vm.newsResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.newsResult.clear()
        vm.searchNews(input, page)
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }
    val scope = rememberCoroutineScope()

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        val listState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                item {
                    InnerPaddingHeight(innerPadding,true)
                    LazyRow(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                        items(words.size) { index ->
                            val word = words[index]
                            AssistChip(onClick = {
                                input = word
                                scope.launch { refreshNetwork() }
                            }, label = { Text(text = word) })
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
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
                            label = { Text("搜索通知或新闻") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        scope.launch { refreshNetwork() }
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = "description"
                                    )
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }
                items(list.size, key = { it }){ item ->
                    val listItem = list[item]
                    AnimationCardListItem(
                        overlineContent = { Text(text = listItem.date) },
                        headlineContent = { Text(listItem.title) },
                        leadingContent = { Text(text = (item + 1).toString()) },
                        modifier = Modifier.clickable {
                            scope.launch {
                                val links = if(isValidWebUrl(listItem.link)) {
                                    listItem.link
                                } else {
                                    MyApplication.NEWS_URL + listItem.link
                                }

                                autoWebVpnForNews(context,links,listItem.title, icon = R.drawable.stream, cookie = cookies)
                            }
                        },
                        index = item
                    )
                }
                item { InnerPaddingHeight(innerPadding,false) }
                item { PaddingForPageControllerButton() }
            }
            PageController(
                listState,
                page,
                onNextPage = { page = it },
                onPreviousPage = { page = it },
//                modifier = Modifier.padding(innerPadding),
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding),
                paddingBottom = false
            )
        }
    }
}



fun transferToPostData(text : String, page : Int = 1)  : String {
    val jsonArray = JSONArray(
        """
        [
            {"field": "pageIndex", "value": 1},
            {"field": "group", "value": 0},
            {"field": "searchType", "value": ""},
            {"field": "keyword", "value": ""},
            {"field": "recommend", "value": "1"},
            {"field": 4, "value": ""},
            {"field": 5, "value": ""},
            {"field": 6, "value": ""},
            {"field": 7, "value": ""}
        ]
        """
    )

    // 更新JSON内容
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        when (jsonObject.get("field")) {
            "pageIndex" -> jsonObject.put("value", page)
            "keyword" -> jsonObject.put("value", text)
        }
    }

    // 转换为字符串
    val updatedJsonString = jsonArray.toString()
    return Crypto.encodeToBase64(updatedJsonString)
}

