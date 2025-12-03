package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.file.backupData
import com.hfut.schedule.logic.util.storage.file.restoreData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.util.picker.copyUriToCacheFile
import com.xah.transition.util.TransitionBackHandler
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlinx.coroutines.launch
import java.io.File

private const val error = "发生逻辑错误"

@Composable
fun BackupScreen(
    innerPadding : PaddingValues,
    navController: NavHostController
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    val activity = LocalActivity.current
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var file by remember { mutableStateOf<File?>(null) }

    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }

    LaunchedEffect(activity) {
        activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                activity?.let {
                    file = copyUriToCacheFile(context,uri)
                    if(file == null) {
                        showToast(error)
                        return@let
                    }
                    showDialog = true
                }
            }
        }
    )

    if(showDialog) {
        LittleDialog(
            onConfirmation = {
                scope.launch {
                    activity?.let {
                        file?.let { backupFile ->
                            restoreData(it,context,backupFile)
                        } ?: showToast(error)
                    }
                    showDialog = false
                }
            },
            onDismissRequest = { showDialog = false },
            dialogText = "确认后将抹除原数据，即使出现恢复数据失败的情况",
            dialogTitle = "高危操作"
        )
    }


    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("备份/导出") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("备份数据至Download文件夹") },
                    supportingContent = {
                        Text("文件名将以'${MyApplication.APP_NAME}备份_时间戳.zip'写出")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.save_clock),null)
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            activity?.let {
                                val result = backupData(it,context)
                                if(result) {
                                    showToast("备份成功，请前往Download目录查看")
                                } else {
                                    showToast("备份失败")
                                }
                            }
                        }
                    }
                )
            }
        }
        DividerTextExpandedWith("恢复/导入") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("恢复数据") },
                    supportingContent = {
                        Text("请务必选择本App备份导出的文件，并且原数据将会丢失！")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.settings_backup_restore),null)
                    },
                    modifier = Modifier.clickable {
                        filePickerLauncher.launch(arrayOf("*/*"))
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("预设数据源下载") },
                    supportingContent = {
                        Text("导入开发者预设的数据源，以方便游客体验更多功能")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.download),null)
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebUrl(context,"https://gitee.com/chiu-xah/HFUT-Schedule/releases/download/Android/hfut_schedule_guest_data.zip")
                        }
                    }
                )
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}