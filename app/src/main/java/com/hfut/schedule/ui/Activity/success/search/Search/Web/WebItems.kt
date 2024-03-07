package com.hfut.schedule.ui.Activity.success.search.Search.Web

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.StartUri

@Composable
fun WebItem() {
    ListItem(
        headlineContent = { Text(text = "信息门户") },
        leadingContent = { Icon(painterResource(R.drawable.person), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://one.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "学生教务") },
        leadingContent = { Icon(painterResource(R.drawable.school), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("http://jxglstu.hfut.edu.cn/eams5-student/login?refer=http://jxglstu.hfut.edu.cn/eams5-student/home") }
    )
    ListItem(
        headlineContent = { Text(text = "邮箱系统") },
        leadingContent = { Icon(painterResource(R.drawable.mail), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://email.mail.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "图书馆") },
        leadingContent = { Icon(painterResource(R.drawable.book), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("http://lib.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "服务大厅") },
        supportingContent = { Text(text = "需接入校园网") },
        leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("http://172.31.248.26:8088/") }
    )
    ListItem(
        headlineContent = { Text(text = "WEBVPN") },
        leadingContent = { Icon(painterResource(R.drawable.vpn_key), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://webvpn.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "Community") },
        supportingContent = { Text(text = "可查看一些教务信息") },
        leadingContent = { Icon(painterResource(R.drawable.toll), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://community.hfut.edu.cn/myCommunity/index") }
    )
    ListItem(
        headlineContent = { Text(text = "缴费平台") },
        leadingContent = { Icon(painterResource(R.drawable.monetization_on), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("http://pay.hfut.edu.cn/payment/pay/payment.jsp") }
    )
    ListItem(
        headlineContent = { Text(text = "工大官网") },
        leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://www.hfut.edu.cn/") }
    )
    ListItem(
        headlineContent = { Text(text = "校园网服务中心") },
        supportingContent = { Text(text = "需接入校园网") },
        leadingContent = { Icon(painterResource(R.drawable.wifi_tethering), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://172.18.7.2:8443/Self/nav_login") }
    )
    ListItem(
        headlineContent = { Text(text = "报修系统") },
        leadingContent = { Icon(painterResource(R.drawable.build), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { StartUri.StartUri("https://ehall.hfut.edu.cn/repairsportal/home/index?from=need-auth") }
    )
}