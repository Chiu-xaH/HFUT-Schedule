package com.hfut.schedule.ui.Activity.success.search.Search.Shower

//import androidx.compose.material.Text
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.zjgd.FeeType
import com.hfut.schedule.logic.datamodel.zjgd.ShowerFeeResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.success.search.Search.Electric.PayFor
import com.hfut.schedule.ui.Activity.success.search.Search.More.LoginGuaGua
import com.hfut.schedule.ui.Activity.success.search.Search.More.startGuagua
import com.hfut.schedule.ui.UIUtils.BottomTip
import com.hfut.schedule.ui.UIUtils.CardForListColor
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.ScrollText
import com.hfut.schedule.ui.UIUtils.WebViewScreen
import com.hfut.schedule.ui.theme.FWDTColr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Shower(vm: LoginSuccessViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    //val interactionSource = remember { MutableInteractionSource() }
    //val isPressed by interactionSource.collectIsPressedAsState()

  //  val scale = animateFloatAsState(
    //    targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
      //  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
       // label = "" // 使用弹簧动画
    //)

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
            , shape = Round(sheetState)
        ) {
            ShowerUI(vm)
        }
    }/*
    val showAdd = SharePrefs.prefs.getBoolean("SWITCHELEADD",true)
    val memoryEle = SharePrefs.prefs.getString("memoryEle","0")
    var showDialog by remember { mutableStateOf(false) }
    val auth = SharePrefs.prefs.getString("auth","")
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
                            title = { androidx.compose.material3.Text("宣城校区 电费缴纳") }
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

    ListItem(
        headlineContent = { if(!card) androidx.compose.material3.Text(text = "寝室电费") else ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") },
        overlineContent = { if(!card) ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") else ScrollText(text = room) },
        leadingContent = { Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",) },
        trailingContent = {
            if(card && showAdd)
                FilledTonalIconButton(
                    modifier = Modifier
                        .scale(scale.value)
                        .size(30.dp),
                    interactionSource = interactionSource,
                    onClick = {
                        showDialog = true
                        // ClipBoard.copy(input)
                        //  MyToast("已将房间号复制到剪切板")
                    },
                    //  colors =  if(test.length <= 4) {
                    //      IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    //  } else IconButtonDefaults.filledTonalIconButtonColors()
                ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
        },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )*/
    ListItem(
        headlineContent = { Text(text = "洗浴") },
        leadingContent = {
            Icon(painterResource(id = R.drawable.bathtub), contentDescription = "")
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerUI(vm : LoginSuccessViewModel) {
    var savedPhoneNumber = prefs.getString("PhoneNumber","")
    var phoneNumber by remember { mutableStateOf(savedPhoneNumber ?: "") }
    var balance by remember { mutableStateOf(0) }
    var givenBalance by remember { mutableStateOf(0) }
    var studentID by remember { mutableStateOf("") }


    var showitem4 by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }


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
        //    shape = sheetState
        ) {

                Column(

                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { androidx.compose.material3.Text("支付订单确认") },
                    )
                    val info by remember { mutableStateOf("绑定手机号 $phoneNumber") }
                    var int by remember { mutableStateOf(payNumber.toInt()) }
                    if(int > 0) {
                        PayFor(vm,int,info,json,FeeType.SHOWER)
                    } else MyToast("输入数值")
                }

        }
    }

    val auth = SharePrefs.prefs.getString("auth","")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { ScrollText("洗浴-宣城校区") },
                actions = {
                    Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                        if(showitem4)
                            IconButton(onClick = {phoneNumber = phoneNumber.replaceFirst(".$".toRegex(), "")}) {
                                Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description") }
                        FilledTonalIconButton(onClick = {
                            show = false
                            CoroutineScope(Job()).launch {
                                async {
                                    showitem4 = false
                                    Handler(Looper.getMainLooper()).post{
                                        vm.ElectricData.value = "{}"
                                    }
                                    SharePrefs.Save("PhoneNumber",phoneNumber )
                                }.await()
                                async { vm.getFee("bearer $auth", FeeType.SHOWER, phoneNumber = phoneNumber) }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post{
                                        vm.showerData.observeForever { result ->
                                            if (result?.contains("success") == true) {
                                                showButton = true
                                                val jsons = Gson().fromJson(result, ShowerFeeResponse::class.java).map.data
                                                try {
                                                    studentID = jsons.identifier.toString()
                                                    //val name = jsons.name
                                                    balance = jsons.accountMoney
                                                    givenBalance = jsons.accountGivenMoney
                                                    val jsonObject = JSONObject(result)
                                                    val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
                                                    dataObject.put("myCustomInfo", "undefined：$phoneNumber")
                                                    json = dataObject.toString()
                                                    show = true
                                                } catch (e:Exception) {
                                                    Log.d("JSON",result)
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                        FilledTonalButton(onClick = {
                            CoroutineScope(Job()).launch {
                                async { vm.getGuaGuaUserInfo() }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post {
                                        vm.guaguaUserInfo.observeForever { result ->
                                            if (result?.contains("成功") == true) {
                                                Save("GuaGuaPersonInfo",result)
                                                startGuagua()
                                            } else if(result?.contains("error") == true) {
                                                LoginGuaGua()
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                            Text(text = "呱呱物联")
                        }
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

            val url = MyApplication.ZJGDBillURL + "charge-app/?name=pays&appsourse=ydfwpt&id=223&name=pays&paymentUrl=http://121.251.19.62/plat&token=" + auth
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
                                    title = { androidx.compose.material3.Text("宣城校区 洗浴缴纳") }
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
                                        androidx.compose.material3.Text(text = "选取金额 ￥${payNumber}", modifier = Modifier.padding(10.dp))
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
                                                }) { androidx.compose.material3.Text(text = items.toString()) }
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
                                                }) { androidx.compose.material3.Text(text = num.toString()) }
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
                    onClick = { showitem4 = !showitem4 },
                    label = { androidx.compose.material3.Text(text = "手机号 ${phoneNumber}") },
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
            ) {
                Row (modifier = Modifier.padding(horizontal = 15.dp)){
                    OutlinedCard{
                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                            item {
                                androidx.compose.material3.Text(
                                    text = " 选取手机号",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        IconButton(onClick = {
                                            if (phoneNumber.length < 11)
                                                phoneNumber += items.toString()
                                            else Toast.makeText(
                                                MyApplication.context,
                                                "11位数",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) { androidx.compose.material3.Text(text = items.toString()) }
                                    }
                                }
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        val num = items + 5
                                        IconButton(onClick = {
                                            if (phoneNumber.length < 11)
                                                phoneNumber += num
                                            else Toast.makeText(MyApplication.context, "11位数", Toast.LENGTH_SHORT).show()
                                        }) { androidx.compose.material3.Text(text = num.toString()) }
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
                            headlineContent = { androidx.compose.material3.Text(text = if(!show)"￥XX.XX" else {     "￥${tranamt(balance)}"
                            }, fontSize = 28.sp) },
                            trailingContent = {
                                if(show) {
                                    if(showButton)
                                        FilledTonalButton(onClick = { if(showAdd && payNumber != "") showBottomSheet = true   else showDialog2 = true  }) { androidx.compose.material3.Text(text = if(showAdd && payNumber != "") "提交订单" else "快速充值") }
                                } else FilledTonalButton(onClick = { null }) { androidx.compose.material3.Text(text = "快速充值") } }
                        )
                        ListItem(
                            //headlineContent = { androidx.compose.material3.Text( text = if(!show)"学号 " + " 2000000000" else "学号 $studentID") },
                            headlineContent = { (if(!show)"手机号 1XXXXXXXXXX" else "手机号 $phoneNumber").let { androidx.compose.material3.Text(text = it) } },
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            BottomTip(str = "慧新易校已集成 可前往 查询中心-一卡通-卡包")
            BottomTip(str = "首次使用前需在微信小程序-呱呱物联绑定手机号")
        }
    }
}
//适配
fun tranamt(bills : Int) : Float {
    return try {
        var num =( bills ).toString()
        //优化0.0X元Bug
        if(num.length == 1)
            num = "000$num"
        if(num.length == 2)
            num = "00$num"
        num = num.substring(0, num.length - 3) + "." + num.substring(num.length - 3)
        val big = BigDecimal(num)
        val num_float = big.toFloat()
        num_float
    }catch (_:Exception) {
        0.0f
    }
}