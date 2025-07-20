package com.hfut.schedule.ui.screen.home.search.function.huiXin.hotWater

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem

@Composable
fun HotWater() {
    TransplantListItem(
        headlineContent = { Text(text = "热水") },
        leadingContent = { Icon(painterResource(R.drawable.water_voc), contentDescription = "")},
        modifier = Modifier.clickable { Starter.startAppUrl(MyApplication.ALIPAY_HOT_WATER_URL) }
    )
}