package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.ScrollText


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfoCard() {
    if(APPVersion.getVersionName().contains("Preview")) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
        ) { ListItem(headlineContent = { Text(text = APPVersion.getVersionName(), fontSize = 28.sp) }) }
    } else
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            ListItem(headlineContent = { Text(text = "聚在工大 " + APPVersion.getVersionName(), fontSize = 28.sp) })
            Row {
                ListItem(
                    overlineContent = { Text(text = "2025-01-01") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "") },
                    headlineContent = { ScrollText(text = "第${APPVersion.getVersionCode()}次更新") },
                    modifier = Modifier.weight(.5f)
                )
                ListItem(
                    headlineContent = { ScrollText(text = "Chiu-xaH") },
                    overlineContent = { Text(text = "构建") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "") },
                    modifier = Modifier.weight(.5f).clickable {
                        MyToast("想成为下个版本的构建者吗?跟随 设置-维护关于-开源主页")
                    }
                )
            }
        }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    VersionInfoCard()
    DividerText(text = "新特性")
    UpdateItems("重构 转专业我的申请","显示更多信息(已经成功转走的会隐藏考试安排等信息)", UpdateType.RENEW)
    UpdateItems("修复 外地访问失效的Bug",null, UpdateType.FIX)
    UpdateItems("优化 外地访问支持更多场景","当大家离校后，由于距离服务器较远，普通的刷新登陆状态大概率无法使用，此时勾选外地访问，再登陆", UpdateType.OPTIMIZE)
    UpdateItems("优化 成绩界面的布局",null, UpdateType.OPTIMIZE)
}

enum class UpdateType {
    //新增
    ADD,
    //下线
    DEGREE,
    //优化
    OPTIMIZE,
    //修复
    FIX,
    //重构
    RENEW,
    //其他
    OTHER
}
@Composable
fun UpdateItems(title : String,info : String?,type : UpdateType) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = updateTypeIcons(type)), contentDescription = "") }
    )
}

fun updateTypeIcons(type : UpdateType) : Int {
    return when(type) {
        UpdateType.ADD -> R.drawable.add_circle
        UpdateType.DEGREE -> R.drawable.do_not_disturb_on
        UpdateType.OPTIMIZE -> R.drawable.tune
        UpdateType.FIX -> R.drawable.build
        UpdateType.RENEW -> R.drawable.alt_route
        UpdateType.OTHER -> R.drawable.stacks
    }
}

