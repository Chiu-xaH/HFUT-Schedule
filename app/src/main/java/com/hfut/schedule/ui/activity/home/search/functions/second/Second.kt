package com.hfut.schedule.ui.activity.home.search.functions.second

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
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Second() {

    var showDialog by remember { mutableStateOf(false) }

    val url = "https://wxaurl.cn/EDy2PLrcrih"

    ListItem(
        headlineContent = { Text(text = "第二课堂") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.hotel_class),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            MyToast("请前往 第二课堂 微信小程序")
        }
    )
    val switch_startUri = SharePrefs.prefs.getBoolean("SWITCHSTARTURI",true)

    if (showDialog) {
        if(switch_startUri) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            actions = {
                                Row{
                                    IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(painterResource(id = R.drawable.net), contentDescription = "") }
                                    IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
                                }
                            },
                            title = { Text("跳转至 第二课堂 微信小程序") }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        WebViewScreen(url)
                    }
                }
            }
        } else {
            Starter.startWebUrl(url)
        }
    }
}