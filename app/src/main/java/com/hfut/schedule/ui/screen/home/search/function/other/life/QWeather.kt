package com.hfut.schedule.ui.screen.home.search.function.other.life

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNow
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.html.FloorMap
import com.hfut.schedule.network.api.model.response.html.RoomRect
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.button.StartAppIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.nav.window.FloorMapWindow
import com.hfut.schedule.ui.screen.home.search.function.other.life.HumidityLevel.DEFAULT
import com.hfut.schedule.ui.screen.home.search.function.other.life.HumidityLevel.HIGH
import com.hfut.schedule.ui.screen.home.search.function.other.life.HumidityLevel.LOW
import com.hfut.schedule.ui.screen.home.search.function.other.life.HumidityLevel.MID
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.sharedContainer
import com.sharednav.common.helper.NoneRoundShape
import com.xah.floating.util.LocalFloatingController
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun getLocation(campus : CampusRegion = getCampusRegion()) : String = when(campus) {
    CampusRegion.XUANCHENG -> "101221401"
    CampusRegion.HEFEI -> "101220101"
}

@Composable
fun HuoZaiFeiXuan() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                Starter.startWebUrlInner(context,"https://survive-hfut.cc/intro", title = "活在肥宣")
            }
        }
    )
}

/*
天气 预警   ->    天气

校园地图 楼层导向     ->     校园

外部应用 新生      ->       外部
 */
@Composable
fun StarterScreen() {
    val context = LocalContext.current

    HuoZaiFeiXuan()
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
fun RoomMap(
    floor: FloorMap,
    modifier: Modifier = Modifier,
    selectedIds: Set<String> = emptySet(),
) {
    val selectedColor = Color(0x66D900F6)
//    val color by rememberInfiniteTransition(label = "blink")
//        .animateColor(
//            initialValue = Color.Transparent,
//            targetValue = selectedColor,
//            animationSpec = infiniteRepeatable(
//                animation = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED*2),
//                repeatMode = RepeatMode.Reverse
//            ),
//            label = "alpha"
//        )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(floor.width / floor.height)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val w = size.width
            val h = size.height

            floor.rooms.forEach { room ->

                val left = room.left * w
                val top = room.top * h
                val right = room.right * w
                val bottom = room.bottom * h

                val isSelected = room.id in selectedIds

                if (isSelected) {
                    drawRect(
                        color = selectedColor,
                        topLeft = Offset(left, top),
                        size = Size(right - left, bottom - top)
                    )
                }
            }
        }
    }
}

fun RoomRect.contains(offset: Offset, size: Size): Boolean {
    val x = offset.x / size.width
    val y = offset.y / size.height

    return x in left..right && y in top..bottom
}

@Composable
fun CampusMapScreen(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues
) {
    val floatingController = LocalFloatingController.current
    val uiState by vm.githubBuildingMapsResp.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.githubBuildingMapsResp.clear()
        vm.getBuildingMaps()
    }

    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as NetworkUiState.Success).data
        LazyColumn {
            item { InnerPaddingHeight(innerPadding,true) }
            items(list.size,key = { list[it].building.id} ) { index ->
                val item = list[index]
                val floors = item.detail.sortedBy { it.floor }
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = {
                            Text(item.building.nameZh)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.domain),null)
                        }
                    )
                    LazyRow(modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP)) {
                        item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        items(floors.size, key = { floors[it].floor }) { index ->
                            val item = floors[index]
                            val window = FloorMapWindow(item,vm)
                            NoPadding {
                                AssistChip(
                                    onClick = {
                                        floatingController.push(window)
                                    },
                                    shape = NoneRoundShape,
                                    border = null,
                                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                    label = { Text("${item.floor}F") },
                                    modifier = Modifier
                                        .padding(end = if(index == floors.size-1) 0.dp else CARD_NORMAL_DP*2)
                                        .sharedContainer(window.key, (AssistChipDefaults.shape as? CornerBasedShape) ?: MaterialTheme.shapes.small,MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }
                        }
                        item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                    }
                }
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }
}
@Composable
fun WeatherScreen(vm: NetWorkViewModel) {
    var campus by remember { mutableStateOf(getCampusRegion()) }
    val uiState by vm.qWeatherResult.state.collectAsState()
    val uiStateWarn by vm.weatherWarningData.state.collectAsState()

    val loading = uiState !is NetworkUiState.Success
    val refreshNetwork: suspend () -> Unit = {
        val showWeather = DataStoreManager.enableShowFocusWeatherWarn.first()
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
    var data by remember { mutableStateOf( QWeatherNow("XX","XX","晴","X风","X","XX","XXX")) }

    LaunchedEffect(uiState) {
        if (uiState is NetworkUiState.Success) {
            val response = (uiState as NetworkUiState.Success).data
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
        if (uiStateWarn is NetworkUiState.Success) {
            val list = (uiStateWarn as NetworkUiState.Success).data
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
        BottomTip("天气数据源为和风天气")
        DividerTextExpandedWith(text = TermReportDestination.title.asString()) {
            val navController = LocalNavController.current
            CardListItem(
                shape = NoneRoundShape,
                cardModifier = Modifier
                    .sharedContainer(
                        TermReportDestination.key,
                        MaterialTheme.shapes.medium,
                        cardNormalColor()
                    ),
                headlineContent = {
                    Text(TermReportDestination.title.asString())
                },
                leadingContent = {
                    Icon(
                        painterResource(TermReportDestination.icon),
                        null
                    )
                },
                supportingContent = {
                    Text("开启你的学期总结")
                },
                modifier = Modifier.clickable {
                    navController.push(TermReportDestination)
                }
            )
        }
        DividerTextExpandedWith(text = "校园地图") {
            SchoolMapScreen(vm)
        }
    }
}


/**
 * 湿度
 */
private enum class HumidityLevel {
    HIGH,MID,LOW,DEFAULT
}

private fun humidityLevel(humidity : Int?) : HumidityLevel = if (humidity != null) {
    if(humidity >= 70) HIGH
    else if(humidity in 0 until 50) LOW
    else if(humidity in 50 until 70) MID
    else DEFAULT
} else DEFAULT

@Composable
private fun HumidityIcons(level : HumidityLevel) {
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
        val resourceId = reflectResId(context,"qweather$code",ReflectType.DRAWABLE)

        resourceId?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

enum class ReflectType {
    DRAWABLE,STRING,DIMEN
}

/**
 * 以反射形式拿到资源
 */
fun reflectResId(context : Context, resName : String,type : ReflectType) : Int? {
    val result = context.resources.getIdentifier(resName, type.name.lowercase(), context.packageName)
    // 确保资源存在
    return if(result == 0) null else result
}