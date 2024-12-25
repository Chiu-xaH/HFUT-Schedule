package com.hfut.schedule.ui.activity.home.search.functions.grade

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.activity.funiction.GradeActivity
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.SharePrefs.saveString
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.grade.getGrade
import com.hfut.schedule.ui.activity.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.activity.grade.grade.jxglstu.GradeItemUIJXGLSTU
import com.hfut.schedule.ui.utils.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Grade(vm : NetWorkViewModel, ifSaved : Boolean, webVpn : Boolean)  {
    val cookie = if(!webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")

    if(!ifSaved)
    vm.getGrade(cookie!!,null)

    val sheetState_Grade = rememberModalBottomSheetState()
    var showBottomSheet_Grade by remember { mutableStateOf(false) }
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var term = ""
    val month = DateTimeManager.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var years = DateTimeManager.Date_yyyy
    if (month <= 8) years = (years.toInt() - 1).toString()

    CoroutineScope(Job()).launch {
        async{ CommuityTOKEN?.let { vm.getGrade(it,years+"-"+(years.toInt()+1),term) } }
        async { CommuityTOKEN?.let { vm.getAvgGrade(it) } }
        async {  CommuityTOKEN?.let { vm.getAllAvgGrade(it) } }
    }




    ListItem(
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
            getGrade()
            saveString("GradeNum", getGrade().size.toString())
            //showBottomSheet_Grade = true
            val it = Intent(MyApplication.context, GradeActivity::class.java).apply {
                putExtra("saved",ifSaved)
                putExtra("webVpn",webVpn)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            MyApplication.context.startActivity(it)
        }
    )


    if (showBottomSheet_Grade) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Grade = false },
            sheetState = sheetState_Grade,
            shape = Round(sheetState_Grade)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("成绩") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    if (ifSaved) GradeItemUI(vm,innerPadding)
                    else GradeItemUIJXGLSTU(innerPadding,vm)
                }
            }
        }
    }
}


