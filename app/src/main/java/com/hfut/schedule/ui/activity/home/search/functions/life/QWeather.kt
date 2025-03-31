package com.hfut.schedule.ui.activity.home.search.functions.life

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.QWeatherNowBean
import com.hfut.schedule.logic.beans.QWeatherResponse
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.activity.home.search.functions.life.QWeatherLevel.*
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.components.DevelopingUI
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.LoadingLargeCard
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

var countFunc = 0
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeUIS(vm : NetWorkViewModel) {
    val sheetState_Weather = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Weather by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(true) }

    if (showBottomSheet_Weather) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Weather = false },
            sheetState = sheetState_Weather,
            shape = bottomSheetRound(sheetState_Weather)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("生活服务")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    WeatherInfo(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val scale2 = animateFloatAsState(
        targetValue = if (loading) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loading) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
    )
    fun refreshWeather() {
        CoroutineScope(Job()).launch {
            async {
                loading = true
                reEmptyLiveDta(vm.weatherData)
            }.await()
            async{
                vm.getWeather()
                countFunc++
            }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.weatherData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("200")) {
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }
    if(countFunc == 0) {
        refreshWeather()
    }
    val cityName = if((getPersonInfo().school ?: "合肥").contains("宣城")) "宣城" else "合肥"
    val data = getWeather(vm)
    DividerTextExpandedWith(text = "天气预警",false) {
        LoadingLargeCard(
            title = data.text + " " + data.temp + "℃",
            loading = loading,
            leftTop = {
                QWeatherIcon(data.icon.toIntOrNull())
            },
            rightTop = {
                Text(text = cityName)
            }
        ) {
            Row {
                TransplantListItem(
                    headlineContent = { Text(text = data.feelsLike + "℃") },
                    overlineContent = { Text(text = "体感")},
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.temp_preferences_eco), contentDescription = null)
                    },
                    modifier = Modifier
                        .weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { Text(text = data.humidity + "%") },
                    overlineContent = { Text(text = "湿度")},
                    leadingContent = {
                        HumidityIcons(level = humidityLevel(data.humidity.toIntOrNull()))
                    },
                    modifier = Modifier
                        .weight(.5f)
                )
            }
            Row {
                TransplantListItem(
                    headlineContent = { Text(text = data.windScale + "级" ) },
                    overlineContent = { Text(text = data.windDir)},
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.air), contentDescription = null)
                    },
                    trailingContent = {
                        Button(onClick = {
                            showToast("正在开发")
//                            countWeather = 0
//                            showBottomSheet_Weather = true
                        }) {
                            Text(text = "天气详情")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
//        Card(
//            elevation = CardDefaults.cardElevation(defaultElevation = AppHorizontalDp()),
//            modifier = Modifier
//                .fillMaxWidth()
//                .scale(scale2.value)
//                .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
//            shape = MaterialTheme.shapes.medium,
//            colors = CardForListColor()
//        ) {
//            Column (modifier = Modifier
//                .blur(blurSize)
//                .scale(scale.value)){
//
//                ListItem(
//                    headlineContent = { Text(text = data.text + " " + data.temp + "℃", fontSize = 28.sp) },
//                    leadingContent = {
//                        QWeatherIcon(data.icon.toIntOrNull())
//                    },
//                    trailingContent = {
//                        Text(text = cityName)
//                    }
//                )
//                Row {
//                    ListItem(
//                        headlineContent = { Text(text = data.feelsLike + "℃") },
//                        overlineContent = { Text(text = "体感")},
//                        leadingContent = {
//                            Icon(painterResource(id = R.drawable.temp_preferences_eco), contentDescription = null)
//                        },
//                        modifier = Modifier
//                            .weight(.5f)
//                    )
//                    ListItem(
//                        headlineContent = { Text(text = data.humidity + "%") },
//                        overlineContent = { Text(text = "湿度")},
//                        leadingContent = {
//                            HumidityIcons(level = humidityLevel(data.humidity.toIntOrNull()))
//                        },
//                        modifier = Modifier
//                            .weight(.5f)
//                    )
//                }
//                Row {
//                    ListItem(
//                        headlineContent = { Text(text = data.windScale + "级" ) },
//                        overlineContent = { Text(text = data.windDir)},
//                        leadingContent = {
//                            Icon(painterResource(id = R.drawable.air), contentDescription = null)
//                        },
//                        trailingContent = {
//                            Button(onClick = {
//                                MyToast("正在开发")
////                            countWeather = 0
////                            showBottomSheet_Weather = true
//                            }) {
//                                Text(text = "天气详情")
//                            }
//                        },
//                        modifier = Modifier
//                            .weight(1f)
//                    )
//                }
//            }
//        }
        BottomTip("数据来源 和风天气")
    }


    DividerTextExpandedWith(text = "楼层导向") {
        DevelopingUI()
    }

    DividerTextExpandedWith(text = "校园地图") {
        DevelopingUI()
    }
}


fun getWeather(vm : NetWorkViewModel) : QWeatherNowBean {
    val json = vm.weatherData.value
    return try {
        Gson().fromJson(json, QWeatherResponse::class.java).now
    } catch (e: Exception) {
//        e.printStackTrace()
        QWeatherNowBean("XX","XX","晴","X风","X","XX","XXX")
    }
}


enum class QWeatherLevel {
    HIGH,MID,LOW,DEFAULT
}
fun humidityLevel(humidity : Int?) : QWeatherLevel {
    if (humidity != null) {
        return if(humidity >= 70) HIGH
        else if(humidity in 0 until 50) LOW
        else if(humidity in 50 until 70) MID
        else DEFAULT
    } else return DEFAULT
}

@Composable
fun HumidityIcons(level : QWeatherLevel) {
    when(level) {
        HIGH -> Icon(painterResource(id = R.drawable.humidity_high), contentDescription = null)
        MID -> Icon(painterResource(id = R.drawable.humidity_mid), contentDescription = null)
        LOW -> Icon(painterResource(id = R.drawable.humidity_low), contentDescription = null)
        DEFAULT -> Icon(painterResource(id = R.drawable.water_drop), contentDescription = null)
    }
}

@Composable
fun QWeatherIcon(code: Int?) {
    if (code != null) {
        val context = LocalContext.current
        val resourceName = "qweather$code"
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

        if (resourceId != 0) { // 确保资源存在
            Icon(
                painter = painterResource(id = resourceId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun WeatherInfo(vm: NetWorkViewModel) {

}