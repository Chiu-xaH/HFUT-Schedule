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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.MyAPIItem
import com.hfut.schedule.ui.UIUtils.ScrollText

@Composable
fun WebItem() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
        ){
            ListItem(
                headlineContent = { Text(text = "信息门户") },
                leadingContent = { Icon(painterResource(R.drawable.person), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://one.hfut.edu.cn/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
        ){
            ListItem(
                headlineContent = { Text(text = "教务系统") },
                leadingContent = { Icon(painterResource(R.drawable.school), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://jxglstu.hfut.edu.cn/eams5-student/login?refer=http://jxglstu.hfut.edu.cn/eams5-student/home") }
            )
        }
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
        ){
            ListItem(
                headlineContent = { Text(text = "大创系统") },
                leadingContent = { Icon(Icons.Filled.MoreVert, contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://dcxt.hfut.edu.cn/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
        ){
            ListItem(
                headlineContent = { Text(text = "图书馆") },
                overlineContent = { Text(text = "需接入校园网") },
                leadingContent = { Icon(painterResource(R.drawable.book), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("http://lib.hfut.edu.cn/") }
            )
        }
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
        ){
            ListItem(
                headlineContent = { Text(text = "校官网") },
                leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.StartUri("https://www.hfut.edu.cn/") }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            shape = MaterialTheme.shapes.small,
        ){
            ListItem(
                headlineContent = { ScrollText(text = "旧热水(支付宝社慧通)") },
                leadingContent = { Icon(painterResource(R.drawable.water_voc), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { StartApp.openAlipay(MyApplication.AlipayHotWaterOldURL) }
            )
        }
    }
}