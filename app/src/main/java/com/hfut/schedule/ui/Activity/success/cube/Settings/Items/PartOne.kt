package com.hfut.schedule.ui.Activity.success.cube.Settings.Items

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.logic.Enums.CardBarItems
import com.hfut.schedule.logic.Enums.FixBarItems
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.SaveBoolean
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp.StartUri
import com.hfut.schedule.ui.Activity.success.cube.Settings.Monet.MonetColorItem
import com.hfut.schedule.ui.Activity.success.cube.Settings.Update.VersionInfo
import com.hfut.schedule.ui.Activity.success.cube.Settings.Update.downloadUI
import com.hfut.schedule.ui.Activity.success.cube.Settings.Update.getUpdates
import com.hfut.schedule.ui.Activity.success.search.Search.LePaoYun.InfoSet
import com.hfut.schedule.ui.Activity.success.search.Search.Person.getPersonInfo
import com.hfut.schedule.ui.Activity.success.search.Search.Survey.getSemseter
import com.hfut.schedule.ui.Activity.success.search.Search.Survey.getSemseterCloud
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import java.math.BigDecimal
import java.math.RoundingMode


fun apiCheck() : Boolean {
    return try {
        val classes = getPersonInfo().classes
        return classes!!.contains("计算机") && classes.contains("23")
    } catch (e : Exception) {
        false
    }
}
@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartOne(vm : LoginSuccessViewModel,
            showlable : Boolean,
            showlablechanged :(Boolean) -> Unit,
            ifSaved : Boolean,
            blur : Boolean,
            blurchanged :(Boolean) -> Unit,
            navController: NavController) {
    ListItem(
        headlineContent = { Text(text = "界面显示") },
        supportingContent = { Text(text = "实时模糊 莫奈取色 动画时长")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.stacks), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.UIScreen.route)  }
    )
    ListItem(
        headlineContent = { Text(text = "应用行为") },
        supportingContent = { Text(text = "快速启动 预加载 主页面")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.empty_dashboard), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.APPScreen.route)  }
    )
    ListItem(
        headlineContent = { Text(text = "网络相关") },
        supportingContent = { Text(text = "云端接口 请求范围 登录状态")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.net), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.NetWorkScreen.route)  }
    )
    ListItem(
        headlineContent = { Text(text = "维护关于") },
        supportingContent = { Text(text = "疑难修复 反馈信息 分享推广")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.responsive_layout), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.FIxAboutScreen.route)  }
    )
}

enum class DetailSettings {
    Home,UI,APP,NetWork,FIxAbout
}
sealed class Screen(val route: String) {
    object HomeScreen : Screen(DetailSettings.Home.name)
    object UIScreen : Screen(DetailSettings.UI.name)
    object NetWorkScreen : Screen(DetailSettings.NetWork.name)
    object APPScreen : Screen(DetailSettings.APP.name)
    object FIxAboutScreen : Screen(DetailSettings.FIxAbout.name)
    object FIxScreen : Screen(FixBarItems.Fix.name)
    object AboutScreen : Screen(FixBarItems.About.name)

    object DebugScreen : Screen("DEBUG")
    object CardBillsScreen : Screen(CardBarItems.BILLS.name)

    object CardCountScreen : Screen(CardBarItems.COUNT.name)
    object CardHomeScreen : Screen(CardBarItems.HOME.name)

}


@Composable
fun UIScreen(navController: NavController,innerPaddings : PaddingValues,
             showlable : Boolean,
             showlablechanged :(Boolean) -> Unit,
             blur : Boolean,
             blurchanged :(Boolean) -> Unit,) {
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        SaveBoolean("SWITCH",true,showlable)
        SaveBoolean("SWITCHBLUR",true,blur)

        var sliderPosition by remember { mutableStateOf((prefs.getInt("ANIMATION",MyApplication.Animation)).toFloat()) }
        val bd = BigDecimal(sliderPosition.toString())
        val str = bd.setScale(0, RoundingMode.HALF_UP).toString()
        SharePrefs.SaveInt("ANIMATION",sliderPosition.toInt())

        ListItem(
            headlineContent = { Text(text = "底栏标签") },
            supportingContent = { Text(text = "屏幕底部的Tab栏底栏标签")},
            leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
            modifier = Modifier.clickable { showlablechanged }
        )
        ListItem(
            headlineContent = { Text(text = "实时模糊") },
            supportingContent = {
                    if(AndroidVersion.sdkInt >= 32) {
                        Text(text = "开启后将会转换部分渲染为实时模糊")
                    } else {
                        Text(text = "需为 Android 13+")
                    }
                                },
            leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
            trailingContent = {  Switch(checked = blur, onCheckedChange = blurchanged, enabled = AndroidVersion.sdkInt >= 32 ) },
            modifier = Modifier.clickable { blurchanged }
        )
        ListItem(
            headlineContent = { Text(text = "转场动画") },
            supportingContent = {
                                Column {
                                    Text(text = "时长 $str 毫秒 重启后生效")
                                    Slider(
                                        value = sliderPosition,
                                        onValueChange = {
                                            sliderPosition = it
                                            SharePrefs.SaveInt("ANIMATION",sliderPosition.toInt())
                                                        },
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.secondary,
                                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                        ),
                                        steps = 19,
                                        valueRange = 0f..1000f
                                    )
                                }
            },
            leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
            trailingContent = {  Switch(checked = true, onCheckedChange = null, enabled = false ) },
            modifier = Modifier.clickable {  }
        )

        MonetColorItem()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeSettingScreen(navController: NavController,
                      vm : LoginSuccessViewModel,
                      showlable : Boolean,
                      showlablechanged: (Boolean) -> Unit,
                      ifSaved : Boolean,
                      innerPaddings : PaddingValues,
                      blur : Boolean,
                      blurchanged : (Boolean) -> Unit) {
   //


    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {

        Spacer(modifier = Modifier.height(5.dp))

        DividerText(text = "登录信息")
        PersonPart()

        MyAPIItem()

        if (APPVersion.getVersionName() != getUpdates().version) {
            if(!APPVersion.getVersionName().contains("Preview")) {
                DividerText(text = "更新版本")
                UpdateUI()
                downloadUI()
            }
        }

        DividerText(text = "猜你想用")
        AlwaysItem()

        DividerText(text = "应用设置")
        PartOne(vm,showlable,showlablechanged,ifSaved,blur,blurchanged,navController)

        Spacer(modifier = Modifier.height(5.dp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlwaysItem() {
    var version by remember { mutableStateOf(getUpdates()) }
    var showBadge by remember { mutableStateOf(false) }
    if (version.version != APPVersion.getVersionName()) showBadge = true
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("本版本新特性") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    VersionInfo()
                }
            }
        }
    }
    ListItem(
        headlineContent = { Text(text = "刷新登录状态") },
        supportingContent = { Text(text = "如果一卡通或者考试成绩等无法查询,可能是登陆过期,需重新登录一次") },
        leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("nologin",false)
            }
            MyApplication.context.startActivity(it) }
    )
    if (APPVersion.getVersionName() == getUpdates().version)
        ListItem(
            headlineContent = { Text(text = "查看本版本新特性") },
            supportingContent = { Text(text = if(version.version == APPVersion.getVersionName()) "当前为最新版本 ${APPVersion.getVersionName()}" else "当前版本  ${APPVersion.getVersionName()}\n最新版本  ${version.version}") },
            leadingContent = {
                BadgedBox(badge = {
                    if(showBadge)
                        Badge(modifier = Modifier.size(7.dp)) }) {
                    Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
                }
            },
            modifier = Modifier.clickable{
                if (version.version != APPVersion.getVersionName())
                    StartUri(MyApplication.UpdateURL+ "releases/download/Android/${version.version}.apk")
                else {
                    showBottomSheet = true
                }
            }
        )

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APPScreen(navController: NavController,
              innerPaddings : PaddingValues,
              ifSaved : Boolean,) {
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        val switch_focus = prefs.getBoolean("SWITCHFOCUS",true)
        var showfocus by remember { mutableStateOf(switch_focus) }
        val switch_faststart = SharePrefs.prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)
        var faststart by remember { mutableStateOf(switch_faststart) }
        SaveBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false,faststart)

        val switch_startUri = prefs.getBoolean("SWITCHSTARTURI",true)
        var showStartUri by remember { mutableStateOf(switch_startUri) }
        SaveBoolean("SWITCHSTARTURI",true,showStartUri)

        val switch_update = prefs.getBoolean("SWITCHUPDATE",true)
        var showSUpdate by remember { mutableStateOf(switch_update) }
        SaveBoolean("SWITCHUPDATE",true,showSUpdate)


        SaveBoolean("SWITCHFOCUS",true,showfocus)
        var showBottomSheet_card by remember { mutableStateOf(false) }
        var sheetState_card = rememberModalBottomSheetState()
        if (showBottomSheet_card) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_card = false },
                sheetState = sheetState_card,
                shape = Round(sheetState_card)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("即时卡片") },
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        FocusCardSettings()
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
        var showBottomSheet_lock by remember { mutableStateOf(false) }
        var sheetState_lock = rememberModalBottomSheetState()
        if (showBottomSheet_lock) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_lock = false },
                sheetState = sheetState_lock,
                shape = Round(sheetState_lock)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("支付设置") },
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        LockUI()
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }




        ListItem(
            headlineContent = { Text(text = "快速启动") },
            supportingContent = { Text(text = "打开后,再次打开应用时将默认打开免登录二级界面,而不是登陆教务页面,但您仍可通过查询中心中的选项以登录")},
            leadingContent = { Icon(painterResource(R.drawable.speed), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = faststart, onCheckedChange = {faststartch -> faststart = faststartch }) },
            modifier = Modifier.clickable { faststart = !faststart }
        )


        if(ifSaved)
            ListItem(
                headlineContent = { Text(text = "主页面") },
                supportingContent = {
                    Column {
                        Text(text = "选择作为本地速览的第一页面")
                        Row {
                            FilterChip(
                                onClick = {
                                    showfocus = true
                                    SaveBoolean("SWITCHFOCUS",true,showfocus)
                                },
                                label = { Text(text = "聚焦") }, selected = showfocus)
                            Spacer(modifier = Modifier.width(10.dp))
                            FilterChip(
                                onClick = {
                                    showfocus = false
                                    SaveBoolean("SWITCHFOCUS",false,showfocus)
                                },
                                label = { Text(text = "课程表") }, selected = !showfocus)
                        }
                    }
                },
                leadingContent = { Icon(painterResource(if(showfocus)R.drawable.lightbulb else R.drawable.calendar), contentDescription = "Localized description",) },
                //trailingContent = { Switch(checked = showfocus, onCheckedChange = {showfocusch -> showfocus = showfocusch }) },
                modifier = Modifier.clickable {
                    showfocus = !showfocus
                    SaveBoolean("SWITCHFOCUS",true,showfocus)
                }
            )

        ListItem(
            headlineContent = { Text(text = "学期") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.approval), contentDescription = "")
            },
            supportingContent = {
                Column {
                    Text(text = "全局学期 ${getSemseter(getSemseterCloud())}", fontWeight = FontWeight.Bold)
                    Text(text = "其他部分功能如教评、成绩等需要在全局学期下切换学期的，可在相应功能区切换")
                }
            },
            modifier = Modifier.clickable {  MyToast("全局学期不可修改,受服务器云控") }
        )

        ListItem(
            headlineContent = { Text(text = "即时卡片") },
            supportingContent = { Text(text = "启动APP时会自动加载或更新一些即时数据,您可按需调整") },
            leadingContent = { Icon(painterResource(R.drawable.reset_iso), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_card = true }
        )

        ListItem(
            headlineContent = { Text(text = "打开外部链接") },
            supportingContent = {
                Column {
                    Text(text = "您希望链接从应用内部打开或者调用系统浏览器")
                    Row {
                        FilterChip(
                            onClick = {
                                showStartUri = true
                                SaveBoolean("SWITCHSTARTURI",true,showStartUri)
                            },
                            label = { Text(text = "应用内部") }, selected = showStartUri)
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            onClick = {
                                showStartUri = false
                                SaveBoolean("SWITCHSTARTURI",false,showStartUri)
                            },
                            label = { Text(text = "外部浏览器") }, selected = !showStartUri)
                    }
                }
            },
            leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                showStartUri = !showStartUri
                SaveBoolean("SWITCHSTARTURI",true,showStartUri)
            }
        )

        ListItem(
            headlineContent = { Text(text = "支付设置") },
            supportingContent = {
                Text(text = "调用校园卡进行网电缴费时,启用生物识别快速验证")
            },
            leadingContent = { Icon(painterResource(R.drawable.lock), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_lock = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetWorkScreen(navController: NavController,
              innerPaddings : PaddingValues,
                  ifSaved : Boolean
             ) {
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPI", apiCheck())
        var showapi by remember { mutableStateOf(switch_api) }


        SaveBoolean("SWITCHMYAPI",false,showapi)
        val my = prefs.getString("my","")

        val switch_upload = SharePrefs.prefs.getBoolean("SWITCHUPLOAD",true )
        var upload by remember { mutableStateOf(switch_upload) }
        SaveBoolean("SWITCHUPLOAD",true,upload)


       // val deault = try { Gson().fromJson(my,MyAPIResponse::class.java).useNewAPI } catch (e : Exception) { true }
        val switch_server = SharePrefs.prefs.getBoolean("SWITCHSERVER",false )
        var server by remember { mutableStateOf(switch_server) }
        SaveBoolean("SWITCHSERVER",false,server)



        var showBottomSheet_input by remember { mutableStateOf(false) }
        val sheetState_input = rememberModalBottomSheetState()
        if (showBottomSheet_input) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_input = false },
                sheetState = sheetState_input,
                shape = Round(sheetState_input)
            ) {
                InfoSet()
                Spacer(modifier = Modifier.height(100.dp))
            }
        }



        var showBottomSheet_arrange by remember { mutableStateOf(false) }
        var sheetState_arrange = rememberModalBottomSheetState()
        if (showBottomSheet_arrange) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_arrange = false },
                sheetState = sheetState_arrange,
                shape = Round(sheetState_arrange)
            ) {
                RequestArrange()
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        ListItem(
            headlineContent = { Text(text = "云端交互(Beta) & 新聚焦接口") },
            supportingContent = { Text(text = "打开后,部分信息将回传云端,不包括用户敏感信息,即使关闭状态下仍保持部分必须云端数据交换")},
            leadingContent = { Icon(painterResource(R.drawable.filter_drama), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = false, onCheckedChange = {serverch -> server = serverch }, enabled = false) },
           // modifier = Modifier.clickable { server = !server }
        )


        ListItem(
            headlineContent = { Text(text = "请求范围") },
            supportingContent = { Text(text = "自定义加载一页时出现的数目,数目越大,加载时间相应地会更长,但可显示更多信息") },
            leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {  showBottomSheet_arrange = true }
        )
        ListItem(
            headlineContent = { Text(text = "网络接口") },
            supportingContent = { Text(text = "本接口为计算机23-X班提供了除学校系统之外的聚焦信息") },
            leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = showapi, onCheckedChange = {showapich -> showapi = showapich }) },
            modifier = Modifier.clickable { showapi = !showapi }
        )
        ListItem(
            headlineContent = { Text(text = "云运动 信息配置") },
            supportingContent = { Text(text = "需要提交已登录手机的信息") },
            leadingContent = { Icon(painterResource(R.drawable.mode_of_travel), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_input = true }
        )

        if(ifSaved)
            ListItem(
                headlineContent = { Text(text = "刷新登录状态") },
                supportingContent = { Text(text = "如果一卡通或者考试成绩等无法查询,可能是登陆过期,需重新登录一次") },
                leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {
                    val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("nologin",false)
                    }
                    MyApplication.context.startActivity(it) }
            )

        ListItem(
            headlineContent = { Text(text = "用户统计数据") },
            supportingContent = { Text(text = "允许上传非敏感数据,以帮助更好的改进体验") },
            leadingContent = { Icon(painterResource(R.drawable.cloud_upload), contentDescription = "Localized description",) },
            trailingContent = {Switch(checked = upload, onCheckedChange = { uploadch -> upload = uploadch }, enabled = true)}
        )
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockUI() {
    val switch_pin = SharePrefs.prefs.getBoolean("SWITCHPIN",false)
    var pin by remember { mutableStateOf(switch_pin) }
    SaveBoolean("SWITCHPIN", false,pin)

    var psk = SharePrefs.prefs.getString("pins",null)
   // var password by remember { mutableStateOf(psk ?: "") }
    var input by remember { mutableStateOf("") }
   // Save("pins",password )
    var showDialog by remember { mutableStateOf(psk == null) }


    var sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    ListItem(
        headlineContent = { Text(text = "需要密码") },
        supportingContent = { Text(text = "在调用支付时选择是否需要验证") },
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.lock), contentDescription = "")
        },
        trailingContent = {
            Switch(checked = pin, onCheckedChange = {_->
                    showDialog = true
            })
        }
    )

   // if(pin) {
        if (showDialog) {
           // if(password.length != 6)
            ModalBottomSheet(
                onDismissRequest = { showDialog = false },
                sheetState = sheetState,
               // shape = Round(sheetState)
            ) {
                Column {
                    CirclePoint(text = if(!pin)"录入新密码" else "请输入密码", password = input)
                    Spacer(modifier = Modifier.height(20.dp))
                    KeyBoard(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        onKeyClick = { num ->
                            if (input.length < 6) {
                                input += num.toString()
                            }
                            if(input.length == 6) {
                                if(pin) {
                                    psk = SharePrefs.prefs.getString("pins",null)
                                    if(input == psk) {
                                        Save("pins",null)
                                        pin = false
                                        MyToast("已移除密码")
                                        showDialog = false
                                    } else {
                                        input = ""
                                    }

                                } else {
                                    Save("pins",input)
                                    pin = true
                                    showDialog = false
                                    MyToast("新建密码成功 密码为${input}")
                                }
                            }
                        },
                        onBackspaceClick = {
                            if (input.isNotEmpty()) {
                                input = input.dropLast(1)
                            }
                        }
                    )
                }
            }
        }
    if(pin)
        ListItem(
            headlineContent = { Text(text = "生物识别") },
            supportingContent = { Text(text = "调用指纹传感器以免密码") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.how_to_reg), contentDescription = "")
            },
            modifier = Modifier.clickable {
                MyToast("正在开发")
            }
        )
    //}
}

@Composable
fun KeyBoard(modifier : Modifier = Modifier,onKeyClick: (Int) -> Unit, onBackspaceClick: () -> Unit) {
    Column(modifier = modifier) {
        for(i in 1 until 8 step 3) {
            Divider()
            Row {
                Key(num = i, modifier = Modifier
                    .weight(.33f)
                    .height(65.dp),onKeyClick = onKeyClick,
                )
                //Divider()
                Key(num = i+1,modifier = Modifier
                    .weight(.33f)
                    .height(65.dp),onKeyClick = onKeyClick,)
                //Divider()
                Key(num = i+2,modifier = Modifier
                    .weight(.33f)
                    .height(65.dp),onKeyClick = onKeyClick,)
            }
        }
        Divider()
        Row {
            TextButton(onClick = { /*TODO*/ }, modifier = Modifier
                .weight(.33f)
                .height(65.dp)
                ,shape = RoundedCornerShape(0.dp)
            ) {
                Text("", fontSize = 13.sp)
            }
            //Divider()
            Key(num = 0,modifier = Modifier
                .weight(.33f)
                .height(65.dp),onKeyClick = onKeyClick
            )
            //Divider()
            TextButton(onClick =  onBackspaceClick , modifier = Modifier
                .weight(.33f)
                .height(65.dp),shape = RoundedCornerShape(0.dp)) {
                Icon(painter = painterResource(id = R.drawable.backspace), contentDescription = "", modifier = Modifier.size(30.dp))
            }
        }
       // Spacer(modifier = Modifier.height(10.dp))
    }
}
@Composable
fun Key(num : Int,modifier: Modifier = Modifier,onKeyClick: (Int) -> Unit) {
    TextButton(onClick = { onKeyClick(num) }, modifier = modifier, shape = RoundedCornerShape(0.dp)) {
        Text(num.toString(), fontSize = 28.sp)
    }
}

@Composable
fun CirclePoint(modifier: Modifier = Modifier,text : String,password : String) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            Text(text = text)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            for(i in 0 until 6) {
                Icon(painter = painterResource(id = if(i < password.length) R.drawable.circle_filled else R.drawable.circle), contentDescription = "",modifier = Modifier.padding(7.dp).size(17.dp),tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}