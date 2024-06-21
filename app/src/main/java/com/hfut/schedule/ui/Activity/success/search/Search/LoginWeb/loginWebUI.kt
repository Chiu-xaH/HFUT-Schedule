package com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.getWeb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode



@Composable
fun loginWebUI(vmUI : UIViewModel) {
    val memoryWeb = SharePrefs.prefs.getString("memoryWeb","0")
    val flow = vmUI.webValue.value?.flow ?: memoryWeb
    val bd = BigDecimal((flow?.toDouble() ?: 0.0) / 1024)
    val str = bd.setScale(2, RoundingMode.HALF_UP).toString()
    var textStatus by  remember { mutableStateOf("已用 ${flow}MB (${str}GB)\n余额 ￥${vmUI.webValue.value?.fee?: "0"}") }

    val bd2 = BigDecimal(((flow?.toDouble() ?: 0.0) / 20480) * 100)
    val precent = bd2.setScale(2, RoundingMode.HALF_UP).toString()
       // return str

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ){
        ListItem(
            headlineContent = { Text(text = "月免费额度 20GB") },
            trailingContent = { Text(text = "已用 ${precent}%")},
            leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {

            },
        )
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Icon(painter = painterResource(id = R.drawable.net), contentDescription = "",
            Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Text(text = textStatus, color = MaterialTheme.colorScheme.primary)
    }
    Spacer(modifier = Modifier.height(15.dp))

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
    getWeb(vmUI)



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
        Button(
            onClick = {
                vmUI.loginWeb()
               // vmUI.loginWeb2()
            },modifier = Modifier
                .scale(scale.value),
            interactionSource = interactionSource,) {
            Text(text = textLogin)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
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
        val resultFee = (fee1 / 100000).toString()
        val resultFlow : String = ((flow1 / 1024).toString() + flow3 + (flow0 / 1024)).substringBefore(".")

        return WebInfo(resultFee,resultFlow)
    } catch (e : Exception) {
        return WebInfo("未获取到数据","未获取到数据")
    }

}

data class WebInfo(val fee : String, val flow : String)