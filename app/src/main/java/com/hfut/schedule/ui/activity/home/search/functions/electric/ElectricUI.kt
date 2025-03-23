package com.hfut.schedule.ui.activity.home.search.functions.electric

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.zjgd.FeeResponse
import com.hfut.schedule.logic.beans.zjgd.FeeType
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.LoadingLargeCard
import com.hfut.schedule.ui.utils.components.MenuChip
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EleUI(vm : NetWorkViewModel,hazeState: HazeState) {
    val auth = SharePrefs.prefs.getString("auth","")
    val zjgdUrl = MyApplication.ZJGD_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.ELECTRIC.code}&name=pays&paymentUrl=${MyApplication.ZJGD_URL}plat&token=" + auth
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb, url = zjgdUrl, title = "慧新易校",showChanged = { showDialogWeb = false }, showTop = false)

    val SavedBuildNumber = prefs.getString("BuildNumber", "0") ?: "0"
    var BuildingsNumber by remember { mutableStateOf(SavedBuildNumber) }
    val SavedRoomNumber = prefs.getString("RoomNumber", "")
    var RoomNumber by remember { mutableStateOf(SavedRoomNumber ?: "") }
    val SavedEndNumber = prefs.getString("EndNumber", "")
    var EndNumber by remember { mutableStateOf(SavedEndNumber ?: "") }

    var region by remember { mutableStateOf("选择南北") }

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
   // var jsons = "{ \"query_elec_roominfo\": { \"aid\":\"0030000000007301\", \"account\": \"24027\",\"room\": { \"roomid\": \"${input}\", \"room\": \"${input}\" },  \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"\", \"building\": \"\" },\"extdata\":\"info1=\" } }"

    var showitem by remember { mutableStateOf(false) }
    var showitem2 by remember { mutableStateOf(false) }
    var showitem3 by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }

    var Result by remember { mutableStateOf("") }
    var Result2 by remember { mutableStateOf("") }

    var showButton by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var show by remember { mutableStateOf(false) }

    var json by remember { mutableStateOf("") }
    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            autoShape = false,
            hazeState = hazeState
//            sheetState = sheetState,
        ) {
                Column(

                ) {
                    HazeBottomSheetTopBar("支付订单确认", isPaddingStatusBar = false)
                    val roomInfo by remember { mutableStateOf("${BuildingsNumber}号楼${RoomNumber}寝室${region}") }
                    val int by remember { mutableStateOf(payNumber.toFloat()) }
                    if(int > 0) {
                        PayFor(vm,int,roomInfo,json,FeeType.ELECTRIC,hazeState)
                    } else MyToast("输入数值")
                }
        }
    }

    region = when(EndNumber) {
        "11"-> "南边照明"
        "12" -> "南边空调"
        "21" -> "北边照明"
        "22" -> "北边空调"
        else -> "选择南北"
    }

    var menuOffset by remember { mutableStateOf<DpOffset?>(null) }

    Column() {

        HazeBottomSheetTopBar("寝室电费-宣城校区", isPaddingStatusBar = false) {
            Row() {
                if(showitem4)
                    IconButton(onClick = {RoomNumber = RoomNumber.replaceFirst(".$".toRegex(), "")}) {
                        Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description") }
                FilledTonalIconButton(onClick = {
                    show = false
                    CoroutineScope(Job()).launch {
                        async {
                            showitem4 = false
                            Handler(Looper.getMainLooper()).post{
                                vm.ElectricData.value = "{}"
                            }
                            SharePrefs.saveString("BuildNumber", BuildingsNumber)
                            SharePrefs.saveString("EndNumber", EndNumber)
                            SharePrefs.saveString("RoomNumber", RoomNumber)
                            SharePrefs.saveString("RoomText","${BuildingsNumber}号楼${RoomNumber}寝室${region}" )
                        }.await()
                        async { vm.getFee("bearer $auth", FeeType.ELECTRIC, room = input) }.await()
                        // async { vm.searchEle(jsons) }.await()
                        async {
                            Handler(Looper.getMainLooper()).post{
                                vm.ElectricData.observeForever { result ->
                                    if (result?.contains("success") == true) {
                                        showButton = true
                                        val jsons = Gson().fromJson(result, FeeResponse::class.java).map
                                        val data = jsons.showData
                                        for ((_, value) in data) {
                                            Result = value
                                        }

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
                }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }

                FilledTonalButton(
                    onClick = {
                        showDialogWeb = true
                    }
                ) {
                    Text("官方充值")
                }
            }
        }

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

        if (BuildingsNumber == "0") BuildingsNumber = ""

        var showDialog2 by remember { mutableStateOf(false) }
        if(showDialog2)
            Dialog(onDismissRequest = { showDialog2 = false }) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                        OutlinedCard{
                            LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                                item {
                                    Text(text = "选取金额 ￥${payNumber}", modifier = Modifier.padding(10.dp))
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
                                                else Toast.makeText(MyApplication.context, "最高999元", Toast.LENGTH_SHORT).show()
                                            }) { Text(text = num.toString()) }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                        FilledTonalIconButton(
                            onClick = {payNumber = payNumber.replaceFirst(".$".toRegex(), "")},
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description")
                        }

                        FilledTonalIconButton(
                            onClick = {
                                showDialog2 = false
                                if(payNumber != "" && payNumber != "0" && payNumber != "00" && payNumber != "000")
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
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = appHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start) {

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
                            BuildingsNumber.toInt() in 1..4 -> showitem3 = true
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
            Row (modifier = Modifier.padding(horizontal = appHorizontalDp())){
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


        DividerTextExpandedWith(text = "查询结果") {
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                Spacer(modifier = Modifier.height(100.dp))
                LoadingLargeCard(
                    title = if(!show)"￥XX.XX"
                    else
                        "￥${BigDecimal(Result2.substringAfter(" ")).setScale(2, RoundingMode.HALF_UP).toString()}",
                    loading = !show ,
                    rightTop = {
                        if(show) {
                            if(showButton)
                                FilledTonalButton(onClick = { if(showAdd && payNumber != "") showBottomSheet = true   else showDialog2 = true  }) { Text(text = if(showAdd && payNumber != "") "提交订单" else "快速充值") }
                        } else FilledTonalButton(onClick = { null }) { Text(text = "快速充值") }
                    }
                ) {
                    TransplantListItem(
                        overlineContent = {Text( text = if(!show)"房间号 " + " 300XXXXX1" else "房间号 " + Result.substringAfter(" ")  )},
                        headlineContent = { (if(!show)"X号楼XXX寝室方向设施" else prefs.getString("RoomText",null))?.let { Text(text = it) } },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                    )
                }
//                Card(
//                    elevation = CardDefaults.cardElevation(defaultElevation = AppHorizontalDp()),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .scale(scale2.value)
//                        .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
//                    shape = MaterialTheme.shapes.medium,
//                    colors = CardForListColor()
//                ) {
//                    Column(modifier = Modifier
//                        .blur(blurSize)
//                        .scale(scale.value)) {
//                        ListItem(
//                            headlineContent = { Text(
//                                text =
//
//                                , fontSize = 28.sp) },
//                            trailingContent = {
//                                 }
//                        )
//
//                    }
//                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            BottomTip(str = "月末补贴 照明空调各￥15")
            BottomTip(str = "寝室缴费实测存在一定延迟")
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

}
