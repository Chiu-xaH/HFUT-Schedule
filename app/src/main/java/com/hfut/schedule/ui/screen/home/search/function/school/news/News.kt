package com.hfut.schedule.ui.screen.home.search.function.school.news

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem

@Composable
fun News() {
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




