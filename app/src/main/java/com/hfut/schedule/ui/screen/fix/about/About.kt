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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.screen.home.cube.Screen
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
 
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.focus.funiction.openOperation
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import java.util.Hashtable

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutUI(innerPadding : PaddingValues, vm : NetWorkViewModel, cubeShow : Boolean, navController : NavController, hazeState: HazeState) {
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)){
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
                        HazeBottomSheetTopBar("本版本新特性")
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
                        HazeBottomSheetTopBar("功能可用性支持")
                    },
                ) { innerPadding ->
                    Support(innerPadding)
                }
            }
        }

        DividerTextExpandedWith("关于") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "本版本新特性") },
                    supportingContent = { Text(text = "查看此版本的更新内容")},
                    modifier = Modifier.clickable { showBottomSheet_version = true },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "")}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "关于") },
                    supportingContent = { Text(text = "开源 构建 开发者")},
                    modifier = Modifier.combinedClickable(
                        onClick = { showBottomSheet_info = true},
                        onLongClick = {
                            //长按操作
                            showBottomSheet_icon = true
                        }),
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                )
            }
        }
       DividerTextExpandedWith("支持") {
           MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
               TransplantListItem(
                   headlineContent = { Text(text = "推广应用") },
                   supportingContent = { Text(text = "长按分享APK安装包,点击展示下载链接二维码,双击复制链接")},
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
                           ClipBoardUtils.copy(MyApplication.GITEE_UPDATE_URL + "releases/tag/Android","已将下载链接复制到剪切板")
                       }
                   )
               )
               PaddingHorizontalDivider()
               TransplantListItem(
                   headlineContent = { Text(text = "功能可用性支持") },
                   supportingContent = { Text(text = "根据Android版本与国内不同厂商定制UI的不同，APP会有若干功能不被支持")},
                   modifier = Modifier.clickable { showBottomSheet_support = true },
                   leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "")}
               )
           }
       }

        if(cubeShow) {
            DividerTextExpandedWith("Develop") {
                MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                    TransplantListItem(
                        headlineContent = { Text(text = "开发者选项") },
                        supportingContent = { Text(text = "一些可用于有经验用户的选项")},
                        modifier = Modifier.clickable { navController.navigate(Screen.DeveloperScreen.route) },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "")}
                    )
                    if(AppVersion.isPreview()) {
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = { Text(text = "测试 调试") },
                            supportingContent = { Text(text = "用户禁入!")},
                            leadingContent = { Icon(painterResource(R.drawable.error), contentDescription = "Localized description",) },
                            modifier = Modifier.clickable{ navController.navigate(Screen.DebugScreen.route) }
                        )
                    }
                }
            }
            DividerTextExpandedWith("修复") {
                StyleCardListItem(
                    headlineContent = { Text(text = "疑难解答与修复") },
                    supportingContent = { Text(text = "当出现问题时,可从此处进入或长按桌面图标选择修复")},
                    leadingContent = { Icon(painterResource(R.drawable.build), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ navController.navigate(Screen.FIxScreen.route) },
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}


@Composable
fun createQRCodeBitmap(
    content: String,
    width: Int,
    height: Int,
    character_set: String = "UTF-8",
    error_correction: String = "H",
    margin: String = "1"
): Bitmap? {
    if (width < 0 || height < 0) {
        return null
    }
  //  try {
        val hints: Hashtable<EncodeHintType, String> = Hashtable()
        if (character_set.isNotEmpty()) {
            hints[EncodeHintType.CHARACTER_SET] = character_set
        }
        if (error_correction.isNotEmpty()) {
            hints[EncodeHintType.ERROR_CORRECTION] = error_correction
        }
        if (margin.isNotEmpty()) {
            hints[EncodeHintType.MARGIN] = margin
        }
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

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
   // } catch (e: WriterException) {
  //      e.printStackTrace()
  //  }
   // return null
}
