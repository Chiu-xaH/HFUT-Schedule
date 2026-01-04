package com.hfut.schedule.ui.screen.home.search.function.school.scan

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.parseQRCode
import com.hfut.schedule.logic.util.parse.isWifiContent
import com.hfut.schedule.logic.util.parse.parseWifiQrCode
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.logic.util.shortcut.AppShortcutManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.LiquidTopBarNavigateIcon
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.camera.ScanQrCode
import com.hfut.schedule.ui.component.camera.ScanQrCodeView
import com.hfut.schedule.ui.component.camera.ScanQrCodeView2
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getWxAuth
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
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
    val backdrop = rememberLayerBackdrop()

    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        bottomBar = {
            val auth by produceState<String?>(initialValue = null) {
                value = getWxAuth()
            }
            ShareTwoContainer2D(
                modifier = Modifier
                    .padding(bottom = APP_HORIZONTAL_DP)
                    .navigationBarsPadding()
                ,
                !showTip,
                defaultContent = {
                    CardListItem(
                        headlineContent = { Text("使用提示") },
                        supportingContent = {
                            Text("打开CAS统一认证登录页面，选择右上角的扫码登录(还可扫网页或Wifi码,可在系统控制中心添加磁贴)")
                        },
                        color = color.copy(.7f),
                    )
                },
                secondContent = {
                    val isCas = resultText.contains("cas/app/scanQrCodeLogin")
                    CustomCard(
                        color = if(!isCas) MaterialTheme.colorScheme.primaryContainer.copy(.7f) else MaterialTheme.colorScheme.errorContainer.copy(.7f),
                        modifier = Modifier
                            .clickable {
                                if (!isCas) {
                                    ClipBoardHelper.copy(resultText)
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
                                supportingContent = { Text(
                                    if(isCas) {
                                        "点击进行CAS统一认证登录"
                                    } else if(isValidWebUrl(resultText)) {
                                        resultText
                                    } else if(isWifiContent(resultText)) {
                                        parseWifiQrCode(resultText).toString()
                                    } else {
                                        resultText
                                    }
                                ) },
                                leadingContent = {
                                    Icon(painterResource(
                                        if(isCas) {
                                            R.drawable.login
                                        } else if(isValidWebUrl(resultText)) {
                                            R.drawable.net
                                        } else if(isWifiContent(resultText)) {
                                            if(parseWifiQrCode(resultText)?.password == null) {
                                                R.drawable.wifi
                                            } else{
                                                R.drawable.wifi_password
                                            }
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
                            if(isCas) {

                            } else if(isValidWebUrl(resultText)) {
                                CardBottomButtons(
                                    listOf(
                                        CardBottomButton("复制") {
                                            ClipBoardHelper.copy(resultText)
                                        },
                                        CardBottomButton("打开链接",isValidWebUrl(resultText)) {
                                            scope.launch {
                                                Starter.startWebView(context,resultText)
                                            }
                                        },
                                        CardBottomButton("隐藏") {
                                            resultText = ""
                                        },
                                    )
                                )
                            } else if(isWifiContent(resultText)) {
                                CardBottomButtons(
                                    listOf(
                                        CardBottomButton("连接") {
                                            parseWifiQrCode(resultText)?.password?.let { ClipBoardHelper.copy(it) }
                                            Starter.startWlanSettings(context)
                                        },
                                        CardBottomButton("隐藏") {
                                            resultText = ""
                                        },
                                    )
                                )
                            } else {
                                CardBottomButtons(
                                    listOf(
                                        CardBottomButton("复制") {
                                            ClipBoardHelper.copy(resultText)
                                        },
                                        CardBottomButton("隐藏") {
                                            resultText = ""
                                        },
                                    )
                                )
                            }
                        }
                    }
                }
            )
        },
        topBar = {
            TopAppBar(
                colors = topBarTransplantColor(),
                title = { Text("") },
                navigationIcon = {
                    LiquidTopBarNavigateIcon(backdrop,navController,route, AppNavRoute.Scan.icon,)
                },
                actions = {
                    Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
//                        LiquidButton(
//                            onClick = {
//                                showToast("正在开发")
//                            },
//                            backdrop = backdrop,
//                            isCircle = true
//                        ) {
//                            Icon(painterResource(R.drawable.qr_code_2),null)
//                        }
//                        Spacer(Modifier.width(BUTTON_PADDING))
                        LiquidButton(
                            onClick = {
                                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            backdrop = backdrop,
                            isCircle = true
                        ) {
                            Icon(painterResource(R.drawable.image),null)
                        }
                    }
                }
            )
        },
    ) { innerPadding ->

        LaunchedEffect(activity) {
            AppShortcutManager.createScanShortcut(context)
            activity?.let { PermissionSet.checkAndRequestCameraPermission(it) }
        }
        Box(
            modifier = Modifier
                .backDropSource(backdrop)
                .fillMaxSize()
        ) {
            ScanQrCode(modifier = Modifier.fillMaxSize()) { result ->
                val text = result.text
                // 处理扫描结果
                if(text.isNotEmpty() || text.isNotBlank()) {
                    try {
                        resultText = text
                    } catch (e:Exception) {
                        e.printStackTrace()
                        showToast("解析出错")
                    }
                }
            }
        }
    }
}