package com.hfut.schedule.ui.ComposeUI.Search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hfut.schedule.R
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LePaoYun() {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState_input = rememberModalBottomSheetState()
    var showBottomSheet_input by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )


    ListItem(
        overlineContent = { Text(text = "Beta")},
        headlineContent = { Text(text = "校园跑") },
        supportingContent = { Text(text = "本接口需要提交已登录手机的信息,否则会认定为异端登录视为异常!")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mode_of_travel), contentDescription = "")},
        modifier = Modifier.clickable {
            if (prefs.getString("LePaoYun","")?.contains("distance") == false) showDialog = true
            else showBottomSheet_input = true
        }
    )
    
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                showBottomSheet_input = true
                showDialog = false },
            dialogTitle = "提示",
            dialogText = "未检测到输入数据,首次使用需要配置设备信息",
          //  icon = Icons.Filled.Warning,
            conformtext = "开始配置",
            dismisstext = "取消"
        )
    }
    
    if (showBottomSheet_input) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet_input = false },sheetState = sheetState_input){
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("信息配置") },
                        actions = {
                            FilledTonalIconButton(
                                modifier = Modifier
                                    .scale(scale.value)
                                    .padding(25.dp)
                                ,
                                interactionSource = interactionSource,
                                onClick = { /*TODO*/ }
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = "")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                ) {
                    InfoSetUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false },sheetState = sheetState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("校园跑") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LePaoYunUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun LePaoYunUI() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium
    ){
        ListItem(
            headlineContent = { Text(text = "已跑 公里")},
            leadingContent = { Icon(painterResource(id = R.drawable.directions_run), contentDescription = "")}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoSetUI() {
    val savedUA = prefs.getString("LePaoYunUA","")
    var inputUA by remember { mutableStateOf(savedUA ?: "") }
    val savedToken = prefs.getString("LePaoYunToken","")
    var inputToken by remember { mutableStateOf(savedToken ?: "") }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium
    ){
        ListItem(
            headlineContent = { Text(text = "请通过抓包获取常用设备的UA与token,填写在对应位置,并点击提交")},
            leadingContent = { Icon(painterResource(id = R.drawable.info), contentDescription = "")}
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 15.dp),
            value = inputUA,
            onValueChange = { inputUA = it },
            label = { Text("User-Agent" ) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,),
            leadingIcon = { Icon( painterResource(R.drawable.net), contentDescription = "Localized description") }
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 15.dp),
            value = inputToken,
            onValueChange = { inputToken = it },
            label = { Text("token") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,),
            leadingIcon = { Icon( painterResource(R.drawable.key), contentDescription = "Localized description") }
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

}