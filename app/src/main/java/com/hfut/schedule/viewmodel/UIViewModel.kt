package com.hfut.schedule.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.zjgd.ReturnCard
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.logic.util.storage.SharePrefs.saveString
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.WebInfo
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.getIdentifyID
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UIViewModel : ViewModel()  {
    val findNewCourse = MutableLiveData<Boolean>()
    var cardValue by mutableStateOf<ReturnCard?>(null)
    var electricValue = MutableLiveData<String?>()
    var webValue = MutableLiveData<WebInfo>()

    var isAddUIExpanded by mutableStateOf(false)

    // 缓存复用 由于数据过大
    var jxglstuCourseScheduleList by mutableStateOf(
        getJxglstuCourseSchedule()
    )
    fun refreshJxglstuCourseScheduleList(json : String) {
        jxglstuCourseScheduleList = getJxglstuCourseSchedule(json)
    }
}