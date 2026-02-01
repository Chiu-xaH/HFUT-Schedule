package com.hfut.schedule.ui.screen.home.search.function.community.library.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.library.BorrowedStatus
import com.hfut.schedule.logic.util.network.getPageSize
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryBorrowedScreen(
    vm: NetWorkViewModel,
    navController : NavHostController
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.LibraryBorrowed.route }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.LibraryBorrowed.label) },
                navigationIcon = {
                    TopBarNavigationIcon(route, AppNavRoute.LibraryBorrowed.icon)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            BorrowUI(vm,innerPadding)
        }
    }
}

private fun List<String>.filterKeyword() : List<String> {
   try {
       // 过滤拼音
       if(this.size > 1) {
           if(this[0].contains(' ')) {
               // 删掉
               return this.drop(1)
           } else if(this[0][0] in 'a'..'b') {
               // 删掉
               return this.drop(1)
           }
       }
       return this
   } catch (e : Exception) {
       LogUtil.error(e)
       return this
   }
}

private val statusList = BorrowedStatus.entries
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BorrowUI(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues
) {
    val uiState by vm.libraryBorrowedResp.state.collectAsState()
    val refreshNetwork = suspend m@ {
        if(uiState is UiState.Success) {
            return@m
        }
        val token = prefs.getString(LIBRARY_TOKEN,"") ?: return@m
        val pageSize = (vm.libraryStatusResp.state.value as? UiState.Success)?.data?.borrowCount
        vm.libraryBorrowedResp.clear()
        vm.getBorrowed(token,1,null,pageSize ?: getPageSize())
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload =  refreshNetwork) {
        val list = (uiState as UiState.Success).data.sortedByDescending { it.createdTime }
        val analysis = list.map { l -> statusList.find { e -> e.status == l.status } }
        val totalCount = analysis.size
        val returnedCount = analysis.filter { it == BorrowedStatus.RETURNED }.size
        val overdueCount = analysis.filter { it == BorrowedStatus.OVERDUE }.size
        val borrowingCount = analysis.filter { it == BorrowedStatus.BORROWING }.size
        val listState = rememberLazyListState()
        val mostFrequents: List<Pair<Int, String>> = list
            .map {
                it.libraryDetail.detail.keywords
                    .split("/")
                    .filterKeyword()
            }
            .flatMap { it }
            .groupingBy { it }
            .eachCount()
            .map { it.value to it.key }
            .sortedByDescending { it.first }

        Box() {
            LazyColumn(state = listState) {
                item { InnerPaddingHeight(innerPadding,true) }
                item {
                    CustomCard(color = MaterialTheme.colorScheme.secondaryContainer) {
                        Row {
                            TransplantListItem(
                                overlineContent = { Text("总计")},
                                headlineContent = { Text("${totalCount}本")},
                                leadingContent = {
                                    Icon(painterResource(R.drawable.filter_vintage),null)
                                },
                                modifier = Modifier.weight(.5f)
                            )
                            TransplantListItem(
                                overlineContent = { Text("已归还")},
                                headlineContent = { Text("${returnedCount}本")},
                                leadingContent = {
                                    Icon(painterResource(R.drawable.check_circle),null)
                                },
                                modifier = Modifier.weight(.5f)
                            )
                        }
                        Row {
                            TransplantListItem(
                                overlineContent = { Text("借阅中") },
                                headlineContent = { Text("${borrowingCount}本") },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.book_5), null)
                                },
                                modifier = Modifier.weight(.5f)
                            )
                            TransplantListItem(
                                overlineContent = { Text("逾期待还") },
                                headlineContent = { Text("${overdueCount}本") },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.schedule), null,)
                                },
                                modifier = Modifier.weight(.5f)
                            )
                        }
                        LazyRow(modifier = Modifier.padding(bottom = CARD_NORMAL_DP*3)) {
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            items(mostFrequents.size) { index ->
                                val item = mostFrequents[index]
                                AssistChip(
                                    onClick = { showToast("筛选功能开发中") },
                                    border = null,
                                    colors = AssistChipDefaults.assistChipColors(containerColor = cardNormalColor()),
                                    label = { Text(item.second) },
                                    trailingIcon = {
                                        Text("x" + item.first.toString() )
                                    },
                                    modifier = Modifier.padding(end = if(index == mostFrequents.size-1) 0.dp else CARD_NORMAL_DP*2)
                                )
                            }
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        }
                    }
                }
                items(list.size, key = { it }) { index ->
                    val item = list[index]
                    with(item) {
                        val borrowStatus = statusList.find { it.status == status }
                        val detail = libraryDetail.detail
                        CustomCard(
                            color = when(borrowStatus) {
                                BorrowedStatus.OVERDUE -> MaterialTheme.colorScheme.errorContainer
                                else -> cardNormalColor()
                            },
                            modifier = Modifier.clickable {}
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(detail.title, fontWeight = FontWeight.Bold) },
                                leadingContent = {
                                    when(borrowStatus) {
                                        BorrowedStatus.BORROWING -> {
                                            LoadingIcon()
                                        }
                                        BorrowedStatus.RETURNED -> {
                                            Icon(painterResource(R.drawable.check_circle), null)
                                        }
                                        BorrowedStatus.OVERDUE -> {
                                            LoadingIcon()
                                        }
                                        else -> {
                                            Icon(painterResource(R.drawable.book_5),null)
                                        }
                                    }
                                },
                                supportingContent = {
                                    Text(location)
                                },
                                trailingContent = {
                                    borrowStatus?.description?.let { Text(it) }
                                },
                                overlineContent = {
                                    val borrowing = borrowStatus == BorrowedStatus.OVERDUE || borrowStatus == BorrowedStatus.BORROWING
                                    Text("借于 $createdTime\n"
                                    + if(borrowing) {
                                        val t = returnTime?.substringBefore(' ')
                                        "应还 $t" + t?.let { " (${DateTimeManager.daysBetween(it)}天后)" }
                                    } else {
                                        "还于 $realReturnTime"
                                    }
                                    )
                                },
                            )
                            PaddingHorizontalDivider()
                            with(detail) {
                                TransplantListItem(
                                    headlineContent = { Text(publishers + " (${year})") },
                                    overlineContent = { Text("ISBN $isbn\n索书号 $callNo") },
                                    supportingContent = { Text(authors) },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.person), null)
                                    },
                                )
                                PaddingHorizontalDivider()
                                TransplantListItem(
                                    headlineContent = {
                                        Text("介绍")
                                    },
                                    supportingContent = { Text(detail.digest) },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.info), null)
                                    },
                                )
                                val list = detail.keywords.split("/").filterKeyword()
                                LazyRow(modifier = Modifier.padding(bottom = CARD_NORMAL_DP*3)) {
                                    item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                    items(list.size) { index ->
                                        val str = list[index]
                                        AssistChip(
                                            onClick = { },
                                            border = null,
                                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                            label = { Text(str) },
                                            modifier = Modifier.padding(end = if(index == list.size-1) 0.dp else CARD_NORMAL_DP*2)
                                        )
                                    }
                                    item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                }
                            }
//                            UrlImageNoCrop(MyApplication.NEW_LIBRARY_URL + "svc/fileserver/images/book/cover/undefined/${detail.title}/${detail.authors}?e=${detail.isbn.replace("-","")}")
                        }
                    }
                }
                item { BottomTip("续借请前往旧图书馆官网(校园网)") }
//                if(needPageScroll) {
//                    item { PaddingForPageControllerButton() }
//                }
                item { InnerPaddingHeight(innerPadding,false) }
            }
//            if(needPageScroll) {
//                PageController(listState,page,false,{ page = it },{ page = it }, modifier = Modifier.padding(innerPadding))
//            }
        }
    }
}