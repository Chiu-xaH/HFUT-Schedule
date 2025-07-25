package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.getStorageJxglstuCookie
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@Composable
fun DeveloperScreen(vm : NetWorkViewModel,innerPadding : PaddingValues) {
    val cookie by produceState(initialValue = "") {
        getStorageJxglstuCookie(isWebVpn = false)?.let {
            value = it
        }
    }
    val cookieWebVpn by produceState(initialValue = "") {
        getStorageJxglstuCookie(isWebVpn = true)?.let {
            value = it
        }
    }
    val cookieCommunity = remember { prefs.getString("TOKEN","") }
    val cookieHuiXin = remember { prefs.getString("auth","") }
    val cookieCAS = remember { prefs.getString("CAS_COOKIE","") }

    val studentId by vm.studentId.state.collectAsState()
    val bizTypeId by vm.bizTypeIdResponse.state.collectAsState()


    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("Cookies") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("教务系统 Cookie") },
                    supportingContent = {
                        Text(cookie)
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
                    headlineContent = { Text("WebVpn Cookie") },
                    supportingContent = {
                        Text(cookieWebVpn)
                    },
                    modifier = Modifier.clickable {
                        ClipBoardUtils.copy(cookieWebVpn)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cookie),null)
                    }
                )
                cookieCommunity?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("Community Cookie") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        }
                    )
                }
                cookieHuiXin?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("慧新易校 Cookie") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        }
                    )
                }
                cookieCAS?.let {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("CAS Cookie") },
                        supportingContent = {
                            Text(it)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.cookie),null)
                        }
                    )
                }
            }

        }
        DividerTextExpandedWith("教务系统 重要参数") {
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