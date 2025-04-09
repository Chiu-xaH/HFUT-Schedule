package com.hfut.schedule.ui.activity.home.main

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.logic.utils.data.JxglstuParseUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.cube.items.subitems.getEleNew
import com.hfut.schedule.ui.activity.home.cube.items.subitems.getWebInfoFromZJGD
import com.hfut.schedule.ui.activity.home.focus.funictions.initCardNetwork
import com.hfut.schedule.ui.activity.home.search.functions.transfer.Campus
import com.hfut.schedule.ui.activity.home.search.functions.transfer.getCampus
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
        // 刷新个人接口
        launch { vm2.My() }
        // 用于更新ifSaved
        launch { vm.getExamJXGLSTU(cookie!!) }
        // 更新课程表
        if(!ifSaved)
            launch { updateCourses(vm,vmUI) }
        // 更新社区
        communityToken?.let {
            launch { vm.GetCourse(it) }
            launch { vm.getFriends(it) }
        }
        //检查更新
        launch { vmUI.getUpdate() }
        // 更新聚焦卡片
        if(showWeb)
            launch { getWebInfoFromZJGD(vm,vmUI) }
        if(showEle)
            launch { getEleNew(vm, vmUI) }
        if(showToday)
            launch { getTodayNet(vm) }
        if(showCard)
            launch { initCardNetwork(vm,vmUI) }
    }
}

private suspend fun getTodayNet(vm : NetWorkViewModel) = withContext(Dispatchers.IO) { prefs.getString("TOKEN","")?.let { vm.getToday(it) } }

//更新教务课表与课程汇总
suspend fun updateCourses(vm: NetWorkViewModel,vmUI: UIViewModel) = withContext(Dispatchers.IO) {
    val cookie = if (!vm.webVpn) prefs.getString("redirect", "") else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    val studentIdObserver = Observer<Int> { result ->
        if (result != 0) {
            SharePrefs.saveString("studentId", result.toString())
            CoroutineScope(Job()).launch {
                async { vm.getBizTypeId(cookie!!) }.await()
            }
        }
    }
    val getBizTypeIdObserver = Observer<String?> { result ->
        if(result != null) {
            // 开始解析
            val bizTypeId = JxglstuParseUtils.bizTypeId ?: JxglstuParseUtils.getBizTypeId(result)
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