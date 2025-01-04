package com.hfut.schedule.ui.activity.home.search.functions.loginWeb

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.activity.home.cube.items.subitems.getWebNew
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode



@Composable
fun loginWebUI(vmUI : UIViewModel,vm : NetWorkViewModel) {
    val memoryWeb = SharePrefs.prefs.getString("memoryWeb","0")
    val flow = vmUI.webValue.value?.flow ?: memoryWeb
    val str = try {
        ReservDecimal.reservDecimal((flow?.toDouble() ?: 0.0) / 1024,2)
    } catch (_:Exception) {
        0.0
    }
    var textStatus by  remember { mutableStateOf("已用 ${flow}MB (${str}GB)\n余额 ￥${vmUI.webValue.value?.fee?: "0"}") }

    val bd2 = BigDecimal(((flow?.toDouble() ?: 0.0) / (1024 * MyApplication.maxFreeFlow)) * 100)
    val precent = bd2.setScale(2, RoundingMode.HALF_UP).toString()
       // return str

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    var textLogin by  remember { mutableStateOf("登录") }
    var textLogout by  remember { mutableStateOf("注销") }
    vmUI.getWebInfo()
    getWebNew(vm,vmUI)
    DividerTextExpandedWith(text = "账户数据") {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            val scrollState = rememberScrollState()
            LaunchedEffect(key1 = textStatus.substringBefore("\n") ) {
                delay(500L)
                scrollState.animateScrollTo(scrollState.maxValue)
                delay(4000L)
                scrollState.animateScrollTo(0)
            }


            ListItem(
                headlineContent = {
                    Text(
                        text = "已用 ${str} GB",
                        fontSize = 28.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.horizontalScroll(scrollState))
                },
                trailingContent = {
                    Text(text = "$flow MB")
                }
            )
            ListItem(
                headlineContent = { Text(text = "余额 ￥${vmUI.webValue.value?.fee?: "0"}") },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")},
                trailingContent = {
                    FilledTonalButton(
                        onClick = {
                            vmUI.loginWeb()
                            vmUI.loginWeb2()
                        },modifier = Modifier
                            .scale(scale.value),
                        interactionSource = interactionSource,) {
                        Text(text = textLogin)
                    }
                }
            )
            Row {
                ListItem(
                    modifier = Modifier.weight(.5f),
                    overlineContent = { Text(text = "月免费额度 50GB") },
                    headlineContent = { Text(text = "已用 ${precent} %", fontWeight = FontWeight.Bold)},
                    leadingContent = { Icon(painterResource(R.drawable.percent), contentDescription = "Localized description",) },
                    trailingContent = {
                        FilledTonalButton (
                            onClick = {
                                vmUI.logoutWeb()
                            },
                            //modifier = Modifier
                            //  .scale(scale.value),
                            //interactionSource = interactionSource,
                        ) {
                            Text(text = textLogout)
                        }
                    }
                )
            }
        }
    }

    DividerTextExpandedWith(text = "使用说明") {
        ListItem(
            headlineContent = { Text(text = "WLAN连接'hfut-wlan'后自动弹出认证") },
            supportingContent = { Text(text = "校区内两大校园网中心位于图书馆、教室，WLAN质量最好") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.wifi_tethering), contentDescription = "")
            }
        )
        ListItem(
            headlineContent = { Text(text = "宿舍连接缆线后自动弹出认证") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.lan), contentDescription = "")
            }
        )
        ListItem(
            headlineContent = { Text(text = "认证初始密码位于 查询中心-个人信息") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.key), contentDescription = "")
            }
        )
        ListItem(
            headlineContent = { Text(text = "部分内网必须连接校园网打开") },
            supportingContent = {
                Text(text = "学校提供WEBVPN供外网访问部分内网地址,可在 查询中心-网址导航 打开")
            },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.vpn_key), contentDescription = "")
            }
        )
        ListItem(
            headlineContent = { Text(text = "免费时期") },
            supportingContent = {
                Text(text = "法定节假日与寒暑假均不限额度")
            },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")
            }
        )
    }




    CoroutineScope(Job()).launch {
        Handler(Looper.getMainLooper()).post{
            vmUI.resultValue.observeForever { result ->
                if (result != null) {
                    if(result.contains("登录成功") && !result.contains("已使用")) {
                        vmUI.getWebInfo()
                        textLogin = "已登录"
                        textLogout = "注销"
                       // textStatus = "已登录"
                    } else if(result == "Error") {
                        textStatus = "网络错误"
                    } else if(result.contains("已使用")) {
                        textLogout = "已注销"
                      //  textStatus = "已注销"
                        textLogin   = "登录"
                    }
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

        Spacer(modifier = Modifier.width(10.dp))

    }
}

fun getWebInfos(html : String) : WebInfo {

    try {
        //本段照搬前端
        val flow = html.substringAfter("flow").substringBefore(" ").substringAfter("'").toDouble()
        val fee = html.substringAfter("fee").substringBefore(" ").substringAfter("'").toDouble()
        var flow0 = flow % 1024
        val flow1 = flow - flow0
        flow0 *= 1000
        flow0 -= flow0 % 1024
        var fee1 = fee - fee % 100
        var flow3 = "."
        if (flow0 / 1024 < 10) flow3 = ".00"
        else { if (flow0 / 1024 < 100) flow3 = ".0"; }
        val resultFee = (fee1 / 10000).toString()
        val resultFlow : String = ((flow1 / 1024).toString() + flow3 + (flow0 / 1024)).substringBefore(".")

        return WebInfo(resultFee,resultFlow)
    } catch (e : Exception) {
        return WebInfo("未获取到数据","未获取到数据")
    }

}

data class WebInfo(val fee : String, val flow : String)