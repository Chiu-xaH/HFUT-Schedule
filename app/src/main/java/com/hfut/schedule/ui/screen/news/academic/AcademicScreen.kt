package com.hfut.schedule.ui.screen.news.academic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
   
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.autoWebVpnForNews
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicTotalScreen(innerPadding : PaddingValues,vm : NetWorkViewModel) {
    val titles = AcademicType.entries.map { it.title }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val cookies by produceState<String?>(initialValue = null) {
        value = getWebVpnCookie(vm)
    }

    var page by remember { mutableIntStateOf(1) }
    var totalPage by remember { mutableStateOf<Int?>(null) }

    val uiState by vm.academicResp.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = r@{
        vm.academicResp.clear()
        vm.getAcademicNews(AcademicType.entries[pagerState.currentPage],page, totalPage = totalPage)
    }
    val context = LocalContext.current

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
    val scope = rememberCoroutineScope()
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
                                    scope.launch {
                                        val link = if (isValidWebUrl(item.link)) {
                                            item.link
                                        } else {
                                            MyApplication.ACADEMIC_URL + if (item.link.startsWith("..")) {
                                                item.link.substringAfter("..")
                                            } else {
                                                item.link
                                            }
                                        }
                                        autoWebVpnForNews(
                                            context,
                                            link,
                                            item.title,
                                            icon = R.drawable.stream,
                                            cookie = cookies
                                        )
                                    }
                                },
                                index = index
                            )
//                    }
                        }
                        item { InnerPaddingHeight(innerPadding,false) }
                        item { PaddingForPageControllerButton() }
                    }
                    PageController(
                        listState,
                        page,
                        nextPage = { page = it },
                        previousPage = { page = it },
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding),
//                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


}