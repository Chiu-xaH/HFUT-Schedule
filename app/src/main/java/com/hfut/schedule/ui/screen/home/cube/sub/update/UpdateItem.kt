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
                overlineContent = { Text(text = "2025-05-14") },
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
//        UpdateItems("新增 登陆校园网的快捷按钮(Tile)", "仅宣城，位于系统的下拉控制中心", UpdateType.ADD)
        UpdateItems("重构 网络请求层，重新进行封装，使用Flow代替LiveData，优化潜在的内存泄漏问题，增加不同状态下的展示", "进度: 完成54/总101",UpdateType.RENEW)
        UpdateItems("新增 合肥校区寝室电费、洗浴、洗衣机的充值入口", "暂时性方案，有机会再适配", UpdateType.ADD)
        UpdateItems("修复 选课功能由于验证接口早期修复遗漏而导致可能除23级宣区用户无法使用的Bug", null, UpdateType.FIX)
        UpdateItems("修复 聚焦卡片点击操作对U校园AI版的适应", null, UpdateType.FIX)
        UpdateItems("优化 聚焦卡片点击操作对打开网页的判断逻辑", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 添加聚焦卡片的一些选项可输入多行文本", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 合肥校区与宣城校区之间的界面壁垒显示", "不再用学籍校区来区分显示功能的不同，因为可能存在暂时性的异地校区居住", UpdateType.OPTIMIZE)
//        UpdateItems("优化 添加聚焦卡片适用范围的添加班级逻辑", null, UpdateType.OPTIMIZE)
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

