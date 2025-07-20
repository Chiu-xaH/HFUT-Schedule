package com.hfut.schedule.ui.screen.home.search.function.school.work

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.fastMap
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.WorkSearchType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun Work(hazeState : HazeState,vm: NetWorkViewModel) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb,{ showDialogWeb = false }, MyApplication.WORK_URL,"就业网", showTop = false)

    TransplantListItem(
        headlineContent = { Text(text = "就业信息") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.azm), contentDescription = "") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
    var campus by remember { mutableStateOf(getCampus()) }

    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("就业信息") {
                        Row {
                            FilledTonalIconButton(
                                onClick = {
                                    showDialogWeb = true
                                }
                            ) {
                                Icon(painterResource(R.drawable.net),null)
                            }
                            FilledTonalButton(
                                onClick = {
                                    campus = when(campus) {
                                        Campus.HEFEI -> Campus.XUANCHENG
                                        Campus.XUANCHENG -> Campus.HEFEI
                                    }
                                }
                            ) {
                                Text(campus.description)
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    WorkSearchUI(vm,campus)
                }
            }
        }
    }
}
// 模范写法
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun WorkSearchUI(vm : NetWorkViewModel,campus: Campus) {

    var showDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    var webTitle by remember { mutableStateOf("详情") }
    val types = remember { listOf(
        WorkSearchType.ALL,
        WorkSearchType.JOB_FAIR,
        WorkSearchType.JOB_FAIR_COMPANY,
        WorkSearchType.PRESENTATION,
        WorkSearchType.ONLINE_RECRUITMENT,
        WorkSearchType.POSITION,
        WorkSearchType.ANNOUNCEMENT
    ) }
    val pagerState = rememberPagerState(pageCount = { types.size })
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    WebDialog(showDialog,{ showDialog = false }, url,webTitle, showTop = false)

    CustomTabRow(pagerState,types.fastMap { it.description })

    HorizontalPager(state = pagerState) { page ->
        val uiState by vm.workSearchResult.state.collectAsState()
        var currentPage by remember { mutableIntStateOf(1) }
        val refreshNetwork: suspend () -> Unit = {
            vm.workSearchResult.clear()
            vm.searchWorks(keyword = input.let { if(it.isBlank() || it.isEmpty()) null else it }, page = currentPage, type = page,campus)
        }

        LaunchedEffect(currentPage,campus) {
            // 避免旧数据影响
            refreshNetwork()
        }

        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val response = (uiState as UiState.Success).data
            val repos = response.data
            val listState = rememberLazyListState()
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    item {
                        CustomTextField(
                            input = input,
                            label = { Text("搜索") },
                            trailingIcon = { IconButton(
                                onClick = { scope.launch { refreshNetwork() } }
                            ) { Icon(painterResource(R.drawable.search),null) } },
                            leadingIcon = if(!(input.isEmpty() || input.isBlank())) {
                                {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                input = ""
                                                refreshNetwork()
                                            }
                                        }
                                    ) { Icon(painterResource(R.drawable.close),null) }
                                }
                            } else null
                        ) { input = it }
                        Spacer(Modifier.height(CARD_NORMAL_DP))
                    }
                    items(repos.size, key = { it }) { index ->
                        with(repos[index]) {
                            val enumType = when(type) {
                                WorkSearchType.JOB_FAIR.code.toString() -> WorkSearchType.JOB_FAIR
                                WorkSearchType.JOB_FAIR_COMPANY.code.toString() -> WorkSearchType.JOB_FAIR_COMPANY
                                WorkSearchType.PRESENTATION.code.toString() -> WorkSearchType.PRESENTATION
                                WorkSearchType.ONLINE_RECRUITMENT.code.toString() -> WorkSearchType.ONLINE_RECRUITMENT
                                WorkSearchType.POSITION.code.toString() -> WorkSearchType.POSITION
                                WorkSearchType.ANNOUNCEMENT.code.toString() -> WorkSearchType.ANNOUNCEMENT
                                else -> WorkSearchType.ALL
                            }
                            AnimationCardListItem(
                                headlineContent = { Text(title) },
                                overlineContent = { Text(time + if(page == 0) " " + enumType.description else "") },
                                index = index,
                                modifier = Modifier.clickable {
                                    url = when(campus) {
                                        Campus.HEFEI -> MyApplication.WORK_URL
                                        Campus.XUANCHENG -> MyApplication.WORK_XC_URL
                                    } + "detail/" + enumType.url +  id
                                    webTitle = title
                                    showDialog = true
                                },
                                leadingContent = { Text((index+1).toString()) }
                            )
                        }
                    }
                    item { PaddingForPageControllerButton() }
                }
                PagingController(listState,currentPage,showUp = true, nextPage = { currentPage = it }, previousPage = { currentPage = it })
            }
        }
    }
}