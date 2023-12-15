package com.hfut.schedule.ui.ComposeUI.Search.SchoolCard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.OpenAlipay
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.datamodel.zjgd.BillResponse
import com.hfut.schedule.logic.datamodel.zjgd.records
import com.hfut.schedule.ui.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SchoolCardItem(vm : LoginSuccessViewModel) {


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

    val sheetState_Bills = rememberModalBottomSheetState()
    var showBottomSheet_Bills by remember { mutableStateOf(false) }

    val sheetState_Range = rememberModalBottomSheetState()
    var showBottomSheet_Range by remember { mutableStateOf(false) }

    val sheetState_Month = rememberModalBottomSheetState()
    var showBottomSheet_Month by remember { mutableStateOf(false) }


    val sheetState_Search = rememberModalBottomSheetState()
    var showBottomSheet_Search by remember { mutableStateOf(false) }

    val sheetState_Settings = rememberModalBottomSheetState()
    var showBottomSheet_Settings by remember { mutableStateOf(false) }


    var page by remember { mutableStateOf(1) }

    var loading by remember { mutableStateOf(true) }



    fun BillItem() :MutableList<records> {
        val billjson = prefs.getString("cardliushui",MyApplication.NullBill)
        val bill = Gson().fromJson(billjson, BillResponse::class.java)
        val data = bill.data.records
        val msg = bill.data.msg
        var BillItems = mutableListOf<records>()
        val totalpage = bill.data.pages

        SharePrefs.Save("totalpage",totalpage.toString())

        if (msg != null) {
            if (msg.contains("成功")) {
                val cardAccount = bill.data.records[0].fromAccount
                SharePrefs.Save("cardAccount", cardAccount)
            } else { Toast.makeText(MyApplication.context,msg,Toast.LENGTH_SHORT).show() }
        }
        data.forEach {  BillItems.add(it) }
        return BillItems
    }

    fun get() {
        val auth = prefs.getString("auth","")
        vm.CardGet("bearer " + auth,page)
        showBottomSheet_Bills = true
    }


    if (showBottomSheet_Search) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Search = false },
            sheetState = sheetState_Search
        ) { SearchBillsUI(vm) }
    }

    if(showBottomSheet_Range) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Range = false },
            sheetState = sheetState_Range
        ) { SelecctDateRange(vm) }
    }


    if(showBottomSheet_Month) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Month = false },
            sheetState = sheetState_Month
        ) { MonthBillsUI(vm) }
    }

    if (showBottomSheet_Settings) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Settings = false },
            sheetState = sheetState_Settings
        ) { CardLimit(vm) }
    }

    if (showBottomSheet_Bills) {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    delay(1000)
                    val billjson = prefs.getString("cardliushui", MyApplication.NullBill)
                    if (billjson != null) {
                        if (billjson.contains("操作成功")) BillItem()
                        else {
                            val ONE = prefs.getString("ONE","")
                            val TGC = prefs.getString("TGC","")
                            vm.OneGotoCard(ONE + ";" + TGC)

                           MyToast("空数据,请再次尝试或登录")
                        }
                    }
                }.await()
                async { loading = false }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Bills = false },
            sheetState = sheetState_Bills
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("一卡通 服务") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
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
                                var date = GetDate.Date_yyyy_MM_dd
                                for (item in 0 until BillItem().size) {
                                    val get = BillItem()[item].effectdateStr
                                    val name = BillItem()[item].resume
                                    val todaydate = get?.substringBefore(" ")
                                    var num = BillItem()[item].tranamt.toString()

                                    num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)

                                    val num_float = num.toFloat()

                                    if (date == todaydate) {
                                        if (name.contains("充值") == false) todaypay += num_float
                                    }

                                }

                                Column {
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

                                        AssistChip(
                                            onClick = { OpenAlipay.openAlipay() },
                                            label = { Text(text = "充值") },
                                            leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))


                                        AssistChip(
                                            onClick = { showBottomSheet_Range = true },
                                            label = { Text(text = "范围支出") },
                                            leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        AssistChip(
                                            onClick = { showBottomSheet_Month = true },
                                            label = { Text(text = "月份查询") },
                                            leadingIcon = { Icon(painter = painterResource(R.drawable.calendar_view_month), contentDescription = "description") }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(0.dp))

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

                                        AssistChip(
                                            onClick = { showBottomSheet_Search = true },
                                            label = { Text(text = "搜索") },
                                            leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))

                                        AssistChip(
                                            onClick = { showBottomSheet_Settings = true },
                                            label = { Text(text = "限额修改") },
                                            leadingIcon = { Icon(painter = painterResource(R.drawable.price_change), contentDescription = "description") }
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        AssistChip(
                                            onClick = { Toast.makeText(MyApplication.context,"暂未开发(等开发者丢卡了再做)",Toast.LENGTH_SHORT).show() },
                                            label = { Text(text = "挂失解挂") },
                                            leadingIcon = { Icon(painterResource(id = R.drawable.error), contentDescription = "description") }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))


                                    LazyColumn {


                                        if (page == 1){

                                            item {
                                                val now = prefs.getString("card_now","0.0")
                                                val settle = prefs.getString("card_settle","0.0")
                                                val num = todaypay.toString()
                                                val bd = BigDecimal(num)
                                                val str = bd.setScale(2, RoundingMode.HALF_UP).toString()


                                                    Card(
                                                        elevation = CardDefaults.cardElevation(
                                                            defaultElevation = 3.dp
                                                        ),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 15.dp,
                                                                vertical = 5.dp
                                                            ),
                                                        shape = MaterialTheme.shapes.medium
                                                    ) {
                                                        Row {
                                                            ListItem(
                                                                headlineContent = { Text(text = "余额 ${now} 元") },
                                                                modifier = Modifier.width(185.dp),
                                                                supportingContent = { Text(text = "待圈存 ${settle} 元")},
                                                                leadingContent = { Icon(painterResource(R.drawable.account_balance_wallet), contentDescription = "Localized description",) },
                                                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                                            )
                                                            ListItem(
                                                                headlineContent = { Text(text = "${str} 元") },
                                                                supportingContent = { Text(text = "今日消费")},
                                                                leadingContent = { Icon(painterResource(R.drawable.send_money), contentDescription = "Localized description",) },
                                                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                                            )
                                                        }
                                                    }
                                            }

                                        }
                                        items(BillItem().size) { item ->
                                            var num = BillItem()[item].tranamt.toString()
                                            num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)
                                            val big = BigDecimal(num)
                                            val num_float = big.toFloat()

                                            var name = BillItem()[item].resume
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
                                                        .padding(
                                                            horizontal = 15.dp,
                                                            vertical = 5.dp
                                                        ),
                                                    shape = MaterialTheme.shapes.medium
                                                ) {
                                                    ListItem(
                                                        headlineContent = { Text(text = name) },
                                                        supportingContent = { Text(text = pay) },
                                                        overlineContent = { Text(text = BillItem()[item].effectdateStr) },
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
                                            val totalpage = prefs.getString("totalpage","1")
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
                                                                    BillItem()
                                                                }
                                                            }
                                                        }

                                                    }) { Text(text = "上一页") }

                                                Spacer(modifier = Modifier.width(15.dp))

                                                OutlinedButton(
                                                    onClick = {
                                                        CoroutineScope(Job()).apply {
                                                            launch {
                                                                async {
                                                                        page = 1
                                                                        loading = true
                                                                        get()
                                                                }.await()
                                                                async {
                                                                    delay(500)
                                                                    loading = false
                                                                    BillItem()
                                                                }
                                                            }
                                                        } }
                                                ) { Text(text = "${page} / ${totalpage}") }

                                                Spacer(modifier = Modifier.width(15.dp))

                                                OutlinedButton(
                                                    onClick = {
                                                        CoroutineScope(Job()).apply {
                                                            launch {
                                                                async {
                                                                    if ( page < totalpage?.toInt() ?: 1) {
                                                                        page++
                                                                        loading = true
                                                                        get()
                                                                    }

                                                                }.await()
                                                                async {
                                                                    delay(500)
                                                                    loading = false
                                                                    BillItem()
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
        modifier = Modifier.clickable {
            page = 1
            get() }
    )


}