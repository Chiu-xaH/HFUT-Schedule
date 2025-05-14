package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CustomTabRow
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.LoadingLargeCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.DEFAULT
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.HIGH
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.LOW
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.MID
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import java.nio.file.WatchEvent

fun getLocation(campus : Campus = getCampus()) : String = when(campus) {
    Campus.XUANCHENG -> "101221401"
    Campus.HEFEI -> "101220101"
}
var countFunc = 0
private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1


@Composable
fun WeatherScreen(vm: NetWorkViewModel) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )
    val titles = remember { listOf("合肥","宣城") }
    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { page ->
        Column (modifier = Modifier.fillMaxSize()) {
            LifeUIS(vm, campus = when(page) {
                HEFEI_TAB -> Campus.HEFEI
                XUANCHENG_TAB -> Campus.XUANCHENG
                else -> getCampus()
            })
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LifeUIS(vm : NetWorkViewModel,campus: Campus) {

    val sheetState_Weather = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Weather by remember { mutableStateOf(false) }
    val uiState by vm.qWeatherResult.state.collectAsState()
    val uiStateWarn by vm.weatherWarningData.state.collectAsState()
    val showWeather by DataStoreManager.showFocusWeatherWarn.collectAsState(initial = true)

    var loading = uiState !is SimpleUiState.Success
    val refreshNetwork: suspend () -> Unit = {
        if(!showWeather) {
            vm.weatherWarningData.clear()
            vm.getWeatherWarn(campus)
        }
        vm.qWeatherResult.clear()
        vm.getWeather(campus)
    }
    //预加载
    LaunchedEffect(campus) {
        refreshNetwork()
    }

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


    val cityName = remember { when (campus) {
        Campus.HEFEI -> "合肥"
        Campus.XUANCHENG -> "宣城"
    } }
    var data by remember { mutableStateOf( QWeatherNowBean("XX","XX","晴","X风","X","XX","XXX")) }

    LaunchedEffect(uiState) {
        if (uiState is SimpleUiState.Success) {
            val response = (uiState as SimpleUiState.Success).data
            response?.let {
                data = it
            }
        }
    }


    DividerTextExpandedWith(text = "实时天气",false) {
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
    }
    if (uiStateWarn is SimpleUiState.Success) {
        val list = (uiStateWarn as SimpleUiState.Success).data
        if(list.isNotEmpty()) {
            DividerTextExpandedWith("气象预警") {
                LazyColumn {
                    items(list.size, key = { it }) { index ->
                        with(list[index]) {
                            StyleCardListItem(
                                headlineContent = { Text(title) },
                                supportingContent = { Text(text) },
                                overlineContent = { Text(typeName) },
                                leadingContent = { Icon(painterResource(R.drawable.warning),null)}
                            )
                        }
                    }
                }
            }
        }
    }

    BottomTip("数据来源 和风天气")

//    DividerTextExpandedWith(text = "楼层导向") {
//        DevelopingUI()
//    }
//
//    DividerTextExpandedWith(text = "校园地图") {
//        DevelopingUI()
//    }
}


enum class QWeatherLevel {
    HIGH,MID,LOW,DEFAULT
}
fun humidityLevel(humidity : Int?) : QWeatherLevel = if (humidity != null) {
    if(humidity >= 70) HIGH
    else if(humidity in 0 until 50) LOW
    else if(humidity in 50 until 70) MID
    else DEFAULT
} else DEFAULT

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