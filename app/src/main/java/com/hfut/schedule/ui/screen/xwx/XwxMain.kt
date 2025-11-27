package com.hfut.schedule.ui.screen.xwx

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.XwxScreen
import com.hfut.schedule.logic.model.xwx.XwxLoginInfo
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.XwxViewModel
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XwxMainScreen(vm: XwxViewModel, navHostController: NavHostController) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by vm.functionsResp.state.collectAsState()
    val savedInfo by produceState<XwxLoginInfo?>(initialValue = null) {
        value = getXwxLogin(context)
    }

    val previewUiState by vm.docPreviewResp.state.collectAsState()

    val refreshNetwork = suspend m@ {
        if(savedInfo == null) {
            return@m
        }
        vm.functionsResp.clear()
        vm.getFunctions(savedInfo!!.data.schoolCode,savedInfo!!.data.userId,savedInfo!!.token)
    }

    LaunchedEffect(savedInfo) {
        vm.docPreviewResp.emitPrepare()
        if(uiState is UiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text("校务行") },
                    navigationIcon = {
                        IconButton(onClick = {
                            activity?.finish()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .verticalScroll(rememberScrollState())
        ) {
            InnerPaddingHeight(innerPadding,true)

            DividerTextExpandedWith("功能") {
                CommonNetworkScreen(uiState = uiState, isFullScreen = false, onReload = { }) {
                    val list = (uiState as UiState.Success).data
                    CustomCard(
                        color = cardNormalColor()
                    ) {
                        for(i in list.indices) {
                            val item = list[i]
                            TransplantListItem(
                                headlineContent = { Text(item.name) },
                                modifier = Modifier.clickable {
                                    scope.launch m@ {
                                        if(savedInfo == null) {
                                            return@m
                                        }
                                        val f = item.filePropertyType.toIntOrNull() ?: return@m
                                        vm.docPreviewResp.clear()
                                        vm.getDocPreview(
                                            savedInfo!!.data.schoolCode,
                                            savedInfo!!.data.userId,
                                            f,
                                            item.fileProperty,
                                            savedInfo!!.token
                                        )
                                    }
                                }
                            )
                            if(i != list.size-1) {
                                PaddingHorizontalDivider()
                            }
                        }
                    }
                }
            }
            CommonNetworkScreen(
                uiState = previewUiState,
                isFullScreen = false,
                onReload = {
                    showToast("请从上面重新点击")
                },
                prepareContent = {

                }
            ) {
                val bitmap = (previewUiState as UiState.Success).data
                DividerTextExpandedWith("预览") {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.White)
                    ) {
                        Image(
                            contentScale = ContentScale.Fit,
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "预览图片",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                    LargeButton(
                        onClick = {
                            scope.launch {
                                // 保存图片
                                activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
                                saveImageToFile(bitmap)
                            }
                        },
                        text = "保存",
                        icon = R.drawable.save,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)
                    )
                }
            }
            DividerTextExpandedWith("选项") {
                CustomCard(
                    color = cardNormalColor()
                ) {
                    TransplantListItem(
                        headlineContent = { Text("官网") },
                        supportingContent = {
                            Text("申请高清无水印证明请前往官网或微信小程序")
                        },
                        modifier = Modifier.clickable {
                            scope.launch {
                                Starter.startWebView(
                                    context, MyApplication.XWX_PICTURE_URL + "xwx-cms/","校务行"
                                )
                            }
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.net),null)
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("刷新登录状态") },
                        supportingContent = {
                            Text("刷新或重新登录校务行")
                        },
                        modifier = Modifier.clickable {
                            navHostController.navigate(XwxScreen.LOGIN.name)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.rotate_right),null)
                        }
                    )
                }
            }

            InnerPaddingHeight(innerPadding,false)
        }
    }
}

suspend fun saveImageToFile(
    bitmap: Bitmap
) {
    withContext(Dispatchers.IO) {
        try {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            // 确保目录存在
            if (!picturesDir.exists()) {
                picturesDir.mkdirs()
            }
            val file = File(picturesDir, "${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // 保存为 PNG 格式
            outputStream.flush()
            outputStream.close()
            showToast("保存完成至${file.absoluteFile}")
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("保存失败")
        }
    }
}

