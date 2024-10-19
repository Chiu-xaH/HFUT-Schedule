package com.hfut.schedule.ui.Activity.success.search.Search.TodayCampus

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
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToadyCampus(ifSaved : Boolean){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    val url = "https://stu.hfut.edu.cn/"
    //val headers = mapOf("X-Access-Token" to CommuityTOKEN.toString())


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
                                    IconButton(onClick = { StartApp.startLaunchAPK("com.wisedu.cpdaily","今日校园") }) { Icon(painterResource(id = R.drawable.net), contentDescription = "") }
                                    IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
                                }
                            },
                            title = { Text("学工系统") }
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
            StartApp.startUri(url)
        }
    }

    ListItem(
        overlineContent = { Text(text = "今日校园") },
        headlineContent = { Text(text = "学工系统") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.handshake), contentDescription = "") },
        modifier = Modifier.clickable {
            //if(ifSaved) Login() else
            // showBottomSheet = true
            StartApp.startLaunchAPK("com.wisedu.cpdaily","今日校园")
           // StartApp.startUri(url)
            //showDialog = true
        }
    )
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("学工系统") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                }
            }
        }
    }
}