package com.hfut.schedule.ui.screen.supabase.manage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.container.CardListItem
  
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SupabaseMeScreenRefresh(vm : NetWorkViewModel,innerPadding : PaddingValues) {
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
    val uiState by vm.supabaseGetMyEventsResp.state.collectAsState()
    val refreshNetwork : suspend () -> Unit = {
        vm.supabaseGetMyEventsResp.clear()
        vm.supabaseDelResp.clear()
        vm.supabaseGetMyEvents()
    }
    val refreshing = uiState is UiState.Loading
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch { refreshNetwork() }
    })

    LaunchedEffect(jwt) {
        if ((jwt.isEmpty() || jwt.isBlank())) {
            showToast("未登录")
            return@LaunchedEffect
        }
        if ((jwt.isNotBlank() && jwt.isNotEmpty()) && refreshing) {
            refreshNetwork()
        }
    }

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(pullRefreshState)){
        RefreshIndicator(refreshing, pullRefreshState, Modifier
            .padding(innerPadding)
            .align(
                Alignment.TopCenter
            ))
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            SupabaseMeScreen(vm,innerPadding) {
                scope.launch { refreshNetwork() }
            }
        }
    }
}


@Composable
private fun SupabaseMeScreen(vm : NetWorkViewModel,innerPadding : PaddingValues,onRefresh :  () -> Unit) {
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
    var input by remember { mutableStateOf("") }
    var id by remember { mutableIntStateOf(-1) }
    var showDialogDel by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if(showDialogDel) {
        LittleDialog(
            onConfirmation = {
                if(id > 0) {
                    scope.launch {
                        vm.supabaseDelResp.clear()
                        vm.supabaseDel(jwt,id)
                        onListenStateHolder(vm.supabaseDelResp) { data ->
                            if(data) {
                                showToast("执行完成")
                                onRefresh()
                            } else {
                                showToast("执行失败")
                            }
                        }
                    }
                }
                showDialogDel = false
            },
            onDismissRequest = { showDialogDel = false },
            dialogText = "要删除这条吗，他人将看不到共享的内容"
        )
    }


    var showDialogEdit by remember { mutableStateOf(false) }
    val uiState by vm.supabaseGetMyEventsResp.state.collectAsState()
    val data = (uiState as UiState.Success).data

    val list = data.filter {
        it.toString().contains(input,ignoreCase = true)
    }
    LazyColumn {
        item { InnerPaddingHeight(innerPadding,true) }
        item {
            CustomTextField(
                input = input,
                label = { Text("搜索") },
                leadingIcon = { Icon(painterResource(R.drawable.search),null)}
            ) { input = it }
            Spacer(Modifier.height(CARD_NORMAL_DP))
        }
        items(list.size) { index ->
            val item = list[index]
            val dateTime = item.dateTime
            val nowTimeNum = DateTimeManager.Date_yyyy_MM_dd.replace("-","").toLong()
            val endNum = with(dateTime.end) { "$year${parseTimeItem(month)}${parseTimeItem(day)}" }.toLong()
            val isOutOfDate = nowTimeNum > endNum
            CardListItem(
                headlineContent = { Text(item.name, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
                overlineContent = { Text(item.timeDescription, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
                supportingContent = item.description?.let { { Text(it, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) } },
                trailingContent = {
                    Row {
//                            FilledTonalIconButton(
//                                onClick = {
//                                    id = item.id
//                                    showToast("正在开发")
//                                    showDialogEdit = true
//                                }
//                            ) { Icon(painterResource(R.drawable.edit),null) }
                        ColumnVertical {
                            FilledTonalIconButton(
                                onClick = {
                                    id = item.id
                                    showDialogDel = true
                                }
                            ) { Icon(painterResource(R.drawable.close),null) }
                            if(isOutOfDate) {
                                Text("已过期不可见")
                            }
                        }
                    }
                },
                leadingContent = {
                    Icon(
                        painterResource(
                            when(item.type) {
                                CustomEventType.SCHEDULE -> R.drawable.calendar
                                CustomEventType.NET_COURSE -> R.drawable.net
                            }
                        ),null)
                }
            )
        }
        items(2) { InnerPaddingHeight(innerPadding,false) }
    }

}