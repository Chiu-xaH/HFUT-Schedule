package com.hfut.schedule.ui.screen.home.cube.sub

import android.app.Dialog
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.screen.home.getStorageJxglstuCookie
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.util.TransitionPredictiveBackHandler

@Composable
fun DeveloperScreen(vm : NetWorkViewModel,innerPadding : PaddingValues,navController : NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController) {
        scale = it
    }
    var showEditDialog by remember { mutableStateOf(false) }
    val cookie by produceState(initialValue = "", key1 = showEditDialog) {
        getStorageJxglstuCookie(isWebVpn = false)?.let {
            value = it
        }
    }
    val cookieWebVpn by produceState(initialValue = "",key1 = showEditDialog) {
        getStorageJxglstuCookie(isWebVpn = true)?.let {
            value = it
        }
    }
    val cookieCommunity by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = prefs.getString("TOKEN","")
    }
    val cookieHuiXin by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = prefs.getString("auth","")
    }
    val cookieCAS by produceState<String?>(initialValue = null,key1 = showEditDialog) {
        value = prefs.getString("CAS_COOKIE","")
    }
    val studentId by vm.studentId.state.collectAsState()
    val bizTypeId by vm.bizTypeIdResponse.state.collectAsState()
    var input by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }

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

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("Cookies") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
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
                        ClipBoardUtils.copy(cookie)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cookie),null)
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("WebVpn") },
                    supportingContent = {
                        Text(cookieWebVpn)
                    },
                    modifier = Modifier.clickable {
                        ClipBoardUtils.copy(cookieWebVpn)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cookie),null)
                    },
                )
                cookieCommunity?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("Community") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
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
                            ClipBoardUtils.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        },
                    )
                }
                cookieCAS?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("CAS") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        },
                    )
                }
            }

        }
        DividerTextExpandedWith("教务系统 重要参数(修改Cookies后自动获取)") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                CommonNetworkScreen(studentId, onReload = null, isFullScreen = false) {
                    val data = (studentId as UiState.Success).data.toString()
                    TransplantListItem(
                        headlineContent = { Text("StudentID") },
                        supportingContent = {
                            Text(data)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(data)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.tag),null)
                        }
                    )
                }
                PaddingHorizontalDivider()
                CommonNetworkScreen(bizTypeId, onReload = null, isFullScreen = false) {
                    val data = (bizTypeId as UiState.Success).data.toString()
                    TransplantListItem(
                        headlineContent = { Text("BizTypeID") },
                        supportingContent = {
                            Text(data)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(data)
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