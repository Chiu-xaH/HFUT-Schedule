package com.hfut.schedule.ui.activity.home.search.functions.notifications

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.EmptyUI
import com.hfut.schedule.ui.utils.components.MyCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationItems() {
    val list = getNotifications()
    var showDialog by remember { mutableStateOf(false) }
    var num by remember { mutableStateOf(0) }

//    val switch_startUri = SharePrefs.prefs.getBoolean("SWITCHSTARTURI",true)
//
//    if (showDialog) {
//        if(switch_startUri) {
//            androidx.compose.ui.window.Dialog(
//                onDismissRequest = { showDialog = false },
//                properties = DialogProperties(usePlatformDefaultWidth = false)
//            ) {
//                Scaffold(
//                    modifier = Modifier.fillMaxSize(),
//                    topBar = {
//                        TopAppBar(
//                            colors = TopAppBarDefaults.mediumTopAppBarColors(
//                                containerColor = Color.Transparent,
//                                titleContentColor = MaterialTheme.colorScheme.primary,
//                            ),
//                            actions = {
//                                Row{
//                                    IconButton(onClick = { list[num].url?.let { Starter.startWebUrl(it) } }) { Icon(painterResource(id = R.drawable.net), contentDescription = "") }
//                                    IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
//                                }
//                            },
//                            title = { Text("网页") }
//                        )
//                    },
//                ) { innerPadding ->
//                    Column(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize()
//                    ) {
//                        list[num].url?.let { WebViewScreen(it) }
//                    }
//                }
//            }
//        } else {
//            list[num].url?.let { Starter.startWebUrl(it) }
//        }
//    }
    list[num].url?.let { WebDialog(showDialog,{ showDialog = false }, it,"宣城校区 电费缴纳") }
    if(list.size == 0) EmptyUI() else {
        for(item in list.indices) {
            MyCard {
                ListItem(
                    headlineContent = { Text(text = list[item].title) },
                    supportingContent = { Text(text = list[item].info) },
                    overlineContent = { Text(text = list[item].remark) },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.notifications), contentDescription = "") },
                    modifier = Modifier.clickable {
                        if(list[item].url != null) {
                            num = item
                            showDialog = true
                        } else {
                            MyToast("暂无点击操作")
                        }
                    }
                )
            }
        }
    }
}