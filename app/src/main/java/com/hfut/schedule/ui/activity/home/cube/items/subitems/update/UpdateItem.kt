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
                    overlineContent = { Text(text = "2024-12-22") },
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
    UpdateItems("新增 聚焦今日课程是否展示已经上完的课，关闭开关后上完的课不再显示首页，让用户聚焦于下一堂课","位于选项-应用设置", UpdateType.ADD)
    UpdateItems("修复 多课表下下学期课程表UI展示错位Bug",null, UpdateType.FIX)
    UpdateItems("修复 下学期课程表功能的逻辑Bug",null, UpdateType.FIX)
    UpdateItems("优化 下学期课表的逻辑，自本版本起，当下学期课表功能开放时，用户也可以查看其课程汇总了",null, UpdateType.OPTIMIZE)
    UpdateItems("优化 登录后课程表无多课表功能的差异",null, UpdateType.OPTIMIZE)
    UpdateItems("优化 教务课表的点击操作，现在点击信息更加详细",null, UpdateType.OPTIMIZE)
    UpdateItems("优化 教务课表和下学期课表的性能",null, UpdateType.OPTIMIZE)
    UpdateItems("优化 聚焦显示今日课程与明日课程的逻辑，当今天课程结束时即立刻展示明日课程",null, UpdateType.OPTIMIZE)
    UpdateItems("优化 部分界面的显示",null, UpdateType.OPTIMIZE)
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

