package com.hfut.schedule.ui.Activity.card.main

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.Enums.CardBarItems
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.datamodel.zjgd.BillResponse
import com.hfut.schedule.logic.datamodel.zjgd.records
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.card.bills.main.CardBills
import com.hfut.schedule.ui.Activity.card.counts.main.CardCounts
import com.hfut.schedule.ui.Activity.card.function.main.CardFunctions
import com.hfut.schedule.ui.Activity.success.focus.Focus.GetZjgdCard
import com.hfut.schedule.ui.UIUtils.MyToast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardUI(vm : LoginSuccessViewModel, activity : Activity,vmUI : UIViewModel) {

    var showBottomSheet_Bills by remember { mutableStateOf(false) }

    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR", AndroidVersion.sdkInt >= 32)
    var blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
    var page by remember { mutableStateOf(1) }
    var loading by remember { mutableStateOf(true) }


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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).50f else 1f),
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("一卡通") },
                    actions = {
                        IconButton(onClick = {
                            //关闭
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "")
                        }
                    }
                )
                Divider()
            }
        },
        bottomBar = {
            Divider()
            NavigationBar(containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                modifier = Modifier
                    .hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)) {

                val items = listOf(
                    NavigationBarItemData(
                        CardBarItems.BILLS.name,"账单", painterResource(R.drawable.receipt_long), painterResource(
                            R.drawable.receipt_long_filled)
                    ),
                    NavigationBarItemData(
                        CardBarItems.COUNT.name,"统计", painterResource(R.drawable.leaderboard),
                        painterResource(R.drawable.leaderboard_filled)
                    ),
                    NavigationBarItemData(
                        CardBarItems.FUNCTION.name,"功能", painterResource(R.drawable.cube), painterResource(
                            R.drawable.deployed_code_filled)
                    )
                )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = { Text(text = item.label) },
                        icon = {
                            BadgedBox(badge = {}) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                        }
                    )
                }
            }
        }
    ) {innerPadding ->
        NavHost(navController = navController, startDestination = CardBarItems.BILLS.name, modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,
            )) {
            composable(CardBarItems.BILLS.name) { CardBills(vm,innerPadding,vmUI) }
            composable(CardBarItems.COUNT.name) {  CardCounts(vm, innerPadding) }
            composable(CardBarItems.FUNCTION.name) { CardFunctions(vm,innerPadding,vmUI) }
        }

    }
}