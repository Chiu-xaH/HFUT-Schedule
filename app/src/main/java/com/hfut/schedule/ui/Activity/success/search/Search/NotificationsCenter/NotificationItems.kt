package com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.Activity.success.search.Search.Person.getPersonInfo
import com.hfut.schedule.ui.UIUtils.EmptyUI

@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationItems() {
    
    if(getNotifications().size == 0) EmptyUI()
    else
    LazyColumn {
        items(getNotifications().size) { item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium
            ){
                ListItem(
                    headlineContent = { Text(text = getNotifications()[item].title) },
                    supportingContent = { Text(text = getNotifications()[item].info) },
                    overlineContent = { Text(text = getNotifications()[item].remark) },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "") }
                )
            }
        }
    }
}