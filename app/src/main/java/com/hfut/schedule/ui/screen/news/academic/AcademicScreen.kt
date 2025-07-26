package com.hfut.schedule.ui.screen.news.academic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicTotalScreen(innerPadding : PaddingValues,vm : NetWorkViewModel) {
    val titles = AcademicType.entries.map { it.title }
    val pagerState = rememberPagerState(pageCount = { titles.size })

    var showDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }

    var page by remember { mutableIntStateOf(1) }
    var totalPage by remember { mutableStateOf<Int?>(null) }

    val uiState by vm.academicResp.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = r@{
        vm.academicResp.clear()
        vm.getAcademicNews(AcademicType.entries[pagerState.currentPage],page, totalPage = totalPage)
    }

    var title by remember { mutableStateOf("通知公告") }
    WebDialog(showDialog,{ showDialog = false },MyApplication.ACADEMIC_URL + url,title)


    LaunchedEffect(page) {
        // 如果pagerState.currentPage动，则执行某操作
        refreshNetwork()
    }
    LaunchedEffect(pagerState.currentPage) {
        if(totalPage != null) {
            totalPage = null
            refreshNetwork()
        }
    }
    Column {
        InnerPaddingHeight(innerPadding,true)
        CustomTabRow(
            pagerState = pagerState,
            titles = titles
        )
        HorizontalPager(state = pagerState) { pagerPage ->
            LaunchedEffect(Unit) {
                totalPage = null
            }
            CommonNetworkScreen(uiState, onReload = {
                page = 1
                totalPage = null
                refreshNetwork()
            }) {
                val data = (uiState as UiState.Success).data
                val list = data.news
                val listState = rememberLazyListState()
                LaunchedEffect(pagerPage) {
                    totalPage = data.totalPage
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
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
                        item { InnerPaddingHeight(innerPadding,false) }
                        item { PaddingForPageControllerButton() }
                    }
                    PagingController(listState,page, showUp = true,nextPage = { page = it }, previousPage = { page = it }, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }


}