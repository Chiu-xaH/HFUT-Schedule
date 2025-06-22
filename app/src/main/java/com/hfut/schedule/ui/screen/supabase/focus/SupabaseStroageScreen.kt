package com.hfut.schedule.ui.screen.supabase.focus

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.network.ParseJsons.getCustomEvent
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.custom.CustomTextField
import com.hfut.schedule.ui.component.custom.LittleDialog
import com.hfut.schedule.ui.component.StyleCardListItem
  
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.focus.funiction.openOperation
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.style.ColumnVertical
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun SupabaseStorageScreen(innerPadding : PaddingValues,hazeState : HazeState) {
//    var customNetCourseList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    var customScheduleList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    val activity = LocalActivity.current
    var refreshDB by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    // 初始化
    LaunchedEffect(refreshDB) {
        // 加载数据库
//        launch { customNetCourseList = getCustomNetCourse(isSupabase = true) }
        launch { customScheduleList = getCustomEvent(isSupabase = true) }
    }
    LaunchedEffect(input) {
        customScheduleList = customScheduleList.filter { it.toString().contains(input,ignoreCase = true) }
    }

    LazyColumn {
        item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
        item {
            CustomTextField(
                input = input,
                label = { Text("搜索") },
                leadingIcon = { Icon(painterResource(R.drawable.search),null)}
            ) { input = it }
            Spacer(Modifier.height(CARD_NORMAL_DP))
        }
        customScheduleList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1) { refreshDB = !refreshDB } } } }
//        customNetCourseList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1) { refreshDB = !refreshDB } } } }
        items(2) { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
    }
}

@Composable
private fun CustomItem(item : CustomEventDTO, hazeState: HazeState, activity: Activity, refresh : () -> Unit) {
    val dateTime = item.dateTime
    val nowTimeNum = DateTimeManager.Date_yyyy_MM_dd.replace("-","").toLong()
    val endNum = with(dateTime.end) { "$year${parseTimeItem(month)}${parseTimeItem(day)}" }.toLong()
    CustomItemUI(
        item,
        activity,
        hazeState,
        isOutOfDate = nowTimeNum > endNum,
        refresh
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomItemUI(item: CustomEventDTO,activity: Activity,hazeState: HazeState,isOutOfDate : Boolean = false,refresh: () -> Unit) {
    val title = item.title
    val description = item.description
    val dateTime = item.dateTime
    var id by remember { mutableIntStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if(showDialog)
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                if(id > 0) {
                    scope.launch {
                        async { DataBaseManager.customEventDao.del(id) }.await()
                        launch { refresh() }
                        launch { showDialog = false }
                    }
                } else {
                    showToast("id错误")
                }
            },
            dialogText = "要删除此项吗",
            hazeState = hazeState
        )


    StyleCardListItem(
        headlineContent = { Text(text = title, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        overlineContent = { Text(text = item.remark,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        supportingContent = { description?.let { Text(text = it,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) } },
        leadingContent = {
            Icon(
                painterResource(if(item.type == CustomEventType.SCHEDULE) R.drawable.calendar else R.drawable.net),
                contentDescription = "Localized description",
            )
        },
        trailingContent = {
            ColumnVertical {
                if(isOutOfDate) {
                    FilledTonalIconButton(
                        onClick = {
                            id = item.id
                            showDialog = true
                        }
                    ) { Icon(painterResource(R.drawable.delete), null) }
                } else {
                    FilledTonalIconButton(
                        onClick = {
                            scope.launch {
                                addToCalendars(
                                    dateTime,
                                    description,
                                    title,
                                    null,
                                    activity,
                                    item.type == CustomEventType.SCHEDULE
                                )
                            }

                        }
                    ) { Icon(painterResource(R.drawable.event_upcoming), null) }
                }
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = { description?.let { openOperation(it) } },
            onDoubleClick = {
                //双击操作
            },
            onLongClick = {
                //长按操作
                id = item.id
                showDialog = true
            })
    )
}