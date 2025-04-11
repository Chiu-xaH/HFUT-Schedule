package com.hfut.schedule.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
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
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onSelected: (Pair<String, String>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var startDateString by remember { mutableStateOf<String?>(null) }
    var endDateString by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dateRangePickerState.selectedEndDateMillis,dateRangePickerState.selectedStartDateMillis) {
        async {
            launch { startDate = dateRangePickerState.selectedStartDateMillis }
            launch { endDate = dateRangePickerState.selectedEndDateMillis }
        }.await()
        launch {
            launch { startDateString = if(startDate != null) DateTimeUtils.simpleFormatter_YYYY_MM_DD.format(startDate) else null }
            launch { endDateString = if(endDate != null) DateTimeUtils.simpleFormatter_YYYY_MM_DD.format(endDate) else null }
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = startDateString  != null && endDateString != null,
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
        DateRangePicker(
            state = dateRangePickerState,
            title = {  },
            headline = { Text("开始 ${startDateString ?: ""}\n结束 ${endDateString ?: ""}", modifier = Modifier.padding(appHorizontalDp())) },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(appHorizontalDp()/3)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(onSelected: (String) -> Unit) {
    val timePickerState = rememberTimePickerState(
        initialHour = DateTimeUtils.Time_Hour.toInt(),
        initialMinute = DateTimeUtils.Time_Minute.toInt(),
        is24Hour = true,
    )

    LaunchedEffect(timePickerState.hour,timePickerState.minute) {
        onSelected(parseTimeItem(timePickerState.hour) + ":" + parseTimeItem(timePickerState.minute))
    }

    TimeInput(state = timePickerState)
}

@Composable
fun TimeRangePicker(onSelected: (Pair<String,String>) -> Unit,onDismiss: () -> Unit) {
    var startTime by remember { mutableStateOf<String?>(null) }
    var endTime by remember { mutableStateOf<String?>(null) }
    var enabled by remember { mutableStateOf(false) }

    // 重选范围 清空
    LaunchedEffect(startTime,endTime) {
        launch {
            if(startTime == null)
                endTime = null
        }
        // 判定时间合法性
        launch {
            enabled = if(startTime != null && endTime != null) {
                DateTimeUtils.compareTime(startTime = startTime!!, endTime = endTime!!) != DateTimeUtils.TimeState.ENDED
            } else {
                false
            }
        }
    }

    Column() {
        TimePicker { startTime = it }
        TimePicker { endTime = it }

        Row(modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("取消")
            }
            Spacer(Modifier.width(appHorizontalDp()))
            TextButton(
                enabled = enabled,
                onClick = {
                    if(startTime != null && endTime != null) {
                        onSelected(Pair(startTime!!,endTime!!))
                    }
                    onDismiss()
                }
            ) { Text("完成") }
        }
    }
}
@Composable
fun TimeRangePickerDialog(onSelected: (Pair<String,String>) -> Unit,onDismiss: () -> Unit)  {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(appHorizontalDp())
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(horizontal = appHorizontalDp()*2, vertical = appHorizontalDp()),
                    verticalArrangement = Arrangement.spacedBy(appHorizontalDp())
                ) {
                    Spacer(Modifier.height(appHorizontalDp()/3))
                    Text(
                        text = "输入起止时间",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TimeRangePicker(
                        onSelected = onSelected,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}
