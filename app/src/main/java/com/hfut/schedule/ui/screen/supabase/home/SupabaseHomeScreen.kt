package com.hfut.schedule.ui.screen.supabase.home

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.logic.util.network.supabaseEventEntityToDto
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.parseToDateTime
import com.hfut.schedule.ui.component.CenterScreen
import com.hfut.schedule.ui.component.DevelopingUI
import com.hfut.schedule.ui.component.LoadingScreen
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.RefreshIndicator
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


fun getEvents(vm: NetWorkViewModel,isMine : Boolean) : List<SupabaseEventsInput> {
    val json = if(!isMine) vm.supabaseGetEventsResp.value else vm.supabaseGetMyEventsResp.value
    return try {
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        val newList = list.mapNotNull { item -> supabaseEventEntityToDto(item) }
        return newList
    } catch (e : Exception) {
        e.printStackTrace()
        emptyList()
    }
}

private const val TAB_LEFT = 0
private const val TAB_RIGHT = 1

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SupabaseHomeScreen(vm: NetWorkViewModel,innerPadding : PaddingValues,pagerState : PagerState) {
    var refreshing by remember { mutableStateOf(true) }
    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")
//    val showAll by DataStoreManager.supabaseShowAllScheduleFlow.collectAsState(initial = false)
    var addRefresh by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
            }.await()
            launch {
                if ((jwt.isNotBlank() || jwt.isNotEmpty()) && refreshing) {
                    reEmptyLiveDta(vm.supabaseGetEventsResp)
                    vm.supabaseGetEvents(jwt)
                }
            }
        }
    })

    LaunchedEffect(jwt,addRefresh) {
        if ((jwt.isNotBlank() || jwt.isNotEmpty()) && refreshing) {
            refreshing = true
            reEmptyLiveDta(vm.supabaseGetEventsResp)
            vm.supabaseGetEvents(jwt)
        }
    }
    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).post {
            vm.supabaseGetEventsResp.observeForever { result ->
                if (result != null) {
                    refreshing = false
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(pullRefreshState)){
        RefreshIndicator(refreshing, pullRefreshState, Modifier.padding(innerPadding).align(Alignment.TopCenter).zIndex(1f))
        HorizontalPager(state = pagerState) { page ->
            Scaffold {
                when(page) {
                    TAB_LEFT -> {
                        SupabaseScheduleUI(vm, innerPadding,refreshing) { refreshing = it }
                    }
                    TAB_RIGHT -> {
                        CenterScreen { DevelopingUI() }
                    }
                }
            }
        }
    }
}
@Composable
fun SupabaseScheduleUI(vm: NetWorkViewModel,innerPadding : PaddingValues,refresh : Boolean,onRefresh : (Boolean) -> Unit) {
    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")
    val scope = rememberCoroutineScope()
    if(refresh) {
        LoadingScreen()
    } else {
        val list = getEvents(vm,false)
        val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }
        val downloadStates = remember { mutableStateMapOf<Int, Boolean>() }


        LazyColumn {
            item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
            items(list.size, key = { list[it].id }) { index ->
                val item = list[index]
                var count by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    async { vm.supabaseGetEventForkCount(jwt,item.id) }.await()
                    launch {
                        Handler(Looper.getMainLooper()).post{
                            Handler(Looper.getMainLooper()).post {
                                vm.supabaseGetEventForkCountResp.observeForever { result ->
                                    if (result != null && result.toIntOrNull() != null) {
                                        count = result
                                    }
                                }
                            }
                        }
                    }
                }
                val isExpanded = expandedStates[index] ?: true
                val downloaded = downloadStates[index] ?: false

                MyCustomCard(containerColor = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text(item.name) },
                        overlineContent = { Text(item.timeDescription) },
                        supportingContent = item.description?.let { { Text(it) } },
                        trailingContent = {
                            FilledTonalButton(
                                enabled = (downloadStates[index] != false),
                                onClick = {
                                    scope.launch {
                                        async {
                                            val entity = CustomEventDTO(
                                                title = item.name,
                                                dateTime = item.dateTime,
                                                type = item.type,
                                                description = item.description,
                                                remark = item.timeDescription
                                            )
                                            // 添加到数据库
                                            DataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity))
                                        }.await()
                                        async {
                                            showToast("已下载到本地，位于聚焦")
                                        }.await()
                                        // 下载量++上传 下载量+1
                                        if(!downloaded)
                                            launch {
                                                downloadStates[index] = true
                                                expandedStates[index] = true
                                                vm.supabaseAddCount(jwt,item.id)
                                                count = count.toIntOrNull()?.let { ( it + 1).toString() } ?: count
                                            }
                                    }
                                }
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (count.isNotEmpty() || count.isNotBlank())
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 5.dp)
                                            ) {
                                                Text(text = count)
                                            }
                                    }
                                ) { Icon(painter = painterResource(id = if(downloaded) R.drawable.check else R.drawable.download), contentDescription = null) }
                            }
                        },
                        leadingContent = {
                            Icon(painterResource(
                                when(item.type) {
                                    CustomEventType.SCHEDULE -> R.drawable.calendar
                                    CustomEventType.NET_COURSE -> R.drawable.net
                                }
                            ),null)
                        },
                        modifier = Modifier.clickable { expandedStates[index] = !isExpanded }
                    )
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = slideInVertically(
                            initialOffsetY = { -40 }
                        ) + expandVertically(
                            expandFrom = Alignment.Top
                        ) + scaleIn(
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        ) + fadeIn(initialAlpha = 0.3f),
                        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                    ) {
                        HorizontalDivider()
                        TransplantListItem(
                            overlineContent = { Text("上传时间 ${item.createTime}") },
                            headlineContent = { Text("来自 " +item.contributorClass + " ******" + item.contributorId.substring(6,10)) },
                            supportingContent = {
                                var t = item.applicableClasses.toString().replace("[","").replace("]","")
                                if(t.isBlank() || t.isEmpty()) t = "所有人可见"
                                Text(t)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.info),null)
                            },
                            // 反馈：信息不实、不适宜展示
                        )
                    }
                }
            }
            items(2) { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
        }
    }
}