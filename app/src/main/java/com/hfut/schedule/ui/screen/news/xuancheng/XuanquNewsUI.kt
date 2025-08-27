package com.hfut.schedule.ui.screen.news.xuancheng

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PagingController
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.autoWebVpnForNews
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie

import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

//*******最新模范写法****
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XuanquNewsUI(innerPadding : PaddingValues,vm : NetWorkViewModel) {
    val cookies by produceState<String?>(initialValue = null) {
        value = getWebVpnCookie(vm)
    }
    var page by remember { mutableIntStateOf(1) }
    val uiState by vm.newsXuanChengResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.newsXuanChengResult.clear()
        vm.getXuanChengNews(page)
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        val listState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                item { InnerPaddingHeight(innerPadding,true) }
                items(list.size, key = { it }) { index ->
                    val item = list[index]
//                    MyCustomCard {
                    AnimationCardListItem(
                        headlineContent = { Text(item.title) },
                        overlineContent = { Text(item.date) },
                        leadingContent = { Text((index+1).toString()) },
                        modifier = Modifier.clickable {
                            autoWebVpnForNews(MyApplication.NEWS_XC_URL + item.link,item.title,icon = R.drawable.stream, cookie = cookies)
                        },
                        index = index
                    )
//                    }
                }
                item { InnerPaddingHeight(innerPadding,false) }
                item { PaddingForPageControllerButton() }
            }
            PagingController(
                listState,
                page,
                nextPage = { page = it },
                previousPage = { page = it },
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding),
//                modifier = Modifier.padding(innerPadding),
                paddingBottom = false
            )
        }
    }
}