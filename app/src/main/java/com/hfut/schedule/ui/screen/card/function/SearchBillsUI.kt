package com.hfut.schedule.ui.screen.card.function

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.icon.BillsIcons
import com.hfut.schedule.ui.component.input.CustomTextField
import com.xah.common.ui.style.align.CenterScreen
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.screen.card.bill.main.BillsInfo
import com.hfut.schedule.ui.screen.card.bill.main.processTranamt
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeBarItems
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.logic.model.HuiXinBillRecord
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.remove
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBillsUI(vm : NetWorkViewModel) {
    val hazeState = rememberHazeBlur()
    val backdrop = rememberLayerBackdrop()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var input by remember { mutableStateOf(EMPTY_STRING) }
    var currentPage by remember { mutableIntStateOf(1) }
    var startUse by remember { mutableStateOf(false) }

    val uiState by vm.huiXinSearchBillsResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val auth = prefs.getString("auth","")
        vm.huiXinSearchBillsResult.clear()
        vm.searchBills("bearer $auth",input,currentPage)
    }

    LaunchedEffect(Unit) {
        vm.huiXinSearchBillsResult.emitPrepare()
    }
    LaunchedEffect(currentPage) {
        if(startUse) {
            refreshNetwork()
        }
    }

    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var infoNum by remember { mutableStateOf<HuiXinBillRecord?>(null) }

    if(showBottomSheet && infoNum != null) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
        ){
            BillsInfo(infoNum!!)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState)
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = {
                        Text("账单搜索")
                    },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
                CustomTextField(
                    modifier = Modifier
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .containerBackDrop(backdrop, MaterialTheme.shapes.medium),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { refreshNetwork() }
                            }) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "description"
                            )
                        }
                    },
                    colors = textFiledAllTransplant(),
                    input = input,
                    label = { Text("检索标题") },
                ) {
                    input = it
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .backDropSource(backdrop)
        ){
            CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchIcon() }) {
                if(!startUse) {
                    startUse = true
                }

                val response = (uiState as NetworkUiState.Success).data
                val list = response.records
                if(list.isEmpty()) {
                    CenterScreen {
                        EmptyIcon()
                    }
                } else {
                    val listState = rememberLazyListState()
                    Box {
                        LazyColumn(state = listState) {
                            item { InnerPaddingHeight(innerPadding,true) }
                            items(list.size, key = { list[it].orderId }) { index ->
                                val item = list[index]
                                val name = item.resume.remove("有限公司")
                                CardListItem(
                                    headlineContent = { Text(text = name) },
                                    supportingContent = { Text(text = processTranamt(item)) },
                                    overlineContent = { Text(text = "交易 " + item.jndatetimeStr + "\n入账 " + item.effectdateStr) },
                                    leadingContent = { BillsIcons(name) },
                                    modifier = Modifier.clickable {
                                        infoNum = item
                                        showBottomSheet = true
                                    }
                                )
                            }
                            item { PaddingForPageControllerButton() }
                            item { InnerPaddingHeight(innerPadding,false) }
                        }
                        PageController(listState,currentPage, onNextPage = { currentPage = it }, onPreviousPage = { currentPage = it })
                    }
                }
            }
        }
    }
}