package com.hfut.schedule.ui.screen.home.search.function.school.scan

import android.net.Uri
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.QRCodeAnalyzer
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.camera.CameraScan
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getWxAuth
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.hfut.schedule.logic.util.other.parseQRCode
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val color = MaterialTheme.colorScheme.surface
    var resultText by remember { mutableStateOf("") }
    val route = remember { AppNavRoute.Scan.route }
    val context = LocalContext.current
    val activity = LocalActivity.current
    val showTip = resultText.isEmpty() || resultText.isBlank()
    val scope = rememberCoroutineScope()
    val pickMultipleMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imageUri ->
            scope.launch {
                parseQRCode(imageUri)?.let {
                    resultText = it
                } ?: showToast("未识别到二维码")
            }
        }
    }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            bottomBar = {
                val auth by produceState<String?>(initialValue = null) {
                    value = getWxAuth()
                }
                ShareTwoContainer2D(
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP).navigationBarsPadding(),
                    !showTip,
                    defaultContent = {
                        StyleCardListItem(
                            headlineContent = { Text("使用提示") },
                            supportingContent = {
                                Text("打开CAS统一认证登录页面，选择右上角的扫码登录；通过扫码登录的平台，关闭后登录就会失效")
                            },
                            color = color.copy(.8f),
                        )
                    },
                    secondContent = {
                        val isCas = resultText.contains("cas/app/scanQrCodeLogin")
                        MyCustomCard(
                            containerColor = if(!isCas) MaterialTheme.colorScheme.primaryContainer.copy(.8f) else MaterialTheme.colorScheme.errorContainer.copy(.8f),
                            modifier = Modifier
                                .clickable {
                                    if (!isCas) {
                                        ClipBoardUtils.copy(resultText)
                                    } else {
                                        // 跳转登录
                                        if (auth == null) {
                                            showToast("未登录或初始化指尖工大")
                                            return@clickable
                                        }
                                        scope.launch {
                                            vm.wxLoginCasResponse.clear()
                                            vm.wxLoginCas(auth!!, resultText)
                                            val loginResult =
                                                (vm.wxLoginCasResponse.state.value as? UiState.Success)?.data
                                            if (loginResult == null) {
                                                showToast("登陆失败")
                                                resultText = ""
                                            } else if (loginResult.second == true) {
                                                // 解析uuid
                                                val uuid =
                                                    resultText.toUri().getQueryParameter("uuid")
                                                if (uuid == null) {
                                                    showToast("解析UUID失败")
                                                    resultText = ""
                                                } else {
                                                    vm.wxConfirmLogin(auth!!, uuid)
                                                    val confirmResult =
                                                        (vm.wxConfirmLoginResponse.state.value as? UiState.Success)?.data
                                                    if (confirmResult == null) {
                                                        showToast("登陆中途失败")
                                                        resultText = ""
                                                    } else {
                                                        showToast(confirmResult)
                                                        navController.popBackStack()
                                                    }
                                                }
                                            } else {
                                                showToast(loginResult.first)
                                                resultText = ""
                                            }
                                        }
                                    }
                                }
                        ) {
                            Column {
                                TransplantListItem(
                                    headlineContent = { Text("识别结果") },
                                    supportingContent = { Text(if (!isCas) resultText else "点击进行CAS统一认证登录") },
                                    leadingContent = {
                                        Icon(painterResource(
                                            if(isCas) {
                                                R.drawable.login
                                            } else if(isValidWebUrl(resultText)) {
                                                R.drawable.net
                                            } else {
                                                R.drawable.qr_code_2
                                            }
                                        ),null)
                                    },
                                    trailingContent = if (isCas) {
                                        {
                                            FilledTonalIconButton(
                                                onClick = { resultText = "" },
                                                colors = IconButtonDefaults. filledTonalIconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                                )
                                            ) {
                                                Icon(painterResource(R.drawable.close), null)
                                            }
                                        }
                                    } else null
                                )
                                if (!isCas) {
                                    PaddingHorizontalDivider()
                                    Row(modifier = Modifier.align(Alignment.End)) {
                                        Text(
                                            text = "复制",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .align(Alignment.Bottom)
                                                .padding(
                                                    horizontal = APP_HORIZONTAL_DP,
                                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                                )
                                                .clickable {
                                                    ClipBoardUtils.copy(resultText)
                                                }
                                        )
                                        if (isValidWebUrl(resultText)) {
                                            Text(
                                                text = "打开链接",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 14.sp,
                                                modifier = Modifier
                                                    .align(Alignment.Bottom)
                                                    .padding(
                                                        horizontal = APP_HORIZONTAL_DP,
                                                        vertical = APP_HORIZONTAL_DP - 5.dp
                                                    )
                                                    .clickable {
                                                        Starter.startWebView(resultText)
                                                    }
                                            )
                                        }
                                        Text(
                                            text = "隐藏",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .align(Alignment.Top)
                                                .padding(
                                                    horizontal = APP_HORIZONTAL_DP,
                                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                                )
                                                .clickable {
                                                    resultText = ""
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            },
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(color.copy(.8f)),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Scan.label) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Scan.icon)
                    },
                    actions = {
                        Row {
                            IconButton(
                                onClick = {
                                    pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            ) {
                                Icon(painterResource(R.drawable.image),null, tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = { resultText = "" }
                            ) {
                                Icon(painterResource(R.drawable.rotate_right),null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->

            LaunchedEffect(activity) {
                activity?.let { PermissionSet.checkAndRequestCameraPermission(it) }
            }
            val imageAnalysis = ImageAnalysis
                .Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeAnalyzer { result ->
                    result?.let {
                        val resultT = result.text
                        // 处理扫描结果
                        if(resultT.isNotEmpty() || resultT.isNotBlank()) {
                            try {
                                resultText = resultT
                            } catch (_:Exception) {
                                showToast("解析出错")
                            }
                        }
                    }
                    })
                }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CameraScan(imageAnalysis, modifier = Modifier.fillMaxSize())
            }
        }
    }
}