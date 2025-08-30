package com.hfut.schedule.ui.screen.home

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.HolidayBean
import com.hfut.schedule.logic.model.HolidayResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.cube.sub.getElectricFromHuiXin
import com.hfut.schedule.ui.screen.home.cube.sub.getWebInfoFromHuiXin
import com.hfut.schedule.ui.screen.home.focus.funiction.initCardNetwork
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun getJxglstuCookie(vm: NetWorkViewModel) : String? {
    var cookie : String?
    if(vm.webVpn) {
        val webVpnCookie = DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }
        cookie = MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    } else {
        cookie =  prefs.getString("redirect", "")
    }
    return cookie
}

suspend fun getWxAuth() : String? {
    val wx = DataStoreManager.wxAuth.first()
    if(!wx.contains("Bearer")) {
        return null
    }
    return wx
}

suspend fun getStorageJxglstuCookie(isWebVpn : Boolean) : String? {
    var cookie : String?
    if(isWebVpn) {
        val webVpnCookie = DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }
        cookie = MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    } else {
        cookie =  prefs.getString("redirect", "")
    }
    return cookie
}
// 应用冷启动主界面时的网络请求
suspend fun initNetworkRefresh(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel, ifSaved : Boolean) = withContext(
    Dispatchers.IO) {
    val isXuanCheng = getCampusRegion() == CampusRegion.XUANCHENG
    val communityToken = prefs.getString("TOKEN","")
    val showEle = prefs.getBoolean("SWITCHELE", isXuanCheng)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",isXuanCheng)
    val showCard = prefs.getBoolean("SWITCHCARD",true)
    val webVpnCookie = DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }

    val cookie =  getJxglstuCookie(vm)
    // 刷新个人接口
    launch { vm2.getMyApi() }
    // 用于更新ifSaved
    launch {
        vm.getStudentId(cookie!!)
        val studentId = (vm.studentId.state.value as? UiState.Success)?.data
        if(studentId == null) {
            // 切换到WEBVPN模式尝试
            vm.webVpn = true
            vm.updateServices()
            val c = MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
            vm.getStudentId(c)
            val studentId = (vm.studentId.state.value as? UiState.Success)?.data
            if(studentId == null) {
                // 复原
                vm.webVpn = false
                vm.updateServices()
                return@launch
            }
            launch { vm.getBizTypeId(c,studentId) }
            launch { vm.getExamJXGLSTU(c) }
        } else {
            launch { vm.getBizTypeId(cookie,studentId) }
            launch { vm.getExamJXGLSTU(cookie) }
        }
    }
    // 更新课程表
    if(!ifSaved)
        launch { updateCourses(vm,vmUI) }
    // 更新社区
    communityToken?.let {
        launch { vm.getCoursesFromCommunity(it) }
        launch { vm.getFriends(it) }
        if(showToday)
            launch {
                vm.todayFormCommunityResponse.clear()
                vm.getToday(communityToken)
            }
    }
    //检查更新
    launch { vm.getUpdate() }
    // 更新聚焦卡片
    if(showWeb && getCampusRegion() == CampusRegion.XUANCHENG)
        launch { getWebInfoFromHuiXin(vm,vmUI) }
    if(showEle)
        launch { getElectricFromHuiXin(vm, vmUI) }
    if(showCard)
        launch { initCardNetwork(vm,vmUI) }
    launch {
        val showWeather = DataStoreManager.enableShowFocusWeatherWarn.first()
        val state = vm.weatherWarningData.state.first() // 只发送一次请求 API有次数限制
        if(showWeather && state  !is UiState.Success) {
            vm.getWeatherWarn(getCampusRegion())
        }
    }
    // 更新节假日信息
    if(DateTimeManager.Date_yyyy != getHolidayYear()) {
        launch { vm.downloadHoliday() }
    }
    launch {
        if(vm.wxPersonInfoResponse.state.first() is UiState.Success) {
            return@launch
        }
        // 检查指尖工大是否失效
        val auth = DataStoreManager.wxAuth.first()
        if(auth.contains("Bearer")) {
            vm.wxGetPersonInfo(auth)
            val bean = (vm.wxPersonInfoResponse.state.value as? UiState.Success)?.data
            if(bean == null) {
                // 重新登陆
                val newAuth = refreshWxAuth(vm) ?: return@launch
                showToast("已登录指尖工大平台")
                vm.wxGetPersonInfo(newAuth)
            }
            // 仍有效
        } else {
            // 第一次登陆
            val newAuth = refreshWxAuth(vm) ?: return@launch
            showToast("首次登录指尖工大平台成功")
            vm.wxGetPersonInfo(newAuth)
        }
    }
}


private suspend fun refreshWxAuth(vm: NetWorkViewModel) : String? = withContext(Dispatchers.IO) {
    vm.wxLoginResponse.clear()
    vm.wxLogin()
    when(vm.wxLoginResponse.state.first()) {
        is UiState.Success<*> ->  return@withContext DataStoreManager.wxAuth.first()
        else -> return@withContext null
    }
}

//更新教务课表与课程汇总
suspend fun updateCourses(vm: NetWorkViewModel, vmUI: UIViewModel) = withContext(Dispatchers.IO) {
    val webVpnCookie = DataStoreManager.webVpnCookies.first { it.isNotEmpty() }

    val cookie = if (!vm.webVpn) {
            prefs.getString("redirect", "") ?: return@withContext
        } else {
            if(webVpnCookie.isEmpty()) {
                return@withContext
            } else {
                MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
            }
        }

    if(vm.studentId.state.first() !is UiState.Success) {
        vm.getStudentId(cookie)
    }
    val studentId = (vm.studentId.state.value as? UiState.Success)?.data ?: return@withContext
    if(vm.bizTypeIdResponse.state.first() !is UiState.Success) {
        vm.getBizTypeId(cookie,studentId)
    }
    val bizTypeId = (vm.bizTypeIdResponse.state.value as? UiState.Success)?.data ?: return@withContext
    vm.getLessonIds(cookie, studentId = studentId, bizTypeId = bizTypeId)
    val lessonResponse = (vm.lessonIds.state.value as? UiState.Success)?.data ?: return@withContext
    vm.getLessonTimes(cookie,lessonResponse.timeTableLayoutId)
    vm.getDatum(cookie,lessonResponse.lessonIds)
    val datum = (vm.datumData.state.value as? UiState.Success)?.data ?: return@withContext
    vmUI.refreshJxglstuCourseScheduleList(datum)
}

private fun getHoliday() : HolidayResponse? {
    val json = prefs.getString("HOLIDAY",null)
    return try {
        Gson().fromJson(json, HolidayResponse::class.java)
    } catch (e : Exception) {
        null
    }
}

fun getHolidayYear() : String? = getHoliday()?.year

fun getHolidays() : List<HolidayBean> = getHoliday()?.days ?: emptyList()


