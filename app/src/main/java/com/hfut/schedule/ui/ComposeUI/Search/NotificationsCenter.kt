package com.hfut.schedule.ui.ComposeUI.Search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.lazy.items
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.SharePrefs.Save
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.Notifications
import com.hfut.schedule.ui.ComposeUI.Settings.MyAPIItem

//解析通知
fun getNotifications() : MutableList<Notifications>{
    val json = SharePrefs.prefs.getString("my",MyApplication.NullMy)
    val list = Gson().fromJson(json,MyAPIResponse::class.java).Notifications
    var Notifications = mutableListOf<Notifications>()
    list.forEach { Notifications.add(it) }
    return Notifications
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsCenter() {
  
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        Save("Notifications",getNotifications().size.toString())
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("消息中心") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) { NotificationItems() }
            }
        }
    }

    ListItem(
        headlineContent = { Text(text = "消息中心") },
        supportingContent = {},
        modifier = Modifier.clickable { showBottomSheet = true },
        leadingContent = {
            BadgedBox(badge = {
                if (prefs.getString("Notifications","") != getNotifications().size.toString())
                Badge { Text(text = getNotifications().size.toString())}
            }) {
                Icon(painter = painterResource(id = R.drawable.notifications), contentDescription = "")
            }

        }
    )
}

@Composable
fun NotificationItems() {
    LazyColumn {
        items(getNotifications().size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium

            ){
                ListItem(
                    headlineContent = { Text(text = getNotifications()[item].title) },
                    supportingContent = { Text(text = getNotifications()[item].info)},
                    overlineContent = { Text(text = getNotifications()[item].remark)},
                    leadingContent = {
                        Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")
                    }
                )
            }
        }
    }
}