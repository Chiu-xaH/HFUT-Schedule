package com.hfut.schedule.ui.activity.home.search.functions.repair

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.TransplantListItem

@Composable
fun Repair() {
    TransplantListItem(
        headlineContent = { Text(text = "报修") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.build), contentDescription = "") },
        modifier = Modifier.clickable {
            Starter.startWebUrl("http://xcfw.hfut.edu.cn/school/index.html")
           // MyToast("暂未开发")
        }
    )

}