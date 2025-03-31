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
                overlineContent = { Text(text = "2025-03-31") },
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
        UpdateItems("新增 教务课程表导出至系统日历", "位于 课程表-多课表",UpdateType.ADD)
        UpdateItems("新增 彩蛋", "位于 选项-维护关于",UpdateType.ADD)
        UpdateItems("修复 增量更新时由于补丁包找不到而崩溃的Bug", null,UpdateType.FIX)
        UpdateItems("修复 第二次使用增量更新或全量更新时错误显示100%进度的Bug", null,UpdateType.FIX)
        UpdateItems("修复 部分区块由于未初始化完成而崩溃的Bug", null,UpdateType.FIX)
        UpdateItems("修复 功能性可用性支持显示信息错乱的Bug", null,UpdateType.FIX)
        UpdateItems("优化 开屏界面的显示", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 部分界面的显示", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 重复添加日程的处理", null,UpdateType.OPTIMIZE)
        UpdateItems("优化 添加日程时的提醒", null,UpdateType.OPTIMIZE)
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

