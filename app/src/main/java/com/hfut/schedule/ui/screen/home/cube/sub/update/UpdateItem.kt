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
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith

@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + AppVersion.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-11-12") },
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
        CustomCard (color = cardNormalColor()) {
//            UpdateItems("新增 云端共建支持对上传的日程更新")
//            UpdateItems("新增 新课程表的日视图")
//            UpdateItems("修复 一卡通消费统计一直加载的Bug")
//            UpdateItems("优化 点击聚焦卡片后延迟的问题")
//            UpdateItems("新增 聚焦小组件的自定义调节")
//            UpdateItems("新增 聚焦卡片小组件(4*2和2*1)")
//            UpdateItems("新增 校园网小组件(2*2)")
            UpdateItems("修复 取色风格进度条在已知部分设备上闪烁的Bug")
            UpdateItems("修复 课程表顶部日期显示动画导致整体微位移的Bug")
            UpdateItems("修复 聚焦小组件无法及时30min自动更新的Bug")
            UpdateItems("优化 聚焦小组件的显示效果和点击事件")
            UpdateItems("优化 小组件的刷新数据机制","1.可在App选项-外观与显示-桌面组件，点击手动刷新；2.冷启动App的同时也会刷新；3.点击组件顶部的标题文字手动刷新；4.借助WorkManager，30分钟后自动刷新(受系统调度影响会有延迟)")
//            UpdateItems("优化 方格高度调节的精度到一位小数")
//            UpdateItems("新增 智慧社区的座位预约","位于 查询中心-社区预约")
//            UpdateItems("修复 冲突预览不显示课表的Bug")
//            UpdateItems("新增 自动CAS登录")
//            UpdateItems("修复 启动台开启后,上下滑动手势不灵敏的Bug")
//            UpdateItems("新增 地图和校车支持为游客显示了")
//            UpdateItems("修复 窗口变化与深浅色切换而导致启动台自动收起的Bug")
//            UpdateItems("优化 冷启动速度", type = UpdateType.PERFORMANCE)
//            UpdateItems("优化 打通培养方案完成情况和培养方案")
//            UpdateItems("新增 崩溃的自动处理")
//            UpdateItems("新增 为低版本Android用户的开屏显示")
//            UpdateItems("新增 空教室、教室课表","位于 查询中心-教室")
//            UpdateItems("新增 图书馆我的书架、收藏、斛兵知搜","位于 查询中心-图书馆")
//            UpdateItems("新增 合肥校区电费的快速充值")
//            UpdateItems("新增 启动台支持固定项目")
//            UpdateItems("新增 单独登录教务系统")
//            UpdateItems("新增 备份与恢复数据","位于 选项-应用及配置，可将数据导出到另一台设备以实现多端共存")
//            UpdateItems("新增 为选项适配了新的转场动画")
//            UpdateItems("新增 教务成绩计算每学期的平均绩点与均分，以及可以自定义排除的课程")
//            UpdateItems("新增 共建平台忘记密码、修改密码、注销")
//            UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//            UpdateItems("新增 本地聚焦卡片快速转化为云端卡片", null, UpdateType.ADD)
//            UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//            UpdateItems("新增 智慧后勤的登录")
            /*
            就业二级界面 通知公告二级界面
            教师检索二级界面 课程详情查教师三级界面
            开课查询二级界面 课程汇总二级界面
            课程详情查挂科率三级界面 挂科率二级界面
            一卡通搜索，一卡通付款码，一卡通范围支出，一卡通慧新易校
             */
        }
    }
}

private enum class UpdateType(val res : Int) {
    //新增
    ADD(R.drawable.add_2),
    //下线
    DEGREE(R.drawable.delete),
    //优化
    OPTIMIZE(R.drawable.tune),
    //修复
    FIX(R.drawable.build),
    //重构
    RENEW(R.drawable.alt_route),
    //其他
    OTHER(R.drawable.more_vert),
    UPDATE(R.drawable.arrow_upward),
    // 回归
    COME_BACK(R.drawable.rotate_right),
    // 性能
    PERFORMANCE(R.drawable.flash_on)
}

@Composable
private fun UpdateItems(
    title : String,
    info : String? = null,
    type : UpdateType = when(title.substringBefore(" ")) {
        "新增" -> UpdateType.ADD
        "重构" -> UpdateType.RENEW
        "重写" -> UpdateType.RENEW
        "修改" -> UpdateType.RENEW
        "调整" -> UpdateType.RENEW
        "优化" -> UpdateType.OPTIMIZE
        "修复" -> UpdateType.FIX
        "更新" -> UpdateType.UPDATE
        "升级" -> UpdateType.UPDATE
        "下线" -> UpdateType.DEGREE
        "移除" -> UpdateType.DEGREE
        "删除" -> UpdateType.DEGREE
        "回归" -> UpdateType.COME_BACK
        else -> UpdateType.OTHER
    }
) {
    TransplantListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = type.res), contentDescription = "") }
    )
}



