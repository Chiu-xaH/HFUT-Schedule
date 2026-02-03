package com.hfut.schedule.ui.util.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.SearchAppBeanLite

// 跨Activity的类似UIViewModel
object GlobalUIStateHolder {
    var useEnterAnimation by mutableStateOf(false)
    var postedUse = false
    var excludeJxglstu by mutableStateOf(false)
    var refreshImageCode by mutableIntStateOf(1)
    var isTransiting by mutableStateOf<Boolean>(false)
    var globalWebVpn by mutableStateOf(false)
    var webVpn by mutableStateOf(false)
    var isSupabaseRegistering = mutableStateOf(false)
    val routeQueue = mutableStateListOf<RouteQueueBean>()
    fun pushToFront(route: String,app : AppNavRoute) {
        routeQueue.removeAll { it.route == route }
        routeQueue.add(0, RouteQueueBean(route,app))
    }
    // 用过的ID 不要再用了，比如之前删除的功能ID
    val funcDefault = listOf(
        SearchAppBeanLite(1, R.string.navigation_label_school_card ,R.drawable.credit_card),
        SearchAppBeanLite(2, AppNavRoute.ScanQrCode.label, AppNavRoute.ScanQrCode.icon),
        SearchAppBeanLite(3, R.string.navigation_label_dormitory_electricity_bill ,R.drawable.flash_on),
        SearchAppBeanLite(4, R.string.navigation_label_school_net , R.drawable.net),
        SearchAppBeanLite(5, R.string.navigation_label_school_email ,R.drawable.mail),
        SearchAppBeanLite(6, AppNavRoute.Exam.label,AppNavRoute.Exam.icon),
        SearchAppBeanLite(7, AppNavRoute.Grade.label,AppNavRoute.Grade.icon),
        SearchAppBeanLite(8, AppNavRoute.FailRate.label, AppNavRoute.FailRate.icon),
        SearchAppBeanLite(9, AppNavRoute.TermCourses.label, AppNavRoute.TermCourses.icon),
        SearchAppBeanLite(10, AppNavRoute.PersonInfo.label,  AppNavRoute.PersonInfo.icon),
        SearchAppBeanLite(11, AppNavRoute.WebFolder.label,AppNavRoute.WebFolder.icon),
        SearchAppBeanLite(12, R.string.navigation_label_shower,R.drawable.bathtub),
        SearchAppBeanLite(13, AppNavRoute.SelectCourses.label,  AppNavRoute.SelectCourses.icon),
        SearchAppBeanLite(14, AppNavRoute.Dormitory.label, AppNavRoute.Dormitory.icon),
        SearchAppBeanLite(15, AppNavRoute.Notifications.label, AppNavRoute.Notifications.icon),
        SearchAppBeanLite(16, AppNavRoute.Survey.label, AppNavRoute.Survey.icon),
        SearchAppBeanLite(17, AppNavRoute.News.label, AppNavRoute.News.icon),
        SearchAppBeanLite(18, AppNavRoute.Program.label, AppNavRoute.Program.icon),
        SearchAppBeanLite(19, AppNavRoute.Library.label, AppNavRoute.Library.icon),
        SearchAppBeanLite(20, AppNavRoute.Bus.label, AppNavRoute.Bus.icon),
        SearchAppBeanLite(21, R.string.navigation_label_repair,R.drawable.build),
//        SearchAppBeanLite(22, AppNavRoute.NextCourse.label, AppNavRoute.NextCourse.icon),
        SearchAppBeanLite(23, R.string.navigation_label_hot_water,R.drawable.water_voc),
        SearchAppBeanLite(24, AppNavRoute.Classroom.label,AppNavRoute.Classroom.icon),
        SearchAppBeanLite(25, R.string.navigation_label_physical_fitness_test,R.drawable.sports_volleyball),
        SearchAppBeanLite(26, AppNavRoute.WorkAndRest.label, AppNavRoute.WorkAndRest.icon),
        SearchAppBeanLite(27, R.string.navigation_label_chsi, R.drawable.school),
        SearchAppBeanLite(28, AppNavRoute.Life.label,AppNavRoute.Life.icon),
        SearchAppBeanLite(29, AppNavRoute.TransferMajor.label,  AppNavRoute.TransferMajor.icon),
        SearchAppBeanLite(30, AppNavRoute.CourseSearch.label, AppNavRoute.CourseSearch.icon),
        SearchAppBeanLite(31, AppNavRoute.TeacherSearch.label, AppNavRoute.TeacherSearch.icon),
        SearchAppBeanLite(32, AppNavRoute.Fee.label,AppNavRoute.Fee.icon),
//        SearchAppBeanLite(33,"实习",R.drawable.work),
        SearchAppBeanLite(34, AppNavRoute.Alumni.label,AppNavRoute.Alumni.icon),
        SearchAppBeanLite(35, AppNavRoute.StuTodayCampus.label,AppNavRoute.StuTodayCampus.icon),
        SearchAppBeanLite(36, R.string.navigation_label_ietp,R.drawable.groups),
        SearchAppBeanLite(37, AppNavRoute.Work.label,AppNavRoute.Work.icon),
        SearchAppBeanLite(38, AppNavRoute.Holiday.label,AppNavRoute.Holiday.icon),
        SearchAppBeanLite(39, R.string.navigation_label_supabase,R.drawable.cloud),
        SearchAppBeanLite(40, R.string.navigation_label_laundry, R.drawable.local_laundry_service),
        SearchAppBeanLite(41, AppNavRoute.Admission.label,AppNavRoute.Admission.icon),
        SearchAppBeanLite(42, AppNavRoute.WebVpn.label,  AppNavRoute.WebVpn.icon),
        SearchAppBeanLite(43, AppNavRoute.OfficeHall.label,  AppNavRoute.OfficeHall.icon),
        SearchAppBeanLite(44, R.string.navigation_label_hui_xin ,R.drawable.corporate_fare),
        SearchAppBeanLite(45, AppNavRoute.SecondClass.label,AppNavRoute.SecondClass.icon),
        SearchAppBeanLite(46, AppNavRoute.CommunityAppointment.label,AppNavRoute.CommunityAppointment.icon),
        SearchAppBeanLite(47, AppNavRoute.AI.label,AppNavRoute.AI.icon),
        SearchAppBeanLite(48, AppNavRoute.Track.label,AppNavRoute.Track.icon),
    )

    val funcMaps = funcDefault.toMutableStateList()
}


data class RouteQueueBean(val route : String,val app : AppNavRoute)
