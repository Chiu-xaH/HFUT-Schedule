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
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.LargeCard
import com.hfut.schedule.ui.component.TransplantListItem

@SuppressLint("SuspiciousIndentation")
@Composable
private fun VersionInfoCard() {
    LargeCard(
        title = "版本 " + AppVersion.getVersionName()
    ) {
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "2025-05-09") },
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
//        UpdateItems("新增 教务课程表导出为ics文件", "位于 课程表-多课表-写入日历日程",UpdateType.ADD)
//        UpdateItems("重构 提纯出CAS统一认证登录逻辑，为更多平台的一键登录做准备", null,UpdateType.RENEW)
//        UpdateItems("新增 本地聚焦卡片快速转化为云端卡片，一键共享本地卡片", null, UpdateType.ADD)
//        UpdateItems("新增 对共建平台已上传卡片的信息编辑", null, UpdateType.ADD)
        UpdateItems("重构 网络请求层，重新进行封装，使用Flow代替LiveData，优化潜在的内存泄漏问题，增加不同状态下的展示", "进度: 完成38/搁置12/总101",UpdateType.RENEW)
        UpdateItems("重构 聚焦卡片网课类型的逻辑","网课卡片只保留截止日期时间，并与日程类型卡片依照开始时间共同参与排序，两者再无明确界限", UpdateType.RENEW)
        UpdateItems("重构 节假日时绽放礼花动画", "解耦至APP端，自动根据节假日开启", UpdateType.RENEW)
        UpdateItems("新增 聚焦首页对本地聚焦卡片的排序", null, UpdateType.ADD)
        UpdateItems("新增 共建平台聚焦卡片的下载量显示", null, UpdateType.ADD)
        UpdateItems("新增 共建平台队聚焦卡片的搜索", null, UpdateType.ADD)
        UpdateItems("新增 共建平台上传卡片时附带本地添加的开关", null, UpdateType.ADD)
        UpdateItems("新增 气象预警", "位于 查询中心-生活服务 与 聚焦首页，仅在需要时显示", UpdateType.ADD)
        UpdateItems("重置 实时层级模糊和运动模糊的开关持久化存储","以修复安卓10及其以下用户升级之后运动模糊开关无法关闭的Bug", UpdateType.FIX)
        UpdateItems("优化 共建平台已贡献列表中对过期项目的特别显示", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 共建平台重复下载聚焦日程的逻辑问题", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 添加网课类型聚焦卡片时的日期时间选择机制", null, UpdateType.OPTIMIZE)
//        UpdateItems("优化 部分列表快速滑动时首张卡片卡顿的问题", null, UpdateType.OPTIMIZE)
//        UpdateItems("优化 层级转场时的圆角", null, UpdateType.OPTIMIZE)
    }
}

private enum class UpdateType(val res : Int) {
    //新增
    ADD(R.drawable.add_2),
    //下线
    DEGREE(R.drawable.do_not_disturb_on),
    //优化
    OPTIMIZE(R.drawable.tune),
    //修复
    FIX(R.drawable.build),
    //重构
    RENEW(R.drawable.alt_route),
    //其他
    OTHER(R.drawable.stacks),
    UPDATE(R.drawable.arrow_upward)
}

@Composable
private fun UpdateItems(title : String,info : String?,type : UpdateType) {
    TransplantListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = updateTypeIcons(type)), contentDescription = "") }
    )
}

private fun updateTypeIcons(type : UpdateType) = type.res

