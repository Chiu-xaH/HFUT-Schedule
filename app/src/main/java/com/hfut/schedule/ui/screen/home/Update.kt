package com.hfut.schedule.ui.screen.home

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.logic.model.HolidayBean
import com.hfut.schedule.logic.model.HolidayResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.cube.sub.getEleNew
import com.hfut.schedule.ui.screen.home.cube.sub.getWebInfoFromZJGD
import com.hfut.schedule.ui.screen.home.focus.funiction.initCardNetwork
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 应用冷启动主界面时的网络请求
fun initNetworkRefresh(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel, ifSaved : Boolean){
    val isXuanCheng = getCampus() == Campus.XUANCHENG
    val communityToken = prefs.getString("TOKEN","")
    val showEle = prefs.getBoolean("SWITCHELE", isXuanCheng)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",isXuanCheng)
    val showCard = prefs.getBoolean("SWITCHCARD",true)
    val cookie = if(!vm.webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")
    CoroutineScope(Job()).apply {
        launch {

        }
        // 刷新个人接口
        launch { vm2.getMyApi() }
        // 用于更新ifSaved
        launch { vm.getExamJXGLSTU(cookie!!) }
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
            launch { getWebInfoFromZJGD(vm,vmUI) }
        if(showEle)
            launch { getEleNew(vm, vmUI) }
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
}


//更新教务课表与课程汇总
suspend fun updateCourses(vm: NetWorkViewModel, vmUI: UIViewModel) = withContext(Dispatchers.IO) {
    val cookie = if (!vm.webVpn) prefs.getString("redirect", "") else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    val studentIdObserver = Observer<Int> { result ->
        if (result != 0) {
            SharedPrefs.saveString("studentId", result.toString())
            CoroutineScope(Job()).launch {
                async { vm.getBizTypeId(cookie!!) }.await()
            }
        }
    }
    val getBizTypeIdObserver = Observer<String?> { result ->
        if(result != null) {
            // 开始解析
            val bizTypeId = CasInHFUT.bizTypeId ?: CasInHFUT.getBizTypeId(result)
            if(bizTypeId != null) {
                vm.getLessonIds(cookie!!,bizTypeId,vm.studentId.value.toString())
            }
        }
    }
    val lessonIdObserver = Observer<List<Int>> { result ->
        if (result.toString() != "") {
            val lessonIdsArray = JsonArray()
            result.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
            val jsonObject = JsonObject().apply {
                add("lessonIds", lessonIdsArray)//课程ID
                addProperty("studentId", vm.studentId.value)//学生ID
                addProperty("weekIndex", "")
            }
            vm.getDatum(cookie!!, jsonObject)
            vm.bizTypeIdResponse.removeObserver(getBizTypeIdObserver)
            vm.studentId.removeObserver(studentIdObserver)
        }
    }
    val datumObserver = Observer<String?> { result ->
        if (result != null) {
            if (result.contains("result")) {
                // 刷新缓存
                vmUI.refreshJxglstuCourseScheduleList(result)
                Handler(Looper.getMainLooper()).post { vm.lessonIds.removeObserver(lessonIdObserver) }
            }
        }
    }

    vm.getStudentId(cookie!!)
    Handler(Looper.getMainLooper()).post {
        vm.studentId.observeForever(studentIdObserver)
        vm.bizTypeIdResponse.observeForever(getBizTypeIdObserver)
        vm.lessonIds.observeForever(lessonIdObserver)
        vm.datumData.observeForever(datumObserver)
    }
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


