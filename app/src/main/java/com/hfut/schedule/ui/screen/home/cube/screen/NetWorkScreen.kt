package com.hfut.schedule.ui.screen.home.cube.screen

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveBoolean
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.screen.home.cube.Screen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
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

//        val switch_api = SharedPrefs.prefs.getBoolean("SWITCHMYAPIS", isFocus())
//        var showapi by remember { mutableStateOf(switch_api) }


//        saveBoolean("SWITCHMYAPIS",false,showapi)

        val switch_upload = SharedPrefs.prefs.getBoolean("SWITCHUPLOAD",true )
        var upload by remember { mutableStateOf(switch_upload) }
        saveBoolean("SWITCHUPLOAD",true,upload)


        // val deault = try { Gson().fromJson(my,MyAPIResponse::class.java).useNewAPI } catch (e : Exception) { true }
        val switch_server = SharedPrefs.prefs.getBoolean("SWITCHSERVER",false )
        var server by remember { mutableStateOf(switch_server) }
        saveBoolean("SWITCHSERVER",false,server)

        DividerTextExpandedWith("配置") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "预加载数据") },
                    supportingContent = { Text(text = "APP首页聚焦第一张卡片显示的一些数据,冷启动或下拉刷新时会自动更新这些数据") },
                    leadingContent = { Icon(painterResource(R.drawable.reset_iso), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable { navController.navigate(Screen.FocusCardScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "请求范围") },
                    supportingContent = { Text(text = "自定义加载一页时出现的数目,数目越大,加载时间相应地会更长,但可显示更多信息") },
                    leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {  navController.navigate(Screen.RequestRangeScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "一卡通密码") },
                    supportingContent = { Text(text = "若您已经修改过一卡通初始密码(身份证后6位),请在此录入新的密码以使用快速充值和校园网登录功能") },
                    leadingContent = { Icon(painterResource(R.drawable.password), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable { navController.navigate(Screen.PasswordScreen.route) }
                )
                //        if(isFocus())
//            TransplantListItem(
//                headlineContent = { Text(text = "显示班级内部事件(旧接口)") },
//                supportingContent = { Text(text = "为您提供除学校系统之外的聚焦信息\n(开发者同班同学仅可见此开关)") },
//                leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
//                trailingContent = { Switch(checked = showapi, onCheckedChange = {showapich -> showapi = showapich }) },
//                modifier = Modifier.clickable { showapi = !showapi }
//            )
            }
        }

        DividerTextExpandedWith("其它") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface){
                TransplantListItem(
                    headlineContent = { Text(text = "用户统计数据") },
                    supportingContent = { Text(text = "允许上传非敏感数据,以帮助更好的改进体验") },
                    leadingContent = { Icon(painterResource(R.drawable.cloud_upload), contentDescription = "Localized description",) },
                    trailingContent = { Switch(checked = upload, onCheckedChange = { uploadch -> upload = uploadch }, enabled = true) }
                )
                if(ifSaved) {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text(text = "刷新登录状态") },
                        supportingContent = { Text(text = "如果一卡通或者考试成绩等无法查询,可能是登陆过期,需重新登录一次") },
                        leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
                        modifier = Modifier.clickable {
                            refreshLogin()
                        }
                    )
                }
            }
        }
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