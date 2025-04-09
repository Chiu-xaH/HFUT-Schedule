package com.hfut.schedule.ui.activity.home.cube.items.NavUIs

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.SharePrefs.saveBoolean
import com.hfut.schedule.ui.activity.home.cube.items.main.Screen
import com.hfut.schedule.ui.activity.home.cube.items.subitems.RequestArrange
import com.hfut.schedule.ui.activity.home.search.functions.lepaoYun.InfoSet
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetWorkScreen(navController: NavController,
                  innerPaddings : PaddingValues,
                  ifSaved : Boolean,
                  hazeState: HazeState
) {
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPIS", isFocus())
        var showapi by remember { mutableStateOf(switch_api) }


        saveBoolean("SWITCHMYAPIS",false,showapi)

        val switch_upload = SharePrefs.prefs.getBoolean("SWITCHUPLOAD",true )
        var upload by remember { mutableStateOf(switch_upload) }
        saveBoolean("SWITCHUPLOAD",true,upload)


        // val deault = try { Gson().fromJson(my,MyAPIResponse::class.java).useNewAPI } catch (e : Exception) { true }
        val switch_server = SharePrefs.prefs.getBoolean("SWITCHSERVER",false )
        var server by remember { mutableStateOf(switch_server) }
        saveBoolean("SWITCHSERVER",false,server)


        TransplantListItem(
            headlineContent = { Text(text = "即时卡片(预加载)") },
            supportingContent = { Text(text = "冷启动APP或下拉刷新时会自动更新一些即时数据,可按需调整") },
            leadingContent = { Icon(painterResource(R.drawable.reset_iso), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { navController.navigate(Screen.FocusCardScreen.route) }
        )

        TransplantListItem(
            headlineContent = { Text(text = "请求范围") },
            supportingContent = { Text(text = "自定义加载一页时出现的数目,数目越大,加载时间相应地会更长,但可显示更多信息") },
            leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {  navController.navigate(Screen.RequestRangeScreen.route) }
        )


        if(isFocus())
            TransplantListItem(
                headlineContent = { Text(text = "网络接口") },
                supportingContent = { Text(text = "为您提供除学校系统之外的聚焦信息\n(开发者同班同学仅可见此开关)") },
                leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showapi, onCheckedChange = {showapich -> showapi = showapich }) },
                modifier = Modifier.clickable { showapi = !showapi }
            )


        if(ifSaved)
            TransplantListItem(
                headlineContent = { Text(text = "刷新登录状态") },
                supportingContent = { Text(text = "如果一卡通或者考试成绩等无法查询,可能是登陆过期,需重新登录一次") },
                leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {
                    refreshLogin()
                }
            )

        TransplantListItem(
            headlineContent = { Text(text = "用户统计数据") },
            supportingContent = { Text(text = "允许上传非敏感数据,以帮助更好的改进体验") },
            leadingContent = { Icon(painterResource(R.drawable.cloud_upload), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = upload, onCheckedChange = { uploadch -> upload = uploadch }, enabled = true) }
        )
        TransplantListItem(
            headlineContent = { Text(text = "云控") },
            supportingContent = { Text(text = "聚在工大部分内容受云端控制") },
            leadingContent = { Icon(painterResource(R.drawable.cloud), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                // TODO
            }
        )
        Spacer(Modifier.height(innerPaddings.calculateBottomPadding()))

    }

}

private fun isFocus() : Boolean = try {
    val personInfo = getPersonInfo()
    val classInfo = personInfo.classes
    personInfo.school!!.contains("宣城") && classInfo!!.contains("计算机") && classInfo.contains("23-3")
} catch (_:Exception) {
    false
}