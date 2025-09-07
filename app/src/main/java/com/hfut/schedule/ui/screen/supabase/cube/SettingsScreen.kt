package com.hfut.schedule.ui.screen.supabase.cube

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.status.CustomSwitch
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun SupabaseSettingsScreen(vm : NetWorkViewModel,innerPadding : PaddingValues,hazeState: HazeState) {
    val filter by DataStoreManager.enableSupabaseFilterEvent.collectAsState(initial = false)
    val supabaseAutoCheck by DataStoreManager.enableSupabaseAutoCheck.collectAsState(initial = true)
    val context = LocalContext.current
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")

    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    if(showBottomSheet)
        HazeBottomSheet(
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("隐私保护")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    CardListItem(
                        headlineContent = { Text("本地检查") },
                        supportingContent = { Text("APP验证是否拥有教务个人信息，没有则证明用户没登陆过教务系统，不是在校生，直接不展示此功能") },
                        leadingContent = { Text("1") }
                    )
                    CardListItem(
                        headlineContent = { Text("一人一号") },
                        supportingContent = { Text("APP登陆注册锁死为用户自己的校园邮箱，不允许自己输入") },
                        leadingContent = { Text("2") }
                    )
                    CardListItem(
                        headlineContent = { Text("权限僭越") },
                        supportingContent = { Text("增删查改数据库的接口都要经过中间层验证，是否账户邮箱为校园邮箱，不是则直接禁止操作；有越界行为也会禁止操作，例如删除别人的记录") },
                        leadingContent = { Text("3") }
                    )
                    CardListItem(
                        headlineContent = { Text("注册验证") },
                        supportingContent = { Text("注册需要激活账号，激活链接发送到注册邮箱，只有在校生才可以使用其自己的校园邮箱") },
                        leadingContent = { Text("4") }
                    )
                    CardListItem(
                        headlineContent = { Text("审核管理") },
                        supportingContent = { Text("开发者定期查看平台数据库，审核和清理") },
                        leadingContent = { Text("4") }
                    )
                }
            }
        }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("账户") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("修改密码") },
                    leadingContent = { Icon(painterResource(R.drawable.password), null) },
                    modifier = Modifier.clickable { showToast("正在开发") }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("注销") },
                    leadingContent = { Icon(painterResource(R.drawable.delete), null) },
                    modifier = Modifier.clickable { showToast("正在开发") }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("了解隐私保护") },
                    leadingContent = { Icon(painterResource(R.drawable.visibility), null) },
                    modifier = Modifier.clickable { showBottomSheet = true }
                )
            }
        }

        DividerTextExpandedWith("设置") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(if(jwt.isEmpty() || jwt.isBlank()) "注册/登录" else "刷新登陆状态") },
                    leadingContent = { Icon(painterResource(R.drawable.login), null) },
                    modifier = Modifier.clickable { Starter.loginSupabase(context) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("自动检查登录") },
                    supportingContent = { Text("启动APP时自动检查登陆状态，以减少初次等待时间" ) },
                    leadingContent = { Icon(painterResource(R.drawable.rotate_right), null) },
                    trailingContent = {
                        Switch(checked = supabaseAutoCheck, onCheckedChange = {  scope.launch { DataStoreManager.saveSupabaseAutoCheck(!supabaseAutoCheck) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveSupabaseAutoCheck(!supabaseAutoCheck) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("过滤不适用日程") },
                    supportingContent = { Text("仅展示${if(!filter) "所有" else "包含 " + getPersonInfo().campus  + getPersonInfo().className + " 的"}日程" ) },
                    leadingContent = { Icon(painterResource(R.drawable.filter_alt), null) },
                    trailingContent = {
                        Switch(checked = filter, onCheckedChange = {  scope.launch { DataStoreManager.saveSupabaseFilterEvent(!filter) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveSupabaseFilterEvent(!filter) }
                    }
                )
            }
        }
        BottomTip("平台管理请联系 zsh0908@outlook.com")
        InnerPaddingHeight(innerPadding,false)
    }
}
