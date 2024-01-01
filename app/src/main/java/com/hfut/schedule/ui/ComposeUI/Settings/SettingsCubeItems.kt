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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.utils.SharePrefs.SaveBoolean
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartUri.StartUri
import com.hfut.schedule.ui.ComposeUI.Search.LePaoYun.InfoSet
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
               false -> url = "https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(vm : LoginSuccessViewModel,showlable : Boolean,showlablechanged :(Boolean) -> Unit) {
    val switch_focus = prefs.getBoolean("SWITCHFOCUS",true)
    var showfocus by remember { mutableStateOf(switch_focus) }


    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getBoolean("SWITCH", true) != showlable) { sp.edit().putBoolean("SWITCH", showlable).apply() }
    if (sp.getBoolean("SWITCHFOCUS", true) != showfocus) { sp.edit().putBoolean("SWITCHFOCUS", showfocus).apply() }


    ListItem(
        headlineContent = { Text(text = "底栏标签") },
        leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
        trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
        modifier = Modifier.clickable { showlablechanged }
    )


    ListItem(
        headlineContent = { Text(text = "聚焦优先") },
        supportingContent = { Text(text = "使聚焦作为本地速览的第一页面,而不是课表")},
        leadingContent = { Icon(painterResource(R.drawable.lightbulb), contentDescription = "Localized description",) },
        trailingContent = { Switch(checked = showfocus, onCheckedChange = {showfocusch -> showfocus = showfocusch }) },
        modifier = Modifier.clickable { showfocus = !showfocus }
    )


    var showBottomSheet_input by remember { mutableStateOf(false) }
    val sheetState_input = rememberModalBottomSheetState()
    if (showBottomSheet_input) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_input = false },
            sheetState = sheetState_input
        ) {
            InfoSet()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    var showBottomSheet_focus by remember { mutableStateOf(false) }
    var sheetState_focus = rememberModalBottomSheetState()
    if (showBottomSheet_focus) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_focus = false },
            sheetState = sheetState_focus
        ) {
            FocusSetting()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    ListItem(
        headlineContent = { Text(text = "云运动 信息配置") },
        supportingContent = { Text(text = "需要提交已登录手机的信息")},
        leadingContent = { Icon(painterResource(R.drawable.mode_of_travel), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet_input = true }
    )

    ListItem(
        headlineContent = { Text(text = "聚焦编辑") },
        supportingContent = { Text(text = "自定义聚焦的内容及信息来源")},
        leadingContent = { Icon(painterResource(R.drawable.edit), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet_focus = true }
    )

    ListItem(
        headlineContent = { Text(text = "请求配置") },
        supportingContent = { Text(text = "自定义加载一页时出现的数目,数目越大,加载时间相应地会更长,但可显示更多信息")},
        leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {  }
    )
}