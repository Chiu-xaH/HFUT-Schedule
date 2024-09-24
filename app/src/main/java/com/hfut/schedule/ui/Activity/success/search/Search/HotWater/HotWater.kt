package com.hfut.schedule.ui.Activity.success.search.Search.HotWater

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.MyToast

@Composable
fun HotWater() {
    ListItem(
        headlineContent = { Text(text = "热水机") },
        leadingContent = { Icon(painterResource(R.drawable.water_voc), contentDescription = "")},
        modifier = Modifier.clickable { StartApp.openAlipay(MyApplication.AlipayHotWaterURL) }
    )
}