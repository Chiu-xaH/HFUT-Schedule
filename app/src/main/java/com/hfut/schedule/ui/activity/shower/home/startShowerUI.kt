package com.hfut.schedule.ui.activity.shower.home

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.zjgd.FeeType
import com.hfut.schedule.logic.beans.zjgd.PayStep1Response
import com.hfut.schedule.logic.beans.zjgd.PayStep2Response
import com.hfut.schedule.logic.beans.zjgd.PayStep3Response
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.activity.home.search.functions.electric.getPsk
import com.hfut.schedule.ui.activity.shower.login.getGuaGuaPersonInfo
import com.hfut.schedule.ui.activity.home.search.functions.shower.tranamt
import com.hfut.schedule.ui.utils.BottomTip
import com.hfut.schedule.ui.utils.CardForListColor
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.LittleDialog
import com.hfut.schedule.ui.utils.MyCard
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.Round
import com.hfut.schedule.ui.utils.RowHorizal
import com.hfut.schedule.ui.utils.ScrollText
import com.hfut.schedule.ui.utils.statusUI
import com.hfut.schedule.ui.utils.statusUI2
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun CameraPreview(imageAnalysis: ImageAnalysis) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

class QRCodeAnalyzer(private val listener: (com.google.zxing.Result?) -> Unit) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // 获取YUV数据并转换为ByteArray
            val yBuffer = mediaImage.planes[0].buffer
            val uBuffer = mediaImage.planes[1].buffer
            val vBuffer = mediaImage.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val byteArray = ByteArray(ySize + uSize + vSize)
            yBuffer.get(byteArray, 0, ySize)
            vBuffer.get(byteArray, ySize, vSize)
            uBuffer.get(byteArray, ySize + vSize, uSize)

            val source = PlanarYUVLuminanceSource(
                byteArray, mediaImage.width, mediaImage.height,
                0, 0, mediaImage.width, mediaImage.height, false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = reader.decode(binaryBitmap)
                listener(result)
            } catch (e: Exception) {
                //Log.d("error",e.toString())
                listener(null)
            } finally {
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GuaguaStart(vm: GuaGuaViewModel, innerPadding : PaddingValues) {
    var input by remember { mutableStateOf("") }
    var id by remember { mutableStateOf(-1) }
    var editMode by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialog_Del by remember { mutableStateOf(false) }
    val personInfo = getGuaGuaPersonInfo()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
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
                            .padding(horizontal = 15.dp),
                        value = inputName,
                        onValueChange = {
                            inputName = it
                        },
                        label = { Text("备注名称(可参考花洒红色贴纸)" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                        ),
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
                        ShowerDataBaseManager.addItems(inputName,input)
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
            dialogTitle = "删除",
            dialogText = "要删除这个标签吗",
            conformtext = "确定",
            dismisstext = "取消"
        )
    }

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("结果") },
                    )
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
    if(show)
        CameraPreview(imageAnalysis)
    else {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            DividerText(text = "个人信息")
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardForListColor()
            ) {
                ListItem(
                    headlineContent = { personInfo?.let { Text(text = "￥${tranamt(it.accountMoney)}",fontSize = 28.sp) } },
                )
                Row {
                    ListItem(
                        headlineContent = { personInfo?.let { Text(text = it.name) } },
                        leadingContent = {
                            Icon(painterResource(id = R.drawable.person), contentDescription = "")
                        },
                        overlineContent = { Text(text = "姓名") },
                        modifier = Modifier.weight(.4f)
                    )
                    ListItem(
                        headlineContent = { personInfo?.let { ScrollText(text = it.telPhone) } },
                        leadingContent = {
                            Icon(painterResource(id = R.drawable.call), contentDescription = "")
                        },
                        overlineContent = { Text(text = "手机号") },
                        modifier = Modifier.weight(.6f)
                    )
                }
            }
            DividerText(text = "开始洗浴")
//            RowHorizal {
//                AssistChip(onClick = { show = true }, label = {Text(text = "扫码")})
//                Spacer(modifier = Modifier.width(10.dp))
//                AssistChip(onClick = { input = "" }, label = {Text(text = "填写MAC地址")})
//                Spacer(modifier = Modifier.width(10.dp))
//                AssistChip(onClick = { MyToast("从下方选择标签,填充MAC地址") }, label = {Text(text = "常用标签")})
//            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("花洒MAC地址" ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                show = true
                            }) {
                            Icon(painter = painterResource(R.drawable.qr_code_scanner), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                        unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                    ),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),horizontalArrangement = Arrangement.Center) {
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
                    Text(text = "添加为常用")
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
            DividerText(text = "常用标签")
            val list = ShowerDataBaseManager.queryAll()
            LazyRow(modifier = Modifier.padding(horizontal = 15.dp)) {
                items(list.size) { index ->
                    AssistChip(
                        onClick = {
                               if(!editMode) {
                                   input = list[index].mac
                               } else {
                                   id = list[index].id
                                   showDialog_Del = true
                               }
                    }, label = { Text(text = list[index].name)}, trailingIcon = {
                            if(editMode)
                        Icon(painterResource(id = R.drawable.close), contentDescription = "")
                        })
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            RowHorizal {
                FilledTonalButton(onClick = { editMode = !editMode }) {
                    Text(text = if(editMode)"完成" else "编辑标签")
                }
            }
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
    }
}

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
                CircularProgressIndicator()
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