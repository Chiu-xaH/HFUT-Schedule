package com.hfut.schedule.ui.Activity.card.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.NetWorkViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.Enums.CardBarItems
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.datamodel.zjgd.BillResponse
import com.hfut.schedule.logic.datamodel.zjgd.records
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.card.function.main.HomeScreen
import com.hfut.schedule.ui.Activity.card.bills.main.CardBills
import com.hfut.schedule.ui.Activity.card.counts.CardHome
import com.hfut.schedule.ui.Activity.card.function.main.turnToBottomBar
import com.hfut.schedule.ui.Activity.success.focus.Focus.GetZjgdCard
import com.hfut.schedule.ui.UIUtils.CustomTabRow
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.bottomBarBlur
import com.hfut.schedule.ui.UIUtils.topBarBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardUI(vm : NetWorkViewModel, vmUI : UIViewModel) {

    val showBottomSheet_Bills by remember { mutableStateOf(false) }
    val animation by remember { mutableStateOf(prefs.getInt("ANIMATION",MyApplication.Animation)) }
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR", AndroidVersion.canBlur)
    val blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
    var page by remember { mutableStateOf(1) }
    var loading by remember { mutableStateOf(true) }
    var bottomBarItems by remember { mutableStateOf(CardBarItems.BILLS) }

    val pagerState = rememberPagerState(pageCount = { 4 })
    val titles = listOf("日","月","学期")


    GetZjgdCard(vm, vmUI)

    fun BillItem() :MutableList<records> {
        val billjson = vm.BillsData.value
        var BillItems = mutableListOf<records>()
        if(billjson?.contains("操作成功") == true){
            val bill = Gson().fromJson(billjson, BillResponse::class.java)
            val data = bill.data.records
            val msg = bill.data.msg
            val totalpage = bill.data.pages
            SharePrefs.Save("totalpage",totalpage.toString())
            if (msg != null) {
                if (msg.contains("成功")) {
                    val cardAccount = bill.data.records[0].fromAccount
                    SharePrefs.Save("cardAccount", cardAccount)
                } else { Toast.makeText(MyApplication.context,msg, Toast.LENGTH_SHORT).show() }
            }
            data.forEach {  BillItems.add(it) }
        }

        return BillItems
    }


    if (showBottomSheet_Bills) {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.BillsData.value = "{}"
                    }
                }.await()
                async {
                    //  delay(1000)
                    Handler(Looper.getMainLooper()).post{
                        vm.BillsData.observeForever { result ->
                            if(result != null) {
                                if(result.contains("操作成功")) {
                                    loading = false
                                    if (result.contains("操作成功")) BillItem()
                                    else {
                                        val ONE = prefs.getString("ONE","")
                                        val TGC = prefs.getString("TGC","")
                                        vm.OneGotoCard(ONE + ";" + TGC)
                                        MyToast("空数据,请再次尝试或登录")
                                    }
                                }
                            }
                        }
                    }
                }.await()
            }
        }
    }

    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.topBarBlur(hazeState, blur)) {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("一卡通") },
                    actions = {
                        IconButton(onClick = {
                            (context as? Activity)?.finish()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "")
                        }
                    }
                )
//                if(!blur) {
//                    if(CardBarItems.COUNT != bottomBarItems)
//                        Divider()
//                }
                if(bottomBarItems == CardBarItems.COUNT) {
                    CustomTabRow(pagerState, titles, blur)
                }
            }
        },
        bottomBar = {
            Column {
//                if(!blur) {
//                    Divider()
//                }
                NavigationBar(containerColor = Color.Transparent ,
                    modifier = Modifier
                        .bottomBarBlur(hazeState, blur)) {

                    val items = listOf(
                        NavigationBarItemData(
                            CardBarItems.HOME.name,"卡包", painterResource(R.drawable.credit_card), painterResource(
                                R.drawable.credit_card_filled)
                        ),
                        NavigationBarItemData(
                            CardBarItems.BILLS.name,"账单", painterResource(R.drawable.receipt_long), painterResource(
                                R.drawable.receipt_long_filled)
                        ),
                        NavigationBarItemData(
                            CardBarItems.COUNT.name,"统计", painterResource(R.drawable.leaderboard),
                            painterResource(R.drawable.leaderboard_filled)
                        )
                    )
                    items.forEach { item ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale = animateFloatAsState(
                            targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "" // 使用弹簧动画
                        )
                        val route = item.route
                        val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                        NavigationBarItem(
                            selected = selected,
                            modifier = Modifier.scale(scale.value),
                            interactionSource = interactionSource,
                            onClick = {
                                when(item) {
                                    items[0] -> bottomBarItems = CardBarItems.HOME
                                    items[1] -> bottomBarItems = CardBarItems.BILLS
                                    items[2] -> bottomBarItems = CardBarItems.COUNT
                                }
                                //     atEnd = !atEnd
                                if (!selected) { turnToBottomBar(navController, route) }
                            },
                            label = { Text(text = item.label) },
                            icon = {
                                BadgedBox(badge = {}) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                            }
                        )
                    }
                }
            }

        }
    ) {innerPadding ->
        NavHost(navController = navController,
            startDestination = CardBarItems.HOME.name,
            enterTransition = {
                scaleIn(animationSpec = tween(durationMillis = animation)) +
                        expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            exitTransition = {
                scaleOut(animationSpec = tween(durationMillis = animation)) +
                        shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            modifier = Modifier
            .haze(
                state = hazeState,
                //backgroundColor = MaterialTheme.colorScheme.surface,
            )) {
            composable(CardBarItems.HOME.name) {
                Scaffold {
                    HomeScreen(innerPadding,vm,navController,vmUI)
                }
            }
            composable(CardBarItems.BILLS.name) {
                Scaffold {
                    CardBills(vm,innerPadding,vmUI)
                }

            }
            composable(CardBarItems.COUNT.name) {
                Scaffold {
                    CardHome(innerPadding,vm,blur,pagerState)
                }
            }
        }
    }
}