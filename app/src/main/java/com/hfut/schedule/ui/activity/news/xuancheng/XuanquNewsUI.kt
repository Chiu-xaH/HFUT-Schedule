package com.hfut.schedule.ui.activity.news.xuancheng

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.reEmptyLiveDta
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen
import com.hfut.schedule.ui.utils.style.topBarBlur
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

//*******最新模范写法****
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XuanquNewsUI(innerPadding : PaddingValues,vm : NetWorkViewModel) {
    var refresh by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    val host = MyApplication.XuanChengURL

    var page by remember { mutableIntStateOf(1) }

    fun refresh() {
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.NewsXuanChengData) }.await()
            async { vm.getXuanChengNews(page) }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.NewsXuanChengData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("html")) {
                                refresh = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }

    if(refresh) { refresh() }

    WebDialog(showDialog,{ showDialog = false },host + url,"新闻详情")
//    if(showDialog) {
//        val link = host + url
//        androidx.compose.ui.window.Dialog(
//            onDismissRequest = { showDialog = false },
//            properties = DialogProperties(usePlatformDefaultWidth = false)
//        ) {
//            Scaffold(
//                modifier = Modifier.fillMaxSize(),
//                topBar = {
//                    TopAppBar(
////                        modifier = Modifier.topBarBlur(hazeState, blur),
//                        colors = TopAppBarDefaults.mediumTopAppBarColors(
//                            containerColor = Color.Transparent,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
//                        actions = {
//                            Row{
//                                IconButton(onClick = { Starter.startWebUrl(link) }) { Icon(
//                                    painterResource(id = R.drawable.net), contentDescription = "") }
//                                IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
//                            }
//                        },
//                        title = { Text("新闻详情") }
//                    )
//                },
//            ) { innerPadding ->
//                Column(
//                    modifier = Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
////                        .haze(state = hazeState)
//                ) {
//                    WebViewScreen(url = link)
//                }
//            }
//        }
//    }

    Box {
        if(loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    LoadingUI()
                }
            }
        } else {
            val list = getXuanquNews(vm)
            LazyColumn {
                item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                items(list.size) { index ->
                    val item = list[index]
//                    MyCustomCard {
                        StyleCardListItem(
                            headlineContent = { Text(item.title) },
                            overlineContent = { Text(item.date) },
                            leadingContent = { Text((index+1).toString()) },
                            modifier = Modifier.clickable {
                                url = item.url
                                showDialog = true
                            }
                        )
//                    }
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                item { Spacer(modifier = Modifier.height(85.dp)) }
            }
        }

        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .padding(innerPadding)
                .align(Alignment.BottomStart)
                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
        ) {
            FloatingActionButton(
                onClick = {
                    if (page > 1) {
                        page--
                        refresh()
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
                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
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
                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
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