package com.hfut.schedule.ui.screen.card.function

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.google.gson.JsonObject
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.slider.CustomSlider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private suspend fun click(vm : NetWorkViewModel, limit : String, amt : String) = withContext(
    Dispatchers.IO) {
    val auth = SharedPrefs.prefs.getString("auth","")
    val cardAccount = SharedPrefs.prefs.getString("card_account","")
    val json = JsonObject()
    val limits = "${limit}00".toInt()
    val amts = "${amt}00".toInt()

    json.apply {
        addProperty("account",cardAccount)
        addProperty("autotransFlag","2")
        addProperty("autotransAmt",amts)
        addProperty("autotransLimite",limits)
        addProperty("synAccessSource","h5")
    }
    auth?.let {
        vm.changeLimitResponse.clear()
        vm.changeLimit(it,json)
        // 主线程监听 StateFlow
        onListenStateHolder(vm.changeLimitResponse) { data ->
            showToast(data)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLimit(vm : NetWorkViewModel) {
    val cardValue by remember { derivedStateOf { GlobalUiStateHolder.cardValue } }

    var limit by remember { mutableStateOf((cardValue?.autoTransLimit ?: SharedPrefs.prefs.getString("card_limit","0"))) }
    var amt by remember { mutableStateOf(cardValue?.autoTransAmt?: SharedPrefs.prefs.getString("card_amt","0")) }


    val limitFloat = limit?.toFloat()
    val amtFloat = amt?.toFloat()
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("限额")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CardListItem(
                headlineContent = { Text("此接口即使修改也无效，不用改了")},
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                }
            )
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = "自主转账限额 ￥${limit}")},
                    leadingContent = { Icon(painterResource(id = R.drawable.do_not_disturb_on), contentDescription = "") }
                )
                PaddingHorizontalDivider()

                limitFloat?.let {
                    CustomSlider(
                        value = it,
                        onValueChange = {
                            limit = it.toString()
                            if (amtFloat != null) {
                                scope.launch {
                                    click(
                                        vm,
                                        limitFloat.toInt().toString(),
                                        amtFloat.toInt().toString()
                                    )
                                }
                            }
                        },
                        stepPadding = 10f,
                        valueRange = 10f..200f,
                    )
                }

            }

            CustomCard (color = cardNormalColor()){
                TransplantListItem(
                    headlineContent = { Text(text = "自主转账金额 ￥${amt}")},
                    leadingContent = { Icon(painterResource(id = R.drawable.do_not_disturb_on), contentDescription = "") }
                )
                PaddingHorizontalDivider()

                amtFloat?.let {
                    CustomSlider(
                        value = it,
                        onValueChange = {
                            amt = it.toString()
                            if (limitFloat != null) {
                                scope.launch {
                                    click(
                                        vm,
                                        limitFloat.toInt().toString(),
                                        amtFloat.toInt().toString()
                                    )
                                }
                            }
                        },
                        stepPadding = 10f,
                        valueRange = 10f..200f,
                    )
                }
            }
        }
    }
}