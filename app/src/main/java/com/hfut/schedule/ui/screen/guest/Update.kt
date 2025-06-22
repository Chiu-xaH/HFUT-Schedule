package com.hfut.schedule.ui.screen.guest

import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.getHolidayYear
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun initGuestNetwork(vm : NetWorkViewModel, vm2 : LoginViewModel) = withContext(Dispatchers.IO) {
    // 更新节假日
    if(DateTimeManager.Date_yyyy != getHolidayYear()) {
        launch { vm.downloadHoliday() }
    }
    //检查更新
    launch { vm.getUpdate() }
    // API接口
    launch { vm2.getMyApi() }
}