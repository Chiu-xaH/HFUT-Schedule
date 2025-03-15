package com.hfut.schedule.ui.activity.card.function

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.beans.zjgd.ChangeLimitResponse
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.TransplantListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode


fun Click(vm : NetWorkViewModel, limit : String, amt : String) {
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
fun CardLimit(vm : NetWorkViewModel, vmUI : UIViewModel) {
    var limit by remember { mutableStateOf((vmUI.CardValue.value?.autotrans_limite ?: SharePrefs.prefs.getString("card_limit","0"))) }
    var amt by remember { mutableStateOf(vmUI.CardValue.value?.autotrans_amt?: SharePrefs.prefs.getString("card_amt","0")) }


    var limitFloat = limit?.toFloat()
    var amtFloat = amt?.toFloat()

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
           // var sliderPosition by remember { mutableStateOf(prefss!!.toFloat()) }
            val bd = BigDecimal(limit.toString())
            val limits = bd.setScale(0, RoundingMode.HALF_UP).toString()
            val bd2 = BigDecimal(amt.toString())
            val amts = bd.setScale(0, RoundingMode.HALF_UP).toString()
            //  var Num by remember { mutableStateOf(sliderPosition.toString()) }
            TransplantListItem(
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
            TransplantListItem(
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