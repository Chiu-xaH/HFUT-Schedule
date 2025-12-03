package com.hfut.schedule.ui.screen.home.cube.sub.update

import android.annotation.SuppressLint
import android.content.Context
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
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import java.io.File

@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + AppVersion.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-12-03") },
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
            // 欢迎体验新版本 本版本新集成了新的数据源，可能会导致应用不稳定
            UpdateItems("重构 全校培养方案","更换为合工大教务的数据源")//
            UpdateItems("新增 合工大教务的登录","位于 刷新登陆状态界面的选项中，如果修改过教务系统的默认密码，则需要手动配置新的密码，位于 选项-网络-教务系统密码")//
            UpdateItems("新增 考试数据源：合工大教务","位于 查询中心-考试，App自动将教务和合工大教务两种数据源合二为一，在课程表、聚焦中也会同步显示此数据")//
            UpdateItems("新增 同班同学","位于 课程详情界面")//
            UpdateItems("新增 课程表数据源：合工大教务","重做了数据源选择，前往 选项-应用及配置-默认课程表，切换数据源，聚焦和桌面小组件也同步支持合工大教务了")//
            UpdateItems("新增 成绩数据源：合工大教务","位于 查询中心-成绩，本数据源可以在评教之前查看成绩")//
            UpdateItems("新增 备用与恢复","位于 选项-应用与配置，并且为游客提供了预设数据源，以便其他开发者更便利地体验本项目")//
//            UpdateItems("新增 空教室与教室课表","位于 查询中心-教室，数据源为合工大教务")
//            UpdateItems("新增 开课查询数据源：合工大教务")
//            UpdateItems("重构 寝室评分（宣城与合肥）","学校终于将宣区寝室查询和肥区一样接入到智慧社区了，旧接口将不再返回新数据了")
//            UpdateItems("修复 一卡通消费统计一直加载的Bug")
//            UpdateItems("新增 云端共建支持对上传的日程更新")
//            UpdateItems("新增 新课程表的日视图")
//            UpdateItems("优化 点击聚焦卡片后延迟的问题")
//            UpdateItems("新增 聚焦卡片小组件(4*2和2*1)")
//            UpdateItems("新增 校园网小组件(2*2)")
//            UpdateItems("新增 使用技巧","位于 选项-维护与关于")
//            UpdateItems("新增 智慧社区的座位预约","位于 查询中心-社区预约")
//            UpdateItems("修复 冲突预览不显示课表的Bug")
//            UpdateItems("新增 自动CAS登录")
//            UpdateItems("修复 启动台开启后,上下滑动手势不灵敏的Bug")
//            UpdateItems("新增 地图和校车支持为游客显示了")
//            UpdateItems("修复 窗口变化与深浅色切换而导致启动台自动收起的Bug")
//            UpdateItems("优化 冷启动速度", type = UpdateType.PERFORMANCE)
//            UpdateItems("新增 崩溃的自动处理")
//            UpdateItems("新增 为低版本Android用户的开屏显示")
//            UpdateItems("新增 图书馆我的书架、收藏、斛兵知搜","位于 查询中心-图书馆")
//            UpdateItems("新增 合肥校区电费的快速充值")
//            UpdateItems("新增 启动台支持固定项目")
//            UpdateItems("新增 单独登录教务系统")
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



