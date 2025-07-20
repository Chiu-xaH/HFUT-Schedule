package com.hfut.schedule.ui.screen.news.home

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.NewsBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.news.academic.AcademicTotalScreen
import com.hfut.schedule.ui.screen.news.academic.AcademicXCScreen
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.screen.news.xuancheng.XuanquNewsUI
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import org.json.JSONArray


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsActivityUI(vm: NetWorkViewModel) {
    val navController = rememberNavController()
    val context = LocalActivity.current
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)
    var targetPage by remember { mutableStateOf(NewsBarItems.News) }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }
    val newsTitles = listOf("总","宣城校区")
    val newsPagerState = rememberPagerState(pageCount = { newsTitles.size })
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            isFullExpand = true,
            autoShape = false,
            showBottomSheet = showBottomSheet
        ) {
            Column(){
                HazeBottomSheetTopBar("选择校区", isPaddingStatusBar = false)

                StyleCardListItem(
                    headlineContent = {
                        Text("宣城校区教务处")
                    },
                    modifier = Modifier.clickable {
                        Starter.startWebUrl(MyApplication.XC_ACADEMIC_URL)
                    }
                )
                StyleCardListItem(
                    headlineContent = {
                        Text("总教务处")
                    },
                    modifier = Modifier.clickable {
                        Starter.startWebUrl(MyApplication.ACADEMIC_URL)
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.topBarBlur(hazeState)) {
                TopAppBar(
                    colors = topBarTransplantColor(),
                    title = { Text("通知公告") },
                    actions = {
                        if(targetPage == NewsBarItems.Academic) {
                            IconButton(
                                onClick = {
                                    showBottomSheet = true
                                }
                            ) {
                                Icon(painterResource(R.drawable.net),null)
                            }
                        }
                        IconButton(onClick = {
                            showToast("部分公告需要连接校园网才可阅读（对外私密）")
                        }) {
                            Icon(painterResource(R.drawable.info), contentDescription = "")
                        }
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "")
                        }
                    }
                )
                if(targetPage != NewsBarItems.School) {
                    CustomTabRow(
                        pagerState = newsPagerState,
                        titles = newsTitles
                    )
                }
            }
        },
        bottomBar = {
            Column {
                NavigationBar(containerColor = Color.Transparent,
                    modifier = Modifier
                        .bottomBarBlur(hazeState)) {

                    val items = listOf(
                        NavigationBarItemData(
                            NewsBarItems.News.name,"通知公告", painterResource(R.drawable.star), painterResource(
                                R.drawable.star_filled)
                        ),
                        NavigationBarItemData(
                            NewsBarItems.Academic.name,"教务处", painterResource(R.drawable.star_half), painterResource(
                                R.drawable.star_filled)
                        ),
                        NavigationBarItemData(
                            NewsBarItems.School.name,"各级学院", painterResource(R.drawable.school),
                            painterResource(R.drawable.school_filled)
                        )
                    )
                    items.forEach { item ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale = animateFloatAsState(
                            targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "" // 使用弹簧动画
                        )
                        val route = item.route
                        val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                        NavigationBarItem(
                            selected = selected,
                            modifier = Modifier.scale(scale.value),
                            interactionSource = interactionSource,
                            onClick = {
                                //     atEnd = !atEnd
                                targetPage = when(item) {
                                    items[0] -> NewsBarItems.News
                                    items[1] -> NewsBarItems.Academic
                                    items[2] -> NewsBarItems.School
                                    else -> NewsBarItems.News
                                }
                                if (!selected) { navController.navigateAndSave(route) }

//                                if(route != NewsBarItems.Academic.name) {
//                                } else {
//                                    /
//                                }
                            },
                            label = { Text(text = item.label) },
                            icon = {
                                BadgedBox(badge = {}) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .9f))

                        )
                    }
                }
            }

        }
    ) {innerPadding ->

        val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

        NavHost(navController = navController,
            startDestination = NewsBarItems.News.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            composable(NewsBarItems.News.name) {
                Scaffold {
                    NewsScreen(innerPadding,vm,newsPagerState)
                }
            }
            composable(NewsBarItems.Academic.name) {
                Scaffold {
                    AcademicScreen(innerPadding,vm,newsPagerState)
                }
            }
            composable(NewsBarItems.School.name) {
                Scaffold {
                    SchoolsUI(innerPadding)
                }
            }
        }
    }
}
private const val TAB_TOTAL = 0
private const val TAB_XC = 1
@Composable
fun NewsScreen(innerPadding : PaddingValues,vm : NetWorkViewModel,pagerState : PagerState) {
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
    var title by remember { mutableStateOf("通知公告") }
    var showDialog by remember { mutableStateOf(false) }
    var links by remember { mutableStateOf("") }
    WebDialog(showDialog,{ showDialog = false },links,title)

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        val listState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                item {
                    Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
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
                            val link = listItem.link
                            if(link.contains("http")) link.let { links = link }
                            else links = MyApplication.NEWS_URL + link
                            title = listItem.title
                            showDialog = true
                        },
                        index = item
                    )
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                item { PaddingForPageControllerButton() }
            }
            PagingController(listState,page, showUp = true,nextPage = { page = it }, previousPage = { page = it }, modifier = Modifier.padding(innerPadding))
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
    return Encrypt.encodeToBase64(updatedJsonString)
}

