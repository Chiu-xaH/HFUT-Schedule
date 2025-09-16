package com.hfut.schedule.ui.screen.home.cube.sub

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.AppDownloadManager
import com.hfut.schedule.logic.util.sys.AppDownloadManager.getDownloadProgress
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.ocr.TesseractUtils
import com.hfut.schedule.logic.util.ocr.TesseractUtils.isModelInDownloadFolder
import com.hfut.schedule.logic.util.ocr.TesseractUtils.moveDownloadedModel
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.status.CustomSwitch
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.transition.util.TransitionPredictiveBackHandler

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DownloadMLUI(innerPadding : PaddingValues,navController : NavHostController?) {
    var scale by remember { mutableFloatStateOf(1f) }
    navController?.let {
        TransitionPredictiveBackHandler(it,true) {
            scale = it
        }
    }
    val activity = LocalActivity.current
    activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }

    val switch_open = prefs.getBoolean("SWITCH_ML",false)
    var open by remember { mutableStateOf(switch_open) }
    saveBoolean("SWITCH_ML",false,open)

    var isExistModule by remember { mutableStateOf(TesseractUtils.isExistModule()) }

    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableFloatStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = AppDownloadManager.getDownloadId(AppDownloadManager.DownloadIds.ML)
            val progress = getDownloadProgress(id)
            // 更新 UI，例如进度条
            pro = progress / 100f
            if (progress < 100) {
                handler.postDelayed(this, 1000) // 每秒查询一次
            }
        }
    }
    handler.post(runnable)

    // 下载完成后触发
    LaunchedEffect(pro) {
        if(pro == 1f) {
            moveDownloadedModel()
            isExistModule = TesseractUtils.isExistModule()
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        CustomCard(color = MaterialTheme.colorScheme.surface) {
            TransplantListItem(
                headlineContent = { Text("图片验证码自动填充") },
                trailingContent = {
                    Switch(checked = open, onCheckedChange = { openCh -> open = openCh }, enabled = isExistModule )
                }
            )
        }

        DividerTextExpandedWith("模型") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { ScrollText("English OCR By Tesseract") },
                    supportingContent = { Text("约21MB" + if(isExistModule) " 长按删除" else "") },
                    trailingContent = {
                        if(!isExistModule) {
                            Row {
                                if(!able) {
                                    Text(text =  if(pro == 1f) "完成" else "${(pro*100).toInt()} %")
                                } else {
                                    FilledTonalIconButton(
                                        onClick = {
                                            able = false
                                            AppDownloadManager.downloadMl()
                                        }
                                    ) {
                                        Icon(painterResource(R.drawable.download),null)
                                    }
                                    FilledTonalButton(
                                        onClick = {
                                            if(isModelInDownloadFolder()) {
                                                moveDownloadedModel()
                                                isExistModule = TesseractUtils.isExistModule()
                                            } else {
                                                showToast("请将eng.traineddata放置在存储/Download/,然后点击此按钮")
                                            }
                                        }
                                    ) {
                                        Text("本地安装")
                                    }
                                }
                            }

                        } else {
                            Icon(Icons.Filled.Check, null)
                        }
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            if(isExistModule) {
                                showToast("长按删除")
                            }
                        },
                        onLongClick = {
                            if(isExistModule) {
                                val res = TesseractUtils.deleteModel()
                                if(res) {
                                    showToast("删除成功")
                                    isExistModule = TesseractUtils.isExistModule()
                                } else {
                                    showToast("删除失败")
                                }
                            }
                        }
                    )
                )
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}