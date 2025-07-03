package com.hfut.schedule.ui.screen.home

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.HolidayBean
import com.hfut.schedule.logic.model.HolidayResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.cube.sub.getElectricFromHuiXin
import com.hfut.schedule.ui.screen.home.cube.sub.getWebInfoFromHuiXin
import com.hfut.schedule.ui.screen.home.focus.funiction.initCardNetwork
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 应用冷启动主界面时的网络请求
suspend fun initNetworkRefresh(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel, ifSaved : Boolean) = withContext(
    Dispatchers.IO) {
    val isXuanCheng = getCampus() == Campus.XUANCHENG
    val communityToken = prefs.getString("TOKEN","")
    val showEle = prefs.getBoolean("SWITCHELE", isXuanCheng)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",isXuanCheng)
    val showCard = prefs.getBoolean("SWITCHCARD",true)
    val webVpnCookie = DataStoreManager.webVpnCookie.first()

    val cookie = if(!vm.webVpn) prefs.getString("redirect", "") else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    // 刷新个人接口
    launch { vm2.getMyApi() }
    // 用于更新ifSaved
    launch {
        vm.getStudentId(cookie!!)
        val studentId = (vm.studentId.state.value as? UiState.Success)?.data
        if(studentId == null) {
            val c = MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
            vm.getStudentId(c)
            val studentId = (vm.studentId.state.value as? UiState.Success)?.data ?: return@launch
            vm.webVpn = true
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
    if(showWeb && getCampus() == Campus.XUANCHENG)
        launch { getWebInfoFromHuiXin(vm,vmUI) }
    if(showEle)
        launch { getElectricFromHuiXin(vm, vmUI) }
    if(showCard)
        launch { initCardNetwork(vm,vmUI) }
    launch {
        val showWeather = DataStoreManager.showFocusWeatherWarn.first()
        val state = vm.weatherWarningData.state.first() // 只发送一次请求 API有次数限制
        if(showWeather && state  !is UiState.Success) {
            vm.getWeatherWarn(getCampus())
        }
    }
    // 更新节假日信息
    if(DateTimeManager.Date_yyyy != getHolidayYear()) {
        launch { vm.downloadHoliday() }
    }
}


//更新教务课表与课程汇总
suspend fun updateCourses(vm: NetWorkViewModel, vmUI: UIViewModel) = withContext(Dispatchers.IO) {
    val webVpnCookie = DataStoreManager.webVpnCookie.first()

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
//    val studentIdObserver = Observer<Int> { result ->
//        if (result != 0) {
//            SharedPrefs.saveString("studentId", result.toString())
//            CoroutineScope(Job()).launch {
//                async { vm.getBizTypeId(cookie!!) }.await()
//            }
//        }
//    }
//    val getBizTypeIdObserver = Observer<String?> { result ->
//        if(result != null) {
//            // 开始解析
//            val bizTypeId = CasInHFUT.bizTypeId ?: CasInHFUT.getBizTypeId(result)
//            if(bizTypeId != null) {
//                vm.getLessonIds(cookie!!,bizTypeId,vm.studentId.value.toString())
//            }
//        }
//    }
//    val lessonIdObserver = Observer<List<Int>> { result ->
//        if (result.toString() != "") {
//            val lessonIdsArray = JsonArray()
//            result.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
//            val jsonObject = JsonObject().apply {
//                add("lessonIds", lessonIdsArray)//课程ID
//                addProperty("studentId", vm.studentId.value)//学生ID
//                addProperty("weekIndex", "")
//            }
//            vm.getDatum(cookie!!, jsonObject)
//            vm.bizTypeIdResponse.removeObserver(getBizTypeIdObserver)
//            vm.studentId.removeObserver(studentIdObserver)
//        }
//    }
//    val datumObserver = Observer<String?> { result ->
//        if (result != null) {
//            if (result.contains("result")) {
//                // 刷新缓存
//                vmUI.refreshJxglstuCourseScheduleList(result)
//                Handler(Looper.getMainLooper()).post { vm.lessonIds.removeObserver(lessonIdObserver) }
//            }
//        }
//    }

//    Handler(Looper.getMainLooper()).post {
//        vm.studentId.observeForever(studentIdObserver)
//        vm.bizTypeIdResponse.observeForever(getBizTypeIdObserver)
//        vm.lessonIds.observeForever(lessonIdObserver)
//        vm.datumData.observeForever(datumObserver)
//    }
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


