package com.hfut.schedule.ui.screen.news

//import com.hfut.schedule.ui.activity.card.function.main.turnToBottomBar
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.enumeration.NewsBarItems
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.storage.SharePrefs
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.screen.news.home.NewsItem
import com.hfut.schedule.ui.screen.news.xuancheng.XuanquNewsUI
import com.hfut.schedule.ui.util.NavigateAnimationManager
import com.hfut.schedule.ui.util.NavigateAnimationManager.currentPage

import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.CenterLoadingUI
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsActivityUI(vm: NetWorkViewModel) {
    val navController = rememberNavController()
    val context = LocalActivity.current
    val hazeState = remember { HazeState() }
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
//                if(!blur)
//                    Divider()
            }
        },
        bottomBar = {
            Column {
//                if(!blur)
//                    Divider()
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
                            }
                        )
                    }
                }
            }

        }
    ) {innerPadding ->

        val animation = NavigateAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

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

    val words = listOf(
        "放假","转专业","推免","奖学金"
    )
    var input by remember { mutableStateOf( words[0]) }
//    var onclick by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf(1) }
    var refresh by remember { mutableStateOf(true) }

    fun refresh() {
//        onclick = true
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.NewsData) }.await()
            async { vm.searchNews(Encrypt.encodeToBase64(input), page) }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vm.NewsData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("<!DOCTYPE html>")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }
    if(refresh) {
        refresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            LazyRow(modifier = Modifier.padding(horizontal = appHorizontalDp())) {
                items(words.size) { index ->
                    val word = words[index]
                    AssistChip(onClick = {
                        input = word
                        refresh()
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
                        .padding(horizontal = appHorizontalDp()),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("搜索通知或新闻") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            // shape = RoundedCornerShape(5.dp),
                            onClick = {
                                refresh()
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
            Spacer(modifier = Modifier.height(cardNormalDp()))

            if(loading) {
                CenterLoadingUI()
            } else {
                NewsItem(vm,innerPadding)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .padding(innerPadding)
                .align(Alignment.BottomStart)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
        ) {
            FloatingActionButton(
                onClick = {
                    if (page > 1) {
                        page--
                        refresh()
                    } else {
                        showToast("第一页")
                    }
                },
            ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
        }


        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .padding(innerPadding)
                .align(Alignment.BottomCenter)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    page = 1
                    refresh()
                },
            ) { Text(text = "第${page}页") }
        }

        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(innerPadding)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
        ) {
            FloatingActionButton(
                onClick = {
                    page++
                    refresh()
                },
            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
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