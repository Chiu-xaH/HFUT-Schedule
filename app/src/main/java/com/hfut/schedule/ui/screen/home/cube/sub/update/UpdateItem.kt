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
                overlineContent = { Text(text = "2025-06-05") },
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
        UpdateItems("新增 登录/注销校园网 For 合肥校区", null, UpdateType.ADD)
        UpdateItems("新增 评教按课程归类教师", null, UpdateType.ADD)
        UpdateItems("修复 发送评教界面错位的Bug", null, UpdateType.FIX)
        UpdateItems("修复 共建平台不检查新增日程的Bug", null, UpdateType.FIX)
        UpdateItems("优化 培养方案搜索时的广度", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 成绩跳转评教的体验", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 登录/注销校园网提示失败但实际成功的问题", null, UpdateType.OPTIMIZE)
        UpdateItems("优化 添加聚焦日程按钮的转场动画",null, UpdateType.OPTIMIZE)
        UpdateItems("优化 部分界面的显示", null, UpdateType.OPTIMIZE)
//        UpdateItems("重构 网络请求层，重新进行封装，使用Flow代替LiveData，优化潜在的内存泄漏问题，增加不同状态下的展示", "进度: 完成60/总99",UpdateType.RENEW)
//        UpdateItems("重构 部分界面，使其适配平板、折叠屏等大屏设备", null, UpdateType.RENEW)
//        UpdateItems("优化 添加聚焦卡片适用范围的添加班级逻辑", null, UpdateType.OPTIMIZE)
//         UpdateItems("优化 部分列表快速滑动时首张卡片卡顿的问题", null, UpdateType.OPTIMIZE)
//        UpdateItems("优化 层级转场时的圆角", null, UpdateType.OPTIMIZE)
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
private fun UpdateItems(title : String,info : String?,type : UpdateType) {
    TransplantListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = type.res), contentDescription = "") }
    )
}


