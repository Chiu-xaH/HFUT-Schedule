package com.hfut.schedule.ui.ComposeUI.Search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.StartUri.StartUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebUI() {
    val sheetState_Web = rememberModalBottomSheetState()
    var showBottomSheet_Web by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "网址导航") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.travel_explore),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { showBottomSheet_Web = true }
    )

    if (showBottomSheet_Web) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Web = false
            },
            sheetState = sheetState_Web
        ) {
            Column() {
                WebItem()
                Spacer(modifier = Modifier.height(20.dp))
            }


        }
    }
}
@Composable
fun WebItem() {

    ListItem(
        headlineContent = { Text(text = "工大官网") },
        leadingContent = { Icon(painterResource(R.drawable.publics), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("http://www.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "信息门户") },
        leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("https://one.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "学生教务") },
        leadingContent = { Icon(painterResource(R.drawable.school), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("http://jxglstu.hfut.edu.cn/eams5-student/login?refer=http://jxglstu.hfut.edu.cn/eams5-student/home") }
    )
    ListItem(
        headlineContent = { Text(text = "邮箱系统") },
        leadingContent = { Icon(painterResource(R.drawable.mail), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("https://email.mail.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "图书馆") },
        leadingContent = { Icon(painterResource(R.drawable.book), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("http://lib.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "服务大厅") },
        supportingContent = {Text(text = "需接入校园网")},
        leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("http://172.31.248.26:8088/") }
    )
    ListItem(
        headlineContent = { Text(text = "WEBVPN") },
        supportingContent = {Text(text = "未接入校园网时")},
        leadingContent = { Icon(painterResource(R.drawable.vpn_key), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("https://webvpn.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "校园网服务中心") },
        supportingContent = {Text(text = "需接入校园网")},
        leadingContent = { Icon(painterResource(R.drawable.wifi_tethering), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri("https://172.18.7.2:8443/Self/nav_login") }
    )
}