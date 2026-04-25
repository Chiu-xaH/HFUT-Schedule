package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.button.StartAppIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.DEFAULT
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.HIGH
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.LOW
import com.hfut.schedule.ui.screen.home.search.function.other.life.QWeatherLevel.MID
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.launch

fun getLocation(campus : CampusRegion = getCampusRegion()) : String = when(campus) {
    CampusRegion.XUANCHENG -> "101221401"
    CampusRegion.HEFEI -> "101220101"
}

enum class BuildingMapItem(val title : String) {
    JING_TING("敬亭学堂"),
    XIN_AN("新安学堂")
}


/*
天气 预警   ->    天气

校园地图 楼层导向     ->     校园

外部应用 新生      ->       外部
 */
@Composable
fun StarterScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    CardListItem(
        headlineContent = {
            Text("活在肥宣")
        },
        supportingContent = {
            Text("非官方新生指南，转载")
        },
        leadingContent = {
            StartAppIcon(R.drawable.net)
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(context,"https://survive-hfut.cc/intro", title = "活在肥宣")
            }
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.ANHUI_HALL.appName) },
        supportingContent = {
            Text("医保缴费、宣城市实时公交等功能")
        },
        modifier = Modifier.clickable {
            Starter.startAppLaunch(Starter.AppPackages.ANHUI_HALL,context)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.ANHUI_HALL)
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.PDD.appName) },
        supportingContent = {
            Text("拼多多身份码，校区快递站用")
        },
        modifier = Modifier.clickable {
            Starter.startPddExpress(context)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.PDD)
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.TAO_BAO.appName) },
        supportingContent = {
            Text("淘宝身份码，合肥校区快递站用")
        },
        modifier = Modifier.clickable {
            Starter.startTaoBaoExpress(context)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.TAO_BAO)
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.ALIPAY.appName) },
        supportingContent = {
            Text("校园卡缴费")
        },
        modifier = Modifier.clickable {
            Starter.startAppUrl(context, Constant.ALIPAY_CARD_URL, Starter.AppPackages.ALIPAY.appName)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.ALIPAY)
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.ALIPAY.appName) },
        supportingContent = {
            Text("海乐生活热水机")
        },
        modifier = Modifier.clickable {
            Starter.startAppUrl(context, Constant.ALIPAY_HOT_WATER_URL, Starter.AppPackages.ALIPAY.appName)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.ALIPAY)
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.LE_PAO.appName) },
        supportingContent = {
            Text("大一大二校园跑")
        },
        modifier = Modifier.clickable {
            Starter.startAppLaunch(Starter.AppPackages.LE_PAO,context)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.LE_PAO)
        }
    )
    CardListItem(
        headlineContent = { Text(Starter.AppPackages.TODAY_CAMPUS.appName) },
        supportingContent = {
            Text("节假日离返校、心理测试等")
        },
        modifier = Modifier.clickable {
            Starter.startAppLaunch(Starter.AppPackages.TODAY_CAMPUS,context)
        },
        leadingContent = {
            StartAppIcon(Starter.AppPackages.TODAY_CAMPUS)
        }
    )
}


@Composable
fun CampusMapScreen(vm: NetWorkViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    DividerTextExpandedWith(text = "校园地图") {
        SchoolMapScreen(vm)
    }
    DividerTextExpandedWith("楼层导向") {
        val list = remember { BuildingMapItem.entries }
        val titles = remember { list.map { it.title } }
        val pagerState = rememberPagerState(pageCount = { list.size })

        CardListItem(
            headlineContent = {
                Text("提示")
            },
            supportingContent = {
                Text("仅收录宣城校区的敬亭学堂与新安学堂，两栋教学楼设计比较复杂，感兴趣可点击y阅读文章：《合肥工业大学宣城二期教学楼——徽派文化元素的探索 / 华南理工大学建筑设计研究院陶郅工作室》")
            },
            leadingContent = {
                Icon(painterResource(R.drawable.info),null)
            },
            modifier = Modifier.clickable {
                scope.launch {
                    Starter.startWebView(context,"https://www.archcollege.com/39655.html", title = "合肥工业大学宣城二期教学楼——徽派文化元素的探索")
                }
            }
        )
        CustomTabRow(pagerState,titles)
        HorizontalPager(state = pagerState) { page ->
            DevelopingIcon()
        }
    }
}
@Composable
fun WeatherScreen(vm: NetWorkViewModel) {
    var campus by remember { mutableStateOf(getCampusRegion()) }
    val uiState by vm.qWeatherResult.state.collectAsState()
    val uiStateWarn by vm.weatherWarningData.state.collectAsState()
    val showWeather by DataStoreManager.enableShowFocusWeatherWarn.collectAsState(initial = false)

    val loading = uiState !is UiState.Success
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
                                         painterResource(R.drawable.keyboard_arrow_left),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = cityName,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Icon(
                                         painterResource(R.drawable.keyboard_arrow_right),
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
