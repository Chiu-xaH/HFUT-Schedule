package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.StartAppIcon
import com.hfut.schedule.ui.component.button.StartAppIconButton
import com.hfut.schedule.ui.component.button.StartAppIconShare
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.DEFAULT
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.HIGH
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.LOW
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.MID
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAppNavController
import com.xah.uicommon.style.align.ColumnVertical

fun getLocation(campus : CampusRegion = getCampusRegion()) : String = when(campus) {
    CampusRegion.XUANCHENG -> "101221401"
    CampusRegion.HEFEI -> "101220101"
}

@Composable
fun LifeScreenMini(vm: NetWorkViewModel) {
    val context = LocalContext.current
    WeatherScreen(vm)
    DividerTextExpandedWith(text = "校园地图") {
        SchoolMapScreen(vm)
    }
    DividerTextExpandedWith("办事") {
        CardListItem(
            headlineContent = { Text(Starter.AppPackages.ANHUI_HALL.appName) },
            supportingContent = {
                Text("学校医保缴费、宣城市实时公交等功能")
            },
            modifier = Modifier.clickable {
                Starter.startAppLaunch(Starter.AppPackages.ANHUI_HALL,context)
            },
            leadingContent = {
                StartAppIcon(Starter.AppPackages.ANHUI_HALL)
            }
        )
    }
//    DividerTextExpandedWith("外部App") {
//        LazyRow {
//            items(
//                Starter.AppPackages.entries.size
//            ) { index ->
//                val item = Starter.AppPackages.entries[index]
//                Card(
//                    shape = MaterialTheme.shapes.medium,
//                    colors =  CardDefaults.cardColors(cardNormalColor()),
//                    modifier = Modifier.padding(end = CARD_NORMAL_DP)
//                ) {
//                }
//            }
//        }
//    }
//    DividerTextExpandedWith("楼层导向") { }
}

@Composable
private fun WeatherScreen(vm: NetWorkViewModel) {
    var campus by remember { mutableStateOf(getCampusRegion()) }
    val uiState by vm.qWeatherResult.state.collectAsState()
    val uiStateWarn by vm.weatherWarningData.state.collectAsState()
    val showWeather by DataStoreManager.enableShowFocusWeatherWarn.collectAsState(initial = false)

    var loading = uiState !is UiState.Success
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

    val cityName = when (campus) {
        CampusRegion.HEFEI -> "合肥"
        CampusRegion.XUANCHENG -> "宣城"
    } + "市"
    var data by remember { mutableStateOf( QWeatherNowBean("XX","XX","晴","X风","X","XX","XXX")) }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val response = (uiState as UiState.Success).data
            data = response
        }
    }
    Column {
        DividerTextExpandedWith(text = "实时天气",false) {
            LoadingLargeCard(
                prepare = false,
                title = data.text + " " + data.temp + "℃",
                loading = loading,
                leftTop = {
                    QWeatherIcon(data.icon.toIntOrNull())
                },
//            rightTop = {
//                Text(text = cityName)
//            }
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
                            OutlinedButton (onClick = {
                                campus = when(campus) {
                                    CampusRegion.HEFEI -> CampusRegion.XUANCHENG
                                    CampusRegion.XUANCHENG -> CampusRegion.HEFEI
                                }
                            }) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowLeft,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = cityName,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }
        if (uiStateWarn is UiState.Success) {
            val list = (uiStateWarn as UiState.Success).data
            if(list.isNotEmpty()) {
                DividerTextExpandedWith("气象预警") {
                    Column {
                        for(i in list) {
                            with(i) {
                                CardListItem(
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
        Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
        BottomTip("数据来源 和风天气")
    }
}

private enum class QWeatherLevel {
    HIGH,MID,LOW,DEFAULT
}

private fun humidityLevel(humidity : Int?) : QWeatherLevel = if (humidity != null) {
    if(humidity >= 70) HIGH
    else if(humidity in 0 until 50) LOW
    else if(humidity in 50 until 70) MID
    else DEFAULT
} else DEFAULT

@Composable
private fun HumidityIcons(level : QWeatherLevel) {
    when(level) {
        HIGH -> Icon(painterResource(id = R.drawable.humidity_high), contentDescription = null)
        MID -> Icon(painterResource(id = R.drawable.humidity_mid), contentDescription = null)
        LOW -> Icon(painterResource(id = R.drawable.humidity_low), contentDescription = null)
        DEFAULT -> Icon(painterResource(id = R.drawable.water_drop), contentDescription = null)
    }
}

@Composable
private fun QWeatherIcon(code: Int?) {
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
