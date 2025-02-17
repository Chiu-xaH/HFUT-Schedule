package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.num
import com.hfut.schedule.ui.utils.style.CardForListColor


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfoCard() {
//    if(APPVersion.getVersionName().contains("Preview")) {
//        Card(
//            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 15.dp, vertical = 5.dp),
//            shape = MaterialTheme.shapes.medium,
//        ) { ListItem(headlineContent = { Text(text = APPVersion.getVersionName(), fontSize = 28.sp) }) }
//    } else
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardForListColor()
        ) {
            ListItem(headlineContent = { Text(text = "版本 " + APPVersion.getVersionName(), fontSize = 28.sp) })
            Row {
                ListItem(
                    overlineContent = { Text(text = "2025-02-17") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "") },
                    headlineContent = { Text(text = "版本号 ${APPVersion.getVersionCode()}") },
//                    modifier = Modifier.weight(.5f)
                )
//                ListItem(
//                    headlineContent = { Text(text = "Chiu-xaH") },
//                    overlineContent = { Text(text = "构建") },
//                    leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "") },
//                    modifier = Modifier.weight(.5f).clickable {
//                        MyToast("想成为下个版本的构建者吗?跟随 设置-维护关于-开源主页")
//                    }
//                )
            }
        }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    Spacer(Modifier.height(3.dp))
    VersionInfoCard()
    DividerTextExpandedWith(text = "新特性") {
        UpdateItems("新增 开课查询可检索课程代码",null,UpdateType.ADD)
        UpdateItems("新增 培养方案点击课程可直接进行开课查询",null,UpdateType.ADD)
        UpdateItems("修复 初次使用状态下进入游客模式，由于加载未完成导致崩溃的Bug",null,UpdateType.FIX)
        UpdateItems("优化 开课查询的界面及其功能展示",null,UpdateType.OPTIMIZE)
        UpdateItems("优化 课程详情界面的布局",null,UpdateType.OPTIMIZE)
    }
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
        UpdateType.ADD -> R.drawable.add_2
        UpdateType.DEGREE -> R.drawable.do_not_disturb_on
        UpdateType.OPTIMIZE -> R.drawable.tune
        UpdateType.FIX -> R.drawable.build
        UpdateType.RENEW -> R.drawable.alt_route
        UpdateType.OTHER -> R.drawable.stacks
    }
}

