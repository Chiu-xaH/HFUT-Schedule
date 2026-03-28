package com.hfut.schedule.ui.component.dialog

import android.media.DrmInitData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.WheelPicker
import com.hfut.schedule.ui.screen.home.calendar.common.dateToWeek
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.xah.common.ui.style.align.CenterScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    isSchedule : Boolean = false,
    text : String = "截止",
    allowSelectPrevious : Boolean = false,
    onSelected: (Pair<String, String>) -> Unit,
    onDismiss: () -> Unit
) {

    val dateRangePickerState = rememberDateRangePickerState()

    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var startDateString by remember { mutableStateOf<String?>(null) }
    var endDateString by remember { mutableStateOf<String?>(null) }


    if(!isSchedule) {
        LaunchedEffect(Unit) {
            val todayStartMillis = DateTimeManager.getToday()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            startDate = todayStartMillis
            startDateString = DateTimeManager.simpleFormatter_YYYY_MM_DD.format(startDate)
        }
    }

//    val weekInfoStart = startDateString?.let { dateToWeek(it) }
//    val weekInfoEnd = endDateString?.let { dateToWeek(it) }
    val weekInfoStart by produceState<Pair<Int, Int>?>(initialValue = null,key1 = startDateString) {
        value = startDateString?.let { dateToWeek(it) }
    }
    val weekInfoEnd by produceState<Pair<Int, Int>?>(initialValue = null,key1 = endDateString) {
        value = endDateString?.let { dateToWeek(it) }
    }
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled =
                    startDateString  != null &&
                    endDateString != null &&
                    (
                            if(allowSelectPrevious)
                                true
                            else
                                endDate?.let { e -> startDate?.let { s -> s <= e } } == true
                    ),
                onClick = {
                    if(startDateString  != null && endDateString != null) {
                        onSelected(Pair(startDateString!!,endDateString!!))
                    }
                    onDismiss()
                }
            ) {
                Text("完成")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("取消")
            }
        }
    ) {
        if(isSchedule) {
            LaunchedEffect(dateRangePickerState.selectedEndDateMillis,dateRangePickerState.selectedStartDateMillis) {
                async {
                    launch { startDate = dateRangePickerState.selectedStartDateMillis }
                    launch { endDate = dateRangePickerState.selectedEndDateMillis }
                }.await()
                launch {
                    launch { startDateString = if(startDate != null) DateTimeManager.simpleFormatter_YYYY_MM_DD.format(startDate) else null }
                    launch { endDateString = if(endDate != null) DateTimeManager.simpleFormatter_YYYY_MM_DD.format(endDate) else null }
                }
            }



            DateRangePicker(
                state = dateRangePickerState,
                title = {  },
                headline = { Text(
                    "开始 ${startDateString ?: ""}" + (weekInfoStart?.let { " (第${it.first}周)" } ?: "")
                            + "\n" +  "结束 ${endDateString ?: ""}" + (weekInfoEnd?.let { " (第${it.first}周)" } ?: "")
                    , modifier = Modifier.padding(
                    APP_HORIZONTAL_DP
                )) },
                showModeToggle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(APP_HORIZONTAL_DP /3)
            )
        } else {
            val datePickerState = rememberDatePickerState()
            LaunchedEffect(datePickerState.selectedDateMillis) {
                async {
                    launch { endDate = datePickerState.selectedDateMillis }
                }.await()
                launch {
                    launch { endDateString = if(endDate != null) DateTimeManager.simpleFormatter_YYYY_MM_DD.format(endDate) else null }
                }
            }
            DatePicker(
                state = datePickerState,
                title = {   },
                headline = { Text("$text ${endDateString ?: ""}"  + (weekInfoEnd?.let { " (第${it.first}周)" } ?: ""), modifier = Modifier.padding(
                    APP_HORIZONTAL_DP
                )) },
                showModeToggle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(APP_HORIZONTAL_DP /3)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerOld(onSelected: (String) -> Unit) {
    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true,
    )

    LaunchedEffect(timePickerState.hour,timePickerState.minute) {
        onSelected(parseTimeItem(timePickerState.hour) + ":" + parseTimeItem(timePickerState.minute))
    }

    TimeInput(state = timePickerState)
}

@Composable
fun TimeRangePicker(
    isSchedule : Boolean,
    onSelected: (Pair<String,String>) -> Unit,
    onDismiss: () -> Unit
) {
    var startTime by remember { mutableStateOf<String>("00:00") }
    var endTime by remember { mutableStateOf<String?>(null) }
    var enabled by remember { mutableStateOf(false) }

    // 重选范围 清空
    LaunchedEffect(startTime,endTime) {
        // 判定时间合法性
        enabled = endTime != null
    }

    Column() {
        if(isSchedule) {
            TimePicker { startTime = it }
            PaddingHorizontalDivider(startPadding = false,endPadding = false)
        }
        TimePicker { endTime = it }

        Row(modifier = Modifier.align(Alignment.End)) {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("取消")
            }
            Spacer(Modifier.width(APP_HORIZONTAL_DP))
            TextButton(
                enabled = enabled,
                onClick = {
                    if(endTime != null) {
                        onSelected(Pair(startTime,endTime!!))
                    }
                    onDismiss()
                }
            ) { Text("完成") }
        }
    }
}


@Composable
fun TimeOnePicker(onSelected: (String) -> Unit,onDismiss: () -> Unit) {
    var endTime by remember { mutableStateOf<String?>(null) }
    var enabled by remember { mutableStateOf(false) }

    // 重选范围 清空
    LaunchedEffect(endTime) {
        // 判定时间合法性
        enabled = endTime != null
    }

    Column() {
        TimePicker { endTime = it }

        Row(modifier = Modifier.align(Alignment.End)) {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("取消")
            }
            Spacer(Modifier.width(APP_HORIZONTAL_DP))
            TextButton(
                enabled = enabled,
                onClick = {
                    if(endTime != null) {
                        onSelected(endTime!!)
                    }
                    onDismiss()
                }
            ) { Text("完成") }
        }
    }
}
@Composable
fun TimePickerDialog(onSelected: (String) -> Unit,onDismiss: () -> Unit)  {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(APP_HORIZONTAL_DP)
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP *2, vertical = APP_HORIZONTAL_DP),
                    verticalArrangement = Arrangement.spacedBy(APP_HORIZONTAL_DP)
                ) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP /3))
                    Text(
                        text = "输入截止时间",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TimeOnePicker(
                        onSelected = onSelected,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}
@Composable
fun TimeRangePickerDialog(isSchedule: Boolean,onSelected: (Pair<String,String>) -> Unit,onDismiss: () -> Unit)  {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(APP_HORIZONTAL_DP)
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP *2, vertical = APP_HORIZONTAL_DP),
                    verticalArrangement = Arrangement.spacedBy(APP_HORIZONTAL_DP)
                ) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP /3))
                    Text(
                        text = if(isSchedule) "输入起止时间" else "输入截止时间",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TimeRangePicker(
                        onSelected = onSelected,
                        onDismiss = onDismiss,
                        isSchedule = isSchedule
                    )
                }
            }
        }
    }
}

@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    initData: String? = null,
    onSelected: (String) -> Unit
) {
    var init = initData?.split(":")?.mapNotNull { it.toIntOrNull() } ?: listOf(0,0)
    if(init.size != 2) {
        init = listOf(0,0)
    }
    var hour by remember { mutableStateOf(init[0]) }
    var min by remember { mutableStateOf(init[1]) }

    LaunchedEffect(hour,min) {
        onSelected(parseTimeItem(hour) + ":" + parseTimeItem(min))
    }

    val hours = remember { (0..23).toList() }
    val minutes = remember { (0..59).toList() }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 小时
        WheelPicker(
            data = hours,
            selectIndex = init[0],
            modifier = Modifier.weight(1f),
            onSelect = { _, h ->
                hour = h
            },
        ) { hour ->
            Text(
                text = "%02d".format(hour),
                style = MaterialTheme.typography.titleLarge
            )
        }

        // 冒号
        Text(
            text = ":",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // 分钟
        WheelPicker(
            data = minutes,
            selectIndex = init[1],
            modifier = Modifier.weight(1f),
            onSelect = { _, m ->
                min = m
            },
        ) { minute ->
            Text(
                text = "%02d".format(minute),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}