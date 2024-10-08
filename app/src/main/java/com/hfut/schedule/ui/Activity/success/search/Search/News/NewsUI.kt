package com.hfut.schedule.ui.Activity.success.search.Search.News

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.WebViewScreen

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
                        actions = {
                            Row{
                                IconButton(onClick = { StartApp.startUri(links) }) { Icon(
                                    painterResource(id = R.drawable.net), contentDescription = "") }
                                IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
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
                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
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

