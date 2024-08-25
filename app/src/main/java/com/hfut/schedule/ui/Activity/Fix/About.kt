package com.hfut.schedule.ui.Activity.Fix

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.annotation.ColorInt
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.ShareAPK
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.Screen
import com.hfut.schedule.ui.Activity.success.cube.Settings.VersionInfo
import com.hfut.schedule.ui.Activity.success.cube.Settings.VersionInfoCard
import com.hfut.schedule.ui.Activity.success.cube.Settings.getUpdates
import com.hfut.schedule.ui.UIUtils.DevelopingUI
import com.hfut.schedule.ui.UIUtils.MyToast
import java.util.Hashtable

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutUI(innerPadding : PaddingValues, vm : LoginViewModel,cubeShow : Boolean,navController : NavController) {
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)){
        Spacer(modifier = Modifier.height(5.dp))

        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
                        val qrPainter = createQRCodeBitmap(MyApplication.UpdateURL + "releases/tag/Android",1000,1000)
                        qrPainter?.let { Image(it.asImageBitmap(), contentDescription = "") }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }

            }
        }
        val sheetState_version = rememberModalBottomSheetState()
        var showBottomSheet_version by remember { mutableStateOf(false) }
        if (showBottomSheet_version) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_version = false },
                sheetState = sheetState_version
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("本版本新特性") },
                        )
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
        if(!cubeShow) {
            Spacer(modifier = Modifier.height(5.dp))
            VersionInfoCard()
            Spacer(modifier = Modifier.height(18.dp))
        }

        ListItem(
            headlineContent = { Text(text = "开源主页") },
            supportingContent = { Text(text = "欢迎来开源主页参观一下")},
            leadingContent = {
                Icon(
                    painterResource(R.drawable.net),
                    contentDescription = "Localized description",
                )
            },
            modifier = Modifier.clickable{
                //when(select) {
                //   false -> StartUri("https://gitee.com/chiu-xah/HFUT-Schedule")
                //true ->
                StartApp.StartUri("https://github.com/Chiu-xaH/HFUT-Schedule")
                //  }
            }
        )
        ListItem(
            headlineContent = { Text(text = "联系开发者") },
            leadingContent = {
                Icon(
                    painterResource(R.drawable.mail),
                    contentDescription = "Localized description",
                )
            },
            modifier = Modifier.clickable{
                val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MyApplication.context.startActivity(it)
            }
        )


        ListItem(
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
                onLongClick = { ShareAPK.ShareAPK() },
                onDoubleClick = {
                    ClipBoard.copy(MyApplication.UpdateURL + "releases/tag/Android")
                    MyToast("已将下载链接复制到剪切板")
                }

            )
        )


        var version by remember { mutableStateOf(getUpdates()) }
        var showBadge by remember { mutableStateOf(false) }
        if (version.version != APPVersion.getVersionName()) showBadge = true

        ListItem(
            headlineContent = { Text(text = "获取更新") },
            supportingContent = { Text(text = if(version.version == APPVersion.getVersionName()) "当前为最新版本 ${APPVersion.getVersionName()}" else "当前版本  ${APPVersion.getVersionName()}\n最新版本  ${version.version}") },
            leadingContent = {
                BadgedBox(badge = {
                    if(showBadge)
                        Badge(modifier = Modifier.size(7.dp)) }) {
                    Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
                }
            },
            modifier = Modifier.clickable{
                if (version.version != APPVersion.getVersionName())
                    StartApp.StartUri(MyApplication.UpdateURL + "releases/download/Android/${version.version}.apk")
                else Toast.makeText(MyApplication.context,"与云端版本一致", Toast.LENGTH_SHORT).show()
            }
        )

        ListItem(
            headlineContent = { Text(text = "本版本新特性") },
            supportingContent = { Text(text = "查看此版本的更新内容")},
            modifier = Modifier.clickable { showBottomSheet_version = true },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "")}
        )

        ListItem(
            headlineContent = { Text(text = "版本日志") },
            supportingContent = { Text("查看历代版本的更新内容") },
            leadingContent = {
                    Icon(painterResource(R.drawable.crossword), contentDescription = "Localized description",)
            },
            modifier = Modifier.clickable{
                StartApp.StartUri("https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/UPDATE.md")
            }
        )

        if(cubeShow) {

            ListItem(
                headlineContent = { Text(text = "疑难解答 修复") },
                supportingContent = { Text(text = "当出现问题时,可从此处进入或长按桌面图标选择修复")},
                leadingContent = { Icon(painterResource(R.drawable.build), contentDescription = "Localized description",) },
                modifier = Modifier.clickable{ navController.navigate(Screen.FIxScreen.route) }
            )

            ///////////////////////////////

            if(APPVersion.getVersionName().contains("Preview"))
                ListItem(
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
