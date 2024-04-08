package com.hfut.schedule.ui.Activity.success.search.Search.Web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.StartApp

@Composable
fun WebItem() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "信息门户") },
                leadingContent = { Icon(painterResource(R.drawable.person), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://one.hfut.edu.cn/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "学生教务") },
                leadingContent = { Icon(painterResource(R.drawable.school), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://jxglstu.hfut.edu.cn/eams5-student/login?refer=http://jxglstu.hfut.edu.cn/eams5-student/home") }
            )
        }
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "邮箱系统") },
                leadingContent = { Icon(painterResource(R.drawable.mail), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://email.mail.hfut.edu.cn/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "图书馆") },
                leadingContent = { Icon(painterResource(R.drawable.book), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://lib.hfut.edu.cn/") }
            )
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "服务大厅") },
                overlineContent = { Text(text = "需接入校园网") },
                leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://172.31.248.26:8088/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "校园网服务") },
                overlineContent = { Text(text = "接入校园网") },
                leadingContent = { Icon(painterResource(R.drawable.wifi_tethering), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://172.18.7.2:8443/Self/nav_login") }
            )
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "智慧社区") },
                leadingContent = { Icon(painterResource(R.drawable.toll), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://community.hfut.edu.cn/myCommunity/index") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "缴费平台") },
                leadingContent = { Icon(painterResource(R.drawable.monetization_on), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://pay.hfut.edu.cn/payment/pay/payment.jsp") }
            )
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "工大官网") },
                leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://www.hfut.edu.cn/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "WEBVPN") },
                leadingContent = { Icon(painterResource(R.drawable.vpn_key), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://webvpn.hfut.edu.cn/") }
            )
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "报修") },
                leadingContent = { Icon(painterResource(R.drawable.build), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://xcfw.hfut.edu.cn/school/index.html") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.medium,
        ){
            ListItem(
                headlineContent = { Text(text = "") },
                leadingContent = { Icon(Icons.Filled.MoreVert, contentDescription = "Localized description",) },
                modifier = Modifier.clickable {  }
            )
        }
    }







}