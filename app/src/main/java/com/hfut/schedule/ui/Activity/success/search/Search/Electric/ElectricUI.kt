package com.hfut.schedule.ui.Activity.success.search.Search.Electric

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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.zjgd.FeeResponse
import com.hfut.schedule.logic.datamodel.zjgd.FeeType
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.BottomTip
import com.hfut.schedule.ui.UIUtils.CardForListColor
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.WebViewScreen
import com.hfut.schedule.ui.theme.FWDTColr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

fun EleUIs() {

}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EleUI(vm : LoginSuccessViewModel) {
    val SavedBuildNumber = SharePrefs.prefs.getString("BuildNumber", "0")
    var BuildingsNumber by remember { mutableStateOf(SavedBuildNumber ?: "0") }
    val SavedRoomNumber = SharePrefs.prefs.getString("RoomNumber", "")
    var RoomNumber by remember { mutableStateOf(SavedRoomNumber ?: "") }
    val SavedEndNumber = SharePrefs.prefs.getString("EndNumber", "")
    var EndNumber by remember { mutableStateOf(SavedEndNumber ?: "") }

    var region by remember { mutableStateOf("选择南北") }

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
   // var jsons = "{ \"query_elec_roominfo\": { \"aid\":\"0030000000007301\", \"account\": \"24027\",\"room\": { \"roomid\": \"${input}\", \"room\": \"${input}\" },  \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"\", \"building\": \"\" },\"extdata\":\"info1=\" } }"


    var showitem by remember { mutableStateOf(false) }
    var showitem2 by remember { mutableStateOf(false) }
    var showitem3 by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
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

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            //shape = sheetState
        ) {
                Column(

                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("支付订单确认") },
                    )
                    val roomInfo by remember { mutableStateOf("${BuildingsNumber}号楼${RoomNumber}寝室${region}") }
                    var int by remember { mutableStateOf(payNumber.toInt()) }
                    if(int > 0) {
                        PayFor(vm,int,roomInfo,json,FeeType.ELECTRIC)
                    } else MyToast("输入数值")
                }
        }
    }

    when(EndNumber) {
        "11"-> region = "南边照明"
        "12" -> region = "南边空调"
        "21" -> region = "北边照明"
        "22" -> region = "北边空调"
        else -> region = "选择南北"
    }
    val auth = SharePrefs.prefs.getString("auth","")
    //var roomInfo  = "${BuildingsNumber}号楼${RoomNumber}寝室${region}"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("寝室电费-宣城校区") },
                actions = {
                    Row(modifier = Modifier.padding(horizontal = 15.dp)) {
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
                                    SharePrefs.Save("BuildNumber", BuildingsNumber)
                                    SharePrefs.Save("EndNumber", EndNumber)
                                    SharePrefs.Save("RoomNumber", RoomNumber)
                                    SharePrefs.Save("RoomText","${BuildingsNumber}号楼${RoomNumber}寝室${region}" )
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
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {


            DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = DpOffset(103.dp,0.dp)) {
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
            DropdownMenu(expanded = showitem2, onDismissRequest = { showitem2 = false }, offset = DpOffset(210.dp,0.dp)) {
                DropdownMenuItem(text = { Text(text = "南边照明") }, onClick = { EndNumber = "11"
                    showitem2 = false})
                DropdownMenuItem(text = { Text(text = "南边空调") }, onClick = { EndNumber = "12"
                    showitem2 = false})
                DropdownMenuItem(text = { Text(text = "北边照明") }, onClick = { EndNumber = "21"
                    showitem2 = false})
                DropdownMenuItem(text = { Text(text = "北边空调") }, onClick = { EndNumber = "22"
                    showitem2 = false})
            }
            DropdownMenu(expanded = showitem3, onDismissRequest = { showitem3 = false }) {
                DropdownMenuItem(text = { Text(text = "南边") }, onClick = { EndNumber = "11"
                    showitem3 = false })
                DropdownMenuItem(text = { Text(text = "北边") }, onClick = { EndNumber = "21"
                    showitem3 = false })
            }


            if (BuildingsNumber == "0") BuildingsNumber = ""

            val url = MyApplication.ZJGDBillURL + "charge-app/?name=pays&appsourse=ydfwpt&id=261&name=pays&paymentUrl=http://121.251.19.62/plat&token=" + auth
            val switch_startUri = SharePrefs.prefs.getBoolean("SWITCHSTARTURI",true)
            if (showDialog) {
                if(switch_startUri) {
                    androidx.compose.ui.window.Dialog(
                        onDismissRequest = { showDialog = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopAppBar(
                                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                                        containerColor = FWDTColr,
                                        titleContentColor = Color.White,
                                    ),
                                    actions = {
                                        Row{
                                            IconButton(onClick = { StartApp.startUri( url) }) { Icon(painterResource(id = R.drawable.net), contentDescription = "", tint = Color.White) }
                                            IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "", tint = Color.White) }
                                        }

                                    },
                                    title = { Text("宣城校区 电费缴纳") }
                                )
                            },
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                            ) {
                                WebViewScreen(url)
                            }
                        }
                    }
                } else {
                    StartApp.startUri(url)
                }
            }
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
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start) {

                AssistChip(
                    onClick = { showitem = true },
                    label = { Text(text = "楼栋 $BuildingsNumber") }
                )

                Spacer(modifier = Modifier.width(10.dp))

                AssistChip(
                    onClick = {
                        when {
                            BuildingsNumber.toInt() > 5 -> showitem2 = true
                            BuildingsNumber.toInt() in 1..4 -> showitem3 = true
                            else -> Toast.makeText(MyApplication.context,"请选择楼栋", Toast.LENGTH_SHORT).show()
                        }
                    },
                    label = { Text(text = region) },
                    //    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
                )

                Spacer(modifier = Modifier.width(10.dp))
                AssistChip(
                    onClick = { showitem4 = !showitem4 },
                    label = { Text(text = "寝室 ${RoomNumber}") },
                    //leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
                )
            }


           // Spacer(modifier = Modifier.height(7.dp))

//充值界面
            Spacer(modifier = Modifier.height(7.dp))
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
                Row (modifier = Modifier.padding(horizontal = 15.dp)){
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

            val blurSize by animateDpAsState(
                targetValue = if (!show) 10.dp else 0.dp, label = ""
                ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
            )

            if(Result.contains("剩余金额")){
                Result2 = "剩余金额 " +Result.substringAfter("剩余金额")
                Result2 = Result2.replace(":","")
                Result = Result.substringBefore("剩余金额").replace(":","")
                show = true

            } else if(Result.contains("无法获取房间信息") || Result.contains("hfut")) Result2 = "失败"


            val scale = animateFloatAsState(
                targetValue = if (!show) 0.9f else 1f, // 按下时为0.9，松开时为1
                //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
                label = "" // 使用弹簧动画
            )
            val scale2 = animateFloatAsState(
                targetValue = if (!show) 0.97f else 1f, // 按下时为0.9，松开时为1
                //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
                label = "" // 使用弹簧动画
            )

            DividerText(text = "查询结果")

            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale2.value)
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardForListColor()
                    ) {
                        Column(modifier = Modifier
                            .blur(blurSize)
                            .scale(scale.value)) {
                            ListItem(
                                headlineContent = { Text(text = if(!show)"￥XX.XX" else {     "￥${BigDecimal(Result2.substringAfter(" ")).setScale(2, RoundingMode.HALF_UP).toString()}"
                                }, fontSize = 28.sp) },
                                trailingContent = {
                                    if(show) {
                                        if(showButton)
                                            FilledTonalButton(onClick = { if(showAdd && payNumber != "") showBottomSheet = true   else showDialog2 = true  }) { Text(text = if(showAdd && payNumber != "") "提交订单" else "快速充值") }
                                    } else FilledTonalButton(onClick = { null }) { Text(text = "快速充值") } }
                            )
                            ListItem(
                                overlineContent = {Text( text = if(!show)"房间号 " + " 300XXXXX1" else "房间号 " + Result.substringAfter(" ")  )},
                                headlineContent = { (if(!show)"X号楼XXX寝室方向设施" else prefs.getString("RoomText",null))?.let { Text(text = it) } },
                                leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                            )
                        }
                    }
                }
            Spacer(modifier = Modifier.height(10.dp))
            BottomTip(str = "月末补贴 照明空调各￥15")

            //Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


@Composable
fun searchResult(roomInfo : String) {

}