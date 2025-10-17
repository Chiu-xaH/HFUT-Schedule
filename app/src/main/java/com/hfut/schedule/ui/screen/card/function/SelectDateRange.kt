package com.hfut.schedule.ui.screen.card.function

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.logic.util.sys.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelecctDateRange(vm : NetWorkViewModel) {

    val state = rememberDateRangePickerState()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = " " // 使用弹簧动画
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("范围支出") {
                FilledTonalButton(
                    modifier = Modifier.scale(scale.value),
                    interactionSource = interactionSource,
                    onClick = {
                        scope.launch { getRangeData(vm,state) }
                    },
                    enabled = state.selectedEndDateMillis != null
                ) {
                    Text(text = "查看总支出")
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            DateRangePicker(state = state,
                modifier = Modifier.weight(1f), title = { Text(text = "")},
                colors = DatePickerDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun getRangeData(vm: NetWorkViewModel, state: DateRangePickerState) = withContext(Dispatchers.IO) {
    val startDate = Date(state.selectedStartDateMillis!!)
    val endDate = Date(state.selectedEndDateMillis!!)

    val startDateString = DateTimeManager.simpleFormatter_YYYY_MM_DD.format(startDate)
    val endDateString = DateTimeManager.simpleFormatter_YYYY_MM_DD.format(endDate)

    val auth = prefs.getString("auth", "")
    vm.huiXinRangeResult.clear()
    vm.searchDate("bearer $auth", startDateString, endDateString)
    // 主线程监听 StateFlow
    onListenStateHolder(vm.huiXinRangeResult) { data ->
        showToast("共支出 $data 元")
    }
}