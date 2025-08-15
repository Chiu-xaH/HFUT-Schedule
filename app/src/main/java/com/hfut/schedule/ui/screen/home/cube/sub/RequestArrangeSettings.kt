package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.xah.transition.util.TransitionPredictiveBackHandler

@Composable
fun RequestArrange(innerPadding : PaddingValues,navController: NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController) {
        scale = it
    }
    LazyColumn(modifier = Modifier.scale(scale)) {
        item { InnerPaddingHeight(innerPadding,true) }
        item { ArrangeItem(title = AppNavRoute.Library.label, icon = AppNavRoute.Library.icon, key = "BookRequest") }
        item { ArrangeItem(title = "一卡通", icon = R.drawable.credit_card, key = "CardRequest") }
        item { ArrangeItem(title = AppNavRoute.FailRate.label, icon = AppNavRoute.FailRate.icon, key = "FailRateRequest") }
        item { ArrangeItem(title = AppNavRoute.CourseSearch.label, icon = AppNavRoute.CourseSearch.icon, key = "CourseSearchRequest") }
        item { ArrangeItem(title = AppNavRoute.TeacherSearch.label, icon = AppNavRoute.TeacherSearch.icon, key = "TeacherSearchRequest") }
        item { ArrangeItem(title = AppNavRoute.Work.label, icon = AppNavRoute.Work.icon, key = "WorkSearchRequest") }
        item { ArrangeItem(title = "好友课表列表", icon = AppNavRoute.NextCourse.icon, key = "FriendRequest") }
        item { ArrangeItem(title = AppNavRoute.HaiLeWashing.label, icon = AppNavRoute.HaiLeWashing.icon, key = "HaileRequest") }
        item { ArrangeItem(title = AppNavRoute.OfficeHall.label, icon = AppNavRoute.OfficeHall.icon, key = "OfficeHallRequest") }

//        item { ArrangeItem(title = "通知公告", icon = R.drawable.stream, key = "NewsRequest",false) }
        item { InnerPaddingHeight(innerPadding,false) }
    }
}


@Composable
fun ArrangeItem(title : String, icon : Int, key : String,canUse : Boolean = true) {
    val pageSize = prefs.getString(key,MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE.toString()
    var sliderPosition by remember { mutableFloatStateOf(pageSize.toFloat()) }
    val str = formatDecimal(sliderPosition.toDouble(),0)
    MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
        TransplantListItem(
            overlineContent = { Text(text = title)},
            headlineContent = { Text("$str 条/页")},
            leadingContent = { Icon(painterResource(id = icon), contentDescription = "") },
        )
        Slider(
            enabled = canUse,
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
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
    Spacer(Modifier.height(APP_HORIZONTAL_DP/3))
}