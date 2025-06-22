package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.TransplantListItem

@Composable
fun RequestArrange(innerPadding : PaddingValues) {
    LazyColumn() {
        item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
        item { ArrangeItem(title = "图书", icon = R.drawable.book, key = "BookRequest") }
        item { ArrangeItem(title = "一卡通", icon = R.drawable.credit_card, key = "CardRequest") }
        item { ArrangeItem(title = "挂科率", icon = R.drawable.monitoring, key = "FailRateRequest") }
        item { ArrangeItem(title = "开课查询", icon = R.drawable.search, key = "CourseSearchRequest") }
        item { ArrangeItem(title = "教师检索", icon = R.drawable.group, key = "TeacherSearchRequest") }
        item { ArrangeItem(title = "就业信息", icon = R.drawable.work, key = "WorkSearchRequest") }
        item { ArrangeItem(title = "好友课表列表", icon = R.drawable.calendar, key = "FriendRequest") }
        item { ArrangeItem(title = "通知公告", icon = R.drawable.stream, key = "NewsRequest",false) }
        item { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
    }
}


@Composable
fun ArrangeItem(title : String, icon : Int, key : String,canUse : Boolean = true) {
    val pageSize = prefs.getString(key,MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE.toString()
    var sliderPosition by remember { mutableFloatStateOf(pageSize.toFloat()) }
//    val bd = BigDecimal(sliderPosition.toString())
    val str = formatDecimal(sliderPosition.toDouble(),0)
    TransplantListItem(
            headlineContent = { Text(text = "$title   $str 条/页")},
            leadingContent = { Icon(painterResource(id = icon), contentDescription = "") },
            supportingContent = {
                Slider(
                    enabled = canUse,
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
//                        val bd = BigDecimal(sliderPosition.toString())
                        val str = formatDecimal(sliderPosition.toDouble(),0)
                        SharedPrefs.saveString(key,str)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 39,
                    valueRange = 10f..50f,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }
        )
}