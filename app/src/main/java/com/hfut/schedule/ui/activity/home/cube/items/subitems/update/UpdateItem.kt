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
                    overlineContent = { Text(text = "2025-02-14") },
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
        UpdateItems("重构 校园网", null, UpdateType.RENEW)
        UpdateItems("新增 网费快速充值(宣城)", null, UpdateType.ADD)
        UpdateItems("重构 反馈","原实验室挂网的腾讯文档反馈收集移步到新的位置，位于 选项-疑惑解答 修复-反馈",UpdateType.RENEW)
        UpdateItems("修改 底栏转场动画默认值为淡入淡出", "经测试，原默认动画(底栏吸附)在界面过于复杂时，且性能不在主流的机型易掉帧", UpdateType.RENEW)
        UpdateItems("修复 主界面无限礼花Bug",null,UpdateType.FIX)
        UpdateItems("修复 查询中心-考试 图标显示0红点的Bug",null,UpdateType.FIX)
        UpdateItems("优化 关于 界面的显示", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 课程汇总的显示",null,UpdateType.OPTIMIZE)
        UpdateItems("优化 早期若干功能的性能和稳定性","对早期功能的数据组装代码进行了重构",UpdateType.OPTIMIZE)
        UpdateItems("优化 部分界面的显示",null,UpdateType.OPTIMIZE)
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

