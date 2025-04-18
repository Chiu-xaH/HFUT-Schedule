package com.hfut.schedule.ui.screen.card.function

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.zjgd.BillRangeResponse
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("范围支出") {
                FilledTonalButton(
                    modifier = Modifier.scale(scale.value),
                    interactionSource = interactionSource,
                    //shape = MaterialTheme.shapes.small,
                    onClick = {
                        val startDate = Date(state.selectedStartDateMillis!!)
                        val endDate = Date(state.selectedEndDateMillis!!)

                        val startDateString = DateTimeUtils.simpleFormatter_YYYY_MM_DD.format(startDate)
                        val endDateString = DateTimeUtils.simpleFormatter_YYYY_MM_DD.format(endDate)

                        val auth = prefs.getString("auth", "")
                        CoroutineScope(Job()).apply {
                            launch {
                                async {
                                    Handler(Looper.getMainLooper()).post{
                                        vm.RangeData.value = "{}"
                                    }
                                    vm.searchDate("bearer " + auth, startDateString, endDateString)
                                }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post{
                                        vm.RangeData.observeForever { result ->
                                            if(result.contains("操作成功")){
                                                try {
                                                    val data = Gson().fromJson(result, BillRangeResponse::class.java)
                                                    var zhichu = data.data.expenses
                                                    zhichu /= 100
                                                    showToast("共支出 $zhichu 元")
                                                } catch (e : Exception) {
                                                    showToast("错误")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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