package com.hfut.schedule.ui.screen.shower.home.function

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.PermissionSet.checkAndRequestCameraPermission
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.camera.ScanQrCodeView
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.dialog.LittleDialog
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.status.StatusIcon
import com.xah.uicommon.style.align.RowHorizontal
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun startShower(vm : GuaGuaViewModel, macLocation : String) = withContext(Dispatchers.IO) {
    val loginCode = SharedPrefs.prefs.getString("loginCode",null)
    val phoneNumber = SharedPrefs.prefs.getString("PHONENUM",null)
    phoneNumber?.let { loginCode?.let { it1 -> vm.startShower(phoneNumber = it, loginCode = it1, macLocation = macLocation) } }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowerStatusUI(vm : GuaGuaViewModel,input : String) {
    val uiState by vm.startShowerResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.startShowerResult.clear()
        startShower(vm,input)
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState,isFullScreen = false, onReload = refreshNetwork) {
        val response = (uiState as UiState.Success).data
        StatusIcon(painter = if(response.contains("成功")) Icons.Filled.Check else Icons.Filled.Close, text = response)
    }
}

data class StatusMsgResponse(val message : String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartShowerUI(vm: GuaGuaViewModel, hazeState: HazeState) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var id by remember { mutableIntStateOf(-1) }
    var editMode by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val sheetState_scan = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDialog by remember { mutableStateOf(false) }
    var showDialog_Del by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var list by remember { mutableStateOf<List<ShowerLabelEntity>>(emptyList()) }
    LaunchedEffect(showDialog_Del,showDialog) {
        list = DataBaseManager.showerLabelDao.getAll()
    }
    var isFull by remember { mutableStateOf(false) }
    val height by animateDpAsState(
        targetValue = if(isFull) 1000.dp else 500.dp, label = "",
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED, easing = FastOutSlowInEasing)
    )

    if(show) {
        ModalBottomSheet(
            onDismissRequest = {
                show = false
            },
            sheetState = sheetState_scan,
            shape = if(isFull) bottomSheetRound(sheetState_scan) else BottomSheetDefaults.ExpandedShape,
            dragHandle = {
                if(!isFull) BottomSheetDefaults. DragHandle()
            }
        ) {
            Box(modifier = Modifier.height(height)) {
                ScanQrCodeView(modifier = Modifier.fillMaxSize()) { result ->
                    val text = result.text
                    if(text.contains("KLCXKJ-Water")) {
                        try {
                            val list = text.split(",")
                            input = list[2]
                            show = false
                        } catch (_:Exception) {
                            showToast("解析出错")
                        }
                    }
                }

                FilledTonalIconButton (
                    onClick = { show = false },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .7f)),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(APP_HORIZONTAL_DP).size(60.dp),
                ) {
                    Icon(Icons.Filled.Close,null, modifier = Modifier.size(30.dp))
                }
                FilledTonalIconButton (
                    onClick = { isFull = !isFull },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .7f)),
                    modifier = Modifier.align(Alignment.BottomStart).padding(APP_HORIZONTAL_DP).size(60.dp),
                ) {
                    Icon(painterResource(if(!isFull) R.drawable.expand_content else R.drawable.collapse_content),null, modifier = Modifier.size(30.dp))
                }

            }
        }
    }

    if(showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = APP_HORIZONTAL_DP),
                        value = inputName,
                        onValueChange = {
                            inputName = it
                        },
                        label = { Text("备注名称(可参考花洒红色贴纸)" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(isColorCopy = false),
                        supportingText = {
                            Text(text = input)
                        }
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    FilledTonalButton(onClick = {
                        showDialog = false
                    },modifier = Modifier
                        .weight(.5f)
                    ) {
                        Text(text = "取消")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = {
                        scope.launch {
                            async { DataBaseManager.showerLabelDao.insert(ShowerLabelEntity(name = inputName, mac = input)) }.await()
                            launch { showDialog = false }
                        }
                    },modifier = Modifier
                        .weight(.5f)
                    ) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
    if(showDialog_Del) {
        LittleDialog(
            onDismissRequest = { showDialog_Del = false },
            onConfirmation = {
                if(id != -1) {
                    scope.launch {
                        async { DataBaseManager.showerLabelDao.del(id) }.await()
                        launch { showDialog_Del = false }
                    }
                } else {
                    showToast("id错误")
                    showDialog_Del = false
                }
            },
            dialogText = "要删除这个标签吗",
            hazeState = hazeState
        )
    }

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("结果")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ShowerStatusUI(vm,input)
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = APP_HORIZONTAL_DP),
            value = input,
            onValueChange = {
                input = it
            },
            label = { Text("花洒MAC地址" ) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        activity?.let {
                            checkAndRequestCameraPermission(it)
                            show = true
                        }
                    }) {
                    Icon(painter = painterResource(R.drawable.qr_code_scanner_shortcut), contentDescription = "description")
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = textFiledTransplant(),
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = APP_HORIZONTAL_DP),horizontalArrangement = Arrangement.Center) {
        FilledTonalButton(onClick = {
            if(input == "") showToast("请扫码或填写二维码贴纸下的MAC地址")
            else if(input.length != 12) {
                showToast("MAC地址为12位")
            }
            else {
                showDialog = true
            }
        },modifier = Modifier
            .fillMaxWidth()
            .weight(.5f)
        ) {
            Text(text = "添加到常用标签")
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(onClick = {
            if(input == "") showToast("请扫码或填写二维码贴纸下的MAC地址")
            else if(input.length != 12) {
                showToast("MAC地址为12位")
            } else {
                showBottomSheet = true
            }
        },modifier = Modifier
            .fillMaxWidth()
            .weight(.5f)
        ) {
            Text(text = "开始洗浴")
        }
    }
    BottomTip(str = "开始后预扣￥5,请保证余额>=￥5,否则无法开启洗浴")

    if(list.isNotEmpty()) {
        DividerTextExpandedWith(text = "常用标签") {
            LazyRow(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                items(list.size) { index ->
                    val item = list[index]
                    AssistChip(
                        onClick = {
                            if(!editMode) {
                                input = item.mac
                            } else {
                                id = item.id
                                showDialog_Del = true
                            }
                        }, label = { Text(text = item.name) }, trailingIcon = {
                            if(editMode)
                                Icon(painterResource(id = R.drawable.close), contentDescription = "")
                        })
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            RowHorizontal {
                FilledTonalButton(onClick = { editMode = !editMode }) {
                    Text(text = if(editMode)"完成" else "编辑标签")
                }
            }
        }
    }
}