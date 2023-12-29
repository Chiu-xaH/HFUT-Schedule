package com.hfut.schedule.ui.ComposeUI.Settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.SaveBoolean
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartUri.StartUri
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.ui.UIUtils.LittleDialog


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
fun SettingsItems() {
    val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    getMyVersion()
    val Saveselect = prefs.getBoolean("select",false)
    var select by remember { mutableStateOf(Saveselect) }


    var showBadge by remember { mutableStateOf(false) }
    if (MyApplication.version != getMyVersion()) showBadge = true

    ListItem(
        headlineContent = { Text(text = "降级到2.X版本 (3.0 限定选项)") },
        supportingContent = { Text(text = "由于3.0初期存在若干Bug,如影响使用,可点击此选项获取上版")},
        leadingContent = { Icon(painterResource(R.drawable.trending_down), contentDescription = "Localized description",) },
        modifier = Modifier.clickable{ StartUri("https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Memory") }
    )

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
        leadingContent = { Icon(painterResource(R.drawable.swap_calls), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            select = !select
            SaveBoolean("select",false,select)
        }
    )


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
        headlineContent = { Text(text = "学期刷新") },
        supportingContent = { Text(text = "当开启下一学期后无法获取课表时,可点击刷新")},
        // supportingText =
        leadingContent = { Icon(painterResource(id =R.drawable.rotate_right), contentDescription = "") },
        modifier = Modifier.clickable{
            val semesterId = Gson().fromJson(prefs.getString("my", MyApplication.NullMy), MyAPIResponse::class.java).semesterId
            if(semesterId != null)
                SharePrefs.Save("semesterId",semesterId)
            Toast.makeText(MyApplication.context,"当前为 ${semesterId}",Toast.LENGTH_SHORT).show()
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
        modifier = Modifier.clickable{
            var url = ""
              when(select) {
               false -> url = "https://gitee.com/chiu-xah/HFUT-Schedule"
               true -> url = "https://github.com/Chiu-xaH/HFUT-Schedule"
              }
            // 创建一个分享的Intent
            val it = Intent()
            it.action = Intent.ACTION_SEND
// 设置分享的内容
            it.putExtra(Intent.EXTRA_TEXT, url)
// 设置分享的类型为纯文本
            it.type = "text/plain"
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
// 启动选/择器，让用户选择要分享的应用
            MyApplication.context.startActivity(Intent.createChooser(it, "分享到").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
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
    }

    ListItem(
        headlineContent = { Text(text = "清除数据") },
        supportingContent = { Text(text = "当数据异常或冲突崩溃时,可清除数据,然后重新登录")},
        leadingContent = { Icon(painterResource(R.drawable.delete), contentDescription = "Localized description",) },
        modifier = Modifier.clickable{ showDialog = true }
    )
}