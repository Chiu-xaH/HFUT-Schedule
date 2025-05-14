package com.hfut.schedule.ui.screen.home.search.function.other.xueXin

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog


@Composable
fun XueXin() {
    var showDialog by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = { Text(text = "学信网") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.school), contentDescription = "") },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    val url = "https://my.chsi.com.cn/archive/wap/gdjy/index.action"
    WebDialog(showDialog,{ showDialog = false },url,"学信网")
}