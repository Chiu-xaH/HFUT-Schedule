package com.hfut.schedule.ui.activity.fix.about

//import com.hfut.schedule.ui.utils.componentsAbout

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
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.ShareAPK
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.activity.home.cube.items.main.Screen
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.VersionInfo
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.getUpdates
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.viewmodel.LoginViewModel
import java.util.Hashtable

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutUI(innerPadding : PaddingValues, vm : LoginViewModel,cubeShow : Boolean,navController : NavController) {
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)){
        Spacer(modifier = Modifier.height(5.dp))

        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                //shape = Round(sheetState)
            ) {
                Column {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppHorizontalDp(), vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
                        val qrPainter = createQRCodeBitmap(MyApplication.UpdateURL + "releases/tag/Android",1000,1000)
                        qrPainter?.let { Image(it.asImageBitmap(), contentDescription = "") }
                    }
                    Spacer(modifier = Modifier.height(AppHorizontalDp()))
                }
            }
        }
        val sheetState_version = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showBottomSheet_version by remember { mutableStateOf(false) }
        if (showBottomSheet_version) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_version = false },
                sheetState = sheetState_version,
                shape = Round(sheetState_version)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    topBar = {
                        CustomTopBar("本版本新特性")
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

        val sheetState_info = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showBottomSheet_info by remember { mutableStateOf(false) }
        if (showBottomSheet_info) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_info = false },
                sheetState = sheetState_info,
                shape = Round(sheetState_info)
            ) {
                About(vm)
            }
        }

//
//        ListItem(
//            headlineContent = { Text(text = "Github开源") },
//            supportingContent = { Text(text = "如您想成为下个版本的构建者,来这里提交你的代码吧")},
//            leadingContent = {
//                Icon(
//                    painterResource(R.drawable.github),
//                    contentDescription = "Localized description",
//                )
//            },
//            modifier = Modifier.clickable{
//                //when(select) {
//                //   false -> StartUri("https://gitee.com/chiu-xah/HFUT-Schedule")
//                //true ->
//                Starter.startWebUrl("https://github.com/Chiu-xaH/HFUT-Schedule")
//                //  }
//            }
//        )

        TransplantListItem(
            headlineContent = { Text(text = "推广本应用") },
            supportingContent = { Text(text = "如果你觉得好用的话,可以替开发者多多推广\n长按分享APK安装包,点击展示下载链接,双击复制链接")},
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
                onLongClick = { ShareAPK.shareAPK() },
                onDoubleClick = {
                    ClipBoard.copy(MyApplication.UpdateURL + "releases/tag/Android","已将下载链接复制到剪切板")
                }

            )
        )


        var version by remember { mutableStateOf(getUpdates()) }
        var showBadge by remember { mutableStateOf(false) }
        if (version.version != VersionUtils.getVersionName()) showBadge = true

        TransplantListItem(
            headlineContent = { Text(text = "本版本新特性") },
            supportingContent = { Text(text = "查看此版本的更新内容")},
            modifier = Modifier.clickable { showBottomSheet_version = true },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "")}
        )

        TransplantListItem(
            headlineContent = { Text(text = "关于") },
            supportingContent = { Text(text = "开源 构建 开发者")},
            modifier = Modifier.clickable { showBottomSheet_info = true },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
        )


        if(cubeShow) {

            TransplantListItem(
                headlineContent = { Text(text = "疑难解答 修复") },
                supportingContent = { Text(text = "当出现问题时,可从此处进入或长按桌面图标选择修复")},
                leadingContent = { Icon(painterResource(R.drawable.build), contentDescription = "Localized description",) },
                modifier = Modifier.clickable{ navController.navigate(Screen.FIxScreen.route) }
            )

            ///////////////////////////////

            if(VersionUtils.getVersionName().contains("Preview"))
                TransplantListItem(
                    headlineContent = { Text(text = "测试 调试") },
                    supportingContent = { Text(text = "用户禁入!")},
                    leadingContent = { Icon(painterResource(R.drawable.error), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ navController.navigate(Screen.DebugScreen.route) }
                )
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
