package com.hfut.schedule.ui.Activity.success.main.saved

import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.focus.Focus.AddedItems
import com.hfut.schedule.ui.Activity.success.focus.Focus.MySchedule
import com.hfut.schedule.ui.Activity.success.focus.Focus.MyWangKe
import com.hfut.schedule.ui.Activity.success.focus.Focus.GetZjgdCard
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.getNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

suspend fun NetWorkUpdate(vm : LoginSuccessViewModel, vm2 : LoginViewModel,vmUI : UIViewModel,webVpn : Boolean){
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    val cookie = if(!webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")
    CoroutineScope(Job()).apply {
        async { MyWangKe() }
        async { vm2.My() }
        async { MySchedule() }
        async { AddedItems() }
        async { getNotifications() }
      //  if(ifSaved)   async { CommuityTOKEN?.let { vm.Exam(it) } }
    //    else
            async { vm.getExamJXGLSTU(cookie!!) }
        async { CommuityTOKEN?.let { vm.GetCourse(it) } }
        async { GetZjgdCard(vm,vmUI) }.await()
    }
}