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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.PagingController
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.screen.news.xuancheng.XuanquNewsUI
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.haze
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



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState,),
                    colors = topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = MaterialTheme.colorScheme.primary
        ),
                    title = { Text("通知公告") },
                    actions = {
                        if(targetPage == NewsBarItems.XuanCheng) {
                            IconButton(onClick = {
                                showToast("正在开发")
                            }) {
                                Icon(Icons.Filled.Search, contentDescription = "")
                            }
                        }
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "")
                        }
                    }
                )
            }
        },
        bottomBar = {
            Column {
                NavigationBar(containerColor = Color.Transparent,
                    modifier = Modifier
                        .bottomBarBlur(hazeState)) {

                    val items = listOf(
                        NavigationBarItemData(
                            NewsBarItems.News.name,"全校", painterResource(R.drawable.star), painterResource(
                                R.drawable.star_filled)
                        ),
                        NavigationBarItemData(
                            NewsBarItems.XuanCheng.name,"宣城校区", painterResource(R.drawable.star_half), painterResource(
                                R.drawable.star_filled)
                        ),
                        NavigationBarItemData(
                            NewsBarItems.School.name,"学院", painterResource(R.drawable.school),
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
                                if(currentAnimationIndex == 2) {
                                    targetPage = when(item) {
                                        items[0] -> NewsBarItems.News
                                        items[1] -> NewsBarItems.XuanCheng
                                        items[2] -> NewsBarItems.School
                                        else -> NewsBarItems.News
                                    }
                                }
                                if (!selected) { navController.navigateAndSave( route) }
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
            modifier = Modifier.haze(state = hazeState)) {
            composable(NewsBarItems.News.name) {
                Scaffold {
                    NewsUI(innerPadding,vm)
                }
            }
            composable(NewsBarItems.XuanCheng.name) {
                Scaffold {
                    XuanquNewsUI(innerPadding, vm)
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

    var showDialog by remember { mutableStateOf(false) }
    var links by remember { mutableStateOf("") }
    WebDialog(showDialog,{ showDialog = false },links,"新闻详情")

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
                        overlineContent = { listItem.date?.let { Text(text = it) } },
                        headlineContent = { listItem.title?.let { Text(it) } },
                        leadingContent = { Text(text = (item + 1).toString()) },
                        modifier = Modifier.clickable {
                            val link = listItem.link
                            if (link != null) {
                                if(link.contains("http")) link.let { links = link }
                                else links = MyApplication.NEWS_URL + link
                            }
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