package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric


import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampus
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.HuiXinHefeiBuildingBean
import com.hfut.schedule.logic.model.huixin.FeeResponse
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.network.state.reEmptyLiveDta
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.HefeiElectricStorage
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.getHefeiElectric
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.input.WheelPicker
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.MenuChip
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1


private fun getUrl(page : Int) : String {
    val auth = prefs.getString("auth","")
    return  MyApplication.HUI_XIN_URL +
            "charge-app/?name=pays&appsourse=ydfwpt&id=${
                if(page == XUANCHENG_TAB)
                    FeeType.ELECTRIC_XUANCHENG.code
                else 
                    FeeType.ELECTRIC_HEFEI_UNDERGRADUATE.code
            }&name=pays&paymentUrl=${MyApplication.HUI_XIN_URL}plat&token=" + auth
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EleUI(vm : NetWorkViewModel, hazeState: HazeState) {
    val titles = remember { listOf("合肥","宣城") }
    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampusRegion()) {
            CampusRegion.XUANCHENG -> XUANCHENG_TAB
            CampusRegion.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")

    val SavedBuildNumber = prefs.getString("BuildNumber", "0") ?: "0"
    var BuildingsNumber by remember { mutableStateOf(SavedBuildNumber) }
    val SavedRoomNumber = prefs.getString("RoomNumber", "")
    var RoomNumber by remember { mutableStateOf(SavedRoomNumber ?: "") }
    val SavedEndNumber = prefs.getString("EndNumber", "")
    var EndNumber by remember { mutableStateOf(SavedEndNumber ?: "") }

    var region by remember { mutableStateOf("选择南北") }

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"

    var showitem by remember { mutableStateOf(false) }
    var showitem2 by remember { mutableStateOf(false) }
    var showitem3 by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }

    var Result by remember { mutableStateOf("") }
    var Result2 by remember { mutableStateOf("") }

    var showAdd by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    var show by remember { mutableStateOf(false) }

    var json by remember { mutableStateOf("") }
    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            autoShape = false,
            hazeState = hazeState
        ) {
                Column(

                ) {
                    HazeBottomSheetTopBar("支付订单确认", isPaddingStatusBar = false)
                    val roomInfo by remember { mutableStateOf("${BuildingsNumber}号楼${RoomNumber}寝室${region}") }
                    val int by remember { mutableStateOf(payNumber.toFloat()) }
                    if(int > 0) {
                        PayFor(vm,int,roomInfo,json,FeeType.ELECTRIC_XUANCHENG,hazeState)
                    } else showToast("输入数值")
                }
        }
    }

    region = when(EndNumber) {
        "11"-> if(BuildingsNumber.toInt() > 5 )"南边照明" else "南边"
        "12" -> "南边空调"
        "21" -> if(BuildingsNumber.toInt() > 5 )"北边照明" else "北边"
        "22" -> "北边空调"
        else -> "选择南北"
    }

    var showDialog2 by remember { mutableStateOf(false) }
    if(showDialog2) {
        Dialog(onDismissRequest = { showDialog2 = false }) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedCard {
                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                            item {
                                Text(
                                    text = "选取金额 ￥${payNumber}",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        IconButton(onClick = {
                                            if (payNumber.length < 3)
                                                payNumber += items.toString()
                                            else Toast.makeText(
                                                MyApplication.context,
                                                "最高999元",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) { Text(text = items.toString()) }
                                    }
                                }
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        val num = items + 5
                                        IconButton(onClick = {
                                            if (payNumber.length < 3)
                                                payNumber += num
                                            else Toast.makeText(
                                                MyApplication.context,
                                                "最高999元",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) { Text(text = num.toString()) }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledTonalIconButton(
                        onClick = { payNumber = payNumber.replaceFirst(".$".toRegex(), "") },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.backspace),
                            contentDescription = "description"
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            showDialog2 = false
                            if (payNumber != "" && payNumber != "0" && payNumber != "00" && payNumber != "000")
                                showBottomSheet = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "description")
                    }

                    FilledTonalButton(
                        onClick = {
                            showDialog2 = false
                            payNumber = "0.01"
                            showBottomSheet = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Text("尝试充值0.01")
                    }
                }
            }
        }
    }



    var menuOffset by remember { mutableStateOf<DpOffset?>(null) }


    menuOffset?.let {
        DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "北一号楼") }, onClick = { BuildingsNumber =  "1"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北二号楼") }, onClick = {  BuildingsNumber =  "2"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北三号楼") }, onClick = {  BuildingsNumber =  "3"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北四号楼") }, onClick = {  BuildingsNumber =  "4"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北五号楼") }, onClick = {  BuildingsNumber =  "5"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南六号楼") }, onClick = {  BuildingsNumber =  "6"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南七号楼") }, onClick = {  BuildingsNumber =  "7"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南八号楼") }, onClick = {  BuildingsNumber =  "8"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南九号楼") }, onClick = {  BuildingsNumber =  "9"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南十号楼") }, onClick = {  BuildingsNumber = "10"
                showitem = false})
        }
        DropdownMenu(expanded = showitem2, onDismissRequest = { showitem2 = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "南边照明") }, onClick = { EndNumber = "11"
                showitem2 = false})
            DropdownMenuItem(text = { Text(text = "南边空调") }, onClick = { EndNumber = "12"
                showitem2 = false})
            DropdownMenuItem(text = { Text(text = "北边照明") }, onClick = { EndNumber = "21"
                showitem2 = false})
            DropdownMenuItem(text = { Text(text = "北边空调") }, onClick = { EndNumber = "22"
                showitem2 = false})
        }
        DropdownMenu(expanded = showitem3, onDismissRequest = { showitem3 = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "南边") }, onClick = { EndNumber = "11"
                showitem3 = false })
            DropdownMenuItem(text = { Text(text = "北边") }, onClick = { EndNumber = "21"
                showitem3 = false })
        }
    }

    val scope = rememberCoroutineScope()
    fun searchHefei() {
        scope.launch {
            val data = getHefeiElectric()
            if(data == null) {
                showToast("无")
                return@launch
            }
            show = false
            async { reEmptyLiveDta(vm.hefeiElectric) }.await()
            async { vm.getFee("bearer $auth", FeeType.ELECTRIC_HEFEI_UNDERGRADUATE, room = data.roomNumber, building = data.buildingNumber) }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.hefeiElectric.observeForever { result ->
                        if (result?.contains("success") == true) {
                            try {
                                val jsons = Gson().fromJson(result, FeeResponse::class.java).map
                                val data = jsons.showData
                                for ((_, value) in data) {
                                    scope.launch {
                                        DataStoreManager.saveHefeiElectricFee(value)
                                        show = true
                                    }
                                }
                            } catch (e : Exception) { showToast("错误") }
//                                                    val jsonObject = JSONObject(result)
//                                                    val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
//                                                    dataObject.put("myCustomInfo", "房间：$input")
//                                                    json = dataObject.toString()
//                                                    show = false
                        }
                    }
                }
            }
        }
    }
    Column(modifier = Modifier) {
        HazeBottomSheetTopBar("寝室电费" , isPaddingStatusBar = false) {
                Row() {
                    if(showitem4)
                        IconButton(onClick = {RoomNumber = RoomNumber.replaceFirst(".$".toRegex(), "")}) {
                            Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description") }
                    FilledTonalIconButton(onClick = {
                        when(pagerState.currentPage) {
                            HEFEI_TAB -> {
                                searchHefei()
                            }
                            XUANCHENG_TAB -> {
                                scope.launch {
                                    show = false
                                    async {
                                        showitem4 = false
                                        reEmptyLiveDta(vm.electricData)
                                        SharedPrefs.saveString("BuildNumber", BuildingsNumber)
                                        SharedPrefs.saveString("EndNumber", EndNumber)
                                        SharedPrefs.saveString("RoomNumber", RoomNumber)
                                        SharedPrefs.saveString("RoomText","${BuildingsNumber}号楼${RoomNumber}寝室${region}" )
                                    }.await()
                                    async { vm.getFee("bearer $auth", FeeType.ELECTRIC_XUANCHENG, room = input) }.await()
                                    async {
                                        Handler(Looper.getMainLooper()).post{
                                            vm.electricData.observeForever { result ->
                                                if (result?.contains("success") == true) {
                                                    try {
                                                        val jsons = Gson().fromJson(result, FeeResponse::class.java).map
                                                        val data = jsons.showData
                                                        for ((_, value) in data) {
                                                            Result = value
                                                        }
                                                    } catch (e : Exception) { showToast("错误") }
                                                    val jsonObject = JSONObject(result)
                                                    val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
                                                    dataObject.put("myCustomInfo", "房间：$input")
                                                    json = dataObject.toString()
                                                    show = false
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }

                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                Starter.startWebView(context,getUrl(pagerState.currentPage), title = "慧新易校")
                            }
                        }
                    ) {
                        Text("官方充值")
                    }
                }
        }
        if (BuildingsNumber == "0") BuildingsNumber = ""
        LaunchedEffect(pagerState.currentPage) {
            show = false
        }
        CustomTabRow(pagerState,titles)
        Column(
        ) {
            HorizontalPager(state = pagerState) { page ->
                when(page) {
                    HEFEI_TAB -> {
                        Column {
                            ElectricHefei(vm,show) {
                                searchHefei()
                            }
                        }
                    }
                    XUANCHENG_TAB -> {
                        Column {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = APP_HORIZONTAL_DP, vertical = 0.dp), horizontalArrangement = Arrangement.Start) {

                                MenuChip(
                                    label = { Text(text = "楼栋 $BuildingsNumber") },
                                ) {
                                    menuOffset = it
                                    showitem = true
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                MenuChip(
                                    label = { Text(text = region) },
                                    //    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
                                ) {
                                    menuOffset = it
                                    when {
                                        BuildingsNumber.toIntOrNull() != null -> {
                                            when {
                                                BuildingsNumber.toInt() > 5 -> showitem2 = true
                                                BuildingsNumber.toInt() in 1..5 -> showitem3 = true
                                            }
                                        }
                                        else -> Toast.makeText(MyApplication.context,"请选择楼栋", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))
                                AssistChip(
                                    onClick = { showitem4 = !showitem4 },
                                    label = { Text(text = "寝室 ${RoomNumber}") },
                                    //leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
                                )
                            }

                            // Spacer(modifier = Modifier.height(7.dp))

//充值界面
//        Spacer(modifier = Modifier.height(7.dp))
                            AnimatedVisibility(
                                visible = showitem4,
                                enter = slideInVertically(
                                    initialOffsetY = { -40 }
                                ) + expandVertically(
                                    expandFrom = Alignment.Top
                                ) + scaleIn(
                                    // Animate scale from 0f to 1f using the top center as the pivot point.
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                ) + fadeIn(initialAlpha = 0.3f),
                                exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                            ){
                                Row (modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)){
                                    OutlinedCard{
                                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                                            item {
                                                Text(text = " 选取寝室号", modifier = Modifier.padding(10.dp))
                                            }
                                            item {
                                                LazyRow {
                                                    items(5) { items ->
                                                        IconButton(onClick = {
                                                            if (RoomNumber.length < 3)
                                                                RoomNumber += items.toString()
                                                            else Toast.makeText(
                                                                MyApplication.context,
                                                                "三位数",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }) { Text(text = items.toString()) }
                                                    }
                                                }
                                            }
                                            item {
                                                LazyRow {
                                                    items(5) { items ->
                                                        val num = items + 5
                                                        IconButton(onClick = {
                                                            if (RoomNumber.length < 3)
                                                                RoomNumber += num
                                                            else Toast.makeText(MyApplication.context, "三位数", Toast.LENGTH_SHORT).show()
                                                        }) { Text(text = num.toString()) }
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }

                            if(Result.contains("剩余金额")){
                                Result2 = "剩余金额 " +Result.substringAfter("剩余金额")
                                Result2 = Result2.replace(":","")
                                Result = Result.substringBefore("剩余金额").replace(":","")
                                show = true

                            } else if(Result.contains("无法获取房间信息") || Result.contains("hfut")) Result2 = "失败"


                            DividerTextExpandedWith(text = "查询结果",openBlurAnimation = false) {
                                LoadingLargeCard(
                                    title = if(!show)"￥XX.XX"
                                    else
                                        "￥${formatDecimal(Result2.substringAfter(" ").toDouble(),2)}",
                                    loading = !show ,
                                    prepare = true,
                                    rightTop = {
                                        FilledTonalButton(
                                            enabled = show,
                                            onClick = {
                                                if(showAdd && payNumber != "")
                                                    showBottomSheet = true
                                                else showDialog2 = true
                                            }
                                        ) {
                                            Text(text =
                                                if(showAdd && payNumber != "")
                                                    "提交订单"
                                                else
                                                    "快速充值"
                                            )
                                        }
                                    }
                                ) {
                                    TransplantListItem(
                                        overlineContent = {Text( text = if(!show)"房间号 " + " 300XXXXX1" else "房间号 " + Result.substringAfter(" ")  )},
                                        headlineContent = { (if(!show)"X号楼XXX寝室方向设施" else prefs.getString("RoomText",null))?.let { Text(text = it) } },
                                        leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                                    )
                                }
                                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP/2))
                            }
                            DividerTextExpandedWith("使用说明", defaultIsExpanded = false) {
                                CustomCard(
                                    color = cardNormalColor()
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text("夜间透支") },
                                        supportingContent = { Text("每日23:00之前缴费，最迟23:00到账，23:00之后缴费，次日6点到账，在23:01-6:00时间段，账户如果欠费不会断电")},
                                        leadingContent = { Icon(painterResource(R.drawable.dark_mode),null)}
                                    )
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text("最大功率") },
                                        supportingContent = { Text("超出800W将自动断电，5分钟后自动恢复")},
                                        leadingContent = { Icon(painterResource(R.drawable.hvac_max_defrost),null)}
                                    )
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text("月末补贴") },
                                        supportingContent = { Text("照明空调各￥15，约下旬初到账")},
                                        leadingContent = { Icon(painterResource(R.drawable.paid),null)}
                                    )
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text("寝室缴费实测存在一定延迟") },
                                        leadingContent = { Icon(painterResource(R.drawable.schedule),null)}
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier
                .height(APP_HORIZONTAL_DP)
                .navigationBarsPadding())
        }
    }
}

private enum class ExpandState {
    NONE, CAMPUS, BUILDING, TYPE
}


@Composable
fun ElectricHefei(
    vm : NetWorkViewModel,
    show : Boolean,
    search : () -> Unit
) {
    var expandState by remember { mutableStateOf(ExpandState.NONE) }
    var campus by remember { mutableStateOf(getCampus() ?: Campus.TXL) }
    var buildingCode by remember { mutableIntStateOf(0) }
    var typeCode by remember { mutableStateOf<Type?>(null) }
    val showType = expandState == ExpandState.TYPE
    val showCampus = expandState == ExpandState.CAMPUS
    val showBuildings = expandState == ExpandState.BUILDING


    Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
        AnimatedVisibility(
            visible = !showType,
            enter = expandIn(),
            exit = shrinkOut()
        ) {
            Row {
                AnimatedVisibility(
                    visible = !showBuildings,
                    enter = expandIn(),
                    exit = shrinkOut(),
                ) {
                    AssistChip(
                        onClick = { expandState = if(showCampus) ExpandState.NONE else ExpandState.CAMPUS },
                        label = {
                            Text(if(!showCampus) campus.description + "校区" else "选择校区")
                            AnimatedVisibility(
                                visible = showCampus,
                                enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                                exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
                            ) {
                                WheelPicker(
                                    data = Campus.entries,
                                    selectIndex = Campus.entries.indexOf(campus),
                                    modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                                    onSelect = { index, element ->
                                        campus = element
                                    }
                                ) {
                                    Text(it.description.toString() + "校区")
                                }
                            }
                        }
                    )
                }
                AnimatedVisibility(
                    visible = !showCampus,
                    enter = expandIn(),
                    exit = shrinkOut(),
                    modifier = Modifier.padding(start = if(showBuildings) 0.dp else CARD_NORMAL_DP*2)
                ) {
                    AssistChip(
                        onClick = { expandState = if(showBuildings) ExpandState.NONE else ExpandState.BUILDING },
                        label = {
                            Text(if(showBuildings)"选择楼栋" else getBuildingStr(buildingCode,campus))
                            AnimatedVisibility(
                                visible = showBuildings,
                                enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                                exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
                            ) {
                                WheelPicker(
                                    data = when(campus) {
                                        Campus.XC -> IntArray(10) { it+1 }
                                        Campus.TXL -> IntArray(14) { it+1 }
                                        Campus.FCH -> IntArray(13) { it+1 }
                                    }.toList(),
                                    selectIndex = if(buildingCode <= 0) 0 else buildingCode-1,
                                    modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                                    onSelect = { index, element ->
                                        buildingCode = element
                                    }
                                ) {
                                    val description = getBuildingStr(it,campus)
                                    Text(description)
                                }
                            }
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !showCampus && !showBuildings,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.padding(start = if(showType) 0.dp else CARD_NORMAL_DP*2)
        ) {
            AssistChip(
                onClick = {
                    expandState = if (showType) ExpandState.NONE else ExpandState.TYPE
                },
                label = {
                    Text(if (showType) "选择区域" else typeCode?.description ?: "选择区域")
                    AnimatedVisibility(
                        visible = showType,
                        enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                        exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
                    ) {
                        WheelPicker(
                            data = getType(campus, buildingCode),
                            selectIndex = 0,
                            modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                            onSelect = { index, element ->
                                typeCode = element
                            }
                        ) {
                            Text(it.description)
                        }
                    }
                }
            )
        }
    }

    val finalRegion = typeCode?.let {
        campus.description + getBuildingStr(buildingCode,campus) + it.description
    }
    val buildingResponse by vm.hefeiBuildingsResp.state.collectAsState()
    val getBuildings = suspend m@ {
        if(buildingResponse is UiState.Success) {
            return@m
        }
        val auth = prefs.getString("auth","")
        auth?.let {
            vm.hefeiBuildingsResp.clear()
            vm.getHefeiBuildings("bearer $it")
        }
    }
    val roomResponse by vm.hefeiRoomsResp.state.collectAsState()
    val getRooms : suspend(String) -> Unit =  m@ { building : String ->
        val auth = prefs.getString("auth","")
        auth?.let {
            vm.hefeiRoomsResp.clear()
            vm.getHefeiRooms("bearer $it",building)
        }
    }
    LaunchedEffect(Unit) {
        vm.hefeiRoomsResp.emitPrepare()
    }
    val scope = rememberCoroutineScope()
    var buildingNumber by remember { mutableStateOf<HuiXinHefeiBuildingBean?>(null) }
    LaunchedEffect(finalRegion) {
        finalRegion?.let { final ->
            getBuildings()
            val data = (buildingResponse as? UiState.Success)?.data ?: return@LaunchedEffect
            val bean = data.find {
                val name = it.name
                val isCampus = name.startsWith(campus.description)
                // 判断校区
                if(isCampus) {
                    if(name.contains("研")) {
                        name == final
                    } else {
                        val buildingStr = name.substringBefore("号").substringAfter(campus.description)
                        // 判断楼栋
                        val isBuilding = getBuildingStr(buildingCode,campus).startsWith(buildingStr)
                        if(isBuilding) {
                            // 判断区域
                            val isRegion = name.endsWith(typeCode!!.description) || name.endsWith(typeCode!!.description.replace("楼",""))
                            isRegion
                        } else {
                            false
                        }
                    }
                } else {
                    false
                }
            }
            if(bean == null) {
                showToast("未找到此区域,可能是接口变更,请联系开发者")
            } else {
                buildingNumber = bean
//                showToast("正在获取${bean.name}的房间")
                getRooms(bean.value)
            }
        }
    }
    var showRoom by remember { mutableStateOf(false) }
    var roomNumber by remember { mutableStateOf<HuiXinHefeiBuildingBean?>(null) }
    LaunchedEffect(roomNumber) {
        roomNumber?.let {
            buildingNumber?.let { it1 ->
                DataStoreManager.saveHefeiElectric(HefeiElectricStorage(
                    roomNumber = it.value,
                    name = it.name,
                    buildingNumber = it1.value
                ))
            }
        }
    }
    AssistChip(
        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
        onClick = { showRoom = !showRoom },
        leadingIcon = {
            if(roomResponse is UiState.Loading) {
                LoadingIcon()
            }
        },
        enabled = roomResponse is UiState.Success,
        label = {
            Text(
                if(showRoom) {
                    if(roomResponse is UiState.Loading) {
                        "载入房间列表"
                    } else {
                        "选择房间"
                    }
                } else {
                    roomNumber?.name ?: "选择房间"
                }
            )
            AnimatedVisibility(
                visible = showRoom,
                enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
            ) {
                val list = (roomResponse as? UiState.Success)?.data
                list?.let {
                    WheelPicker(
                        data = it,
                        selectIndex = 0,
                        modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                        onSelect = { index, element ->
                            roomNumber = element
                        }
                    ) {
                        Text(it.name)
                    }
                }
            }
        }
    )
    var useLocal by remember { mutableStateOf(false) }
    val savedData by produceState<HefeiElectricStorage?>(initialValue = null, key1 = show) {
        value = getHefeiElectric()
    }

    AnimatedVisibility(
        visible = !useLocal && finalRegion == null,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        savedData?.let {
            CustomCard (color = cardNormalColor()){
                TransplantListItem(
                    headlineContent = {
                        Text(it.name)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.info),null)
                    }
                )
                BottomButton(
                    onClick = {
                        useLocal = true
                        search()
                    },
                    text = "使用上一次的记录查询"
                )
            }
        }
    }

    val result by DataStoreManager.hefeiElectricFee.collectAsState(initial = "XX.XX")
    DividerTextExpandedWith("查询结果",openBlurAnimation = false) {
        LoadingLargeCard(
            title = if(!show)"￥XX.XX" else "￥$result",
            loading = !show ,
            prepare = true,
            rightTop = {
                FilledTonalButton(
                    enabled = show,
                    onClick = {
                        showToast("正在开发")
                    }
                ) {
                    Text(text = "快速充值")
                }
            }
        ) {
            TransplantListItem(
                headlineContent = {
                    Text(
                        savedData?.name ?: "校区X号X楼XXXXX"
                    )
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                },
            )
        }
        BottomTip("快速充值开发中 请先使用官方充值")
    }
}

private fun getBuildingStr(content : Int, campus : Campus) : String {
    if(content <= 0) {
        return "选择楼栋"
    }
    return when(campus) {
        Campus.XC -> {
            content.toString() + "号楼"
        }
        Campus.FCH -> {
            content.toString() + "号楼"
        }
        Campus.TXL -> {
            if(content > 10) {
                "研${numToChinese(content-7)}"
            } else {
                content.toString() + "号楼"
            }
        }
    }
}
private fun numToChinese(num : Int) : String {
    return when(num) {
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "七"
        else -> ""
    }
}

private fun getType( campus : Campus,buildingCode : Int) : List<Type> {
    return when(campus) {
        Campus.XC -> {
            if(buildingCode <= 5) {
                // 南楼 北楼
                Type1.entries
            } else {
                // 北楼空调 北楼空调 南楼空调 南楼照明
                Type2.entries
            }
        }
        Campus.FCH -> {
            if(buildingCode == 13) {
                // 北楼空调 北楼照明 南楼空调 南楼照明 中楼空调 中楼照明
                Type3.entries
            } else {
                // 北楼空调 北楼照明 南楼空调 南楼照明
                Type2.entries
            }
        }
        Campus.TXL -> {
            if(buildingCode in listOf(3,4,9)) {
                // 空调 照明
                Type4.entries
            } else {
                // 北楼空调 北楼照明 南楼空调 南楼照明
                Type2.entries
            }
        }
    }
}

private interface Type {
    val description: String
}

private enum class Type1(override val description: String) : Type {
    SOUTH("南楼"), NORTH("北楼")
}

private enum class Type2(override val description: String) : Type {
    SOUTH_AIR("南楼空调"), NORTH_AIR("北楼空调"),
    SOUTH_LIGHT("南楼照明"), NORTH_LIGHT("北楼照明"),
}

private enum class Type3(override val description: String) : Type {
    SOUTH_AIR("南楼空调"), NORTH_AIR("北楼空调"), MIDDLE_AIR("中楼空调"),
    SOUTH_LIGHT("南楼照明"), NORTH_LIGHT("北楼照明"), MIDDLE_LIGHT("中楼照明")
}

private enum class Type4(override val description: String) : Type {
    AIR("空调"), LIGHT("照明")
}
