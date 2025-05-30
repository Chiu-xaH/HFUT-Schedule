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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
 
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun EditPasswordScreen(hazeState : HazeState,innerPadding : PaddingValues) {
    val useDefaultCardPassword by DataStoreManager.useDefaultCardPassword.collectAsState(initial = true)
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
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
        StyleCardListItem(
            headlineContent = { Text("修改或重置密码可前往慧新易校(位于查询中心)")},
            leadingContent = { Icon(painterResource(R.drawable.info),null) }
        )
        TransplantListItem(
            headlineContent = { Text("使用自定义密码") },
            supportingContent = { Text( if(useEditedPwd) "现密码 $originPwd" else "初始密码 $originPwd")},
            trailingContent = { Switch(checked = useEditedPwd, onCheckedChange = {
                click()
            }) },
            modifier = Modifier.clickable {
                click()
            }
        )
        Spacer(Modifier.height(innerPadding.calculateBottomPadding()))
    }
}