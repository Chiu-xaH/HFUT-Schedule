package com.hfut.schedule.ui.activity.home.search.functions.news

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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.activity.screen.NewsActivity
import com.hfut.schedule.ui.utils.components.TransplantListItem

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
            val it = Intent(MyApplication.context, NewsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            MyApplication.context.startActivity(it)
        }
    )
}




