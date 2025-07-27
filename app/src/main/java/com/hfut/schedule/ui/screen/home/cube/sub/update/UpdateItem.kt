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
import com.hfut.schedule.ui.component.container.MyCustomCard
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
                overlineContent = { Text(text = "2025-07-26") },
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
        MyCustomCard (containerColor = cardNormalColor()){
            UpdateItems("新增 教务成绩计算每学期的平均绩点与均分，以及可以自定义排除的课程")
            UpdateItems("新增 满绩成绩的礼花动画")//
            UpdateItems("新增 成绩小图标的更多适配")//
            UpdateItems("新增 为课表方格与查询中心的一级界面适配新的转场动画")//
            UpdateItems("回归 转场的预测式返回手势","目前还在探索新的方案，现在的预测式动画相比之前被削弱了")//
            UpdateItems("修复 成绩雷达图在五星制成绩显示为0的Bug")//
            UpdateItems("修复 校园邮箱在登陆后短时间不能保持登录的Bug")
            UpdateItems("修复 转专业申请日期错误的Bug")//
            UpdateItems("优化 部分界面的显示","审美就是轮回，个人信息界面又回到卡片设计了😂")//
        }


        // 当前版本
        // 未实现
//        UpdateItems("修复 好友课表之间无法切换的Bug")
//        UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//        UpdateItems("重构 提纯出CAS统一认证登录逻辑，为更多平台的一键登录做准备", null,UpdateType.RENEW)
//        UpdateItems("新增 本地聚焦卡片快速转化为云端卡片，一键共享本地卡片", null, UpdateType.ADD)
//        UpdateItems("新增 对共建平台已上传卡片的信息编辑", null, UpdateType.ADD)
//        UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//        UpdateItems("优化 添加聚焦卡片适用范围的添加班级逻辑", null, UpdateType.OPTIMIZE)
//        UpdateItems("优化 层级转场时的圆角", null, UpdateType.OPTIMIZE)
        // 下版本规划
//        UpdateItems("重构 网络请求层，重新进行封装，使用Flow代替LiveData，优化潜在的内存泄漏问题，增加不同状态下的展示", "进度: 剩余29")
//        UpdateItems("新增 一卡通-统计中消费预测与统计功能的本地化分析")
//        UpdateItems("新增 学工系统/今日校园的登录")

//        UpdateItems("修复 体测平台、报修打开白屏的Bug")
        // v5.0 2025-07+ 远期规划
//        UpdateItems("新增 磁钉体系", "位于 选项-应用行为，构建全局磁钉体系，任何支持的界面向边缘滑动即可缩放为磁钉最小化", UpdateType.ADD)
//        UpdateItems("重构 CAS登录", "完全重写底层，使其支持更多平台的边界接入，修复部分功能登陆失败的Bug、修复外地访问下无法使用邮箱等功能的Bug、修复偶见无法登录教务的Bug、优化刷新登陆状态后仍需等待较长时间才可操作的逻辑、支持对外开放API等", UpdateType.RENEW)
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

