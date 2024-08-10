package com.hfut.schedule.ui.Activity.success.cube.Settings

import android.annotation.SuppressLint
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
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.ScrollText


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
                    overlineContent = { Text(text = "2024-08-10") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "") },
                    headlineContent = { ScrollText(text = "第${APPVersion.getVersionCode()}次更新") },
                    modifier = Modifier.weight(.5f)
                )
                ListItem(
                    headlineContent = { ScrollText(text = "Chiu-xaH") },
                    overlineContent = { Text(text = "构建") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "") },
                    modifier = Modifier.weight(.5f)
                )
            }

        }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    VersionInfoCard()
    DividerText(text = "新特性")
    UpdateItems("修复 查询中心部分文字由于屏幕宽度较小而错位 Bug",null, UpdateType.FIX)
    UpdateItems("优化 部分界面的显示与动画效果",null, UpdateType.OPTIMIZE)
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
