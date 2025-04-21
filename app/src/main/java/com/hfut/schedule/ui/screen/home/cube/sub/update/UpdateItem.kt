package com.hfut.schedule.ui.screen.home.cube.sub.update

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.LargeCard
import com.hfut.schedule.ui.component.TransplantListItem


@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + AppVersion.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-04-21") },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = "") },
                headlineContent = { Text(text = "版本号 ${AppVersion.getVersionCode()}") },
                modifier = Modifier.weight(.5f)
            )
            AppVersion.getSplitType().let {
                TransplantListItem(
                    overlineContent = { Text(text = if(it == AppVersion.SplitType.COMMON) "全量包" else "架构分包") },
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
//        UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//        UpdateItems("重构 一共107个网络请求函数，全部换新，优化性能", null,UpdateType.OPTIMIZE)
//        UpdateItems("新增 为选项界面适配新的转场动画", null, UpdateType.ADD)
        UpdateItems("新增 共建聚焦卡片源的筛选，减少无用干扰", "位于 查询中心-信息共建-选项", UpdateType.ADD)
        UpdateItems("新增 共建聚焦卡片源的排序", null, UpdateType.ADD)
        UpdateItems("新增 共建聚焦卡片对校区的支持", null, UpdateType.ADD)
        UpdateItems("优化 共建聚焦卡片界面的显示", null, UpdateType.ADD)


//        UpdateItems("修复 时间选取时判断机制错误的Bug", null,UpdateType.FIX)


//        UpdateItems("重构 图书馆", "由于学校图书官网进行了升级，图书功能也带来了更多的可能性，扩展：合肥校区的座位预约入口、借阅图书数据",UpdateType.RENEW)

//        UpdateItems("新增 课程进度实时通知", "仅Android 16+", UpdateType.ADD)
//        UpdateItems("新增 肥工就业信息网的检索", "位于 查询中心", UpdateType.ADD)

//        UpdateItems("优化 层级转场时的圆角", null, UpdateType.OPTIMIZE)
    }
}

private enum class UpdateType {
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
private fun UpdateItems(title : String,info : String?,type : UpdateType) {
    TransplantListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = updateTypeIcons(type)), contentDescription = "") }
    )
}

private fun updateTypeIcons(type : UpdateType) : Int {
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

