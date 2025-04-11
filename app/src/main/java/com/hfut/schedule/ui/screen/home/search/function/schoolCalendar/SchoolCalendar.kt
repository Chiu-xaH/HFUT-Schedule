package com.hfut.schedule.ui.screen.home.search.function.schoolCalendar

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
import com.hfut.schedule.logic.model.MyAPIResponse
import com.hfut.schedule.logic.util.storage.SharePrefs
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getMy
import com.hfut.schedule.ui.component.WebViewScreen
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import java.io.File
import java.io.FileOutputStream

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
                showToast("正在从云端获取数据")
            } else {
                showDialog = true
                showToast("即将打开网页链接,可自行下载保存图片")
            }
        }
    )


    url?.let { WebDialog(showDialog,{showDialog = false}, it,"校历") }
}

//fun savePictures() {
//    val assetManager = MyApplication.context.assets
//    val inputStream = assetManager.open("Calendar.jpg")
//    val outputFile = File(MyApplication.context.filesDir,"Download/Calendar.jpg")
//    MyToast("保存成功")
//    if(!outputFile.parentFile.exists()) outputFile.parentFile.mkdirs()
//    val outputStream = FileOutputStream(outputFile)
//    inputStream.copyTo(outputStream)
//    inputStream.close()
//    outputStream.close()
//}