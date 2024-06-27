package com.hfut.schedule.ui.Activity.success.cube.Settings.Items

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.datamodel.SearchEleResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.focus.Focus.GetZjgdCard
import com.hfut.schedule.ui.Activity.success.focus.Focus.TodayUI
import com.hfut.schedule.ui.Activity.success.focus.Focus.getTodayNet
import com.hfut.schedule.ui.Activity.success.search.Search.Electric.Electric
import com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb.LoginWeb
import com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb.getWebInfos
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun FocusCardSettings() {


    val switch_ele = SharePrefs.prefs.getBoolean("SWITCHELE", true)
    var showEle by remember { mutableStateOf(switch_ele) }
    SharePrefs.SaveBoolean("SWITCHELE", true, showEle)

    val switch_web = SharePrefs.prefs.getBoolean("SWITCHWEB", true)
    var showWeb by remember { mutableStateOf(switch_web) }
    SharePrefs.SaveBoolean("SWITCHWEB", true, showWeb)

    val switch_card = SharePrefs.prefs.getBoolean("SWITCHCARD", true)
    var showCard by remember { mutableStateOf(switch_card) }
    SharePrefs.SaveBoolean("SWITCHCARD", true, showCard)

    val switch_today = SharePrefs.prefs.getBoolean("SWITCHTODAY", true)
    var showToday by remember { mutableStateOf(switch_today) }
    SharePrefs.SaveBoolean("SWITCHTODAY", true, showToday)


    val switch_card_add = SharePrefs.prefs.getBoolean("SWITCHCARDADD", true)
    var showCardAdd by remember { mutableStateOf(switch_card_add) }
    SharePrefs.SaveBoolean("SWITCHCARDADD", true, showCardAdd)


    val switch_ele_add = SharePrefs.prefs.getBoolean("SWITCHELEADD", true)
    var showEleAdd by remember { mutableStateOf(switch_ele_add) }
    SharePrefs.SaveBoolean("SWITCHELEADD", true, showEleAdd)



    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ){
        ListItem(headlineContent = { Text(text = "打开开关则会在APP启动时自动获取信息,并显示在聚焦即时卡片内，如需减少性能开销可按需开启或关闭") }, leadingContent = { Icon(
            painter = painterResource(id = R.drawable.info),
            contentDescription = ""
        )})
    }


    Spacer(modifier = Modifier.height(5.dp))

    ListItem(
        headlineContent = { Text(text = "一卡通")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.credit_card), contentDescription = "")},
        trailingContent = {
            Row {
                Switch(checked = showCardAdd, onCheckedChange = {showch -> showCardAdd = showch}, thumbContent = { Icon(painter = painterResource(id = R.drawable.add), contentDescription = "")})
                Spacer(modifier = Modifier.width(10.dp))
                Switch(checked = showCard, onCheckedChange = {showch -> showCard = showch})
            }
        }
    )
    ListItem(
        headlineContent = { Text(text = "寝室电费")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.flash_on), contentDescription = "")},
        trailingContent = {
            Row {
                Switch(checked = showEleAdd, onCheckedChange = {showch -> showEleAdd = showch },thumbContent = { Icon(painter = painterResource(id = R.drawable.add), contentDescription = "")})
                Spacer(modifier = Modifier.width(10.dp))
                Switch(checked = showEle, onCheckedChange = {showch -> showEle = showch })
            }
        }
    )
    ListItem(
        headlineContent = { Text(text = "校园网")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.net), contentDescription = "")},
        trailingContent = { Switch(checked = showWeb, onCheckedChange = {showch -> showWeb = showch})}
    )
    ListItem(
        headlineContent = { Text(text = "聚焦通知")} ,
        supportingContent = { Text(text = "明日早八,临近课程,催还图书,临近考试")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.sentiment_very_satisfied), contentDescription = "")},
        trailingContent = { Switch(checked = showToday, onCheckedChange = {showch -> showToday = showch})}
    )
    ListItem(
        headlineContent = { Text(text = "倒计时")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.schedule), contentDescription = "")},
        trailingContent = { Switch(checked = false, onCheckedChange = {}, enabled = false)}
    )
    ListItem(
        headlineContent = { Text(text = "绩点排名")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.hive), contentDescription = "")},
        trailingContent = { Switch(checked = false, onCheckedChange = {}, enabled = false)}
    )
    ListItem(
        headlineContent = { Text(text = "寝室评分")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.psychiatry), contentDescription = "")},
        trailingContent = { Switch(checked = false, onCheckedChange = {}, enabled = false)}
    )
    ListItem(
        headlineContent = { Text(text = "图书借阅")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.book), contentDescription = "")},
        trailingContent = { Switch(checked = true, onCheckedChange = {}, enabled = false)}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FocusCard(vmUI : UIViewModel,vm : LoginSuccessViewModel) {
    val showEle = prefs.getBoolean("SWITCHELE",true)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",true)
    val showCard = prefs.getBoolean("SWITCHCARD",true)

    CoroutineScope(Job()).launch {
        async {
            if(showWeb)
                getWeb(vmUI)
        }
        async {
            if(showEle)
                getEle(vm, vmUI)
        }
        async {
            if(showToday)
                getTodayNet(vm,vmUI)
        }
        async {
            if(showCard)
                GetZjgdCard(vm,vmUI)
        }
    }
    if(showCard || showEle || showToday || showWeb)
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ){
            if(showCard || showToday)
            Row {
                if(showCard)
                Box(modifier = Modifier.weight(.5f)) {
                    SchoolCardItem(vmUI,true)
                }
                if(showToday)
                Box(modifier = Modifier
                    .weight(.5f)) {
                    TodayUI()
                }
            }
            if(showWeb || showEle)
            Row {
                if(showEle)
                Box(modifier = Modifier.weight(.5f)) {
                    Electric(vm,true,vmUI)
                }
                if(showWeb)
                Box(modifier = Modifier
                    .weight(.5f)) {
                    LoginWeb(vmUI,true)
                }
            }
        }
    }
}

fun getWeb(vmUI : UIViewModel)  {
    CoroutineScope(Job()).launch {
        Handler(Looper.getMainLooper()).post{
            vmUI.infoValue.observeForever { result ->
                if (result != null)
                    if(result.contains("flow")) {
                        vmUI.webValue.value = getWebInfos(result)
                        SharePrefs.Save("memoryWeb", vmUI.webValue.value?.flow)
                    }
            }
        }
    }
}
fun getEle(vm : LoginSuccessViewModel,vmUI : UIViewModel) {
    val BuildingsNumber = prefs.getString("BuildNumber", "0")
    val RoomNumber = prefs.getString("RoomNumber", "")
    val EndNumber = prefs.getString("EndNumber", "")

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
    var jsons = "{ \"query_elec_roominfo\": { \"aid\":\"0030000000007301\", \"account\": \"24027\",\"room\": { \"roomid\": \"${input}\", \"room\": \"${input}\" },  \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"\", \"building\": \"\" },\"extdata\":\"info1=\" } }"

    CoroutineScope(Job()).launch {
        async { vm.searchEle(jsons) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.ElectricData.observeForever { result ->
                    if (result?.contains("query_elec_roominfo") == true) {
                        var msg = Gson().fromJson(result, SearchEleResponse::class.java).query_elec_roominfo.errmsg
                        if(msg.contains("剩余金额")) {
                            val bd = BigDecimal(msg.substringAfter("剩余金额").substringAfter(":"))
                            vmUI.electricValue.value =  bd.setScale(2, RoundingMode.HALF_UP).toString()
                            Save("memoryEle",vmUI.electricValue.value)
                        }
                    }
                }
            }
        }
    }
}
