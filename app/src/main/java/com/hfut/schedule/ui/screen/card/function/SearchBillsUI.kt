package com.hfut.schedule.ui.screen.card.function

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.BillsIcons
import com.hfut.schedule.ui.component.CenterScreen
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.PagingController
import com.hfut.schedule.ui.component.PrepareSearchUI
import com.hfut.schedule.ui.screen.card.bill.main.processTranamt
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchBillsUI(vm : NetWorkViewModel) {
    var input by remember { mutableStateOf("") }
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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("流水搜索")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            RowHorizontal {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("输入关键字检索") },
                    singleLine = true,
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
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
            }

            CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
                if(!startUse) startUse = true

                val response = (uiState as UiState.Success).data
                val list = response.records
                list.let {
                    if(it.isEmpty()) {
                        CenterScreen { EmptyUI() }
                    } else {
                        val listState = rememberLazyListState()
                        Box {
                            LazyColumn(state = listState) {
                                items(list.size, key = { it }) { index ->
                                    val item = list[index]
                                    var name = item.resume
                                    if (name.contains("有限公司")) name = name.replace("有限公司","")
                                    AnimationCardListItem(
                                        headlineContent = { Text(text = name) },
                                        supportingContent = { Text(text = processTranamt(item))},
                                        overlineContent = { Text(text = item.effectdateStr)},
                                        leadingContent = { BillsIcons(name) },
                                        index = index
                                    )
                                }
                                item { PaddingForPageControllerButton() }
                            }
                            PagingController(listState,currentPage,showUp = true, nextPage = { currentPage = it }, previousPage = { currentPage = it })
                        }
                    }
                }

            }
        }
    }
}