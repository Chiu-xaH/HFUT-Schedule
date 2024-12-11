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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.CardForListColor
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun MyApply(vm: NetWorkViewModel) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

    val campus = if(getCampus()?.contains("宣城") == true) CampusId.XUANCHENG else CampusId.HEFEI

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let { vm.getMyApply(it,campus)} }.await()
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

    DividerText(text = "我的申请")
    Box {

        val data = getMyTransfer(vm)



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

                        ListItem(headlineContent = { Text(text = if(getApplyStatus(vm) == true) "转入申请已通过" else " 状态未知或未通过", fontSize = 28.sp) })

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

                        Row {
                            ListItem(
                                leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
                                overlineContent = { ScrollText(text = "已申请/计划录取") },
                                headlineContent = { Text(text = "${data.applyStdCount} / ${data.preparedStdCount}", fontWeight = FontWeight.Bold ) },
                                modifier = Modifier.weight(.5f)
                            )
                            ListItem(
                                leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
                                overlineContent = { ScrollText(text = "考核成绩") },
                                headlineContent = { Text(text = "XX 第X名", fontWeight = FontWeight.Bold ) },
                                modifier = Modifier.weight(.5f)
                            )
                        }
                    Row {
                        ListItem(
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                            overlineContent = { ScrollText(text = "绩点") },
                            headlineContent = { Text(text = "X.X 第X名", fontWeight = FontWeight.Bold ) },
                            modifier = Modifier.weight(.5f)
                        )
                        ListItem(
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.hive), contentDescription = "") },
                            overlineContent = { ScrollText(text = "均分") },
                            headlineContent = { Text(text = "XX 第X名", fontWeight = FontWeight.Bold ) },
                            modifier = Modifier.weight(.5f)
                        )
                    }
                }
            }
    }

}


