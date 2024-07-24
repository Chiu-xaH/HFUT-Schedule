package com.hfut.schedule.ui.Activity.card.function

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.datamodel.zjgd.ChangeLimitResponse
import com.hfut.schedule.ui.Activity.card.limitRow
import com.hfut.schedule.ui.UIUtils.WheelPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode


fun Click(vm : LoginSuccessViewModel,limit : String,amt : String) {
    val auth = SharePrefs.prefs.getString("auth","")
    val cardAccount = SharePrefs.prefs.getString("card_account","")
    val result = SharePrefs.prefs.getString("changeResult","{\"msg\":\"\"}")
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

    //Log.d("log",limits.toString()+ "  " + amts.toString())
    CoroutineScope(Job()).launch {
        async { auth?.let { vm.changeLimit(it,json) } }.await()
        async {
            delay(1000)
            Handler(Looper.getMainLooper()).post{
                val msg = Gson().fromJson(result,ChangeLimitResponse::class.java).msg
                Toast.makeText(MyApplication.context,msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLimit(vm : LoginSuccessViewModel,vmUI : UIViewModel) {
    var limit by remember { mutableStateOf((vmUI.CardValue.value?.autotrans_limite ?: SharePrefs.prefs.getString("card_limit","0"))) }
    var amt by remember { mutableStateOf(vmUI.CardValue.value?.autotrans_amt?: SharePrefs.prefs.getString("card_amt","0")) }


    var limitFloat = limit?.toFloat()
    var amtFloat = amt?.toFloat()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("限额") }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
           // var sliderPosition by remember { mutableStateOf(prefss!!.toFloat()) }
            val bd = BigDecimal(limit.toString())
            val limits = bd.setScale(0, RoundingMode.HALF_UP).toString()
            val bd2 = BigDecimal(amt.toString())
            val amts = bd.setScale(0, RoundingMode.HALF_UP).toString()
            //  var Num by remember { mutableStateOf(sliderPosition.toString()) }
            ListItem(
                headlineContent = { Text(text = "自主转账限额 ￥${limit}")},
                leadingContent = { Icon(painterResource(id = R.drawable.do_not_disturb_on), contentDescription = "") }
            )

            limitFloat?.let {
                Slider(
                    value = it,
                    onValueChange = {
                        limit = it.toString()
                        if (amtFloat != null) {
                            Click(vm,limitFloat.toInt().toString(),amtFloat.toInt().toString())
                        }
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 18,
                    valueRange = 10f..200f,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }
            ListItem(
                headlineContent = { Text(text = "自主转账金额 ￥${amt}")},
                leadingContent = { Icon(painterResource(id = R.drawable.do_not_disturb_on), contentDescription = "") }
            )

            amtFloat?.let {
                Slider(
                    value = it,
                    onValueChange = {
                        amt = it.toString()
                        if (limitFloat != null) {
                            Click(vm,limitFloat.toInt().toString(),amtFloat.toInt().toString())
                        }
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 18,
                    valueRange = 10f..200f,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }
        }
    }
}