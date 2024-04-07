package com.hfut.schedule.ui.Activity.success.search.Search

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.NewsResponse
import com.hfut.schedule.logic.utils.BaseSixtyFourEncrypt
import com.hfut.schedule.logic.utils.StartUri
import com.hfut.schedule.ui.Activity.success.search.Search.Electric.WebViewScreen
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.Click
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.FailRateUI
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.getFailRate
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.getLists
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun News(vm : LoginSuccessViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf( "") }
    var onclick by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(text = "新闻检索") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.newspaper),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
showBottomSheet = true
        }
    )

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("新闻检索") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

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
                            label = { Text("无文字直接搜索则为默认推荐" ) },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    // shape = RoundedCornerShape(5.dp),
                                    onClick = {
                                        CoroutineScope(Job()).launch{
                                            async{
                                               // Click(vm, input, 1)
                                                vm.searchNews(BaseSixtyFourEncrypt.encodeToBase64(input))
                                                loading = true
                                                onclick = true
                                                Handler(Looper.getMainLooper()).post{
                                                    vm.NewsData.value = "{}"
                                                }
                                            }.await()
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


                    if(onclick){
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItem(vm : LoginSuccessViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var links by remember { mutableStateOf("") }
    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = { IconButton(onClick = { showDialog = false }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "")
                        }
                        },
                        title = { Text("新闻详情") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    WebViewScreen(url = links)
                }
            }
        }
    }

    LazyColumn {
        items(getNews(vm).size){ item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            overlineContent = { getNews(vm)[item].date?.let { Text(text = it) } },
                            headlineContent = { getNews(vm)[item].title?.let { Text(it) } },
                            leadingContent = { Icon(painterResource(R.drawable.newspaper), contentDescription = "Localized description",) },
                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                            modifier = Modifier.clickable {
                                val link = getNews(vm)[item].link
                                if (link != null) {
                                    if(link.contains("http")) link?.let { links = link }
                                    else links = MyApplication.NewsURL + link
                                }
                                showDialog = true
                                 },
                        )
                    }
                }
            }
        }
    }
}


fun getNews(vm : LoginSuccessViewModel): MutableList<NewsResponse> {
    var newsList = mutableListOf<NewsResponse>()
    try {
        val html= vm.NewsData.value
        val doc: Document = Jsoup.parse(html)
        val newsItems = doc.select("ul.list li")

        for (item in newsItems) {
            val date = item.select("i.timefontstyle252631").text()
            val title = item.select("p.titlefontstyle252631").text()
            val link = item.select("a").attr("href")
            newsList.add(NewsResponse(title, date, link))
        }
        return newsList
    } catch (e : Exception) {
        return newsList
    }
}

