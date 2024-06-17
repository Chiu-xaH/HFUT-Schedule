package com.hfut.schedule.ui.Activity.success.cube.Settings.Items

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.SaveBoolean
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.LePaoYun.InfoSet


fun apiCheck() : Boolean {
    try {
        val username = prefs.getString("Username","")?.toInt()
        return username in 2023218488 until 2023218533
    } catch (e : Exception) {
        return false
    }
}
@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartOne(vm : LoginSuccessViewModel, showlable : Boolean, showlablechanged :(Boolean) -> Unit, ifSaved : Boolean, blur : Boolean, blurchanged :(Boolean) -> Unit) {
    val switch_focus = prefs.getBoolean("SWITCHFOCUS",true)
    var showfocus by remember { mutableStateOf(switch_focus) }
    val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPI", apiCheck())
    var showapi by remember { mutableStateOf(switch_api) }
    SaveBoolean("SWITCH",true,showlable)
    SaveBoolean("SWITCHBLUR",true,blur)
    SaveBoolean("SWITCHMYAPI",false,showapi)

    val switch_faststart = SharePrefs.prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)
    var faststart by remember { mutableStateOf(switch_faststart) }
    SaveBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false,faststart)


    ListItem(
        headlineContent = { Text(text = "底栏标签") },
        supportingContent = { Text(text = "屏幕底部的Tab栏底栏标签")},
        leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
        trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
        modifier = Modifier.clickable { showlablechanged }
    )
    ListItem(
        headlineContent = { Text(text = "实时模糊") },
        supportingContent = { Text(text = if(AndroidVersion.sdkInt >= 32) "开启后将会转换部分渲染为实时模糊" else "需为 Android 13+")},
        leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
        trailingContent = {  Switch(checked = blur, onCheckedChange = blurchanged, enabled = AndroidVersion.sdkInt >= 32 ) },
        modifier = Modifier.clickable { blurchanged }
    )
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



    var showBottomSheet_arrange by remember { mutableStateOf(false) }
    var sheetState_arrange = rememberModalBottomSheetState()
    if (showBottomSheet_arrange) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_arrange = false },
            sheetState = sheetState_arrange
        ) {
            RequestArrange()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    var showBottomSheet_card by remember { mutableStateOf(false) }
    var sheetState_card = rememberModalBottomSheetState()
    if (showBottomSheet_card) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_card = false },
            sheetState = sheetState_card
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("即时卡片设置") },
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


    ListItem(
        headlineContent = { Text(text = "请求范围") },
        supportingContent = { Text(text = "自定义加载一页时出现的数目,数目越大,加载时间相应地会更长,但可显示更多信息") },
        leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {  showBottomSheet_arrange = true }
    )
    ListItem(
        headlineContent = { Text(text = "网络接口") },
        supportingContent = { Text(text = "本接口为23地科提供了除学校系统之外的聚焦信息") },
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
        headlineContent = { Text(text = "即时卡片设置") },
        supportingContent = { Text(text = "即时卡片位于聚焦首页，现在您可自定义卡片") },
        leadingContent = { Icon(painterResource(R.drawable.stacks), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet_card = true }
    )
   // ListItem(
     //   headlineContent = { Text(text = "简化选项布局") },
     //   supportingContent = { Text(text = "仅保留设置选项") },
      //  leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
       // modifier = Modifier.clickable {
         //   val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
           //     addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //    putExtra("nologin",false)
           // }
           // MyApplication.context.startActivity(it) }
   // )


}