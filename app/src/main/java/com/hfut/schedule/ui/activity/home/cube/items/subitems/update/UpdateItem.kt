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
import com.hfut.schedule.logic.utils.VersionUtils
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
        title = "版本 " + VersionUtils.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-03-28") },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "") },
                headlineContent = { Text(text = "版本号 ${VersionUtils.getVersionCode()}") },
                modifier = Modifier.weight(.5f)
            )
            VersionUtils.getSplitType().let {
                TransplantListItem(
                    overlineContent = { Text(text = if(it == VersionUtils.SplitType.COMMON) "全量包" else "架构分包") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.package_2), contentDescription = "") },
                    headlineContent = { Text(text = it.description) },
                    modifier = Modifier.weight(.5f)
                )
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    Spacer(Modifier.height(3.dp))
    DividerTextExpandedWith("版本信息",openBlurAnimation = false) {
        VersionInfoCard()
    }
    DividerTextExpandedWith(text = "新特性") {
        UpdateItems("新增 若干网页链接", "就业信息网、合肥校区智慧后勤、大创系统",UpdateType.ADD)
        UpdateItems("新增 纯黑深色背景", "位于 选项-界面显示",UpdateType.ADD)
        UpdateItems("新增 请求范围对开课查询、教师检索的适配", null,UpdateType.ADD)
        UpdateItems("新增 深浅色切换", "位于 选项-界面显示",UpdateType.ADD)
        UpdateItems("新增 功能可用性展示", "位于 选项-维护关于",UpdateType.ADD)
        UpdateItems("新增 校历中的合肥校区", null,UpdateType.ADD)
        UpdateItems("修复 早期经验不足而写的某些代码健壮性不足导致的崩溃Bug", null,UpdateType.FIX)
        UpdateItems("修复 已借图书接口错误的Bug", "修不修也没信息，智慧社区的图书借阅接口似乎都不更新信息了，后期酌情替换掉",UpdateType.FIX)
        UpdateItems("优化 合肥校区用户和宣城校区用户的差异性显示", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 部分界面的显示", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 冷启动初始化时的一些冗余网络请求，优化性能", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 选项中一些页面的层级显示", null,UpdateType.OPTIMIZE)
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
    OTHER,
    UPDATE
}
@Composable
fun UpdateItems(title : String,info : String?,type : UpdateType) {
    TransplantListItem(
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
        UpdateType.UPDATE -> R.drawable.arrow_upward
    }
}

