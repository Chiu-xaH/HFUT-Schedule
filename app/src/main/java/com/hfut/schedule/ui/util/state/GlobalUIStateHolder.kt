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
        SearchAppBeanLite(1,"一卡通" ,R.drawable.credit_card),
        SearchAppBeanLite(2, AppNavRoute.Scan.label, AppNavRoute.Scan.icon),
        SearchAppBeanLite(3,"寝室电费" ,R.drawable.flash_on),
        SearchAppBeanLite(4,"校园网" , R.drawable.net),
        SearchAppBeanLite(5,"教育邮箱" ,R.drawable.mail),
        SearchAppBeanLite(6, AppNavRoute.Exam.label,AppNavRoute.Exam.icon),
        SearchAppBeanLite(7, AppNavRoute.Grade.label,AppNavRoute.Grade.icon),
        SearchAppBeanLite(8, AppNavRoute.FailRate.label, AppNavRoute.FailRate.icon),
        SearchAppBeanLite(9, AppNavRoute.TotalCourse.label, AppNavRoute.TotalCourse.icon),
        SearchAppBeanLite(10, AppNavRoute.Person.label,  AppNavRoute.Person.icon),
        SearchAppBeanLite(11, AppNavRoute.WebNavigation.label,AppNavRoute.WebNavigation.icon),
        SearchAppBeanLite(12,"洗浴",R.drawable.bathtub),
        SearchAppBeanLite(13, AppNavRoute.SelectCourse.label,  AppNavRoute.SelectCourse.icon),
        SearchAppBeanLite(14, AppNavRoute.DormitoryScore.label, AppNavRoute.DormitoryScore.icon),
        SearchAppBeanLite(15, AppNavRoute.Notifications.label, AppNavRoute.Notifications.icon),
        SearchAppBeanLite(16, AppNavRoute.Survey.label, AppNavRoute.Survey.icon),
        SearchAppBeanLite(17, AppNavRoute.News.label, AppNavRoute.News.icon),
        SearchAppBeanLite(18, AppNavRoute.Program.label, AppNavRoute.Program.icon),
        SearchAppBeanLite(19, AppNavRoute.Library.label, AppNavRoute.Library.icon),
        SearchAppBeanLite(20, AppNavRoute.Bus.label, AppNavRoute.Bus.icon),
        SearchAppBeanLite(21,"报修",R.drawable.build),
        SearchAppBeanLite(22, AppNavRoute.NextCourse.label, AppNavRoute.NextCourse.icon),
        SearchAppBeanLite(23,"热水",R.drawable.water_voc),
        SearchAppBeanLite(24, AppNavRoute.Classroom.label,AppNavRoute.Classroom.icon),
        SearchAppBeanLite(25,"体测平台",R.drawable.sports_volleyball),
        SearchAppBeanLite(26, AppNavRoute.TimeTable.label, AppNavRoute.TimeTable.icon),
        SearchAppBeanLite(27,"学信网", R.drawable.school),
        SearchAppBeanLite(28, AppNavRoute.Life.label,AppNavRoute.Life.icon),
        SearchAppBeanLite(29, AppNavRoute.Transfer.label,  AppNavRoute.Transfer.icon),
        SearchAppBeanLite(30, AppNavRoute.CourseSearch.label, AppNavRoute.CourseSearch.icon),
        SearchAppBeanLite(31, AppNavRoute.TeacherSearch.label, AppNavRoute.TeacherSearch.icon),
        SearchAppBeanLite(32, AppNavRoute.Fee.label,AppNavRoute.Fee.icon),
        SearchAppBeanLite(33,"实习",R.drawable.work),
        SearchAppBeanLite(34, AppNavRoute.Wechat.label,AppNavRoute.Wechat.icon),
        SearchAppBeanLite(35, AppNavRoute.StuTodayCampus.label,AppNavRoute.StuTodayCampus.icon),
        SearchAppBeanLite(36,"大创系统",R.drawable.groups),
        SearchAppBeanLite(37, AppNavRoute.Work.label,AppNavRoute.Work.icon),
        SearchAppBeanLite(38, AppNavRoute.Holiday.label,AppNavRoute.Holiday.icon),
        SearchAppBeanLite(39,"共建平台",R.drawable.cloud),
        SearchAppBeanLite(40,"洗衣机", R.drawable.local_laundry_service),
        SearchAppBeanLite(41, AppNavRoute.Admission.label,AppNavRoute.Admission.icon),
        SearchAppBeanLite(42, AppNavRoute.WebVpn.label,  AppNavRoute.WebVpn.icon),
        SearchAppBeanLite(43, AppNavRoute.OfficeHall.label,  AppNavRoute.OfficeHall.icon),
        SearchAppBeanLite(44,"慧新易校" ,R.drawable.corporate_fare),
        SearchAppBeanLite(45, AppNavRoute.SecondClass.label,AppNavRoute.SecondClass.icon),
    )

    val funcMaps = funcDefault.toMutableStateList()
}


data class RouteQueueBean(val route : String,val app : AppNavRoute)
