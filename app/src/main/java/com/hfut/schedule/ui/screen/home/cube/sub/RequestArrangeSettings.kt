package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.TransplantListItem
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun RequestArrange(innerPadding : PaddingValues) {
    LazyColumn() {
        item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
        item { ArrangeItem(title = "图书", icon = R.drawable.book, key = "BookRequest") }
        item { ArrangeItem(title = "一卡通", icon = R.drawable.credit_card, key = "CardRequest") }
        item { ArrangeItem(title = "挂科率", icon = R.drawable.monitoring, key = "FailRateRequest") }
        item { ArrangeItem(title = "开课查询", icon = R.drawable.search, key = "CourseSearchRequest") }
        item { ArrangeItem(title = "教师检索", icon = R.drawable.group, key = "TeacherSearchRequest") }
        item { ArrangeItem(title = "通知公告", icon = R.drawable.stream, key = "NewsRequest",false) }
        item { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
    }
}


@Composable
fun ArrangeItem(title : String, icon : Int, key : String,canUse : Boolean = true) {
    val pageSize = prefs.getString(key,MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE.toString()
    var sliderPosition by remember { mutableStateOf(pageSize.toFloat()) }
    val bd = BigDecimal(sliderPosition.toString())
    val str = bd.setScale(0, RoundingMode.HALF_UP).toString()
    TransplantListItem(
            headlineContent = { Text(text = "$title   $str 条/页")},
            leadingContent = { Icon(painterResource(id = icon), contentDescription = "") },
            supportingContent = {
                Slider(
                    enabled = canUse,
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                        val bd = BigDecimal(sliderPosition.toString())
                        val str = bd.setScale(0, RoundingMode.HALF_UP).toString()
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