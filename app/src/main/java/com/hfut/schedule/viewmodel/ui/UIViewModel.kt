package com.hfut.schedule.viewmodel.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.zjgd.ReturnCard
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo

class UIViewModel : ViewModel()  {
    val findNewCourse = MutableLiveData<Boolean>()
    var cardValue by mutableStateOf<ReturnCard?>(null)
    var electricValue = MutableLiveData<String?>()
    var webValue = MutableLiveData<WebInfo>()

    var isAddUIExpanded by mutableStateOf(false)
    var specialWOrkDayChange by mutableIntStateOf(0)

    var isAddUIExpandedSupabase by mutableStateOf(false)

    // 缓存复用 由于数据过大
    var jxglstuCourseScheduleList by mutableStateOf(
        getJxglstuCourseSchedule()
    )
    fun refreshJxglstuCourseScheduleList(json : String) {
        jxglstuCourseScheduleList = getJxglstuCourseSchedule(json)
    }

}