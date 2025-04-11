package com.hfut.schedule.ui.screen.home.search.function.news

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.activity.screen.NewsActivity
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.TransplantListItem

@Composable
fun News(vm : NetWorkViewModel) {
    TransplantListItem(
        headlineContent = { Text(text = "通知公告") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.stream),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            Starter.startNews()
        }
    )
}




