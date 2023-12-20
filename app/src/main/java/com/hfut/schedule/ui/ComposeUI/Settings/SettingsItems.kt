package com.hfut.schedule.ui.ComposeUI.Settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.SharePrefs.Save
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.logic.StartUri.StartUri
import com.hfut.schedule.logic.datamodel.data4


fun Clear() {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()
    Toast.makeText(MyApplication.context,"已清除缓存", Toast.LENGTH_SHORT).show()
    //崩溃操作
    val s = listOf("")
    println(s[2])
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItems() {
    val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    val my = SharePrefs.prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, data4::class.java).SettingsInfo
    val version = data.version
    val Saveselect = prefs.getBoolean("select",false)
    var select by remember { mutableStateOf(Saveselect) }

    ListItem(
        headlineContent = { Text(text = "仓库切换") },
        supportingContent = {
            Column {
                Text(text = "如可以翻墙推荐Github,否则为了获取更新请使用Gitee")
                Row {
                    FilterChip(
                        onClick = {
                        select = true
                        if (saved.getBoolean("select",false) != select) { saved.edit().putBoolean("select",select).apply() }
                                         }, label = { Text(text = "Github") }, selected = select)
                    Spacer(modifier = Modifier.width(10.dp))
                    FilterChip(
                        onClick = {
                        select = false
                        if (saved.getBoolean("select",false) != select) { saved.edit().putBoolean("select",select).apply() }
                                         }, label = { Text(text = "Gitee") }, selected = !select)
                }
            }
                            },
        leadingContent = { Icon(painterResource(R.drawable.swap_horiz), contentDescription = "Localized description",) },
    )

    ListItem(
        headlineContent = { Text(text = "获取更新") },
        supportingContent = { Text(text = "当前版本  ${MyApplication.version}\n最新版本  ${version}")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.net),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{
            if (version != MyApplication.version){
                when(select) {
                    true -> StartUri("https://github.com/Chiu-xaH/HFUT-Schedule/releases/tag/Android")
                    false ->  StartUri("https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android")
                }
            }
            else Toast.makeText(MyApplication.context,"与云端版本一致",Toast.LENGTH_SHORT).show()
        }
    )

    ListItem(
        headlineContent = { Text(text = "向我反馈") },
        // supportingText =
        leadingContent = {
            Icon(
                painterResource(R.drawable.feedback),
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
                painterResource(R.drawable.home),
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
            val semesterId = Gson().fromJson(prefs.getString("my",MyApplication.NullMy), data4::class.java).semesterId
            if(semesterId != null)
                SharePrefs.Save("semesterId",semesterId)
            Toast.makeText(MyApplication.context,"当前为 ${semesterId}",Toast.LENGTH_SHORT).show()
        }
    )



    ListItem(
        headlineContent = { Text(text = "清除数据") },
        supportingContent = { Text(text = "当数据异常或冲突崩溃时,可清除数据,然后重新登录")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.delete),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{ Clear() }
    )
}