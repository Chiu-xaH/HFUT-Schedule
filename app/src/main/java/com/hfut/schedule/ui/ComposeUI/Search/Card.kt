package com.hfut.schedule.ui.ComposeUI.Search

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.OpenAlipay
import com.hfut.schedule.logic.datamodel.ZJGD.LiushuiMonthResponse
import com.hfut.schedule.logic.datamodel.ZJGD.LiushuiResponse
import com.hfut.schedule.logic.datamodel.ZJGD.liushuiMonth
import com.hfut.schedule.logic.datamodel.ZJGD.record
import com.hfut.schedule.logic.datamodel.content
import com.hfut.schedule.ui.ComposeUI.SelecctDateRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SchoolCard(vm : JxglstuViewModel) {


    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val isPressed by interactionSource1.collectIsPressedAsState()
    val isPressed2 by interactionSource2.collectIsPressedAsState()


    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    val scale2 = animateFloatAsState(
        targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    var card =prefs.getString("card","正在获取")

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState2 = rememberModalBottomSheetState()
    var showBottomSheet2 by remember { mutableStateOf(false) }

    val sheetState3 = rememberModalBottomSheetState()
    var showBottomSheet3 by remember { mutableStateOf(false) }

    var clicked by remember { mutableStateOf(false) }

    var page by remember { mutableStateOf(1) }

    var loading by remember { mutableStateOf(true) }

    var loading2 by remember { mutableStateOf(true) }
    
    fun LiushuiItem() :MutableList<record> {
        val liushuijson = prefs.getString("cardliushui",MyApplication.NullLiushui)
        val liushui = Gson().fromJson(liushuijson, LiushuiResponse::class.java)
        val data = liushui.data.records
      //  Log.d("流水",data[0].resume)
        var LiushuiItems = mutableListOf<record>()
        val totalpage = liushui.data.pages
        val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if(sp.getInt("totalpage",0) != totalpage){ sp.edit().putInt("totalpage", totalpage).apply() }
        data.forEach {  LiushuiItems.add(it) }
        return LiushuiItems
    }

    fun get() {
        val auth = prefs.getString("auth","")
        vm.CardGet("bearer " + auth,page)
        showBottomSheet = true
    }
    
    fun getliushuimonth() : List<liushuiMonth> {
        val json = prefs.getString("monthbalance",MyApplication.NullSearch)
        val data = Gson().fromJson(json,LiushuiMonthResponse::class.java)
        val liushui = data.data
        val list = liushui.map { (date,balance) -> liushuiMonth(date, balance) }
        return list
    }



    if(showBottomSheet2) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet2 = false
            },
            sheetState = sheetState2
        ) {
            SelecctDateRange(vm)
        }

    }


    if(showBottomSheet3) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet3 = false
            },
            sheetState = sheetState3
        ) {
            val auth = prefs.getString("auth","")

            val date = GetDate.Date3
            var input by remember { mutableStateOf(date) }
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                    TextField(
                        //  modifier = Modifier.size(width = 170.dp, height = 70.dp).padding(horizontal = 15.dp, vertical = 5.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        value = input,
                        onValueChange = {
                            input = it
                            clicked = false
                        },
                        label = { Text("输入年月" ) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    CoroutineScope(Job()).apply {
                                        launch {
                                            async {
                                                clicked = true
                                                loading2 = true
                                                vm.getMonthYue("bearer " + auth,input) }.await()
                                            async {
                                                delay(500)
                                                getliushuimonth()
                                                loading2 = false
                                            }
                                        }
                                    }

                                }) {
                                Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                        ),
                    )
                    Spacer(modifier = Modifier.height(500.dp))

                    if (clicked) {

                        AnimatedVisibility(
                            visible = loading2,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                                Spacer(modifier = Modifier.height(5.dp))
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(50.dp))
                            }
                        }


                        AnimatedVisibility(
                            visible = !loading2,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            //填充界面
                            Column{

                                //Spacer(modifier = Modifier.height(50.dp))
                                LazyColumn {
                                    item {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                                            TextField(
                                                //  modifier = Modifier.size(width = 170.dp, height = 70.dp).padding(horizontal = 15.dp, vertical = 5.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                                value = input,
                                                onValueChange = {
                                                    input = it
                                                    clicked = false
                                                },
                                                label = { Text("输入年月") },
                                                singleLine = true,
                                                trailingIcon = {
                                                    IconButton(
                                                        onClick = {
                                                            CoroutineScope(Job()).apply {
                                                                launch {
                                                                    async {
                                                                        clicked = true
                                                                        loading2 = true
                                                                        vm.getMonthYue("bearer " + auth, input)
                                                                    }.await()
                                                                    async {
                                                                        delay(500)
                                                                        getliushuimonth()
                                                                        loading2 = false
                                                                    }
                                                                }
                                                            }

                                                        }) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.search),
                                                            contentDescription = "description"
                                                        )
                                                    }
                                                },
                                                shape = MaterialTheme.shapes.medium,
                                                colors = TextFieldDefaults.textFieldColors(
                                                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                                                ),
                                            )
                                        }

                                    }
                                    item {
                                        var total = 0.0
                                        for (i in 0 until getliushuimonth().size) {
                                            var balance = getliushuimonth()[i].balance
                                            balance = balance / 100
                                            total += balance
                                        }


                                        val num = total.toString()
                                        val bd = BigDecimal(num)
                                        val str = bd.setScale(2, RoundingMode.HALF_UP).toString()

                                        Card(
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 3.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp, vertical = 5.dp),
                                            shape = MaterialTheme.shapes.medium
                                        ) {
                                            ListItem(
                                                headlineContent = { Text(text = "月支出  ${str} 元") },
                                                leadingContent = {
                                                    Icon(
                                                        painterResource(R.drawable.credit_card),
                                                        contentDescription = "Localized description",
                                                    )
                                                },
                                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                            )
                                        }
                                    }

                                    items(getliushuimonth().size) { item ->
                                        var balance = getliushuimonth()[item].balance
                                        balance = balance / 100
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        )
                                        {
                                            Card(
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = 3.dp
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                                shape = MaterialTheme.shapes.medium
                                            ) {
                                                ListItem(
                                                    headlineContent = { Text(text = getliushuimonth()[item].date) },
                                                    supportingContent = { Text(text = balance.toString() + " 元") },
                                                    leadingContent = {
                                                        Icon(
                                                            painterResource(R.drawable.paid),
                                                            contentDescription = "Localized description",
                                                        )
                                                    }
                                                )
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }


                }
            }

            }


    }

    if (showBottomSheet) {

        CoroutineScope(Job()).apply {
            launch {

                async {
                    delay(1000)
                    val liushuijson = prefs.getString("cardliushui",MyApplication.NullLiushui)
                    if (liushuijson != null) {
                        if (liushuijson.contains("操作成功")) LiushuiItem()
                        else {
                            val ONE = prefs.getString("ONE","")
                            val TGC = prefs.getString("TGC","")
                            vm.OneGotoCard(ONE + ";" + TGC)

                            Handler(Looper.getMainLooper()).post{
                                Toast.makeText(MyApplication.context, "空数据,请再次尝试或登录", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.await()
               async { loading = false }

            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {

            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                    Spacer(modifier = Modifier.height(5.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }


            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column() {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        var todaypay = 0.0
                        var date = GetDate.Date
                        for (item in 0 until LiushuiItem().size) {
                            val get = LiushuiItem()[item].effectdateStr
                            val name = LiushuiItem()[item].resume
                            val todaydate = get?.substringBefore(" ")
                            var num = LiushuiItem()[item].tranamt.toString()

                            num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)

                            //val big = BigDecimal(num)
                           val num_float = num.toFloat()
                            //Log.d("num",num_float.toString())
                            //if (num_float <1) num = "0" + num

                            if (date == todaydate) {
                                if (name.contains("充值") == false) todaypay += num_float
                            }

                        }

                        Column {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.End){

                                AssistChip(
                                    onClick = { showBottomSheet2 = true },
                                    label = { Text(text = "范围支出") },
                                    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description")}
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                AssistChip(
                                    onClick = { showBottomSheet3 = true },
                                    label = { Text(text = "月份查询") },
                                    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar_view_month), contentDescription = "description")}
                                )

                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            LazyColumn {
                                if (page == 1){
                                    val num = todaypay.toString()
                                    val bd = BigDecimal(num)
                                    val str = bd.setScale(2, RoundingMode.HALF_UP).toString()
                                    item {
                                        Card(
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 3.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp, vertical = 5.dp),
                                            shape = MaterialTheme.shapes.medium
                                        ) {
                                            ListItem(
                                                headlineContent = { Text(text = "今日消费  ${str} 元") },
                                                leadingContent = {
                                                    Icon(
                                                        painterResource(R.drawable.paid),
                                                        contentDescription = "Localized description",
                                                    )
                                                },
                                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                            )
                                        }
                                    }
                                }
                                items(LiushuiItem().size) { item ->
                                    var num = LiushuiItem()[item].tranamt.toString()
                                    num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)
                                    val big = BigDecimal(num)
                                    val num_float = big.toFloat()

                                    var name = LiushuiItem()[item].resume
                                    if (name.contains("有限公司")) name = name.replace("有限公司","")

                                    var pay = "$num_float 元"
                                    if (name.contains("充值") || name.contains("补助")) pay = "+" + pay
                                    else pay = "-" + pay

                                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
                                    {
                                        Card(
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 3.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp, vertical = 5.dp),
                                            shape = MaterialTheme.shapes.medium
                                        ) {
                                            ListItem(
                                                headlineContent = { Text(text = name) },
                                                supportingContent = {Text(text = pay)},
                                                overlineContent = {Text(text = LiushuiItem()[item].effectdateStr)},
                                                leadingContent = {
                                                    when {
                                                        name.contains("淋浴") ->  Icon(painterResource(R.drawable.bathtub), contentDescription = "")
                                                        name.contains("网") -> Icon(painterResource(R.drawable.net), contentDescription = "")
                                                        name.contains("餐饮") -> Icon(painterResource(R.drawable.restaurant), contentDescription = "")
                                                        name.contains("电") -> Icon(painterResource(R.drawable.flash_on), contentDescription = "")
                                                        name.contains("超市") || name.contains("贸易") || name.contains("商店") -> Icon(painterResource(R.drawable.storefront), contentDescription = "",)
                                                        name.contains("打印") -> Icon(painterResource(R.drawable.print), contentDescription = "",)
                                                        name.contains("充值") -> Icon(painterResource(R.drawable.add_card), contentDescription = "",)
                                                        name.contains("补助") -> Icon(painterResource(R.drawable.payments), contentDescription = "",)
                                                        else ->  Icon(painterResource(R.drawable.paid), contentDescription = "")
                                                    }
                                                }
                                            )



                                        }
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {

                                        OutlinedButton(
                                            onClick = {
                                                CoroutineScope(Job()).apply {
                                                    launch {
                                                        async {
                                                            if(page > 1) {
                                                                page--
                                                                loading = true
                                                                get()
                                                            }
                                                        }.await()
                                                        async {
                                                            delay(500)
                                                            loading = false
                                                            LiushuiItem()
                                                        }
                                                    }
                                                }

                                            }) { Text(text = "上一页") }

                                        Spacer(modifier = Modifier.width(15.dp))

                                        OutlinedButton(
                                            onClick = {
                                                page = 1
                                                loading = true
                                                get() }
                                        ) { Text(text = "${page} / ${prefs.getInt("totalpage",1)}") }

                                        Spacer(modifier = Modifier.width(15.dp))

                                        OutlinedButton(
                                            onClick = {
                                                CoroutineScope(Job()).apply {
                                                    launch {
                                                        async {
                                                            if ( page <= prefs.getInt("totalpage",1)) {
                                                                page++
                                                                loading = true
                                                                get()
                                                            }

                                                        }.await()
                                                        async {
                                                            delay(500)
                                                            loading = false
                                                            LiushuiItem()
                                                        }
                                                    }
                                                }

                                            }) { Text(text = "下一页") }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    ListItem(
        headlineContent = { Text(text = "一卡通   ${card} 元") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.credit_card),
                contentDescription = "Localized description",
            )
        },
        trailingContent={
            Row {
                FilledTonalIconButton(
                    modifier = Modifier.scale(scale.value),
                    interactionSource = interactionSource1,
                    onClick = { page = 1
                        get() })
                {
                    Icon( painterResource(R.drawable.attach_money),
                        contentDescription = "Localized description",)
                }
                FilledTonalIconButton(
                    modifier = Modifier.scale(scale2.value),
                    interactionSource = interactionSource2,
                    onClick = { OpenAlipay.openAlipay() }
                ) {
                    Icon( painterResource(R.drawable.add),
                        contentDescription = "Localized description",)
                }

            }

        },
        modifier = Modifier.clickable { page = 1
            get() }
    )


}