package com.hfut.schedule.ui.Activity.success.main.saved

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.ViewModel.NetWorkViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.focus.Focus.GetZjgdCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

suspend fun NetWorkUpdate(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel, webVpn : Boolean, ifSaved : Boolean){
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    val auth = prefs.getString("auth","")
    val grade = prefs.getString("Username","")?.substring(2,4)

    val cookie = if(!webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")
    CoroutineScope(Job()).apply {
        //async { MyWangKe() }
        async { vm2.My() }
        //async { MySchedule() }
        //async { AddedItems() }
        //async { getNotifications() }
        async { vm.getExamJXGLSTU(cookie!!) }
        if(!ifSaved) {
            async {
                val studentIdObserver = Observer<Int> { result ->
                    if (result != 0) {
                        SharePrefs.Save("studentId", result.toString())
                        CoroutineScope(Job()).launch {
                            async { grade?.let { vm.getLessonIds(cookie!!, it, result.toString()) } }
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
                    vm.lessonIds.observeForever(lessonIdObserver)
                    vm.datumData.observeForever(datumObserver)
                }
            }
        }
        async { CommuityTOKEN?.let { vm.GetCourse(it) } }
        async { GetZjgdCard(vm,vmUI) }.await()
        async { CommuityTOKEN?.let { vm.getFriends(it) } }
    }
}
//更新教务课表与课程汇总
fun UpdateCourses(vm: NetWorkViewModel) {
    val grade = prefs.getString("Username","")?.substring(2,4)
    val cookie = prefs.getString("redirect", "")
    CoroutineScope(Job()).async {
        val studentIdObserver = Observer<Int> { result ->
            if (result != 0) {
                SharePrefs.Save("studentId", result.toString())
                CoroutineScope(Job()).launch {
                    async { grade?.let { vm.getLessonIds(cookie!!, it, result.toString()) } }
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
            vm.lessonIds.observeForever(lessonIdObserver)
            vm.datumData.observeForever(datumObserver)
        }
    }
}