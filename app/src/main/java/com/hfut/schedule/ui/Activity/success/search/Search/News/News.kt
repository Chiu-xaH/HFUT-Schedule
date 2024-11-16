package com.hfut.schedule.ui.Activity.success.search.Search.News

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.Encrypt.encodeToBase64
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.success.search.Search.Web.Schools
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.RowHorizal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun News(vm : LoginSuccessViewModel) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf( "放假") }
    var onclick by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf(1) }
    ListItem(
        headlineContent = { Text(text = "通知公告") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.stream),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
showBottomSheet = true
        }
    )

    val words = listOf(
        "放假","转专业","推免","奖学金"
    )

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("通知公告") },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                                Schools()
                                FilledTonalIconButton(onClick = { StartApp.startUri(MyApplication.NewsURL) }) {
                                    Icon(painterResource(id = R.drawable.net), contentDescription = "")
                                }
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Column {
                        LazyRow(modifier = Modifier.padding(horizontal = 15.dp)) {
                            items(words.size) { index->
                                val word = words[index]
                                AssistChip(onClick = {
                                    input = word
                                    loading = true
                                    onclick = true
                                                     }, label = { Text(text = word) })
                                Spacer(modifier = Modifier.width(5.dp))
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
                                label = { Text("搜索通知或新闻" ) },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(
                                        // shape = RoundedCornerShape(5.dp),
                                        onClick = {
                                            loading = true
                                            onclick = true
                                        }) {
                                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
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
                        if(onclick) {
                            CoroutineScope(Job()).launch{
                                async { Handler(Looper.getMainLooper()).post{ vm.NewsData.value = "{}" } }.await()
                                async{ vm.searchNews(encodeToBase64(input),page) }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post{
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
                                    CircularProgressIndicator()
                                }
                            }////加载动画居中，3s后消失

                            AnimatedVisibility(
                                visible = !loading,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                //写布局

                                NewsItem(vm)
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 15.dp, vertical = 15.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if(page > 1) {
                                    page--
                                    onclick = true
                                    loading = true
                                } else {
                                    MyToast("第一页")
                                }
                            },
                        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                    }


                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
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

                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
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
        }
    }
}




