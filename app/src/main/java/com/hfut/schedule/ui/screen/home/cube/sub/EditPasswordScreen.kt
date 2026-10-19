package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getHuiXinPsk
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getSchoolNetPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.isEmptyOrBlank

import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.InnerPaddingHeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditHuixXinPasswordScreen(innerPadding : PaddingValues) {
    val scope = rememberCoroutineScope()
    val campus = remember { getCampusRegion() }
    val context = LocalContext.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith(
            when(campus) {
                CampusRegion.HEFEI -> {
                    "一卡通密码"
                }
                CampusRegion.XUANCHENG -> {
                    "一卡通及校园网密码"
                }
            }
        ) {
            val useDefaultCardPassword by DataStoreManager.enableUseDefaultCardPassword.collectAsState(initial = true)
            val useEditedPwd = !useDefaultCardPassword
            var input by remember { mutableStateOf(EMPTY_STRING) }
            var showDialog by remember { mutableStateOf(false) }
            var originPwd by remember { mutableStateOf(EMPTY_STRING) }

            LaunchedEffect(showDialog,useEditedPwd) {
                originPwd = getHuiXinPsk() ?: EMPTY_STRING
            }

            if (showDialog) {
                HazeBottomSheet (
                    onDismissRequest = { showDialog = false },
                    showBottomSheet = showDialog
                ) {
                    Column {
                        Spacer(Modifier.height(APP_HORIZONTAL_DP*1.5f))
                        CirclePoint(text = "录入新密码" , password = input)
                        Spacer(modifier = Modifier.height(20.dp))
                        KeyBoard(
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                            onKeyClick = { num ->
                                if (input.length < 6) {
                                    input += num.toString()
                                }
                                if(input.length == 6) {
                                    scope.launch {
                                        DataStoreManager.saveUseDefaultCardPassword(false)
                                        DataStoreManager.saveCardPassword(input)
                                        showDialog = false
                                        showToast("定义密码成功")
                                        // 更新UI显示的密码
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

            val auth = remember { prefs.getString("auth","") }

            val click = {
                if(useEditedPwd) {
                    scope.launch { DataStoreManager.saveUseDefaultCardPassword(true) }
                } else {
                    input = EMPTY_STRING
                    showDialog = true
                }
            }
            CustomCard(
                color = MaterialTheme.colorScheme.surface
            ) {
                TransplantListItem(
                    headlineContent = { Text("修改密码")},
                    supportingContent = {
                        Text("前往慧新易校修改密码")
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebUrlInner(context,"${Constant.HUI_XIN_URL}campus-card/cardSetPwd" + "?synjones-auth=" + auth,"修改密码", icon = R.drawable.lock_reset)
                        }
                    },
                    leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("使用自定义密码") },
                    supportingContent = {
                        if(!useEditedPwd) {
                            Text("若已不是初始密码,请打开开关并录入新密码")
                        } else {
                            Text("若已不是当前密码,请重新切换开关以录入新密码到本地")
                        }
                    },
                    trailingContent = { Switch(checked = useEditedPwd, onCheckedChange = {
                        click()
                    }) },
                    leadingContent = { Icon(painterResource(R.drawable.edit),null) },
                    modifier = Modifier.clickable {
                        click()
                    },
                )
            }
            Spacer(Modifier.height(CARD_NORMAL_DP))
            CardListItem(
                leadingContent = { Icon(painterResource(R.drawable.info),null) },
                color = MaterialTheme.colorScheme.surface,
                supportingContent = {
                    Text("初始密码为身份证后6位(若以X结尾则为X前6位)")
                },
                headlineContent = { Text( "当前APP使用" + (if(useEditedPwd) "密码" else "初始密码") + " " + originPwd) },
            )
        }
        when(campus) {
            CampusRegion.XUANCHENG -> Unit
            CampusRegion.HEFEI -> {
                DividerTextExpandedWith("校园网密码") {
                    val useDefaultSchoolNetPassword by DataStoreManager.enableUseDefaultSchoolNetPassword.collectAsState(initial = true)
                    val useEditedPwd = !useDefaultSchoolNetPassword
                    var input by remember { mutableStateOf(EMPTY_STRING) }
                    var showDialog by remember { mutableStateOf(false) }
                    var originPwd by remember { mutableStateOf(EMPTY_STRING) }

                    LaunchedEffect(showDialog,useEditedPwd) {
                        originPwd = getSchoolNetPsk() ?: EMPTY_STRING
                    }

                    if (showDialog) {
                        HazeBottomSheet (
                            onDismissRequest = { showDialog = false },
                            showBottomSheet = showDialog
                        ) {
                            Column {
                                Spacer(Modifier.height(APP_HORIZONTAL_DP*1.5f))
                                CirclePoint(text = "录入新密码" , password = input)
                                Spacer(modifier = Modifier.height(20.dp))
                                KeyBoard(
                                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                                    onKeyClick = { num ->
                                        if (input.length < 6) {
                                            input += num.toString()
                                        }
                                        if(input.length == 6) {
                                            scope.launch {
                                                DataStoreManager.saveUseDefaultSchoolNetPassword(false)
                                                DataStoreManager.saveSchoolNetPassword(input)
                                                showDialog = false
                                                showToast("定义密码成功")
                                                // 更新UI显示的密码
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

                    val click = {
                        if(useEditedPwd) {
                            scope.launch { DataStoreManager.saveUseDefaultSchoolNetPassword(true) }
                        } else {
                            input = EMPTY_STRING
                            showDialog = true
                        }
                    }
                    CustomCard(
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        TransplantListItem(
                            headlineContent = { Text("修改密码")},
                            supportingContent = {
                                Text("前往管理平台修改校园网的密码")
                            },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    Starter.startWebUrlInner(
                                        context,
                                        "http://172.31.243.228/Self/setting/changePassword",
                                        "修改密码",
                                        icon = R.drawable.lock_reset
                                    )
                                }
                            },
                            leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                        )
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = { Text("使用自定义密码") },
                            supportingContent = {
                                if(!useEditedPwd) {
                                    Text("若已不是初始密码,请打开开关并录入新密码")
                                } else {
                                    Text("若已不是当前密码,请重新切换开关以录入新密码到本地")
                                }
                            },
                            trailingContent = { Switch(checked = useEditedPwd, onCheckedChange = {
                                click()
                            }) },
                            leadingContent = { Icon(painterResource(R.drawable.edit),null) },
                            modifier = Modifier.clickable {
                                click()
                            },
                        )
                    }
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                    CardListItem(
                        leadingContent = { Icon(painterResource(R.drawable.info),null) },
                        color = MaterialTheme.colorScheme.surface,
                        supportingContent = {
                            Text("初始密码为身份证后6位(包含X)")
                        },
                        headlineContent = { Text( "当前APP使用" + (if(useEditedPwd) "密码" else "初始密码") + " " + originPwd) },
                    )
                }
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}

// 不完全准确，如果有用户后6位一模一样就会增加%数量，只作为最终替补
fun getJxglstuDefaultPassword() : String? {
    val p = getPersonInfo().chineseID?.takeLast(6) ?: return null
    return "Hfut@#$%${p}"
}

suspend fun getJxglstuPassword() : String? = withContext(Dispatchers.IO) {
    val pwd = DataStoreManager.jxglstuPassword.first()
    if(pwd.isEmptyOrBlank()) {
        getJxglstuDefaultPassword()
    } else {
        pwd
    }
}

@Composable
fun EditJxglstuPasswordScreen(vm : NetWorkViewModel,innerPadding : PaddingValues) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    val pwd by DataStoreManager.jxglstuPassword.collectAsState(initial = EMPTY_STRING)

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        InnerPaddingHeight(innerPadding,true)
        CustomCard(
            color = MaterialTheme.colorScheme.surface
        ) {
            TransplantListItem(
                headlineContent = { Text("修改密码")},
                supportingContent = {
                    Text("如未登录教务系统，需要先刷新教务系统登录后才可用本选项")
                },
                modifier = Modifier.clickable {
                    scope.launch {
                        val cookie = getJxglstuCookie()
                        Starter.startWebUrlInner(context, Constant.JXGLSTU_URL + "my/account", cookie = cookie,title = "修改密码", icon = R.drawable.lock_reset)
                    }
                },
                leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                color = MaterialTheme.colorScheme.surface,
                leadingContent = {
                    if(loading) {
                        LoadingIcon()
                    } else {
                        Icon(painterResource(R.drawable.rotate_right),null)
                    }
                },
                headlineContent = {
                    Text("刷新获取最新密码")
                },
                supportingContent = {
                    Text("每次刷新教务系统登录后即可自动更新密码，或者也可以点击手动刷新")
                },
                modifier = Modifier.clickable {
                    scope.launch {
                        loading = true
                        val cookie = getJxglstuCookie() ?: run {
                            loading = false
                            return@launch
                        }
                        vm.getAccount(cookie)
                        loading = false
                    }
                }
            )
        }
        Spacer(Modifier.height(CARD_NORMAL_DP))
        CustomCard(
            color = MaterialTheme.colorScheme.surface
        ) {
            TransplantListItem(
                color = MaterialTheme.colorScheme.surface,
                leadingContent = { Icon(painterResource(R.drawable.info),null) },
                headlineContent = {
                    Text("当前密码")
                },
                supportingContent = {
                    Text(pwd)
                }
            )
        }
        InnerPaddingHeight(innerPadding,false)
    }
}