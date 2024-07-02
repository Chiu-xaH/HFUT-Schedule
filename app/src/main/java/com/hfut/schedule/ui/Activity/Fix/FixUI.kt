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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
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
import com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb.loginWebUI
import com.hfut.schedule.ui.UIUtils.LittleDialog
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
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

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("常见问题解答") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    questionUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

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
            modifier = Modifier.clickable{ StartApp.StartUri(MyApplication.DownloadURL) }
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
        ListItem(
            headlineContent = { Text(text = "联系开发者") },
         //   overlineContent = { Text(text = "有什么更好的建议吗,或者发现了问题纰漏,可以给我发邮件哦")},
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
            headlineContent = { Text(text = "常见问题解答") },
            leadingContent = { Icon(painterResource(R.drawable.help), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {showBottomSheet = true }
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

@Composable
fun questionUI() {
    questionItem(
        "登录界面点击登录提示“重定向失败“，无法登录？",
        "有时候教务系统会崩，可能外网会卡，建议切换到校园网尝试，如果还不行去网址导航选择教务系统，不出意外大概率网站也进不去，等一等吧"
    )
    questionItem(
        "有的功能跳转到登陆界面",
        "有的功能为了保证数据的新鲜，要求登录一下教务系统"
    )
    questionItem(
        "到底那些信息时不需要登录就能获取的，哪些数据是实时更新的，课表会不会过时",
        "大多数数据都是实时的，大家常用的一卡通、课程表等等都是实时数据，除以下数据：个人信息、培养方案、考试、课程汇总为缓存的数据，每次登录自动刷新缓存；校车为静态数据，默认写入到APP内"
    )
    questionItem(
        "为什么一卡通功能或挂科率等功能用不了了",
        "有可能是保持登陆的种子过期了，我也不知道多少天的期限，不过肯定能坚持好几个月，而且每次登录都会刷新新的种子的，如果过期了，去选项里的刷新登陆状态点一下就行"
    )
    questionItem(
        "为什么我的校园网功能用不了",
        "我在写这个功能时，要求提交学号密码，密码和信息门户不同，初始为身份证后6位，就是与你登录校园网的密码一致，但是如果你改了密码，那就获取不到了"
    )
    questionItem(
        "APP会收集隐私信息吗",
        "不会，APP会收集部分用户信息（机型、安卓版本、软件版本）发送到开发者的服务器数据库，且数据库经过加密"
    )
    questionItem(
        "聚焦接口的信息来源与真实度",
        "这些信息我只给23地科提供，全为我得到的信息"
    )
    questionItem(
        "本应用的数据与官方渠道有出入",
        "信息大部分来源于官方系统，如有出入请以官方为准，其中存在部分信息为开发者的服务器分发"
    )
    questionItem(
        "联系我",
        "欢迎给我发邮件 zsh0908@outlook.com"
    )
}

@Composable
fun questionItem(title : String,
                 info : String) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = androidx.compose.material3.MaterialTheme.shapes.medium

    ) {
        ListItem(
            headlineContent = { Text(text = title) },
            supportingContent = { Text(text = info) }
        )
    }
}