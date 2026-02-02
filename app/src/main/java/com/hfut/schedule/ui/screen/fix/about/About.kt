package com.hfut.schedule.ui.screen.fix.about


import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.screen.home.cube.Screen
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo

import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.GithubDownloadUI
import com.hfut.schedule.ui.screen.home.cube.UpdateContents
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.util.TransitionBackHandler
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Hashtable
import androidx.core.graphics.createBitmap

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutUI(innerPadding : PaddingValues, vm : NetWorkViewModel, cubeShow : Boolean, navController : NavHostController, hazeState: HazeState) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    val context = LocalContext.current
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)
        .scale(scale)){
        Spacer(modifier = Modifier.height(5.dp))

        var showBottomSheet by remember { mutableStateOf(false) }
        if (showBottomSheet) {
            HazeBottomSheet (
                onDismissRequest = { showBottomSheet = false },
                showBottomSheet = showBottomSheet,
                hazeState = hazeState,
                autoShape = false
            ) {
                Column {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
                        val qrPainter = createQRCodeBitmap(MyApplication.GITEE_UPDATE_URL + "releases/tag/Android",1000,1000)
                        qrPainter?.let { Image(it.asImageBitmap(), contentDescription = "") }
                    }
                    Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
                }
            }
        }
        var showBottomSheetUpdate by remember { mutableStateOf(false) }

        if(showBottomSheetUpdate) {
            HazeBottomSheet(
                onDismissRequest = { showBottomSheetUpdate = false },
                showBottomSheet = showBottomSheetUpdate,
                hazeState = hazeState
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    topBar = {
                        HazeBottomSheetTopBar(stringResource(R.string.about_settings_history_update_log_title))
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        UpdateContents(vm)
                    }
                }
            }
        }
        var showBottomSheet_version by remember { mutableStateOf(false) }
        if (showBottomSheet_version) {
            HazeBottomSheet (
                onDismissRequest = { showBottomSheet_version = false },
                hazeState = hazeState,
                showBottomSheet = showBottomSheet_version
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    topBar = {
                        HazeBottomSheetTopBar(stringResource(AppNavRoute.VersionInfo.label)) {
                            FilledTonalButton(onClick = { showBottomSheetUpdate = true }) {
                                Text(stringResource(R.string.about_settings_history_update_log_title))
                            }
                        }
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize()
                    ) {
                        VersionInfo()
                    }
                }
            }
        }

        var showBottomSheet_info by remember { mutableStateOf(false) }
        if (showBottomSheet_info) {
            HazeBottomSheet(
                onDismissRequest = { showBottomSheet_info = false },
                hazeState = hazeState,
                showBottomSheet = showBottomSheet_info,
            ) {
                About(vm)
            }
        }

        var showBottomSheet_icon by remember { mutableStateOf(false) }
        if (showBottomSheet_icon) {
            HazeBottomSheet(
                onDismissRequest = { showBottomSheet_icon = false },
                hazeState = hazeState,
                showBottomSheet = showBottomSheet_icon,
            ) {
                Egg()
            }
        }

        var showBottomSheet_support by remember { mutableStateOf(false) }
        if (showBottomSheet_support) {
            HazeBottomSheet(
                onDismissRequest = { showBottomSheet_support = false },
                hazeState = hazeState,
                showBottomSheet = showBottomSheet_support,
            ) {
                Scaffold(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    topBar = {
                        HazeBottomSheetTopBar(stringResource(R.string.about_settings_different_supported_title))
                    },
                ) { innerPadding ->
                    Support(innerPadding)
                }
            }
        }
        val scope = rememberCoroutineScope()
        val video by produceState<String?>(initialValue = null) {
            scope.launch {
                delay(AppAnimationManager.ANIMATION_SPEED*1L)
                value = checkOrDownloadVideo(context,"example_about.mp4","https://chiu-xah.github.io/videos/example_about.mp4")
            }
        }
        CustomCard (
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            video?.let {
                SimpleVideo(
                    filePath = it,
                    aspectRatio = 16/9f,
                )
            }
        }

        DividerTextExpandedWith(stringResource(R.string.about_settings_about_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(AppNavRoute.VersionInfo.label)) },
                    supportingContent = { Text(text = stringResource(R.string.about_settings_version_info_description))},
                    modifier = Modifier.clickable { showBottomSheet_version = true },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "")}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.about_settings_about_title)) },
                    supportingContent = { Text(text = stringResource(R.string.about_settings_about_description))},
                    modifier = Modifier.combinedClickable(
                        onClick = { showBottomSheet_info = true},
                        onLongClick = {
                            //长按操作
                            showBottomSheet_icon = true
                        }),
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.about_settings_feedback_title)) },
                    supportingContent = { Text(text = stringResource(R.string.about_settings_feedback_description))},
                    leadingContent = {
                        Icon(painterResource(R.drawable.alternate_email), contentDescription = "Localized description")
                    },
                    modifier = Modifier.clickable {
                        Starter.emailMe(context)
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.about_settings_tips_title)) },
                    supportingContent = { Text(text = stringResource(
                        R.string.about_settings_tips_description,
                        stringResource(R.string.app_name)
                    ))},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.lightbulb),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        showDevelopingToast()
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.about_settings_promote_title)) },
                    supportingContent = { Text(text = stringResource(R.string.about_settings_promote_description))},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.ios_share),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            showBottomSheet = true
                        },
                        onLongClick = { ShareTo.shareAPK() },
                        onDoubleClick = {
                            ClipBoardHelper.copy(MyApplication.GITEE_UPDATE_URL + "releases/tag/Android",
                                context.getString(
                                    R.string.about_settings_toast_promote
                                ))
                        }
                    )
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.about_settings_different_supported_title)) },
                    supportingContent = { Text(text = stringResource(R.string.about_settings_different_supported_description))},
                    modifier = Modifier.clickable { showBottomSheet_support = true },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "")}
                )
            }
        }

        if(cubeShow) {
            DividerTextExpandedWith(stringResource(R.string.about_settings_fix_half_title)) {
                CustomCard(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    GithubDownloadUI()
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text(text = stringResource(R.string.about_setting_fix_title)) },
                        supportingContent = { Text(text = stringResource(R.string.about_setting_fix_description))},
                        leadingContent = { Icon(
                            painterResource(R.drawable.build),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable{ navController.navigate(Screen.FIxScreen.route) },
                    )
                }
            }
            DividerTextExpandedWith(stringResource(R.string.about_settings_develop_half_title)) {
                CustomCard(color = MaterialTheme.colorScheme.surface) {
                    TransplantListItem(
                        headlineContent = { Text(text = stringResource(R.string.about_settings_developer_title)) },
                        supportingContent = { Text(text = stringResource(R.string.about_settings_developer_description))},
                        modifier = Modifier.clickable { navController.navigate(Screen.DeveloperScreen.route) },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "")}
                    )
                    if(AppVersion.isPreview()) {
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = { Text(text = stringResource(R.string.about_settings_test_title)) },
                            leadingContent = { Icon(
                                painterResource(R.drawable.error),
                                contentDescription = "Localized description"
                            ) },
                            modifier = Modifier.clickable{ navController.navigate(Screen.DebugScreen.route) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun createQRCodeBitmap(
    content: String,
    width: Int,
    height: Int,
): Bitmap? {
    if (width < 0 || height < 0) {
        return null
    }
  //  try {
        val hints: Hashtable<EncodeHintType, String> = Hashtable()
//        if (character_set.isNotEmpty()) {
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
//        }
//        if (error_correction.isNotEmpty()) {
            hints[EncodeHintType.ERROR_CORRECTION] = "H"
//        }
//        if (margin.isNotEmpty()) {
            hints[EncodeHintType.MARGIN] = "1"
//        }
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

        val colorPrimary = MaterialTheme.colorScheme.primary.toArgb()
        val colorBackground = Color.TRANSPARENT

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitMatrix[x, y]) {
                    pixels[y * width + x] = colorPrimary
                } else {
                    pixels[y * width + x] = colorBackground
                }
            }
        }

        val bitmap = createBitmap(width, height)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
   // } catch (e: WriterException) {
  //      LogUtil.error(e)
  //  }
   // return null
}
