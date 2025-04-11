package com.hfut.schedule.ui.screen.home.search.function.notification

import android.annotation.SuppressLint
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
import com.hfut.schedule.logic.model.Notifications
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.WebDialog

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationItems() {
    val list = getNotifications()
    var showDialog by remember { mutableStateOf(false) }
    var notice : Notifications? by remember { mutableStateOf(null) }

    notice?.let { it.url?.let { it1 -> WebDialog(showDialog,{ showDialog = false }, it1,it.title) } }
    if(list.isEmpty()) EmptyUI() else {
        for(item in list.indices) {
//            MyCustomCard {
            StyleCardListItem(
                    headlineContent = { Text(text = list[item].title) },
                    supportingContent = { Text(text = list[item].info) },
                    overlineContent = { Text(text = list[item].remark) },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.notifications), contentDescription = "") },
                    modifier = Modifier.clickable {
                        if(list[item].url != null) {
                            notice = list[item]
                            showDialog = true
                        } else {
                            showToast("暂无点击操作")
                        }
                    }
                )
//            }
        }
    }
}