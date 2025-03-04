package com.hfut.schedule.ui.activity.news.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.activity.news.getNews
import com.hfut.schedule.ui.utils.components.AnimationCardListItem
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItem(vm : NetWorkViewModel, innerPadding : PaddingValues? = null) {
    var showDialog by remember { mutableStateOf(false) }

    var links by remember { mutableStateOf("") }
    WebDialog(showDialog,{ showDialog = false },links,"新闻详情")


    LazyColumn {
        //item { if (innerPadding != null) Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
        items(getNews(vm).size){ item ->
            AnimationCardListItem(
                overlineContent = { getNews(vm)[item].date?.let { Text(text = it) } },
                headlineContent = { getNews(vm)[item].title?.let { Text(it) } },
                leadingContent = { Text(text = (item + 1).toString()) },
//                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                modifier = Modifier.clickable {
                    val link = getNews(vm)[item].link
                    if (link != null) {
                        if(link.contains("http")) link?.let { links = link }
                        else links = MyApplication.NewsURL + link
                    }
                    showDialog = true
                },
                index = item
            )
        }
        item { if (innerPadding != null) Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
        item { Spacer(modifier = Modifier.height(85.dp)) }
    }
}

