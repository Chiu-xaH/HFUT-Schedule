package com.hfut.schedule.ui.screen.home.cube.sub

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.button.CARD_BOTTOM_BUTTON_SIZE
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.DepartmentsDestination
import com.hfut.schedule.ui.nav.destination.SettingsAppearanceDestination
import com.hfut.schedule.ui.nav.destination.SettingsDeepLinkDestination
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.nav.destination.TrackDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.sharednav.common.util.NoneRoundShape
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.container.component.base.SharedContainer
import com.xah.floating.util.LocalFloatingControllerSafely
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavControllerSafely
import kotlinx.coroutines.launch

private const val RELEASE_DATE = "2026-06-14"

@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    VersionInfoCard()
    DividerTextExpandedWith(text = "新特性") {
//        UpdateItems("新增 适配体测平台、图书馆、指尖工大、智慧社区、学工系统、第二课堂支持以外地访问刷新登录","在刷新登陆状态页面勾选外地访问后即可")
        UpdateItems("新增 深度链接的适配", "供外部应用与网页直接跳转至聚在工大的某些功能")//
        UpdateItems("新增 课程表在有背景时支持容器共享动画")//
        UpdateItems("新增 新增日程时，支持快速选择常用地点",to = To.Screen(AddEventDestination(null, VersionInfoDestination.key)))//
        UpdateItems("新增 智能软件工程学院的图标适配","原软件学院更名",to = To.Screen(DepartmentsDestination))//
        UpdateItems("重构 动画速率调整为统一的动画速率倍数调整",to = To.Screen(SettingsAppearanceDestination))//
        UpdateItems("修复 旧课程表在不使用背景的情况下，方格透明度仍生效的Bug")//
        UpdateItems("修复 部分课程从课程表方格进入课程详情后为空的问题")//
        UpdateItems("修复 合工大教务登录超时显示timeout的问题","再次延长超时时间至40s")//
        UpdateItems("优化 部分界面的显示")//

//        UpdateItems("优化 以Api形式打开课程详情时的性能", type = UpdateType.PERFORMANCE)
//
//            UpdateItems("新增 支持楼层导向图的教室可以在课程表或聚焦快速查看目标教室的所在位置")
//            UpdateItems("修复 在着色器效果关闭时容器共享转场时路径偏移的Bug")
//            UpdateItems("修复 部分设备使用图片验证码自动识别功能时崩溃的Bug")
//            UpdateItems("优化 课程表捏合手势的灵敏度")
//            UpdateItems("新增 课程表交互说明指南","位于 课程表切换菜单内")
//            UpdateItems("新增 合工大教务课表支持写入到日历日程")
//            UpdateItems("新增 适配若干二级界面为新的转场动画")
        // TODO 一卡通搜索，一卡通付款码，一卡通范围支出，一卡通慧新易校 适配新转场动画
        // TODO WebView适配新库 [P2]
        // TODO WebVpn、课程表界面动效掉帧率较高走查 [P2]
        // TODO Drawer重做  [P2]
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
//            UpdateItems("新增 共建平台忘记密码、修改密码、注销")
//            UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//            UpdateItems("新增 本地聚焦卡片快速转化为云端卡片", null, UpdateType.ADD)
//            UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//            UpdateItems("新增 智慧后勤的登录")
//        CustomCard (color = cardNormalColor()) {
//
//        }
        // Fuck 修不完的Bug 加不完的新功能 卷死的就业环境 笔事多的学校 ...
    }
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP), horizontalArrangement = Arrangement.Center) {
        val navController = LocalNavController.current
        SharedContainer(
            TrackDestination.key,
            containerColor = Color.Transparent,
            shape = NoneRoundShape,
        ) {
            Text(
                text = "想成为下个版本的贡献者或建言献策?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.push(TrackDestination)
                }
            )
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
    developers: List<String> = listOf(Constant.GITHUB_DEVELOPER_NAME),
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

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    CustomCard(
        color = cardNormalColor()
    ) {
        Column {
            TransplantListItem(
                headlineContent = { Text(text = title) },
                supportingContent = { info?.let { Text(text = it) } },
                leadingContent = { Icon(painter = painterResource(id = type.res), contentDescription = "") },
            )
            PaddingHorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow (modifier = Modifier.weight(1f).padding(horizontal = APP_HORIZONTAL_DP)) {
                    items(developers.size,key = { it }) { index ->
                        val item = developers[index]
                        UrlImage(
                            Constant.GITHUB_USER_IMAGE_URL + MyApplication.contributors[item],
                            shape = CircleShape,
                            enableClick = false,
                            modifier = Modifier
                                .padding(vertical = APP_HORIZONTAL_DP/2)
                                .padding(end = APP_HORIZONTAL_DP/2)
                                .size(30.dp)
                                .clickable {
                                    scope.launch {
                                        Starter.startWebUrlInner(context, Constant.GITHUB_URL + item)
                                    }
                                }
                        )
                    }
                }
                if(to != null && navController != null && floatingController != null) {
                    Text(
                        text = "立即体验",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = CARD_BOTTOM_BUTTON_SIZE,
                        modifier = Modifier
                            .padding(vertical = APP_HORIZONTAL_DP - 5.dp,horizontal = APP_HORIZONTAL_DP)
                            .clickable {
                                when(to) {
                                    is To.Screen -> navController.push(to.destination, effect = JumpTransitionEffectWallpaper())
                                    is To.Window -> floatingController.push(to.window)
                                }
                            }
                    )
                }
            }
//            if(to != null && navController != null && floatingController != null) {
//                BottomTextButtonGroup(
//                    listOf(
//                        CardBottomButton(
//                            "立即体验",
//                            show = true,
//                            clickable = {
//
//                            }
//                        )
//                    )
//                )
//            }
        }
    }
}