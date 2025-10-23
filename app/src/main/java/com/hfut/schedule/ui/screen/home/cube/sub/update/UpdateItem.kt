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
                overlineContent = { Text(text = "2025-10-23") },
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
            UpdateItems("重构 课程表底部切换按钮","三个按钮合并为一个，既可以原方式点击两边箭头切换周数，点击中间回到本周，也可拖动按钮切换周数")
            UpdateItems("新增 课程表支持页面左右滑动切换周数")
            UpdateItems("新增 课程表方格高度自定义","位于 选项-界面与显示")
            UpdateItems("新增 周末有活动时自动并强制展开课程表7x布局")
            UpdateItems("修复 课程表在5x布局下周末的聚焦日程布局有误的Bug")
            UpdateItems("修复 课程表背景若干天后消失的Bug")
            UpdateItems("移除 课程表隐藏方格的开关","现在，当插入背景时强制隐藏空方格，不插入背景时强制显示空方格")
            UpdateItems("移除 课程表背景混色调整","为了保证可读性，所有文字和图标将被包裹，不需要调整背景混色了")
            UpdateItems("优化 潜在的图片内存泄漏")
//            UpdateItems("新增 自动CAS登录")
//            UpdateItems("修复 启动台开启后,上下滑动手势不灵敏的Bug")
//            UpdateItems("新增 课程表方格支持教师的显示")
//            UpdateItems("新增 课程表方格支持按开始结束时间显示为不同的长度")
//            UpdateItems("新增 地图和校车支持为游客显示了")
//            UpdateItems("修复 冲突预览不显示课表的Bug")
//            UpdateItems("修复 窗口变化与深浅色切换而导致启动台自动收起的Bug")
//            UpdateItems("优化 冷启动速度", type = UpdateType.PERFORMANCE)
//            UpdateItems("优化 降低转场动画的渲染压力", type = UpdateType.PERFORMANCE)
//            UpdateItems("优化 打通培养方案完成情况和培养方案")
//            UpdateItems("新增 崩溃的自动处理")
//            UpdateItems("新增 为低版本Android用户的开屏显示")
//            UpdateItems("新增 空教室、教室课表","位于 查询中心-教室")
//            UpdateItems("新增 图书馆我的书架、收藏、斛兵知搜","位于 查询中心-图书馆")
//            UpdateItems("新增 合肥校区电费的快速充值")
//            UpdateItems("修复 部分翻页界面转场后再回退重置的Bug")
//            UpdateItems("修复 首次开屏时层级模糊暂时失效的Bug")
//            UpdateItems("新增 电费记录","位于 查询中心-寝室电费")
//            UpdateItems("新增 启动台支持固定项目")
//            UpdateItems("新增 单独登录教务系统")
//            UpdateItems("新增 备份与恢复数据","位于 选项-应用及配置，可将数据导出到另一台设备以实现多端共存")
//            UpdateItems("新增 为选项适配了新的转场动画")
//            UpdateItems("新增 教务成绩计算每学期的平均绩点与均分，以及可以自定义排除的课程")
//            UpdateItems("新增 共建平台忘记密码、修改密码、注销")
//            UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//            UpdateItems("新增 本地聚焦卡片快速转化为云端卡片", null, UpdateType.ADD)
//            UpdateItems("新增 对共建平台已上传卡片的信息编辑", null, UpdateType.ADD)
//            UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//            UpdateItems("优化 添加聚焦卡片适用范围的添加班级逻辑", null, UpdateType.OPTIMIZE)
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



