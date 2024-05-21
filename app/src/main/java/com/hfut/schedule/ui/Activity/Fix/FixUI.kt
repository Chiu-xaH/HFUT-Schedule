package com.hfut.schedule.ui.Activity.Fix

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.activity.FixActivity
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.CrashHandler
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.Clear
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.apiCheck
import com.hfut.schedule.ui.Activity.success.focus.Focus.getTimeStamp
import com.hfut.schedule.ui.UIUtils.LittleDialog
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlin.math.log

@Composable
fun FixUI(innerPadding : PaddingValues,vm : LoginViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val switch_faststart = SharePrefs.prefs.getBoolean("SWITCHFASTSTART",
        SharePrefs.prefs.getString("TOKEN","")?.isNotEmpty() ?: false)
    var faststart by remember { mutableStateOf(switch_faststart) }
    SharePrefs.SaveBoolean("SWITCHFASTSTART", SharePrefs.prefs.getString("TOKEN", "")?.isNotEmpty() ?: false, faststart)

    val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPI", apiCheck())
    var showapi by remember { mutableStateOf(switch_api) }
    SharePrefs.SaveBoolean("SWITCHMYAPI", false, showapi)


    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)) {
        Spacer(modifier = Modifier.height(5.dp))

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = androidx.compose.material3.MaterialTheme.shapes.medium

        ){
            ListItem(
                headlineContent = { Text(text = "版本信息") },
                supportingContent = {Text("安卓版本 ${AndroidVersion.sdkInt} | 应用版本 ${APPVersion.getVersionName()} (${APPVersion.getVersionCode()})")},
                leadingContent = { Icon(painterResource(R.drawable.info), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {}
            )
        }
        Spacer(modifier = Modifier.height(5.dp))





        ListItem(
            headlineContent = { Text(text = "快速启动") },
            leadingContent = { Icon(painterResource(R.drawable.speed), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = faststart, onCheckedChange = {faststartch -> faststart = faststartch }) },
            modifier = Modifier.clickable { faststart = !faststart }
        )
        ListItem(
                headlineContent = { Text(text = "聚焦接口") },
        leadingContent = { Icon(painterResource(R.drawable.lightbulb), contentDescription = "Localized description",) },
        trailingContent = { Switch(checked = showapi, onCheckedChange = {showapich -> showapi = showapich }) },
        modifier = Modifier.clickable { showapi = !showapi }
        )
        ListItem(
            headlineContent = { Text(text = "刷新登录状态") },
            leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("nologin",false)
                }
                MyApplication.context.startActivity(it) }
        )
        ListItem(
            headlineContent = { Text(text = "下载最新版本") },
            leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
            modifier = Modifier.clickable{ StartApp.StartUri("https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android") }
        )

        ListItem(
            headlineContent = { Text(text = "抹掉数据") },
            leadingContent = { Icon(painterResource(R.drawable.delete), contentDescription = "Localized description",) },
            modifier = Modifier.clickable{ showDialog = true }
        )
        BugShare()
        ListItem(
            headlineContent = { Text(text = "进入主界面") },
            leadingContent = { Icon(painterResource(R.drawable.login), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                val it = Intent(MyApplication.context, LoginActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                MyApplication.context.startActivity(it)
            }
        )
        ListItem(
            headlineContent = { Text(text = "开发者接口") },
            overlineContent = { getTimeStamp()?.let { Text(text = it) } },
            leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                vm.My()
                MyToast("正在更新信息")
            }
        )



        //Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
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

}
@Composable
fun DebugUI(innerPadding : PaddingValues,vm : LoginViewModel) {
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)){
        Spacer(modifier = Modifier.height(5.dp))


        //Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}


@Composable
fun BugShare() {
    var showDialog by remember { mutableStateOf(false) }
    var times by remember { mutableStateOf(5) }
    val logs = prefs.getString("logs","")
    if(showDialog) {
        logs?.let {
            LittleDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    try {
                        CrashHandler().disableLogging()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, logs)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        MyApplication.context.startActivity(Intent.createChooser(shareIntent, "发送给开发者").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } catch (e : Exception) { MyToast("分享失败") }
                },
                dialogTitle = "日志内容",
                dialogText = it,
                conformtext = "分享",
                dismisstext = "取消"
            )
        }
    }

    if(times == 0) {
            val list = listOf(0,1)
            println(list[9])
    }
    ListItem(
        headlineContent = { Text(text = "日志抓取") },
        overlineContent = {
            if (logs != null) {
                if (logs.substringBefore("*").contains("-")) {
                    if (logs != null) {
                        Text(text = "已保存 ${logs.substringBefore("*")}")
                    }
                }
            }
        },
        leadingContent = { Icon(painterResource(R.drawable.monitor_heart), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            times--
            MyToast("点击${times}次后应用会崩溃,生成测试日志")
        },
        trailingContent = {
            Row{
                FilledTonalIconButton(onClick = {

                    MyToast("已将日志选项暴露在主活动中")
                }) { Icon(painter = painterResource(id =  R.drawable.home ), contentDescription = "") }
                FilledTonalIconButton(onClick = {
                    CrashHandler().enableLogging()
                    MyToast("日志抓取已开启,请复现崩溃的操作,当完成后,回此处点击分享")
                }) { Icon(painter = painterResource(id =  R.drawable.slow_motion_video ), contentDescription = "") }
                FilledTonalIconButton(onClick = {
                    Log.d("s",logs.toString())
                    if (logs != null) {
                        if (logs.substringBefore("*").contains("-")) {
                            if (logs != null) showDialog = true else MyToast("日志为空")
                        } else MyToast("日志为空")
                    } else MyToast("日志为空")
                }) { Icon(painter = painterResource(id =  R.drawable.ios_share ), contentDescription = "") }
            }
        }
    )
}