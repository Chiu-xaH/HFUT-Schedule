package com.hfut.schedule.ui.screen.supabase.manage

import android.os.Handler
import android.os.Looper
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.LoadingScreen
import com.hfut.schedule.ui.component.RefreshIndicator
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.supabase.home.getEvents
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SupabaseMeScreenRefresh(vm : NetWorkViewModel,innerPadding : PaddingValues) {
    var refreshing by remember { mutableStateOf(true) }
    var delRefresh by remember { mutableStateOf(true) }

    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")

    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
            }.await()
            launch {
                reEmptyLiveDta(vm.supabaseGetMyEventsResp)
                reEmptyLiveDta(vm.supabaseDelResp)
                vm.supabaseGetMyEvents(jwt)
            }
        }
    })
    LaunchedEffect(jwt,delRefresh) {
        if ((jwt.isNotBlank() || jwt.isNotEmpty()) && refreshing) {
            refreshing = true
            reEmptyLiveDta(vm.supabaseGetMyEventsResp)
            reEmptyLiveDta(vm.supabaseDelResp)
            vm.supabaseGetMyEvents(jwt)
        }
    }

    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).post {
            vm.supabaseGetMyEventsResp.observeForever { result ->
                if (result != null) {
                    refreshing = false
                }
            }
            vm.supabaseDelResp.observeForever { result ->
                if (result != null) {
                    delRefresh = !delRefresh
                    showToast("执行完成")
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(pullRefreshState)){
        RefreshIndicator(refreshing, pullRefreshState, Modifier.padding(innerPadding).align(
            Alignment.TopCenter))
        SupabaseMeScreen(vm,innerPadding,refreshing) { refreshing = it }
    }
}


@Composable
private fun SupabaseMeScreen(vm : NetWorkViewModel,innerPadding : PaddingValues,refresh : Boolean,onRefresh : (Boolean) -> Unit ) {
    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")

    var id by remember { mutableIntStateOf(-1) }
    var showDialogDel by remember { mutableStateOf(false) }

    if(showDialogDel) {
        LittleDialog(
            onConfirmation = {
                if(id > 0) {
                    vm.supabaseDel(jwt,id)
                    onRefresh(true)
                }
                showDialogDel = false
            },
            onDismissRequest = { showDialogDel = false },
            dialogText = "要删除这条吗，他人将看不到共享的内容"
        )
    }

    var showDialogEdit by remember { mutableStateOf(false) }

//    if(showDialogEdit) {
//
//    }


    if(refresh) {
        LoadingScreen()
    } else {
        val list = getEvents(vm,true)
        LazyColumn {
            item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
            items(list.size) { index ->
                val item = list[index]
                StyleCardListItem(
                    headlineContent = { Text(item.name) },
                    overlineContent = { Text(item.timeDescription) },
                    supportingContent = item.description?.let { { Text(it) } },
                    trailingContent = {
                        Row {
                            FilledTonalIconButton(
                                onClick = {
                                    id = item.id
                                    showToast("正在开发")
                                    showDialogEdit = true
                                }
                            ) { Icon(painterResource(R.drawable.edit),null) }
                            FilledTonalIconButton(
                                onClick = {
                                    id = item.id
                                    showDialogDel = true
                                }
                            ) { Icon(painterResource(R.drawable.close),null) }
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
            items(2) { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
        }
    }
}