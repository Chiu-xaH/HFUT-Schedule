package com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@Composable
fun loginWebUI(vmUI : UIViewModel) {

    var textStatus by  remember { mutableStateOf("请确定已连接校园网") }
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = {
                vmUI.loginWeb()
                vmUI.loginWeb2()
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

    CoroutineScope(Job()).launch {
        Handler(Looper.getMainLooper()).post{
            vmUI.resultValue.observeForever { result ->
                if (result != null) {
                    if(result.contains("登录成功") && !result.contains("已使用")) {
                        vmUI.getWebInfo()
                        textLogin = "已登录"
                        textLogout = "注销"
                        textStatus = "已登录"
                    } else if(result == "Error") {
                        textStatus = "网络错误"
                    } else if(result.contains("已使用")) {
                        textLogout = "已注销"
                        textStatus = "已注销"
                        textLogin   = "登录"
                    }
                }
            }
            vmUI.infoValue.observeForever { result ->
                if (result != null) {
                   // Log.d("ew",result)
                  //  Log.d("sss0",result)
                    if(result.contains("flow")) {
                      //  Log.d("sss0",result)
                       textStatus = "已使用 ${getWebInfos(result).flow} MB\n余额 ${getWebInfos(result).fee}"
                    }
                }
            }
        }
    }
}

fun getWebInfos(html : String) : webInfo {

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

        return webInfo(resultFee,resultFlow)
    } catch (e : Exception) {
        return webInfo("未获取到数据","未获取到数据")
    }

}

data class webInfo(val fee : String,val flow : String)