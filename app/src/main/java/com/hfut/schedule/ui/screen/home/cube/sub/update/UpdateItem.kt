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
                overlineContent = { Text(text = "2025-04-14") },
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
        UpdateItems("新增 对云端聚焦卡片的显示控制", "位于 选项-应用行为", UpdateType.ADD)
        UpdateItems("新增 对大部分转场界面适配预测式返回手势", null, UpdateType.ADD)
//        UpdateItems("新增 为选项界面适配新的转场动画", null, UpdateType.ADD)
//        UpdateItems("新增 进度实时通知的适配(尚未启用)", "仅Android 16+", UpdateType.ADD)

//        UpdateItems("新增 肥工就业信息网的检索", "位于 查询中心", UpdateType.ADD)
        UpdateItems("重构 向中心运动的转场动画", null, UpdateType.RENEW)

        UpdateItems("修复 新建聚焦卡片时某些值未及时更新的Bug", null, UpdateType.FIX)
        UpdateItems("修复 新建网课类型的聚焦卡片时对状态的错误判定的Bug", null, UpdateType.FIX)
        UpdateItems("修复 功能可用性支持页面中预览效果找不到链接的Bug", null, UpdateType.FIX)
        UpdateItems("修复 新建聚焦卡片界面无法滚动的Bug", null, UpdateType.FIX)

//        UpdateItems("优化 层级转场时的圆角", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 教务数据源下的聚焦卡片点击操作", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 部分界面的显示", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 小标题的横向对齐效果", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 聚焦界面已上完课程的显示效果", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 课程表滚动时常驻项目的显示内容", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 普通日程添加日历时的提醒机制为15min", null, UpdateType.OPTIMIZE)
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

