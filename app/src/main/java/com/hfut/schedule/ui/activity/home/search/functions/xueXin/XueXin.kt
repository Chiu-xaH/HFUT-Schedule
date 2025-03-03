package com.hfut.schedule.ui.activity.home.search.functions.xueXin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen


@OptIn(ExperimentalMaterial3Api::class)
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
//
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
//                                containerColor = Color(0xFF26B887),
//                                titleContentColor = Color.White,
//                            ),
//                            actions = {
//                                Row{
//                                    IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(
//                                        painterResource(id = R.drawable.net), contentDescription = "", tint = Color.White
//                                    ) }
//                                    IconButton(onClick = { showDialog = false }) { Icon(
//                                        painterResource(id = R.drawable.close), contentDescription = "", tint = Color.White) }
//                                }
//                            },
//                            title = { Text("学信网") }
//                        )
//                    },
//                ) { innerPadding ->
//                    Column(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize()
//                    ) {
//                        WebViewScreen(url)
//                    }
//                }
//            }
//        } else {
//            Starter.startWebUrl(url)
//        }
//    }
}