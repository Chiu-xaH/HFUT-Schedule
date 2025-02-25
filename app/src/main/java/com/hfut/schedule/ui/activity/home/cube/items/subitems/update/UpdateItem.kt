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
import com.hfut.schedule.ui.utils.components.LargeCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.num
import com.hfut.schedule.ui.utils.style.CardForListColor


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + APPVersion.getVersionName()
    ) {
        TransplantListItem(
            overlineContent = { Text(text = "2025-02-25") },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "") },
            headlineContent = { Text(text = "版本号 ${APPVersion.getVersionCode()}") },
        )
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
//    data class UpdateItem(val title: String,val info : String?=null,val type : UpdateType)
//    val updates = listOf(
//        "新增 寝室电费对合肥校区的适配",
//    )
    Spacer(Modifier.height(3.dp))
    VersionInfoCard()
    DividerTextExpandedWith(text = "新特性") {
        UpdateItems("重构 卡片的UI","去掉普通卡片的低阴影，使用扁平化设计",UpdateType.RENEW)
        UpdateItems("修复 新闻公告不显示项目的Bug",null,UpdateType.FIX)
        UpdateItems("修复 教务登录界面由于网络问题偶发崩溃的Bug",null,UpdateType.FIX)
        UpdateItems("优化 部分界面的布局",null,UpdateType.OPTIMIZE)
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

