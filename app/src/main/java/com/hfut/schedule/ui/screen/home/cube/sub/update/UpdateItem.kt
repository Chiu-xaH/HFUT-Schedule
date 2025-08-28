package com.hfut.schedule.ui.screen.home.cube.sub.update

import android.annotation.SuppressLint
import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + AppVersion.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-08-28") },
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
            UpdateItems("重构 重写刷新登录状态时的慧新易校、信息门户及智慧社区的登录逻辑","本轮又对早期版本遗留的登录代码进行了重写,改善了登录过程的体验")
            UpdateItems("新增 教务课表备用数据源")
            UpdateItems("新增 挂失、解挂入口","位于 查询中心-一卡通-卡包-状态")
            UpdateItems("新增 修改一卡通和校园网的密码入口","位于 查询中心-一卡通-卡包-修改密码 和 选项-网络-一卡通密码")
            UpdateItems("修复 信息门户登录成功的提醒过于频繁的Bug")
            UpdateItems("修复 有庆祝日时首页错位的Bug")
            UpdateItems("优化 庆祝礼花的效果")
            UpdateItems("优化 课程表方格课程过长的溢出显示")

//            UpdateItems("修复 登录教务时首次提示失败的Bug")
//            UpdateItems("新增 慧新易校的课表数据源")
//            UpdateItems("新增 转场时的形变动画")
//            UpdateItems("新增 教务成绩计算每学期的平均绩点与均分，以及可以自定义排除的课程")
//            UpdateItems("新增 共建平台忘记密码、修改密码、注销")
//            UpdateItems("新增 为部分场景适配新的转场动画")

            /*
                        转专业二级界面
            就业二级界面 通知公告二级界面
            教师检索二级界面 课程详情查教师三级界面
            开课查询二级界面 课程汇总二级界面
            课程详情查挂科率三级界面 挂科率二级界面
            一卡通搜索，一卡通付款码，一卡通范围支出，一卡通慧新易校

             */
//            UpdateItems("下线 从外部文件导入课表的功能","后期重构完成后回归")
//            UpdateItems("下线 成绩-统计","后期重构完成后回归")
            // 未实现
//        UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//        UpdateItems("新增 本地聚焦卡片快速转化为云端卡片，一键共享本地卡片", null, UpdateType.ADD)
//        UpdateItems("新增 对共建平台已上传卡片的信息编辑", null, UpdateType.ADD)
//        UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//        UpdateItems("优化 添加聚焦卡片适用范围的添加班级逻辑", null, UpdateType.OPTIMIZE)
//
            // 下版本规划
//        UpdateItems("重构 网络请求层，重新进行封装，使用Flow代替LiveData，优化潜在的内存泄漏问题，增加不同状态下的展示", "进度: 剩余29")
//        UpdateItems("新增 一卡通-统计中消费预测与统计功能的本地化分析")
//        UpdateItems("新增 学工系统/今日校园的登录")
//        UpdateItems("修复 体测平台、报修打开白屏的Bug")
            // v5.0 2025-07+ 远期规划
//        UpdateItems("重构 CAS登录", "完全重写底层，使其支持更多平台的边界接入，修复部分功能登陆失败的Bug、修复外地访问下无法使用邮箱等功能的Bug、修复偶见无法登录教务的Bug、优化刷新登陆状态后仍需等待较长时间才可操作的逻辑、支持对外开放API等", UpdateType.RENEW)
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
    COME_BACK(R.drawable.rotate_right)
}

@Composable
private fun UpdateItems(
    title : String,
    info : String? = null,
    type : UpdateType = when(title.substringBefore(" ")) {
        "新增" -> UpdateType.ADD
        "重构" -> UpdateType.RENEW
        "重写" -> UpdateType.RENEW
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

