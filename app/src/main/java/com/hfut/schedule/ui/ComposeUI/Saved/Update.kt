package com.hfut.schedule.ui.ComposeUI.Saved

import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.ComposeUI.Focus.AddedItems
import com.hfut.schedule.ui.ComposeUI.Focus.MySchedule
import com.hfut.schedule.ui.ComposeUI.Focus.MyWangKe
import com.hfut.schedule.ui.ComposeUI.Focus.zjgdcard
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.getNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

suspend fun NetWorkUpdate(vm : LoginSuccessViewModel, vm2 : LoginViewModel){
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    CoroutineScope(Job()).apply {
        async { MyWangKe() }
        async { vm2.My() }
        async { MySchedule() }
        async { AddedItems() }
        async { getNotifications() }
        async { CommuityTOKEN?.let { vm.Exam(it) } }
        async { CommuityTOKEN?.let { vm.GetCourse(it) } }
        async { zjgdcard(vm) }.await()
    }
}