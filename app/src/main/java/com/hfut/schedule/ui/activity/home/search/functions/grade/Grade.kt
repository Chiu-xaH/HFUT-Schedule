package com.hfut.schedule.ui.activity.home.search.functions.grade

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.activity.screen.GradeActivity
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.WebVpn
import com.hfut.schedule.ui.activity.grade.getGrade
import com.hfut.schedule.ui.utils.components.TransplantListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Grade(vm : NetWorkViewModel, ifSaved : Boolean)  {
//    val cookie = if(!webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")

//    if(!ifSaved)
//    vm.getGrade(cookie!!,null)
//
//    val sheetState_Grade = rememberModalBottomSheetState()
//    var showBottomSheet_Grade by remember { mutableStateOf(false) }
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var term = ""
    val month = DateTimeUtils.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var years = DateTimeUtils.Date_yyyy
    if (month <= 8) years = (years.toInt() - 1).toString()

    CoroutineScope(Job()).launch {
        async{ CommuityTOKEN?.let { vm.getGrade(it,years+"-"+(years.toInt()+1),term) } }
        async { CommuityTOKEN?.let { vm.getAvgGrade(it) } }
        async {  CommuityTOKEN?.let { vm.getAllAvgGrade(it) } }
    }

//    val webVpn = vm.webVpn

    TransplantListItem(
        headlineContent = { Text(text = "成绩") },
        leadingContent = {
            BadgedBox(badge = {
                if(prefs.getString("GradeNum","0") != getGrade().size.toString())
                    Badge {
                        Text(text = getGrade().size.toString())
                    }
            }) { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) }
                         },
        modifier = Modifier.clickable {
            saveString("GradeNum", getGrade().size.toString())
            Starter.startGrade(vm,ifSaved)
        }
    )

}


