package com.hfut.schedule.ui.screen.home

import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.HolidayBean
import com.hfut.schedule.logic.model.HolidayResponse
import com.hfut.schedule.logic.network.repo.JxglstuRepository
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.GsonInstance
import com.hfut.schedule.ui.screen.home.cube.sub.getElectricFromHuiXin
import com.hfut.schedule.ui.screen.home.cube.sub.getWebInfoFromHuiXin
import com.hfut.schedule.ui.screen.home.focus.funiction.initCardNetwork
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun getJxglstuCookie() : String? {
    var cookie : String?
    if(GlobalUiStateHolder.webVpn) {
        val webVpnCookie = DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }
        cookie = Constant.WEBVPN_COOKIE_HEADER + webVpnCookie
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
        cookie = Constant.WEBVPN_COOKIE_HEADER + webVpnCookie
    } else {
        cookie =  prefs.getString("redirect", "")
    }
    return cookie
}

// 应用冷启动主界面时的网络请求
suspend fun initNetworkRefresh(vm : NetWorkViewModel, ifSaved : Boolean) = withContext(Dispatchers.IO) {
    try {
        val isXuanCheng = getCampusRegion() == CampusRegion.XUANCHENG
        val communityToken = prefs.getString("TOKEN","")
        val showEle = prefs.getBoolean("SWITCHELE", isXuanCheng)
        val showToday = prefs.getBoolean("SWITCHTODAY",true)
        val showWeb = prefs.getBoolean("SWITCHWEB",true)
        val showCard = prefs.getBoolean("SWITCHCARD",true)
        val jxglstuCookie = prefs.getString("redirect", "")
        val webVpnCookie = Constant.WEBVPN_COOKIE_HEADER + DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }
        val uniAppJwt = DataStoreManager.uniAppJwt.first()
        // 刷新个人接口
        launch { vm.getMyApi() }
        // 用于更新ifSaved
        launch checkLogin@ {
            // 怎么写的这么冗余，算了
            var studentId: Int? = null
            if(GlobalUiStateHolder.webVpn) {
                // 已经为true，先检查webvpn，再检查教务
                GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(Pair("正在检查WebVpn教务登陆状态","此过程较慢，请稍候"))
                // WebVpn是否能够登录
                vm.getStudentId(webVpnCookie)
                studentId = (vm.studentId.state.value as? NetworkUiState.Success)?.data
                if(studentId == null) {
                    GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(Pair("正在检查教务系统登陆状态","如教务系统封网可能较慢，请稍候"))
                    // 切换到教务模式尝试
                    GlobalUiStateHolder.turnOffWebVpn()
                    if(jxglstuCookie == null) {
                        // 从未登陆过
                        GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(null)
                        return@checkLogin
                    }
                    vm.getStudentId(jxglstuCookie)
                    studentId = (vm.studentId.state.value as? NetworkUiState.Success)?.data
                    if(studentId == null) {
                        // 教务系统也不行，复原
                        GlobalUiStateHolder.turnOnWebVpn()
                        GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(null)
                        return@checkLogin
                    }
                }
            } else {
                if(jxglstuCookie == null) {
                    // 从未登陆过
                    GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(null)
                    return@checkLogin
                }
                // 先检查教务，再检查webvpn
                GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(Pair("正在检查教务系统登陆状态","如教务系统封网可能较慢，请稍候"))
                // 教务是否能够登录
                vm.getStudentId(jxglstuCookie)
                studentId = (vm.studentId.state.value as? NetworkUiState.Success)?.data
                if(studentId == null) {
                    GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(Pair("正在检查WebVpn教务登陆状态","此过程较慢，请稍候"))
                    // 切换到WEBVPN模式尝试
                    GlobalUiStateHolder.turnOnWebVpn()
                    vm.getStudentId(webVpnCookie)
                    studentId = (vm.studentId.state.value as? NetworkUiState.Success)?.data
                    if(studentId == null) {
                        // WebVpn也不行，复原
                        GlobalUiStateHolder.turnOffWebVpn()
                        GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(null)
                        return@checkLogin
                    }
                }
            }
            val finalCookie = if(GlobalUiStateHolder.webVpn) {
                GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(Pair("WebVpn教务已登录，正在获取必需数据","等待消失后即可使用教评、选课等功能"))
                webVpnCookie
            } else {
                GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(Pair("教务系统已登录，正在获取必需数据","等待消失后即可使用教评、选课等功能"))
                jxglstuCookie!!
            }
            launch {
                launch { vm.getBizTypeId(finalCookie,studentId) }
                launch { vm.getExamJXGLSTU(finalCookie) }
            }
            // 登陆成功反馈，可以切屏了
            GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(null)
        }
        // 更新课程表
        if(!ifSaved) {
            launch { updateCourses(vm) }
        }
        // 更新社区
        communityToken?.let {
            launch { vm.getCoursesFromCommunity(it) }
            launch { vm.getFriends(it) }
            if(showToday) {
                launch {
                    vm.todayFormCommunityResponse.clear()
                    vm.getToday(communityToken)
                }
            }
        }
        // 更新合工大教务课表与考试，并检查登录状态并刷新
        launch {
            UniAppRepository.updateCourses(uniAppJwt)
        }
        launch {
            UniAppRepository.updateExams(uniAppJwt)
        }
        // 检查更新
        launch {
            vm.giteeUpdatesResp.clear()
            vm.getUpdate()
        }
        // 更新聚焦卡片
        if(showWeb && getCampusRegion() == CampusRegion.XUANCHENG)
            launch { getWebInfoFromHuiXin(vm) }
        if(showEle)
            launch { getElectricFromHuiXin(vm) }
        if(showCard)
            launch { initCardNetwork(vm) }
        launch {
            val showWeather = DataStoreManager.enableShowFocusWeatherWarn.first()
            val state = vm.weatherWarningData.state.first() // 只发送一次请求 API有次数限制
            if(showWeather && state  !is NetworkUiState.Success) {
                vm.getWeatherWarn(getCampusRegion())
            }
        }
        // 更新节假日信息
        if(DateTimeManager.Date_yyyy != getHolidayYear()) {
            launch { vm.downloadHoliday() }
        }
        launch {
            if(vm.wxPersonInfoResponse.state.first() is NetworkUiState.Success) {
                return@launch
            }
            // 检查指尖工大是否失效
            val auth = DataStoreManager.wxAuth.first()
            if(auth.contains("Bearer")) {
                vm.wxGetPersonInfo(auth)
                val bean = (vm.wxPersonInfoResponse.state.value as? NetworkUiState.Success)?.data
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
    }  catch (e : Exception) {
        LogUtil.error(e)
        GlobalUiStateHolder.focusRefreshProgressFlow.tryEmit(null)
    }
}


private suspend fun refreshWxAuth(vm: NetWorkViewModel) : String? = withContext(Dispatchers.IO) {
    vm.wxLoginResponse.clear()
    vm.wxLogin()
    when(vm.wxLoginResponse.state.first()) {
        is NetworkUiState.Success<*> ->  return@withContext DataStoreManager.wxAuth.first()
        else -> return@withContext null
    }
}

//更新教务课表与课程汇总
suspend fun updateCourses(vm: NetWorkViewModel) = withContext(Dispatchers.IO) {
    val webVpnCookie = DataStoreManager.webVpnCookies.first { it.isNotEmpty() }

    val cookie = if (!GlobalUiStateHolder.webVpn) {
            prefs.getString("redirect", "") ?: return@withContext
        } else {
            if(webVpnCookie.isEmpty()) {
                return@withContext
            } else {
                Constant.WEBVPN_COOKIE_HEADER + webVpnCookie
            }
        }

    if(vm.studentId.state.first() !is NetworkUiState.Success) {
        vm.getStudentId(cookie)
    }
    val studentId = (vm.studentId.state.value as? NetworkUiState.Success)?.data ?: return@withContext
    if(vm.bizTypeIdResponse.state.first() !is NetworkUiState.Success) {
        vm.getBizTypeId(cookie,studentId)
    }
    val bizTypeId = (vm.bizTypeIdResponse.state.value as? NetworkUiState.Success)?.data ?: return@withContext
    vm.getLessonIds(cookie, studentId = studentId, bizTypeId = bizTypeId)
    val lessonResponse = (vm.lessonIds.state.value as? NetworkUiState.Success)?.data ?: return@withContext
    vm.getLessonTimes(cookie,lessonResponse.timeTableLayoutId)
    vm.getDatum(cookie,lessonResponse.lessonIds)
    val datum = (vm.datumData.state.value as? NetworkUiState.Success)?.data ?: return@withContext
    LargeStringDataManager.save(LargeStringDataManager.getJxglstuDatumKey(SemesterParser.getSemester()),datum)
}

private fun getHoliday() : HolidayResponse? {
    val json = prefs.getString("HOLIDAY",null)
    return try {
        GsonInstance.fromJson(json, HolidayResponse::class.java)
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }
}

fun getHolidayYear() : String? = getHoliday()?.year

fun getHolidays() : List<HolidayBean> = getHoliday()?.days ?: emptyList()


