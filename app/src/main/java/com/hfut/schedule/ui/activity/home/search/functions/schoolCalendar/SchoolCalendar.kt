package com.hfut.schedule.ui.activity.home.search.functions.schoolCalendar

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
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.parse.ParseJsons.getMy
import com.hfut.schedule.ui.utils.components.WebViewScreen
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolCalendar() {
    var showDialog by remember { mutableStateOf(false) }
    val url = try {
        getMy()!!.SchoolCalendar
    } catch (e:Exception) {
        null
    }

    TransplantListItem(
        headlineContent = { Text(text = "校历") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar_view_day), contentDescription = "")},
        modifier = Modifier.clickable {
            if(url == null) {
                MyToast("正在从云端获取数据")
            } else {
                showDialog = true
                MyToast("即将打开网页链接,可自行下载保存图片")
            }
        }
    )


    url?.let { WebDialog(showDialog,{showDialog = false}, it,"校历") }
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
//                                    IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(painterResource(id = R.drawable.net), contentDescription = "") }
//                                    IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
//                                }
//                            },
//                            title = { Text("校历") }
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

fun SavePictures() {
    val assetManager = MyApplication.context.assets
    val inputStream = assetManager.open("Calendar.jpg")
    val outputFile = File(MyApplication.context.filesDir,"Download/Calendar.jpg")
    MyToast("保存成功")
    if(!outputFile.parentFile.exists()) outputFile.parentFile.mkdirs()
    val outputStream = FileOutputStream(outputFile)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
}