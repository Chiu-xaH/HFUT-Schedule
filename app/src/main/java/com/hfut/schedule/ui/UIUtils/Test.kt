package com.hfut.schedule.ui.UIUtils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.ComposeUI.Focus.AddItems
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Tests() {
    LazyCellsTest()
}

fun Clicks() {
    val startMillis: Long = Calendar.getInstance().run {
        set(2024, 1, 8, 7, 30)
        timeInMillis
    }
    val endMillis: Long = Calendar.getInstance().run {
        set(2024, 1, 8, 8, 30)
        timeInMillis
    }

    val intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        .putExtra(CalendarContract.Events.TITLE, "Yoga")
        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    MyApplication.context.startActivity(intent)
//
}

fun AddCalendar(Start : List<Int>, End : List<Int>, Place : String, Title : String, Remark : String) {
    //2月10号2
    val beginTime = Calendar.getInstance()
    beginTime.set(Start[0], Start[1] - 1, Start[2], Start[3], Start[4]) // 2024年1月10日上午8:30
    val endTime = Calendar.getInstance()
    endTime.set(End[0], End[1] - 1, End[2], End[3], End[4]) // 2024年1月10日上午9:30

    val uri = CalendarContract.Events.CONTENT_URI

// 创建一个日程的内容值对象，设置日程的相关属性
    val values = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, 1) // 日历的ID，可以通过查询日历提供者获取
        put(CalendarContract.Events.TITLE, Title) // 标题
        put(CalendarContract.Events.DESCRIPTION, Remark) // 备注
        put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
        put(CalendarContract.Events.DTEND, endTime.timeInMillis)
        put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai") // 时区
        put(CalendarContract.Events.EVENT_LOCATION, Place) // 地点
    }

// 在同步适配器的 onPerformSync() 方法中，使用内容解析器插入日程
    val resolver = MyApplication.context.contentResolver
    resolver.insert(uri, values) // 第三个参数是一个Bundle对象，用于指定同步适配器的相关信息，如账户名称和类型，以及是否强制同步

}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyCellsTest() {
    Box(modifier = Modifier
        .fillMaxHeight()
       ) {
        val scrollstate = rememberLazyGridState()
        val shouldShowAddButton = scrollstate.firstVisibleItemScrollOffset == 0
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.padding(10.dp),
            state = scrollstate
        ) {
            items(25) { cell ->
                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.height(120.dp).padding(2.dp)
                ) {
                    Text(text = "第一节课\n    $cell")
                }
            }
        }
      //  AddButton2(isVisible = shouldShowAddButton)
    }
}




