package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

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
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.LargeCard
import com.hfut.schedule.ui.utils.components.TransplantListItem


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + VersionUtils.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-04-09") },
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
//        UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//        UpdateItems("新增 肥工就业信息网的检索", "位于 查询中心",UpdateType.ADD)
//        UpdateItems("重构 一共107个网络请求函数，全部换新，优化性能", null,UpdateType.OPTIMIZE)
        UpdateItems("重构 数据库", "使用Room完全重写，文件导入课表、洗浴标签收藏、聚焦自定义事件均会受到影响，之前的数据会被清空，请自行做好备份",UpdateType.RENEW)
        UpdateItems("重构 两个界面下的课表界面进行融合", null,UpdateType.RENEW)
        UpdateItems("重构 聚焦的添加日程功能", "现在，用户可以自定义添加的类型、日期、与日历联动、显示在课程表中(与课程表联动推迟到后续版本)",UpdateType.RENEW)
        UpdateItems("回归 运动模糊开关", "之前下线做了优化",UpdateType.RENEW)
        UpdateItems("新增 转场动画曲线、增强转场动画", "位于 选项-界面显示-动效",UpdateType.ADD)
        UpdateItems("新增 祝福", null,UpdateType.ADD)
        UpdateItems("修复 实时模糊开关切换时界面未及时刷新的Bug", null,UpdateType.FIX)
        UpdateItems("优化 聚焦课程进度的分钟级自动刷新", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 一卡通相关的数值刷新机制", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 对转专业同学由于选课变动，智慧社区课程表一直不更新数据而导致聚焦的课程提醒有偏差的问题", "现在当在设置中调整默认课程表为教务后，聚焦的数据源也会被更换",UpdateType.OPTIMIZE)
        UpdateItems("优化 压缩冷启动时的网络请求，由最初的32个压缩至最低5个，提高性能", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 主界面页面切换时重复发送不必要的网络请求问题", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 刷新指示器不跟随动态取色以及深色模式下为白色的问题", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 部分界面的显示", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 冗余的网络请求以及一些无用逻辑", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 某些场景下大型数据的缓存复用，提升性能", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 设置返回按钮的遮挡", null,UpdateType.OPTIMIZE)
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

