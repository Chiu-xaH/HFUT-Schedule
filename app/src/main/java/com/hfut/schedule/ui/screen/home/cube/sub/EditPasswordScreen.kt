package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CardListItem

import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.status.CustomSwitch
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.transition.util.TransitionBackHandler
import com.xah.uicommon.style.align.ColumnVertical
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditPasswordScreen(hazeState : HazeState,innerPadding : PaddingValues,navController: NavHostController) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    val useDefaultCardPassword by DataStoreManager.enableUseDefaultCardPassword.collectAsState(initial = true)
    val useEditedPwd = !useDefaultCardPassword
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var originPwd by remember { mutableStateOf("") }

    LaunchedEffect(showDialog,useEditedPwd) {
        originPwd = getCardPsk() ?: ""
    }
    var firstUse by remember { mutableStateOf(true) }

    if (showDialog) {
        HazeBottomSheet (
            onDismissRequest = { showDialog = false },
            autoShape = false,
            hazeState = hazeState,
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
    val context = LocalContext.current

    LaunchedEffect(useEditedPwd) {
        if(useEditedPwd && !firstUse) {
            // 弹出设置密码界面
            input = ""
            showDialog = true
        }
    }
    val click = {
        firstUse = false
        if(useEditedPwd) {
            scope.launch { DataStoreManager.saveUseDefaultCardPassword(true) }
        } else {
            input = ""
            showDialog = true
        }
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        CardListItem(
            headlineContent = { Text("初始密码为身份证后6位(若以X结尾则为X前6位)")},
            color = MaterialTheme.colorScheme.surface,
            leadingContent = { Icon(painterResource(R.drawable.info),null) }
        )
        DividerTextExpandedWith("一卡通及校园网密码") {
            CustomCard(
                color = MaterialTheme.colorScheme.surface
            ) {
                TransplantListItem(
                    headlineContent = { Text("修改密码")},
                    supportingContent = {
                        Text("前往慧新易校修改一卡通及其校园网的密码")
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebView(context,"${MyApplication.HUI_XIN_URL}campus-card/cardSetPwd" + "?synjones-auth=" + auth,"修改密码", icon = R.drawable.lock_reset)
                        }
                    },
                    leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("使用自定义密码") },
                    supportingContent = {
                        if(!useEditedPwd) {
                            Text("若已不是初始密码,请打开开关并录入新密码,否则APP的校园网与一卡通相关功能默认以初始密码进行")
                        } else {
                            Text("若已不是当前密码,请重新切换开关以录入新密码,否则APP的校园网与一卡通相关功能默认以显示的密码进行")
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
        }
        Spacer(Modifier.height(CARD_NORMAL_DP))
        CardListItem(
            color = MaterialTheme.colorScheme.surface,
            headlineContent = { Text( "当前APP使用" + (if(useEditedPwd) "密码" else "初始密码") + " " + originPwd) },
        )
        InnerPaddingHeight(innerPadding,false)
    }
}

fun getJxglstuDefaultPassword() : String? {
    val p = getPersonInfo().chineseID?.takeLast(6) ?: return null
    return "Hfut@#$%${p}"
}

suspend fun getJxglstuPassword() : String? = withContext(Dispatchers.IO) {
    val useDefaultCardPassword = DataStoreManager.enableUseDefaultJxglstuPassword.first()
    if(useDefaultCardPassword) {
        getJxglstuDefaultPassword()
    } else {
        val pwd = DataStoreManager.jxglstuPassword.first()
        if(pwd.isEmpty() || pwd.isBlank()) {
            null
        } else {
            pwd
        }
    }
}

@Composable
fun EditJxglstuPasswordScreen(innerPadding : PaddingValues,navController: NavHostController) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    val useDefaultCardPassword by DataStoreManager.enableUseDefaultJxglstuPassword.collectAsState(initial = true)
    val useEditedPwd = !useDefaultCardPassword
    val originPwd = remember { getJxglstuDefaultPassword() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val pwd by DataStoreManager.jxglstuPassword.collectAsState(initial = originPwd ?: "")


    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        CardListItem(
            headlineContent = { Text("初始密码为 ${originPwd}")},
            color = MaterialTheme.colorScheme.surface,
            leadingContent = { Icon(painterResource(R.drawable.info),null) }
        )
        DividerTextExpandedWith("合工大教务及教务系统密码") {
            CustomCard(
                color = MaterialTheme.colorScheme.surface
            ) {
                TransplantListItem(
                    headlineContent = { Text("修改密码")},
                    supportingContent = {
                        Text("前往教务系统，点击右上角个人信息修改密码")
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebView(context, "","修改密码", icon = R.drawable.lock_reset)
                        }
                    },
                    leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("使用自定义密码") },
                    supportingContent = {
                        if(!useEditedPwd) {
                            Text("若已不是初始密码,请打开开关并录入新密码,否则APP的校园网与一卡通相关功能默认以初始密码进行")
                        } else {
                            Text("若已不是当前密码,请重新切换开关以录入新密码,否则APP的同班同学、教室、全校培养方案功能将无法使用")
                        }
                    },
                    trailingContent = { Switch(checked = useEditedPwd, onCheckedChange = {
                        scope.launch {
                            DataStoreManager.saveUseDefaultJxglstuPassword(useEditedPwd)
                        }
                    }) },
                    leadingContent = { Icon(painterResource(R.drawable.edit),null) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            DataStoreManager.saveUseDefaultJxglstuPassword(useEditedPwd)
                        }
                    },
                )
                if(useEditedPwd) {
                    var input by remember { mutableStateOf("") }
                    LaunchedEffect(pwd) {
                        input = pwd
                    }
                    CustomTextField(
                        input = input,
                        label = { Text("输入新密码") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        DataStoreManager.saveJxglstuPassword(input)
                                        showToast("保存完成")
                                    }
                                }
                            ) {
                                Icon(painterResource(R.drawable.save),null)
                            }
                        }
                    ) {
                        input = it
                    }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))

                }
            }
        }
        Spacer(Modifier.height(CARD_NORMAL_DP))
        CardListItem(
            color = MaterialTheme.colorScheme.surface,
            headlineContent = { Text( "当前APP使用" + (if(useEditedPwd) "密码" else "初始密码") + " " + pwd) },
        )
        InnerPaddingHeight(innerPadding,false)
    }
}