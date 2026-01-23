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
                overlineContent = { Text(text = "2026-01-20") },
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
        // TODO 新增培养方案已修学分计算功能
            UpdateItems("重构 消息中心","新增 已读机制")//
            UpdateItems("重构 成绩详情界面适配新转场动画")

            UpdateItems("新增 课程汇总、教务课程表、合工大教务课程表支持保存多个学期的数据")//
            UpdateItems("新增 合工大教务接口的评教","位于 查询中心-评教")
            UpdateItems("新增 切换颜色时的渐变过渡")//
            UpdateItems("新增 空教室点击方块支持查看详细信息")//
            UpdateItems("新增 自定义学期开始时间","位于 选项-应用与配置-课程表配置，重写学期开始时间的逻辑，现在可以自己自定义了")//

            UpdateItems("修复 部分组件液态玻璃材质的瑕疵显示的Bug")//
            UpdateItems("修复 指间工大课程表下侧按钮阴影异常显示的Bug")//
            UpdateItems("修复 学期切换后课程表顶部日期不变的Bug")//
            UpdateItems("修复 一键评教所有教师失败的Bug","可以用，但是教务系统的接口本身有反爬机制，一键评教速度太快了。尝试通过合工大教务的评教接口吧")//

            UpdateItems("优化 冷启动的速度")//
            UpdateItems("优化 选项-应用与配置 中的归类")//
            UpdateItems("优化 成绩项目的显示效果","弱化挂科课程的显示、增加补考成绩的图标适配、对子成绩分条显示")//
            UpdateItems("优化 部分界面的显示","重构了部分多选一组件的样式")//

            UpdateItems("移除 下学期课程表","想获取任意学期包括下学期的课程表和课程汇总的，可以去设置-应用及配置-课程表配置-学期切换，切换到指定学期后重新刷新登陆状态")//
            UpdateItems("移除 上推卡片(BottomSheet)的背景模糊","考虑到可读性、全屏渲染时的性能开销、与某些新的逻辑出现配色冲突等因素，暂时移除，后续可能会恢复")//
            UpdateItems("移除 主界面选择功能")//

//            UpdateItems("新增 英文语言适配")
//            UpdateItems("优化 长按课程表展开选周预览界面的自适应布局")
            // 寒假计划：大模型应用场景：通知公告的提炼、新增聚焦日程
//            UpdateItems("新增 长按左上角返回图标打开启动台，快速切换到其他界面")
//            UpdateItems("修复 成绩界面横向滑动时卡顿的Bug")
//            UpdateItems("新增 新课程表的日视图")
//            UpdateItems("新增 选课页面中的通知公告快速检索入口")
//            UpdateItems("新增 启动台支持固定项目")
//            UpdateItems("修复 启动台开启后,上下滑动手势不灵敏的Bug")
//            UpdateItems("新增 开课查询数据源：合工大教务")
//            UpdateItems("修复 一卡通消费统计一直加载的Bug")
//            UpdateItems("修复 点击聚焦页面的日程后延迟响应的Bug")
//            UpdateItems("新增 图书馆我的书架、收藏、斛兵知搜","位于 查询中心-图书馆")
//            UpdateItems("新增 合肥校区电费的快速充值")

            // TODO 远期规划
//            UpdateItems("新增 提案板","用户可查看并关注开发进度，提出需求、Bug")
//            UpdateItems("新增 课程表的方格支持自动适应背景透明度")
//            UpdateItems("回归 导入文件形式的课程表")
//            UpdateItems("新增 云端共建支持对上传的日程更新")
//            UpdateItems("新增 聚焦卡片小组件(4*2和2*1)")
//            UpdateItems("新增 校园网小组件(2*2)")
//            UpdateItems("新增 使用技巧","位于 选项-维护与关于")
//            UpdateItems("新增 智慧社区的座位预约","位于 查询中心-社区预约")
//            UpdateItems("新增 自动CAS登录")
//            UpdateItems("新增 地图和校车支持为游客显示了")
//            UpdateItems("修复 窗口变化与深浅色切换而导致启动台自动收起的Bug")
//            UpdateItems("优化 冷启动速度", type = UpdateType.PERFORMANCE)
//            UpdateItems("新增 崩溃的自动处理")
//            UpdateItems("新增 为低版本Android用户的开屏显示")
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



