package com.hfut.schedule.ui.Activity.success.cube.Settings.Items

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.SaveBoolean
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartUri.StartUri
import com.hfut.schedule.ui.Activity.success.cube.Settings.Monet.MonetColorItem
import com.hfut.schedule.ui.Activity.success.cube.Settings.getMyVersion
import com.hfut.schedule.ui.UIUtils.LittleDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

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
fun SettingsCubeItems() {

    getMyVersion()
    val Saveselect = prefs.getBoolean("select",false)
    var select by remember { mutableStateOf(Saveselect) }


    var showBadge by remember { mutableStateOf(false) }
    if (MyApplication.version != getMyVersion()) showBadge = true

    MonetColorItem()

    ListItem(
        headlineContent = { Text(text = "获取更新") },
        supportingContent = { Text(text = "当前版本  ${MyApplication.version}\n最新版本  ${getMyVersion()}")},
        leadingContent = {
            BadgedBox(badge = {
                if(showBadge)
                    Badge(modifier = Modifier.size(7.dp)) }) {
                Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
            }
        },
        modifier = Modifier.clickable{
            if (getMyVersion() != MyApplication.version){
                when(select) {
                    true -> StartUri("https://github.com/Chiu-xaH/HFUT-Schedule/releases/tag/Android")
                    false ->  StartUri("https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android")
                }
            }
            else Toast.makeText(MyApplication.context,"与云端版本一致",Toast.LENGTH_SHORT).show()
        }
    )

    ListItem(
        headlineContent = { Text(text = "仓库切换") },
        supportingContent = {
            Column {
                Text(text = "如可以翻墙推荐Github,否则为了获取更新请使用Gitee")
                Row {
                    FilterChip(
                        onClick = {
                        select = true
                        SaveBoolean("select",false,select)
                                         },
                        label = { Text(text = "Github") }, selected = select)
                    Spacer(modifier = Modifier.width(10.dp))
                    FilterChip(
                        onClick = {
                        select = false
                            SaveBoolean("select",false,select)
                                         },
                        label = { Text(text = "Gitee") }, selected = !select)
                }
            }
                            },
        leadingContent = { Icon(painterResource(R.drawable.arrow_split), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            select = !select
            SaveBoolean("select",false,select)
        }
    )

   // ListItem(
     //   headlineContent = { Text(text = "降级到3.X版本 (4.0 限定选项)") },
      //  supportingContent = { Text(text = "由于4.0初期存在若干Bug,如影响使用,可点击此选项获取上版")},
       // leadingContent = { Icon(painterResource(R.drawable.trending_down), contentDescription = "Localized description",) },
        //modifier = Modifier.clickable{ StartUri("https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Memory") }
    //)

    ListItem(
        headlineContent = { Text(text = "联系开发者") },
         supportingContent = { Text(text = "有更好的想法,或者反馈Bug,都可发邮件联系我")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.mail),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{
            val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(it)
        }
    )

    ListItem(
        headlineContent = { Text(text = "开源主页") },
        supportingContent = { Text(text = "欢迎来开源主页参观一下")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.net),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{
            when(select) {
                false -> StartUri("https://gitee.com/chiu-xah/HFUT-Schedule")
                true -> StartUri("https://github.com/Chiu-xaH/HFUT-Schedule")
            }
        }
    )

    ListItem(
        headlineContent = { Text(text = "推广本应用") },
        supportingContent = { Text(text = "如果你觉得好用的话,可以替开发者多多推广")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.ios_share),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{ ShareAPK() }
    )


    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { Clear() },
            dialogTitle = "警告",
            dialogText = "确定要抹掉数据吗,抹掉数据后,应用将退出",
            conformtext = "抹掉数据",
            dismisstext = "取消"
        )
        //This will look like IOS!
    }

    ListItem(
        headlineContent = { Text(text = "抹掉数据") },
        supportingContent = { Text(text = "当数据异常或冲突崩溃时,可抹掉数据,然后重新登录")},
        leadingContent = { Icon(painterResource(R.drawable.delete), contentDescription = "Localized description",) },
        modifier = Modifier.clickable{ showDialog = true }
    )
}

fun ShareAPK() {
    val path : String = "${ MyApplication.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) }/HFUT-Schedule ${MyApplication.version}.apk"
    try {
        getAPK(path)
        val file = File(path)
        val apkUri = FileProvider.getUriForFile(MyApplication.context,"com.hfut.schedule.provider",file)
        val shareIntent = Intent().apply {
            setAction(Intent.ACTION_SEND)
            setType("application/vnd.android.package-archive")
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Intent.EXTRA_STREAM,apkUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        MyApplication.context.startActivity(Intent.createChooser(shareIntent,"感谢对本应用的认可").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e : Exception) {
        Log.d("错误", e.toString())
    }
}

fun getAPK(destinationPath : String) {
    val apkFile = File(MyApplication.context.packageCodePath)
    val destinationFile = File(destinationPath)
    try {
        val sourceChannel = FileInputStream(apkFile).channel
        val destinationChannel = FileOutputStream(destinationFile).channel
        destinationChannel.transferFrom(sourceChannel,0,sourceChannel.size())
        sourceChannel.close()
        destinationChannel.close()
    } catch (e : IOException) {
        e.printStackTrace()
    }
}
