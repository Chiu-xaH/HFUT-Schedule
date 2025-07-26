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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.BadgedBox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.util.network.state.reEmptyLiveDta
import com.hfut.schedule.logic.util.network.toStr
import com.hfut.schedule.logic.util.network.toTimestampWithOutT
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.status.CenterScreen
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.status.DevelopingUI
import com.hfut.schedule.ui.component.status.LoadingScreen
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
  
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.EventCampus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getEventCampus
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


private const val TAB_LEFT = 0
private const val TAB_RIGHT = 1

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SupabaseHomeScreen(vm: NetWorkViewModel, sortType: SortType, sortReversed : Boolean, innerPadding : PaddingValues, pagerState : PagerState) {
    var refreshing by remember { mutableStateOf(true) }
    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")

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

    LaunchedEffect(jwt) {
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
        RefreshIndicator(refreshing, pullRefreshState,
            Modifier
                .padding(innerPadding)
                .align(Alignment.TopCenter)
                .zIndex(1f))
        HorizontalPager(state = pagerState) { page ->
            Scaffold {
                when(page) {
                    TAB_LEFT -> {
                        SupabaseScheduleUI(vm,sortType,sortReversed, innerPadding,refreshing) { refreshing = it }
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
private fun SupabaseScheduleUI(vm: NetWorkViewModel,sortType : SortType,sortReversed : Boolean,innerPadding : PaddingValues,refresh : Boolean,onRefresh : (Boolean) -> Unit) {
    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")
    val scope = rememberCoroutineScope()
//    var sortType by remember { mutableStateOf(SortType.ID) }
    val filter by DataStoreManager.supabaseFilterEventFlow.collectAsState(initial = false)

    if(refresh) {
        LoadingScreen()
    } else {
        val list = getEvents(vm)

        val filterList = if(filter) list.filter {
            // 自己班
            (
                    it.applicableClasses.toString().contains(getPersonInfo().classes!!) ||
                            it.applicableClasses.contains("EMPTY") ||
                            it.applicableClasses.toString().replace("[","").replace("]","").let { t ->
                                t.isBlank() || t.isEmpty() || t == "EMPTY"
                            }
            ) && // 自己校区
                    (it.campus == getEventCampus() || it.campus == EventCampus.DEFAULT)
        } else list
        var input by remember { mutableStateOf("") }

        val sortList =  when(sortType) {
            SortType.TIME_LINE -> filterList.sortedBy { when(it.type) {
                CustomEventType.NET_COURSE -> it.dateTime.end.toTimestampWithOutT()
                CustomEventType.SCHEDULE -> it.dateTime.start.toTimestampWithOutT()
            } }
            SortType.CREATE_TIME -> filterList.sortedBy { it.createTime.toTimestampWithOutT() }
        }.let { if (sortReversed) it.reversed() else it }.filter {
            it.toString().contains(input,ignoreCase = true)
        }

        val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }
        val downloadStates = remember { mutableStateMapOf<Int, Boolean>() }

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
            items(sortList.size, key = { sortList[it].id }) { index ->
                val item = sortList[index]

                val count by produceState(initialValue = "") {
                    vm.eventForkCountCache[item.id]?.let { value = it }
                }
                LaunchedEffect(item.id) {
                    async { vm.supabaseGetEventForkCount(jwt,item.id) }.await()
                }
                val isExpanded = expandedStates[index] ?: false
                var downloaded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)) {
                    MyCustomCard(containerColor = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { Text(item.name) },
                            overlineContent = { Text(item.timeDescription) },
                            supportingContent = item.description?.let { { Text(it) } },
                            trailingContent = {
                                // 检查是否已经存在 若存在相同id则显示别的按钮和操作
                                val isHasAdded by produceState(initialValue = false) {
                                    value = DataBaseManager.customEventDao.isExistBySupabaseId(item.id)
                                }
                                if(isHasAdded) {
                                    FilledTonalButton(
                                        onClick = {
                                            scope.launch {
                                                async {
                                                    val entity = CustomEventDTO(
                                                        title = item.name,
                                                        dateTime = item.dateTime,
                                                        type = item.type,
                                                        description = item.description,
                                                        remark = item.timeDescription,
                                                        supabaseId = item.id
                                                    )
                                                    // 检查是否已经存在 若存在相同id则直接覆盖掉
                                                    DataBaseManager.customEventDao.delBySupabaseId(item.id)
                                                    // 添加到数据库
                                                    DataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity))
                                                }.await()
                                                async {
                                                    showToast("已覆盖本地日程")
                                                }.await()
                                            }
                                        }
                                    ) {
                                        if(downloaded) {
                                            Icon(painter = painterResource(id = R.drawable.check), contentDescription = null)
                                        } else {
                                            Text("覆盖")
                                        }
                                    }
                                } else {
                                    FilledTonalIconButton(
                                        enabled = !downloaded,
                                        onClick = {
                                            scope.launch {
                                                async {
                                                    val entity = CustomEventDTO(
                                                        title = item.name,
                                                        dateTime = item.dateTime,
                                                        type = item.type,
                                                        description = item.description,
                                                        remark = item.timeDescription,
                                                        supabaseId = item.id
                                                    )
                                                    // 检查是否已经存在 若存在相同id则直接覆盖掉
                                                    // 添加到数据库
                                                    DataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity))
                                                }.await()
                                                async {
                                                    showToast("已克隆到本地")
                                                }.await()
                                                // 下载量++上传 下载量+1
                                                if(!downloaded)
                                                    launch {
                                                        downloaded = true
                                                        expandedStates[index] = true
                                                        vm.supabaseAddCount(jwt,item.id)
//                                                count = count.toIntOrNull()?.let { ( it + 1).toString() } ?: count
                                                    }
                                            }
                                        }
                                    ) {
                                        Icon(painter = painterResource(id = if(downloaded) R.drawable.check else R.drawable.download), contentDescription = null)
//                                BadgedBox(
//                                    badge = {
//                                        if (count.isNotEmpty() || count.isNotBlank())
//                                            Badge(
//                                                containerColor = MaterialTheme.colorScheme.primary,
//                                                modifier = Modifier.padding(horizontal = 5.dp)
//                                            ) {
//                                                Text(text = count)
//                                            }
//                                    }
//                                ) { Icon(painter = painterResource(id = if(downloaded) R.drawable.check else R.drawable.download), contentDescription = null) }
                                    }
                                }
                            },
                            leadingContent = {
                                BadgedBox(
                                    badge = {
                                        if (count.isNotEmpty() || count.isNotBlank())
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                            ) {
                                                Text(text = count)
                                            }
                                    },
                                ) {
                                    Icon(painterResource(
                                        when(item.type) {
                                            CustomEventType.SCHEDULE -> R.drawable.calendar
                                            CustomEventType.NET_COURSE -> R.drawable.net
                                        }
                                    ),null)
                                }
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
                            Column {
                                PaddingHorizontalDivider()
                                TransplantListItem(
                                    overlineContent = {
                                        Text("上传时间 ${item.createTime.toStr()}" ) },
                                    headlineContent = { Text(
                                        "来自 " +
                                                item.contributorClass.substringBefore("-") + "级"
                                                +
                                                " ********" +
                                                item.contributorId.substring(8,10)
                                    ) },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.person),null)
                                    },
                                )

                                TransplantListItem(
                                    headlineContent = { Text( "适用范围 " + when(item.campus) {
                                        EventCampus.DEFAULT -> "通用"
                                        EventCampus.XUANCHENG -> "宣城校区"
                                        EventCampus.HEFEI -> "合肥校区"
                                    }) },
                                    supportingContent = {
                                        var t = item.applicableClasses.toString().replace("[","").replace("]","")
                                        if(t.isBlank() || t.isEmpty() || t == "EMPTY") t = "所有人可见"
                                        Text(t)
                                    },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.target),null)
                                    },
                                )
                            }

                        }
                    }
                }

            }
            items(2) { InnerPaddingHeight(innerPadding,false) }
        }
    }
}