package com.hfut.schedule.ui.ComposeUI.Search.Indevelopment

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R


@Composable
fun XueGong(){
    ListItem(
        headlineContent = { Text(text = "学工系统") },
        supportingContent = { Text(text = "暂未开发")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.work), contentDescription = "")},
        modifier = Modifier.clickable {  }
    )
}