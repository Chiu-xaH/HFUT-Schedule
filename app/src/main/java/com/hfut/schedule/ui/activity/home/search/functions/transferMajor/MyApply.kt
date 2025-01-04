package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.schoolIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@Composable
fun MyApply(vm: NetWorkViewModel) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    var loading2 by remember { mutableStateOf(true) }
    var refresh2 by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    val campus = if(getCampus()?.contains("宣城") == true) CampusId.XUANCHENG else CampusId.HEFEI

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let {
                vm.getMyApply(it,campus)
            } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.myApplyData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("转专业")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }

    if(refresh2) {
        loading2 = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let {
                vm.getMyApplyInfo(it)
            } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.myApplyInfoData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("面试安排")) {
                                loading2 = false
                                refresh2 = false
                            }
                        }
                    }
                }
            }
        }
    }


    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (loading) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loading) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
    )
    val status = getPersonInfo().status
    val data = getMyTransfer(vm)
    val isSuccessTransfer = isSuccessTransfer()
    DividerTextExpandedWith(text = "状态") {
        Box {

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale2.value)
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardForListColor()
            ) {

                Column (modifier = Modifier
                    .blur(blurSize)
                    .scale(scale.value)){

                    ListItem(headlineContent = { Text(text =
                    if(isSuccessTransfer)"恭喜 已转入"
                    else if(getApplyStatus(vm) == true) "学籍尚未变更"
                    else if(getApplyStatus(vm) == false) "未申请或申请不通过"
                    else "状态未知"
                        , fontSize = 28.sp) })

                    if(isSuccessTransfer) {
                        ListItem(
                            headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                            overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                            leadingContent = { getPersonInfo().department?.let { schoolIcons(it) } }
                        )
                    } else {
                        Row {
                            ListItem(
                                headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                                overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                                modifier = Modifier.weight(.4f)
                            )
                            ListItem(
                                headlineContent = { ScrollText(text = data.major.nameZh) },
                                overlineContent = { ScrollText(text = data.department.nameZh) },
                                leadingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                                modifier = Modifier.weight(.6f)
                            )
                        }
                        ListItem(
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
                            overlineContent = { ScrollText(text = "已申请/计划录取") },
                            headlineContent = { Text(text = "${data.applyStdCount} / ${data.preparedStdCount}", fontWeight = FontWeight.Bold ) },
                        )
                    }
                }
            }
        }
    }


    DividerTextExpandedWith("成绩") {
        if(loading2) {
            LoadingUI()
        } else {
            val bean = getMyTransferInfo(vm)
            val grade = bean?.grade

            if(!isSuccessTransfer) {
                val examSchedule = bean?.examSchedule
                val meetSchedule = bean?.meetSchedule

                if(examSchedule != null) {
                    ListItem(
                        headlineContent = { Text(examSchedule.place.replace("；","\n").replace("："," ").replace("。","")) },
                        supportingContent = { Text(examSchedule.time) },
                        overlineContent = { Text("笔试安排") }
                    )
                }
                if(meetSchedule != null) {
                    ListItem(
                        headlineContent = { Text(meetSchedule.place.replace("；","\n").replace("："," ")) },
                        supportingContent = { Text(meetSchedule.time) },
                        overlineContent = { Text("面试安排") }
                    )
                }
            }

            if(grade != null) {
                Row {
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                        overlineContent = { ScrollText(text = "绩点") },
                        headlineContent = { Text(text = "${grade.gpa.score}" ) },
                        supportingContent = {
                            Text("${grade.gpa.rank}/${data.applyStdCount} 名")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "") },
                        overlineContent = { ScrollText(text = "加权均分") },
                        headlineContent = { Text(text = "${grade.weightAvg.score}" ) },
                        supportingContent = {
                            Text("${grade.weightAvg.rank}/${data.applyStdCount} 名")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                }
                Row {
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                        overlineContent = { ScrollText(text = "转专业考核") },
                        headlineContent = { Text(text = "${grade.transferAvg.score}", fontWeight = FontWeight.Bold ) },
                        supportingContent = {
                            val rank = grade.transferAvg.rank
                            if(rank != null) {
                                Text("$rank/${data.applyStdCount} 名")
                            } else {
                                Text("教务无数据")
                            }
                        },
                        modifier = Modifier.weight(.5f)
                    )
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "") },
                        overlineContent = { ScrollText(text = "算术均分") },
                        headlineContent = { Text(text = "${grade.operateAvg.score}") },
                        supportingContent = {
                            Text("${grade.operateAvg.rank}/${data.applyStdCount} 名")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                }
            }
        }
    }


//    BottomTip("具体详情请一定关注QQ群、查询中心-通知公告、教务系统")
}

