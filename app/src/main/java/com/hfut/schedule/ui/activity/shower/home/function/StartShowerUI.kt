package com.hfut.schedule.ui.activity.shower.home.function

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.camera.core.ImageAnalysis
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.PermissionManager.checkAndRequestCameraPermission
import com.hfut.schedule.logic.utils.QRCodeAnalyzer
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.ui.activity.shower.home.ShowerDataBaseManager
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.CameraScan
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.LittleDialog
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.statusUI2
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import com.hfut.schedule.ui.utils.style.RowHorizontal
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun startShower(vm : GuaGuaViewModel, macLocation : String) {
    val loginCode = SharePrefs.prefs.getString("loginCode",null)
    val phoneNumber = SharePrefs.prefs.getString("PHONENUM",null)
    phoneNumber?.let {
        loginCode?.let {
                it1 -> vm.startShower(phoneNumber = it, loginCode = it1, macLocation = macLocation)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowerStatusUI(vm : GuaGuaViewModel) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    var msg  by remember { mutableStateOf("结果") }

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.startShowerData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("message")) {
                                msg = Gson().fromJson(result, StatusMsgResponse::class.java).message
                                refresh = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }


    Box {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                LoadingUI()
            }
        }


        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            statusUI2(painter =
            if(msg.contains("成功")) Icons.Filled.Check
            else Icons.Filled.Close
                , text = msg)
        }
    }

}

data class StatusMsgResponse(val message : String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartShowerUI(vm: GuaGuaViewModel,hazeState: HazeState) {
    val context = LocalContext.current
    val activity = context as Activity

    var input by remember { mutableStateOf("") }
    var id by remember { mutableStateOf(-1) }
    var editMode by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val sheetState_scan = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDialog by remember { mutableStateOf(false) }
    var showDialog_Del by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val imageAnalysis = ImageAnalysis.Builder() .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888).build().also {
        it.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeAnalyzer { result ->
            result?.let {
                // 处理扫描结果
                if(result.text.contains("KLCXKJ-Water")) {
                    try {
                        val list = result.text.split(",")
                        input = list[2]
                        show = false
                    } catch (_:Exception) {
                        MyToast("解析出错")
                    }
                }
            }
        })
    }
    var isFull by remember { mutableStateOf(false) }
    val height by animateDpAsState(
        targetValue = if(isFull) 1000.dp else 500.dp, label = "",
        animationSpec = tween(MyApplication.ANIMATION_SPEED, easing = FastOutSlowInEasing)
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
                CameraScan(imageAnalysis, modifier = Modifier.fillMaxSize())

                FilledTonalIconButton (
                    onClick = { show = false },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .7f)),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(appHorizontalDp()).size(60.dp),
                ) {
                    Icon(Icons.Filled.Close,null, modifier = Modifier.size(30.dp))
                }
                FilledTonalIconButton (
                    onClick = { isFull = !isFull },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .7f)),
                    modifier = Modifier.align(Alignment.BottomStart).padding(appHorizontalDp()).size(60.dp),
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
                            .padding(horizontal = appHorizontalDp()),
                        value = inputName,
                        onValueChange = {
                            inputName = it
                        },
                        label = { Text("备注名称(可参考花洒红色贴纸)" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
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
                        ShowerDataBaseManager.addItems(inputName, input)
                        showDialog = false
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
                if(id != -1)
                    ShowerDataBaseManager.removeItems(id)
                showDialog_Del = false
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
                    ShowerStatusUI(vm)
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
                .padding(horizontal = appHorizontalDp()),
            value = input,
            onValueChange = {
                input = it
            },
            label = { Text("花洒MAC地址" ) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        checkAndRequestCameraPermission(activity)
                        show = true
                    }) {
                    Icon(painter = painterResource(R.drawable.qr_code_scanner), contentDescription = "description")
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = textFiledTransplant(),
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = appHorizontalDp()),horizontalArrangement = Arrangement.Center) {
        FilledTonalButton(onClick = {
            if(input == "") MyToast("请扫码或填写二维码贴纸下的MAC地址")
            else if(input.length != 12) {
                MyToast("MAC地址为12位")
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
            if(input == "") MyToast("请扫码或填写二维码贴纸下的MAC地址")
            else if(input.length != 12) {
                MyToast("MAC地址为12位")
            }
            else {
                startShower(vm,input)
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
    val list = ShowerDataBaseManager.queryAll()
    if(list.size != 0) {
        DividerTextExpandedWith(text = "常用标签") {
            LazyRow(modifier = Modifier.padding(horizontal = appHorizontalDp())) {
                items(list.size) { index ->
                    AssistChip(
                        onClick = {
                            if(!editMode) {
                                input = list[index].mac
                            } else {
                                id = list[index].id
                                showDialog_Del = true
                            }
                        }, label = { Text(text = list[index].name) }, trailingIcon = {
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