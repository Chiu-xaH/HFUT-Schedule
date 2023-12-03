package com.hfut.schedule.ui.ComposeUI

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.logic.datamodel.ZJGD.LiushuiSearchResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelecctDateRange(vm : JxglstuViewModel) {

    val state = rememberDateRangePickerState()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = " " // 使用弹簧动画
    )

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                OutlinedButton(
                    modifier = Modifier.scale(scale.value),
                    interactionSource = interactionSource,
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        val formatter = SimpleDateFormat("yyyy-MM-dd")

                        val startDate = Date(state.selectedStartDateMillis!!)
                        val endDate = Date(state.selectedEndDateMillis!!)

                        val startDateString = formatter.format(startDate)
                        val endDateString = formatter.format(endDate)

                        val prefs = MyApplication.context.getSharedPreferences(
                            "com.hfut.schedule_preferences",
                            Context.MODE_PRIVATE
                        )
                        val auth = prefs.getString("auth", "")
                        CoroutineScope(Job()).apply {
                            launch {
                                async {
                                    vm.searchDate(
                                        "bearer " + auth,
                                        startDateString,
                                        endDateString
                                    )
                                }.await()
                                async {
                                    delay(500)
                                    val json =
                                        prefs.getString("searchyue", MyApplication.NullMonthYue)
                                    val data =
                                        Gson().fromJson(json, LiushuiSearchResponse::class.java)
                                    var zhichu = data.data.expenses
                                    zhichu = zhichu / 100

                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(
                                            MyApplication.context,
                                            "共支出 ${zhichu} 元",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
        }
        DateRangePicker(state = state,
            modifier = Modifier.weight(1f), title = { Text(text = "")},
          )
    }

}