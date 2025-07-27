package com.hfut.schedule.ui.screen.fix.fix

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.getTimeStamp
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.development.CrashHandler
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.emailMe
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.cube.apiCheck
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.util.TransitionPredictiveBackHandler
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixUI(innerPadding : PaddingValues, vm : LoginViewModel, hazeState: HazeState,navController: NavHostController) {
    val firstStart by DataStoreManager.firstStart.collectAsState(initial = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false))

    val switch_api = prefs.getBoolean("SWITCHMYAPI", apiCheck())
    var showapi by remember { mutableStateOf(switch_api) }
    SharedPrefs.saveBoolean("SWITCHMYAPI", false, showapi)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("常见问题解答")
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

//    val sheetState_feedBack = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    var showBottomSheet_feedBack by remember { mutableStateOf(false) }
//
//    if (showBottomSheet_feedBack) {
//        ModalBottomSheet(
//            onDismissRequest = { showBottomSheet_feedBack = false },
//            sheetState = sheetState_feedBack,
//            shape = bottomSheetRound(sheetState_feedBack)
//        ) {
//            feedBackUI(vm2)
//        }
//    }
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController) {
        scale = it
    }
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding).scale(scale)) {
        Spacer(modifier = Modifier.height(5.dp))

//        MyCustomCard{
            StyleCardListItem(
                headlineContent = { Text(text = "版本信息") },
                supportingContent = {Text("安卓版本 ${AppVersion.sdkInt} | 应用版本 ${AppVersion.getVersionName()} (${AppVersion.getVersionCode()})")},
                leadingContent = { Icon(painterResource(R.drawable.info), contentDescription = "Localized description",) },
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clickable {}
            )
//        }
        DividerTextExpandedWith("行为1") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "下载最新版本") },
                    leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ Starter.startWebUrl(MyApplication.GITEE_UPDATE_URL + "releases/tag/Android") }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "刷新登录状态") },
                    leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {
                        refreshLogin()
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "快速启动") },
                    leadingContent = { Icon(painterResource(R.drawable.speed), contentDescription = "Localized description",) },
                    trailingContent = { Switch(checked = firstStart, onCheckedChange = { scope.launch { DataStoreManager.saveFastStart(!firstStart) } }) },
                    modifier = Modifier.clickable { scope.launch { DataStoreManager.saveFastStart(!firstStart) } }
                )
                PaddingHorizontalDivider()
                BugShare()
            }
        }
        DividerTextExpandedWith("行为2") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "开发者接口") },
                    overlineContent = { getTimeStamp()?.let { Text(text = it) } },
                    leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {
                        vm.getMyApi()
                        showToast("正在更新信息")
                    }
                )
                PaddingHorizontalDivider()
//            TransplantListItem(
//                headlineContent = { Text(text = "反馈") },
//                leadingContent = { Icon(painterResource(R.drawable.feedback), contentDescription = "Localized description",) },
//                modifier = Modifier.clickable{ Starter.startWebUrl("https://docs.qq.com/form/page/DWHlwd1JZYlRtcVZ0") }
//            )
                TransplantListItem(
                    headlineContent = { Text(text = "联系开发者") },
                    leadingContent = { Icon(painterResource(R.drawable.mail), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ emailMe() }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "常见问题解答") },
                    leadingContent = { Icon(painterResource(R.drawable.help), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {showBottomSheet = true }
                )
            }
        }

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
                    } catch (e : Exception) { showToast("分享失败") }
                },
                dialogTitle = "日志内容",
                dialogText = it,
                conformText = "分享",
                dismissText = "取消"
            )
        }
    }

    if(times == 0) {
            val list = listOf(0,1)
            println(list[9])
    }
    TransplantListItem(
        headlineContent = { Text(text = "日志抓取") },
        overlineContent = {
            if (logs != null) {
                if (logs.substringBefore("*").contains("-")) {
                    if (true) {
                        Text(text = "已保存 ${logs.substringBefore("*")}")
                    }
                }
            }
        },
        leadingContent = { Icon(painterResource(R.drawable.monitor_heart), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            times--
            showToast("点击${times}次后应用会崩溃,生成测试日志")
        },
        trailingContent = {
            Row{
                FilledTonalIconButton(onClick = {
                    CrashHandler().enableLogging()
                    showToast("日志抓取已开启,请复现崩溃的操作,当完成后,回此处点击分享")
                }) { Icon(painter = painterResource(id =  R.drawable.slow_motion_video ), contentDescription = "") }
                FilledTonalIconButton(onClick = {
//                    Log.d("s",logs.toString())
                    if (logs != null) {
                        if (logs.substringBefore("*").contains("-")) {
                            if (true) showDialog = true else showToast("日志为空")
                        } else showToast("日志为空")
                    } else showToast("日志为空")
                }) { Icon(painter = painterResource(id =  R.drawable.ios_share ), contentDescription = "") }
            }
        }
    )
}

@Composable
fun questionUI() {
    questionItem(
        "登录界面点击登录提示“重定向失败“，无法登录？",
        "有时候教务系统会维护，外网会进不去，建议切换到校园网尝试，如果急需的话勾选外地访问(WEBVPN)，或者在 查询中心-网址导航-WEBVPN 登录"
    )
    questionItem(
        "有的功能跳转到登陆界面",
        "有的功能为了保证数据的新鲜，要求登录教务系统刷新一下"
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
        "这些信息我只给所在班级提供,只是我得到的信息,其他外班级用户看不到我发布的内部日程"
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
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = title) },
            supportingContent = { Text(text = info) }
        )
//    }
}


@Composable
fun feedBackUI(vm : NetWorkViewModel) {
    var input by remember { mutableStateOf("") }
    var inputContact by remember { mutableStateOf("") }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            BottomSheetTopBar("反馈") {
                FilledTonalIconButton(
                    onClick = {
                        if(input == "") {
                            showToast("请输入内容")
                        } else {
//                            vm.feedBack(input,inputContact)
                            showToast("已提交,可关闭此界面")
                        }
                    }) {
                    Icon(Icons.Filled.Check, contentDescription = "")
                }
            }

        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("反馈内容" ) },
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    value = inputContact,
                    onValueChange = { inputContact = it },
                    label = { Text("你的联系方式(可不填)" ) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
//            MyCustomCard {
            StyleCardListItem(
                    headlineContent = { Text(text = "或者通过电子邮件联系") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.mail), contentDescription ="" )},
                    modifier = Modifier.clickable { emailMe() }
                )
//            }
//            MyCustomCard {
            StyleCardListItem(
                    headlineContent = { Text(text = "提交时会自动提交当前APP版本、日期时间等信息，以协助开发者分析反馈内容") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription ="" )},
                )
//            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}