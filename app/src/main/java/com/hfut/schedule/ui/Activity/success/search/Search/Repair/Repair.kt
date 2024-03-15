package com.hfut.schedule.ui.Activity.success.search.Search.Repair

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
fun Repair() {
    ListItem(
        headlineContent = { Text(text = "报修") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.build), contentDescription = "") },
        modifier = Modifier.clickable {
            MyToast("暂未开发")
        }
    )

}