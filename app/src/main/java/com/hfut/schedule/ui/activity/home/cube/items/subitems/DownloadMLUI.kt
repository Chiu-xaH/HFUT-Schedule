package com.hfut.schedule.ui.activity.home.cube.items.subitems

import android.app.Activity
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.MyDownloadManager
import com.hfut.schedule.logic.utils.MyDownloadManager.getDownloadProgress
import com.hfut.schedule.logic.utils.PermissionManager
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.SharePrefs.saveBoolean
import com.hfut.schedule.logic.utils.ocr.TesseractUtils
import com.hfut.schedule.logic.utils.ocr.TesseractUtils.isModelInDownloadFolder
import com.hfut.schedule.logic.utils.ocr.TesseractUtils.moveDownloadedModel
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.components.TransplantListItem

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DownloadMLUI(innerPadding : PaddingValues) {
    val activity = LocalActivity.current
    activity?.let { PermissionManager.checkAndRequestStoragePermission(it) }


    val switch_open = prefs.getBoolean("SWITCH_ML",false)
    var open by remember { mutableStateOf(switch_open) }
    saveBoolean("SWITCH_ML",false,open)

    var isExistModule by remember { mutableStateOf(TesseractUtils.isExistModule()) }

    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = MyDownloadManager.getDownloadId(MyDownloadManager.DownloadIds.ML)
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

    Column {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        TransplantListItem(
            headlineContent = { Text("图片验证码自动填充") },
            trailingContent = {
                Switch(checked = open, onCheckedChange = { openCh -> open = openCh }, enabled = isExistModule )
            }
        )
        DividerTextExpandedWith("模型") {
            TransplantListItem(
                headlineContent = { Text("English OCR By Tesseract") },
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
                                        MyDownloadManager.downloadMl()
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


        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}