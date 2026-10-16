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
import androidx.room.Update
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
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
import com.hfut.schedule.ui.nav.destination.ClassroomDestination
import com.hfut.schedule.ui.nav.destination.SettingsAppearanceDestination
import com.hfut.schedule.ui.nav.destination.SettingsConfigurationDestination
import com.hfut.schedule.ui.nav.destination.SettingsHuiXinPasswordDestination
import com.hfut.schedule.ui.nav.destination.SettingsShortcutEditDestination
import com.hfut.schedule.ui.nav.destination.TrackDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.container.component.base.SharedContainer
import com.xah.floating.util.LocalFloatingControllerSafely
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavControllerSafely
import kotlinx.coroutines.launch

private const val RELEASE_DATE = "2026-10-10"

@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    VersionInfoCard()
    // 适配iOS后版本号跳为5.0
    DividerTextExpandedWith(text = "新特性") {
//        UpdateItems("新增 允许在课程表显示日历账户的日程")
        UpdateItems("重构 创建日程时选择日期不再按范围，而是自由选择若干日期")
        UpdateItems("新增 支持克隆已有日程","编辑日程时右上角的复制图标，点击后会复制此日程信息到新建页面")//
        UpdateItems("新增 支持接入大模型帮助创建日程","位于右上角彩色图标",to = To.Screen(AddEventDestination()))
        // 重新引入历史记录，然后持久化到本地数据库，统计其次数。在查询中心顶部推荐并允许用户自定义固定
//        UpdateItems("重构 去掉翻页器，改为尾部追加")
//        UpdateItems("新增 课程表在使用背景时支持自定义深浅色模式","位于 选项-外观-课程表-深浅色",to = To.Screen(SettingsAppearanceDestination))
//        UpdateItems("优化 部分界面的显示")// ￥滚动
//        UpdateItems("新增 个人画像")
//        UpdateItems("新增 支持在全校培养方案中分享培养方案", to = To.Screen(AllProgramsDestination(false)))
//        UpdateItems("新增 支持将转专业列表生成长截图并保存")
//        UpdateItems("新增 适配体测平台、图书馆、指尖工大、智慧社区、学工系统、第二课堂支持以外地访问刷新登录","在刷新登陆状态页面勾选外地访问后即可")
//        UpdateItems("优化 以Api形式打开课程详情时的性能", type = UpdateType.PERFORMANCE)
//            UpdateItems("新增 支持楼层导向图的教室可以在课程表或聚焦快速查看目标教室的所在位置")
//            UpdateItems("优化 课程表捏合手势的灵敏度")
//            UpdateItems("新增 合工大教务课表支持写入到日历日程")
        // TODO WebView适配新库 [P2]
        // TODO 反射扫描destination文件夹
//            UpdateItems("发生Crash两次后再次进入app进入专属界面")
//            UpdateItems("新增 启动台与聚焦支持固定项目")
//            UpdateItems("新增 图书馆我的书架、收藏","位于 查询中心-图书馆")
//            UpdateItems("新增 图书馆斛兵知搜支持阅读电子书")
//            UpdateItems("新增 大模型","位于 查询中心-大模型,应用场景：通知公告的提炼、新增聚焦日程")
//            UpdateItems("新增 合工大教务接口的评教","位于 查询中心-评教")
//            UpdateItems("新增 开课查询数据源：合工大教务")
//            UpdateItems("新增 合肥校区电费的快速充值")
//            UpdateItems("回归 导入文件形式的课程表")
//            UpdateItems("新增 云端共建支持对上传的日程更新")
//            UpdateItems("新增 聚焦卡片小组件(4*2和2*1)")
//            UpdateItems("新增 校园网小组件(2*2)")
//            UpdateItems("新增 使用技巧","位于 选项-维护与关于") 新增 课程表交互说明指南
//            UpdateItems("新增 智慧社区的座位预约","位于 查询中心-社区预约")
//            UpdateItems("新增 自动CAS登录")
//            UpdateItems("新增 单独登录教务系统")
//            UpdateItems("新增 共建平台忘记密码、修改密码、注销")
//            UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//            UpdateItems("新增 本地聚焦卡片快速转化为云端卡片", null, UpdateType.ADD)
//            UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//            UpdateItems("新增 智慧后勤的登录")
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
        DividerTextExpandedWith("版本信息") {
            LargeCard(
                title = "版本 " + AppVersion.getVersionName()
            ) {
                Row {
                    TransplantListItem(
                        // fixme:这里用gradle自动签日期会影响F-Droid构建后校验Smail代码，暂时还是手动标注吧 [issue#50]
                        overlineContent = { ScrollText(text = RELEASE_DATE) },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.code), contentDescription = EMPTY_STRING) },
                        headlineContent = { Text(text = "版本号 ${AppVersion.getVersionCode()}") },
                        modifier = Modifier.weight(.5f)
                    )
                    AppVersion.getSplitType().let {
                        TransplantListItem(
                            overlineContent = { Text(text = if(it == AppVersion.SplitType.COMMON) "全量包" else "架构分包") },
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.package_2), contentDescription = EMPTY_STRING) },
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
                leadingContent = { Icon(painter = painterResource(id = type.res), contentDescription = EMPTY_STRING) },
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