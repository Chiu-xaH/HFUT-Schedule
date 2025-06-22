package com.hfut.schedule.ui.component

import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    isSchedule : Boolean,
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

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = startDateString  != null && endDateString != null && endDate?.let { e -> startDate?.let { s -> s <= e } } == true,
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
                headline = { Text("开始 ${startDateString ?: ""}\n结束 ${endDateString ?: ""}" , modifier = Modifier.padding(APP_HORIZONTAL_DP)) },
                showModeToggle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(APP_HORIZONTAL_DP/3)
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
                headline = { Text("截止 ${endDateString ?: ""}", modifier = Modifier.padding(APP_HORIZONTAL_DP)) },
                showModeToggle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(APP_HORIZONTAL_DP/3)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(onSelected: (String) -> Unit) {
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
        if(isSchedule)
            TimePicker { startTime = it }
        TimePicker { endTime = it }

        Row(modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
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

        Row(modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
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
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP*2, vertical = APP_HORIZONTAL_DP),
                    verticalArrangement = Arrangement.spacedBy(APP_HORIZONTAL_DP)
                ) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP/3))
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
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP*2, vertical = APP_HORIZONTAL_DP),
                    verticalArrangement = Arrangement.spacedBy(APP_HORIZONTAL_DP)
                ) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP/3))
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
