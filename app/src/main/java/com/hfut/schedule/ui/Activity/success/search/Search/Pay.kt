package com.hfut.schedule.ui.Activity.success.search.Search

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.UIUtils.MyToast

@Composable
fun Pay() {
    ListItem(
        headlineContent = { Text(text = "缴费") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.monetization_on), contentDescription = "") },
        modifier = Modifier.clickable {
            MyToast("暂未开发")
        }
    )
}