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
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem

@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + AppVersion.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-07-17") },
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
        UpdateItems("重构 所有设置界面的设计风格")
        UpdateItems("新增 本科招生","位于 查询中心")
        UpdateItems("新增 网页支持分享、收藏链接")
        UpdateItems("新增 网页收藏夹","支持收藏一些通知公告和自行添加，位于 查询中心-网址导航")
        UpdateItems("新增 开学倒计时","位于 查询中心-作息")
        UpdateItems("重构 分割线的设计风格","分割线保留屏幕边界")
        UpdateItems("修复 上版本导致查询中心搜索无法使用的Bug")
        UpdateItems("优化 校园网登录磁贴的文字在已知华为设备显示不全的问题")
        UpdateItems("优化 查询中心搜索框支持关闭")
        UpdateItems("优化 校历的位置","原 查询中心-校历 迁移至 查询中心-作息 的右上角")
//        UpdateItems("修复 邮箱未登录的Bug")
//        UpdateItems("新增 开发者选项","可供有能力者使用某些功能")
//        UpdateItems("修复 体测平台、报修打开白屏的Bug")
        // v5.0 2025-07+ 远期规划
//        UpdateItems("新增 磁钉体系", "位于 选项-应用行为，构建全局磁钉体系，任何支持的界面向边缘滑动即可缩放为磁钉最小化", UpdateType.ADD)
//        UpdateItems("重构 CAS登录", "完全重写底层，使其支持更多平台的边界接入，修复部分功能登陆失败的Bug、修复外地访问下无法使用邮箱等功能的Bug、修复偶见无法登录教务的Bug、优化刷新登陆状态后仍需等待较长时间才可操作的逻辑、支持对外开放API等", UpdateType.RENEW)
//        UpdateItems("重构 全局转场动画体系", "现在，转场时的动画完全重写，优化整体观感，并优化首次展开时动画卡顿的问题", UpdateType.RENEW)
//        UpdateItems("新增 动画速度挡位调节", null, UpdateType.ADD)
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
    UPDATE(R.drawable.arrow_upward)
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
        else -> UpdateType.OTHER
    }
) {
    TransplantListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = type.res), contentDescription = "") }
    )
}

