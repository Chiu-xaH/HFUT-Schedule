package com.hfut.schedule.ui.screen.home.cube.sub

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.LifeDestination
import com.hfut.schedule.ui.nav.destination.SettingsAppearanceDestination
import com.hfut.schedule.ui.nav.destination.SettingsConfigurationDestination
import com.hfut.schedule.ui.nav.destination.SettingsLiveUpdateDestination
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.xah.common.ui.component.text.ScrollText
import com.xah.floating.util.LocalFloatingControllerSafely
import com.xah.navigation.util.LocalNavControllerSafely

private const val RELEASE_DATE = "2026-05-06"

@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    VersionInfoCard()
    DividerTextExpandedWith(text = "新特性") {
        CustomCard (color = cardNormalColor()) {
//            UpdateItems("新增 实时通知(Android 16+)","位于 选项-偏好与配置-实时通知",To.Screen(SettingsLiveUpdateDestination))
            UpdateItems("新增 楼层导向(Beta)","位于 查询中心-生活服务-楼层导向；目前仅上线新安学堂四楼进行测试，如可行则继续逐渐扩展", to = To.Screen(LifeDestination))//
            UpdateItems("新增 支持关闭容器共享的开关","位于 选项-外观-动效",To.Screen(SettingsAppearanceDestination))// SharedNav
            UpdateItems("修复 低版本系统中偶见部分场景下返回手势失效的Bug")// SharedNav
            UpdateItems("修复 浮窗收起过程的末尾时动效顿挫的Bug")//
            UpdateItems("修复 部分场景下状态栏的未反色问题")//
            UpdateItems("修复 在开启层级模糊的情况下，空教室列表点击时间轴方块展开收起浮窗动效卡顿的Bug")//
            UpdateItems("修复 一键刷新合工大教务登录时因为对面服务器响应时长过长导致误判网络超时而无法登录的Bug")//
            UpdateItems("优化 DDL在聚焦中的显示机制","DDL类型在截止前72h将会显示在重要事项中，以提醒用户完成任务")//
//            UpdateItems("修复 在着色器效果关闭时容器共享转场时路径偏移的Bug")
//            UpdateItems("修复 部分设备使用图片验证码自动识别功能时崩溃的Bug")//
//            UpdateItems("优化 课程表捏合手势的灵敏度")
//            UpdateItems("新增 课程表交互说明指南","位于 课程表切换菜单内")
//            UpdateItems("新增 合工大教务课表支持写入到日历日程")
            // 校车点击跳转导航
            /*
            就业二级界面 通知公告二级界面
            教师检索二级界面
            一卡通搜索，一卡通付款码，一卡通范围支出，一卡通慧新易校
//            UpdateItems("新增 适配若干二级界面为新的转场动画")
             */
            // TODO 挂科率下拉刷新 [P0]
            // TODO WebView适配新库 [P2]
            // TODO WebVpn、课程表界面动效掉帧率较高走查 [P2]
            // TODO Drawer重做  [P2]
//            UpdateItems("新增 培养方案完成情况统计")
//            UpdateItems("翻页器底部自动展开、中间隐藏")
//            UpdateItems("发生Crash后再次进入app进入专属界面")
//            UpdateItems("新增 启动台与聚焦支持固定项目")
//            UpdateItems("新增 图书馆我的书架、收藏","位于 查询中心-图书馆")
//            UpdateItems("新增 图书馆斛兵知搜支持阅读电子书")
//            UpdateItems("新增 英文语言部分适配")
//            UpdateItems("新增 大模型","位于 查询中心-大模型,应用场景：通知公告的提炼、新增聚焦日程")
//            UpdateItems("新增 合工大教务接口的评教","位于 查询中心-评教")
//            UpdateItems("收纳重构 实验室迁移位置")
//            UpdateItems("新增 新课程表的日视图")
//            UpdateItems("新增 开课查询数据源：合工大教务")
//            UpdateItems("修复 一卡通消费统计一直加载的Bug")
//            UpdateItems("修复 点击聚焦页面的日程后延迟响应的Bug")
//            UpdateItems("新增 合肥校区电费的快速充值")

            // TODO 远期规划
//            UpdateItems("新增 课程表的方格支持自动适应背景透明度")
//            UpdateItems("回归 导入文件形式的课程表")
//            UpdateItems("新增 云端共建支持对上传的日程更新")
//            UpdateItems("新增 聚焦卡片小组件(4*2和2*1)")
//            UpdateItems("新增 校园网小组件(2*2)")
//            UpdateItems("新增 使用技巧","位于 选项-维护与关于")
//            UpdateItems("新增 智慧社区的座位预约","位于 查询中心-社区预约")
//            UpdateItems("新增 自动CAS登录")
//            UpdateItems("新增 地图和校车支持为游客显示了")
//            UpdateItems("新增 崩溃的自动处理")
//            UpdateItems("新增 为低版本Android用户的开屏显示")
//            UpdateItems("新增 单独登录教务系统")
//            UpdateItems("新增 教务成绩计算每学期的平均绩点与均分，以及可以自定义排除的课程")
//            UpdateItems("新增 共建平台忘记密码、修改密码、注销")
//            UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//            UpdateItems("新增 本地聚焦卡片快速转化为云端卡片", null, UpdateType.ADD)
//            UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//            UpdateItems("新增 智慧后勤的登录")
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    Column {
        Spacer(Modifier.height(CARD_NORMAL_DP))
        DividerTextExpandedWith("版本信息",openBlurAnimation = false) {
            LargeCard(
                title = "版本 " + AppVersion.getVersionName()
            ) {
                Row {
                    TransplantListItem(
                        // fixme:这里用gradle自动签日期会影响F-Droid构建后校验Smail代码，暂时还是手动标注吧 [issue#50]
                        overlineContent = { ScrollText(text = RELEASE_DATE) },
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
                    // 包体标识
                    /*
                    Dev开发版可能存在更多的日志、更差的性能、以及部分隐藏入口
                     */
                }
            }
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

private sealed class To {
    data class Screen(val destination: NavDestination) : To()
    data class Window(val window : FloatingWindow) : To()
}

@Composable
private fun UpdateItems(
    title : String,
    info : String? = null,
    to: To? = null,
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
    val navController = LocalNavControllerSafely.current
    val floatingController = LocalFloatingControllerSafely.current
    TransplantListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        trailingContent = {
            to?.let {
                if(navController != null && floatingController != null) {
                    FilledTonalIconButton(
                        onClick = {
                            when(it) {
                                is To.Screen -> navController.push(it.destination)
                                is To.Window -> floatingController.push(it.window)
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.arrow_forward),null)
                    }
                }
            }
        },
        leadingContent = { Icon(painter = painterResource(id = type.res), contentDescription = "") }
    )
}