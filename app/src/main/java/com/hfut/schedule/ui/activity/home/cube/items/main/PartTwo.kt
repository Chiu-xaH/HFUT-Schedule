package com.hfut.schedule.ui.activity.home.cube.items.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.funiction.FixActivity
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.Starter.startWebUrl
import com.hfut.schedule.ui.activity.home.cube.items.subitems.MyAPIItem
import com.hfut.schedule.ui.activity.home.cube.items.subitems.monet.MonetColorItem
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.getUpdates
import com.hfut.schedule.ui.utils.components.TransplantListItem

@SuppressLint("SuspiciousIndentation")
@Composable
fun FirstCube() {
    MyAPIItem()
    Spacer(modifier = Modifier.height(5.dp))
    PartTwo()
    //debug()

}


fun Clear() {
    val dbwritableDatabase =  dataBase.writableDatabase
    dbwritableDatabase.delete("Book",null,null)
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()
    Toast.makeText(MyApplication.context,"已清除缓存和数据库", Toast.LENGTH_SHORT).show()
    //崩溃操作
    val s = listOf("")
    println(s[2])
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartTwo() {

    MonetColorItem()

    TransplantListItem(
        headlineContent = { Text(text = "关于 疑难解答 修复") },
        supportingContent = { Text(text = "当出现问题时,可从此处进入或长按桌面图标选择修复")},
        leadingContent = { Icon(painterResource(R.drawable.build), contentDescription = "Localized description",) },
        modifier = Modifier.clickable{
            val it = Intent(MyApplication.context, FixActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            MyApplication.context.startActivity(it)
        }
    )


    var version by remember { mutableStateOf(getUpdates()) }
    var showBadge by remember { mutableStateOf(false) }
    if (version.version != VersionUtils.getVersionName()) showBadge = true


    TransplantListItem(
        headlineContent = { Text(text = "获取更新") },
        supportingContent = { Text(text = if(version.version == VersionUtils.getVersionName()) "当前为最新版本 ${VersionUtils.getVersionName()}" else "当前版本  ${VersionUtils.getVersionName()}\n最新版本  ${version.version}") },
        leadingContent = {
            BadgedBox(badge = {
                if(showBadge)
                    Badge(modifier = Modifier.size(7.dp)) }) {
                Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
            }
        },
        modifier = Modifier.clickable{
            if (version.version != VersionUtils.getVersionName())
                startWebUrl(MyApplication.UpdateURL+ "releases/download/Android/${version.version}.apk")
            else Toast.makeText(MyApplication.context,"与云端版本一致",Toast.LENGTH_SHORT).show()
        }
    )
}

