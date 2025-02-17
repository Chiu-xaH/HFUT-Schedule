package com.hfut.schedule.ui.activity.home.search.functions.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabUI() {
    var showDialog by remember { mutableStateOf(false) }
    var num by remember { mutableStateOf(0) }

    WebDialog(showDialog,{ showDialog = false },getLab()[num].info,getLab()[num].title)
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
//                                    IconButton(onClick = { Starter.startWebUrl(getLab()[num].info) }) { Icon(painterResource(id = R.drawable.net), contentDescription = "") }
//                                    IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
//                                }
//                            },
//                            title = { ScrollText(getLab()[num].title) }
//                        )
//                    },
//                ) { innerPadding ->
//                    Column(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize()
//                    ) {
//                        WebViewScreen(getLab()[num].info)
//                    }
//                }
//            }
//        } else {
//            Starter.startWebUrl(getLab()[num].info)
//        }
//    }
    ListItem(
        headlineContent = { Text(text = "选项会随云端发生变动,即使不更新软件") },
        leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {}
    )
    for(item in 0 until getLab().size) {
        MyCustomCard {
            ListItem(
                headlineContent = { Text(text = getLab()[item].title) },
                leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                trailingContent = { Icon( Icons.Filled.ArrowForward, contentDescription = "") },
                modifier = Modifier.clickable {
                    num = item
                    showDialog = true
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}
