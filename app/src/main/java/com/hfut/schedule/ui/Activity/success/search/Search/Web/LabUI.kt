package com.hfut.schedule.ui.Activity.success.search.Search.Web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import com.hfut.schedule.logic.utils.StartApp

@Composable
fun LabUI() {
    ListItem(
        headlineContent = { Text(text = "选项会随云端发生变动,即使不更新软件") },
        leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {}
    )
    for(item in 0 until getLab().size) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            ListItem(
                headlineContent = { Text(text = getLab()[item].title) },
                leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                trailingContent = { Icon( Icons.Filled.ArrowForward, contentDescription = "") },
                modifier = Modifier.clickable { StartApp.startUri(getLab()[item].info) }
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}
