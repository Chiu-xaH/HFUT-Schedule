package com.hfut.schedule.ui.ComposeUI.Search

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.OpenAlipay

@Composable
fun HotWater() {

    ListItem(
        headlineContent = { Text(text = "热水机") },
        supportingContent = { Text(text = "宣区六号六楼")},
        leadingContent = { Icon(painterResource(R.drawable.water_voc), contentDescription = "")},
        modifier = Modifier.clickable { OpenAlipay.openAlipay(MyApplication.AlipayHotWaterURL) }
    )
}