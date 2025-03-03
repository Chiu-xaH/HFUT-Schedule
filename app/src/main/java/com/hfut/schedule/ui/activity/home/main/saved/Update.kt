package com.hfut.schedule.ui.activity.home.main.saved

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.logic.utils.data.JxglstuParseUtils
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.focus.funictions.GetZjgdCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

suspend fun NetWorkUpdate(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel, ifSaved : Boolean){
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    val auth = prefs.getString("auth","")

    val cookie = if(!vm.webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")
    CoroutineScope(Job()).apply {
        //async { MyWangKe() }
        launch { vm2.My() }
        //async { MySchedule() }
        //async { AddedItems() }
        //async { getNotifications() }
        launch { vm.getExamJXGLSTU(cookie!!) } //用于更新ifSaved
        launch {
            if(!ifSaved) {
                UpdateCourses(vm)
            }
        }

        launch { CommuityTOKEN?.let { vm.GetCourse(it) } }
        async { GetZjgdCard(vm,vmUI) }.await()
        launch { CommuityTOKEN?.let { vm.getFriends(it) } }
    }
}
//更新教务课表与课程汇总
fun UpdateCourses(vm: NetWorkViewModel) {
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    CoroutineScope(Job()).async {
        val getBizTypeIdObserver = Observer<String?> { result ->
            if(result != null) {
                // 开始解析
                val bizTypeId = JxglstuParseUtils.bizTypeId ?: JxglstuParseUtils.getBizTypeId(result)
                if(bizTypeId != null) {
                    vm.getLessonIds(cookie!!,bizTypeId,vm.studentId.value.toString())
                }
            }
        }
        val studentIdObserver = Observer<Int> { result ->
            if (result != 0) {
                SharePrefs.saveString("studentId", result.toString())
                CoroutineScope(Job()).launch {
                    async { vm.getBizTypeId(cookie!!) }.await()
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
                    CoroutineScope(Job()).launch {
                        async { Handler(Looper.getMainLooper()).post { vm.lessonIds.removeObserver(lessonIdObserver) } }
                    }
                }
            }
        }
        async { vm.getStudentId(cookie!!) }.await()
        Handler(Looper.getMainLooper()).post {
            vm.studentId.observeForever(studentIdObserver)
            vm.bizTypeIdResponse.observeForever(getBizTypeIdObserver)
            vm.lessonIds.observeForever(lessonIdObserver)
            vm.datumData.observeForever(datumObserver)
        }
    }
}