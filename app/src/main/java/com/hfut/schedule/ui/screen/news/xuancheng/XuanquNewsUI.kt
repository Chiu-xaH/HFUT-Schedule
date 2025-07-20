package com.hfut.schedule.ui.screen.news.xuancheng

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

//*******最新模范写法****
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XuanquNewsUI(innerPadding : PaddingValues,vm : NetWorkViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }

    var page by remember { mutableIntStateOf(1) }
    val uiState by vm.newsXuanChengResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.newsXuanChengResult.clear()
        vm.getXuanChengNews(page)
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }
    var title by remember { mutableStateOf("通知公告") }
    WebDialog(showDialog,{ showDialog = false },MyApplication.NEWS_XC_URL + url,title)

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        val listState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                items(list.size, key = { it }) { index ->
                    val item = list[index]
//                    MyCustomCard {
                    AnimationCardListItem(
                        headlineContent = { Text(item.title) },
                        overlineContent = { Text(item.date) },
                        leadingContent = { Text((index+1).toString()) },
                        modifier = Modifier.clickable {
                            title = item.title
                            url = item.link
                            showDialog = true
                        },
                        index = index
                    )
//                    }
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                item { PaddingForPageControllerButton() }
            }
            PagingController(listState,page, showUp = true,nextPage = { page = it }, previousPage = { page = it }, modifier = Modifier.padding(innerPadding))
        }
    }
}