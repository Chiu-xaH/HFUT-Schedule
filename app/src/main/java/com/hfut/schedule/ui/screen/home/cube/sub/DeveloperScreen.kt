package com.hfut.schedule.ui.screen.home.cube.sub

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.LanguageHelper
import com.hfut.schedule.logic.util.sys.TemperatureUtil
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.getStorageJxglstuCookie
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.AppMemoryUtil
import com.xah.common.logic.util.CpuFreqUtil
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.ColumnVertical
import com.xah.common.ui.style.padding.InnerPaddingHeight
import kotlinx.coroutines.delay
import java.util.Locale

private fun Long.toGhzString(): String = String.format(Locale.US, "%.2f", this / 1_000_000.0)

@Composable
fun DeveloperScreen(vm : NetWorkViewModel,innerPadding : PaddingValues) {
//    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
//    var scale by remember { mutableFloatStateOf(1f) }
//    TransitionBackHandler(navController,enablePredictive) {
//        scale = it
//    }
    var showEditDialog by remember { mutableStateOf(false) }
    val cookie by produceState(initialValue = EMPTY_STRING, key1 = showEditDialog) {
        getStorageJxglstuCookie(isWebVpn = false)?.let {
            value = it
        }
    }
    val wxAuth by DataStoreManager.wxAuth.collectAsState(initial = EMPTY_STRING)
    val cookieWebVpn by produceState(initialValue = EMPTY_STRING,key1 = showEditDialog) {
        getStorageJxglstuCookie(isWebVpn = true)?.let {
            value = it
        }
    }
    val cookieCommunity by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = prefs.getString("TOKEN",EMPTY_STRING)
    }
    val cookieOne by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = prefs.getString("bearer",EMPTY_STRING)
    }
    val cookieHuiXin by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = prefs.getString("auth",EMPTY_STRING)
    }
    val cookieCAS by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = GlobalUiStateHolder.casCookies
    }
    val studentId by vm.studentId.state.collectAsState()
    val bizTypeId by vm.bizTypeIdResponse.state.collectAsState()
    var input by remember { mutableStateOf(EMPTY_STRING) }
    var label by remember { mutableStateOf(EMPTY_STRING) }

    if(showEditDialog) {
        androidx.compose.ui.window.Dialog (
            onDismissRequest = { showEditDialog = false },
        ) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface,MaterialTheme.shapes.medium).padding(vertical = APP_HORIZONTAL_DP)) {
                ColumnVertical() {
                    CustomTextField(
                        input = input,
                        singleLine = false
                    ) { input = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                    Button(onClick = {
                        SharedPrefs.saveString(label,input)
                        showEditDialog = false
                    }) {
                        Text("保存")
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("系统环境信息") {
            var refresh by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                while(true) {
                    delay(1000L)
                    refresh++
                }
            }
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                key(refresh) {
                    TransplantListItem(
                        headlineContent = {
                            Text("Android Sdk版本 ${AppVersion.sdkInt}")
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("App内存占用 ${AppMemoryUtil.getRssMb()}MB")
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("电池温度 ${TemperatureUtil.getBatteryTemperature()}℃")
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("系统语言 ${LanguageHelper.getCurrentAppLanguageTag()}")
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("CPU频率 当前${CpuFreqUtil.getCurrentCpuMaxFreq()?.toGhzString()}GHz 最大${CpuFreqUtil.getCpuMaxFreq()?.toGhzString()}GHz")
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text("设备型号 ${AppVersion.deviceName}")
                        }
                    )
                }
            }
        }
        DividerTextExpandedWith("Cookies & Authorization") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("教务系统") },
                    supportingContent = {
                        Text(cookie)
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = {
                                label = "redirect"
                                input = cookie
                                showEditDialog = true
                            }
                        ) {
                            Icon(painterResource(R.drawable.edit),null)
                        }
                    },
                    modifier = Modifier.clickable {
                        ClipBoardHelper.copy(cookie)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cookie),null)
                    }
                )
                PaddingHorizontalDivider()
                cookieOne?.let {
                    TransplantListItem(
                        headlineContent = { Text("信息门户") },
                        supportingContent = {
                            Text(it)
                        },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = {
                                    label = "bearer"
                                    input = it
                                    showEditDialog = true
                                }
                            ) {
                                Icon(painterResource(R.drawable.edit),null)
                            }
                        },
                        modifier = Modifier.clickable {
                            ClipBoardHelper.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        }
                    )
                    PaddingHorizontalDivider()
                }
                TransplantListItem(
                    headlineContent = { Text("WebVpn") },
                    supportingContent = {
                        Text(cookieWebVpn)
                    },
                    modifier = Modifier.clickable {
                        ClipBoardHelper.copy(cookieWebVpn)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cookie),null)
                    },
                )
                cookieCommunity?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("智慧社区") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardHelper.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        },
                    )
                }
                cookieHuiXin?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("慧新易校") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardHelper.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        },
                    )
                }
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("指尖工大") },
                    supportingContent = {
                        Text(wxAuth)
                    },
                    modifier = Modifier.clickable {
                        ClipBoardHelper.copy(wxAuth)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cookie),null)
                    },
                )
                cookieCAS?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("CAS统一认证登录") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardHelper.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        },
                    )
                }
            }

        }
        DividerTextExpandedWith("教务系统 重要参数(修改Cookies后自动获取)") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                CommonNetworkScreen(studentId, onReload = null, isFullScreen = false) {
                    val data = (studentId as NetworkUiState.Success).data.toString()
                    TransplantListItem(
                        headlineContent = { Text("StudentID") },
                        supportingContent = {
                            Text(data)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardHelper.copy(data)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.tag),null)
                        }
                    )
                }
                PaddingHorizontalDivider()
                CommonNetworkScreen(bizTypeId, onReload = null, isFullScreen = false) {
                    val data = (bizTypeId as NetworkUiState.Success).data.toString()
                    TransplantListItem(
                        headlineContent = { Text("BizTypeID") },
                        supportingContent = {
                            Text(data)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardHelper.copy(data)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.tag),null)
                        }
                    )
                }
            }
        }

        InnerPaddingHeight(innerPadding,false)
    }
}