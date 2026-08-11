package com.hfut.schedule.ui.screen.home.cube.screen


import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.SettingsAboutDeveloperDestination
import com.hfut.schedule.ui.nav.destination.SettingsDeveloperDestination
import com.hfut.schedule.ui.nav.destination.SettingsTipsDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.hfut.schedule.ui.screen.fix.about.Egg
import com.hfut.schedule.ui.screen.fix.fix.BugShare
import com.hfut.schedule.ui.screen.home.cube.GithubDownloadUI
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.launch
import java.util.Hashtable

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutSettingsScreen(innerPadding : PaddingValues,) {
//    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
//    var scale by remember { mutableFloatStateOf(1f) }
    val context = LocalContext.current
//    TransitionBackHandler(navController,enablePredictive) {
//        scale = it
//    }
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)
    ){
        Spacer(modifier = Modifier.height(5.dp))

        var showBottomSheet by remember { mutableStateOf(false) }
        if (showBottomSheet) {
            HazeBottomSheet (
                onDismissRequest = { showBottomSheet = false },
                showBottomSheet = showBottomSheet,
//                isFullScreen = false
            ) {
                Column {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
                        val qrPainter = rememberCreateQrCode(Constant.GITEE_UPDATE_URL + "releases/tag/Android")
                        qrPainter?.let { Image(it.asImageBitmap(), contentDescription = "") }
                    }
                    Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
                }
            }
        }

        var showBottomSheet_icon by remember { mutableStateOf(false) }
        if (showBottomSheet_icon) {
            HazeBottomSheet(
                onDismissRequest = { showBottomSheet_icon = false },
                showBottomSheet = showBottomSheet_icon,
            ) {
                Egg()
            }
        }


        val scope = rememberCoroutineScope()
        val navController = LocalNavController.current
        val video by produceState<String?>(initialValue = null) {
            scope.launch {
                navController.awaitTransition()
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

        val navTopController = LocalNavController.current

        DividerTextExpandedWith(stringResource(R.string.about_settings_about_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                SharedContainer(
                    key = VersionInfoDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = VersionInfoDestination.title.asString()) },
                        supportingContent = { Text(text = stringResource(R.string.about_settings_version_info_description))},
                        modifier = Modifier.clickable {
                            navTopController.push(VersionInfoDestination)
                        },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "")}
                    )
                }

                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsAboutDeveloperDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.about_settings_about_title)) },
                        supportingContent = { Text(text = stringResource(R.string.about_settings_about_description))},
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                navTopController.push(SettingsAboutDeveloperDestination)
                            },
                            onLongClick = {
                                //长按彩蛋
                                showBottomSheet_icon = true
                            }),
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                    )
                }

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
                SharedContainer(
                    key = SettingsTipsDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.about_settings_tips_title)) },
                        supportingContent = { Text(text = stringResource(
                            R.string.about_settings_tips_description,
                            MyApplication.APP_NAME
                        ))},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.lightbulb),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsTipsDestination)
                        }
                    )
                }
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
                            ClipBoardHelper.copy(Constant.GITEE_UPDATE_URL + "releases/tag/Android",
                                context.getString(
                                    R.string.about_settings_toast_promote
                                ))
                        }
                    )
                )
                /*
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsAvailableDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.about_settings_different_supported_title)) },
                        supportingContent = { Text(text = stringResource(R.string.about_settings_different_supported_description))},
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsAvailableDestination)
                        },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "")}
                    )
                }
                 */
            }
        }
        DividerTextExpandedWith(stringResource(R.string.about_settings_fix_half_title)) {
            CustomCard(
                color = MaterialTheme.colorScheme.surface
            ) {
                TransplantListItem(
                    headlineContent = { Text(text = "下载最新版本") },
                    supportingContent = {
                        Text("从Gitee下载最新版本的apk安装包")
                    },
                    leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ Starter.startWebUrlOuter(context,Constant.GITEE_UPDATE_URL + "releases/tag/Android") }
                )
                PaddingHorizontalDivider()
                GithubDownloadUI()
                PaddingHorizontalDivider()
                BugShare()
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsDeveloperDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.about_settings_developer_title)) },
                        supportingContent = { Text(text = stringResource(R.string.about_settings_developer_description))},
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsDeveloperDestination)
                        },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "")}
                    )
                }
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}


/**
 * TODO 通用组件 展示二维码和URL，URL支持打开、分享
 */

@Composable
fun rememberCreateQrCode(
    content: String,
    contentColor : Color = MaterialTheme.colorScheme.primary,
    backgroundColor : Color = Color.Transparent,
    size: Int = 1000,
) : Bitmap? = remember(content,contentColor,backgroundColor,size) {
    createQrCode(content,contentColor,backgroundColor,size)
}

fun createQrCode(
    content: String,
    contentColor : Color = Color.Black,
    backgroundColor : Color = Color.Transparent,
    size: Int = 1000,
): Bitmap? {
    if (size < 0) {
        return null
    }
    try {
        val hints: Hashtable<EncodeHintType, String> = Hashtable()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = "H"
        hints[EncodeHintType.MARGIN] = "1"
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (bitMatrix[x, y]) {
                    pixels[y * size + x] = contentColor.toArgb()
                } else {
                    pixels[y * size + x] = backgroundColor.toArgb()
                }
            }
        }

        val bitmap = createBitmap(size, size)
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        return bitmap
    } catch (e: Exception) {
        LogUtil.error(e)
        return null
    }
}

val QR_CODE_PADDING = CARD_NORMAL_DP*4