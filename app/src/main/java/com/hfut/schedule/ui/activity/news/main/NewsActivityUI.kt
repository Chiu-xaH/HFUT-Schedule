package com.hfut.schedule.ui.activity.news.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.enums.NewsBarItems
import com.hfut.schedule.logic.beans.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.Encrypt
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.activity.card.function.main.turnToBottomBar
import com.hfut.schedule.ui.activity.news.home.NewsItem
import com.hfut.schedule.ui.activity.news.departments.SchoolsUI
import com.hfut.schedule.ui.activity.news.xuancheng.XuanquNewsUI
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.bottomBarBlur
import com.hfut.schedule.ui.utils.style.topBarBlur
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
    val animation by remember { mutableStateOf(SharePrefs.prefs.getInt("ANIMATION", MyApplication.Animation)) }
    val navController = rememberNavController()
    val context = LocalContext.current
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR",  AndroidVersion.canBlur)
    val blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }
    var bottomBarItems by remember { mutableStateOf(NewsBarItems.News) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, blur),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("通知公告") },
                    actions = {
                        if(bottomBarItems == NewsBarItems.XuanCheng) {
                            IconButton(onClick = {
                                MyToast("正在开发")
                            }) {
                                Icon(Icons.Filled.Search, contentDescription = "")
                            }
                        }
                        IconButton(onClick = {
                            (context as? Activity)?.finish()
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
                        .bottomBarBlur(hazeState, blur)) {

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
                                bottomBarItems = when(item) {
                                    items[0] -> NewsBarItems.News
                                    items[1] -> NewsBarItems.XuanCheng
                                    items[2] -> NewsBarItems.School
                                    else -> NewsBarItems.News
                                }
                                if (!selected) { turnToBottomBar(navController, route) }
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
        NavHost(navController = navController,
            startDestination = NewsBarItems.News.name,
            enterTransition = {
                scaleIn(animationSpec = tween(durationMillis = animation)) +
                        expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            exitTransition = {
                scaleOut(animationSpec = tween(durationMillis = animation)) +
                        shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
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
    var onclick by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf(1) }



    Box(
        modifier = Modifier
           // .padding(innerPadding)
            .fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            LazyRow(modifier = Modifier.padding(horizontal = 15.dp)) {
                items(words.size) { index ->
                    val word = words[index]
                    AssistChip(onClick = {
                        input = word
                        loading = true
                        onclick = true
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
                        .padding(horizontal = 15.dp),
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
                                loading = true
                                onclick = true
                            }) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "description"
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                        unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                    ),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (onclick) {
                CoroutineScope(Job()).launch {
                    async {
                        Handler(Looper.getMainLooper()).post {
                            vm.NewsData.value = "{}"
                        }
                    }.await()
                    async { vm.searchNews(Encrypt.encodeToBase64(input), page) }.await()
                    async {
                        Handler(Looper.getMainLooper()).post {
                            vm.NewsData.observeForever { result ->
                                if (result != null) {
                                    if (result.contains("<!DOCTYPE html>")) {
                                        loading = false
                                    }
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                        LoadingUI()
                    }
                }////加载动画居中，3s后消失

                AnimatedVisibility(
                    visible = !loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    //写布局
                    NewsItem(vm,innerPadding)
                }
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
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    if (page > 1) {
                        page--
                        onclick = true
                        loading = true
                    } else {
                        MyToast("第一页")
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
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    page = 1
                    onclick = true
                    loading = true
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
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    page++
                    onclick = true
                    loading = true
                },
            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
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