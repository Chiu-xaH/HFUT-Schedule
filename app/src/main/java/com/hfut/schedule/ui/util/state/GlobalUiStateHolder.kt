package com.hfut.schedule.ui.util.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.huixin.ReturnCard
import com.hfut.schedule.logic.network.repo.JxglstuRepository
import com.hfut.schedule.ui.nav.destination.AdmissionDestination
import com.hfut.schedule.ui.nav.destination.AiDestination
import com.hfut.schedule.ui.nav.destination.AlumniDestination
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.ClassroomDestination
import com.hfut.schedule.ui.nav.destination.CommunityAppointmentDestination
import com.hfut.schedule.ui.nav.destination.CourseSearchDestination
import com.hfut.schedule.ui.nav.destination.DormitoryDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.nav.destination.FailRateDestination
import com.hfut.schedule.ui.nav.destination.FeeDestination
import com.hfut.schedule.ui.nav.destination.GradeDestination
import com.hfut.schedule.ui.nav.destination.HolidayDestination
import com.hfut.schedule.ui.nav.destination.LibraryDestination
import com.hfut.schedule.ui.nav.destination.LifeDestination
import com.hfut.schedule.ui.nav.destination.NewsDestination
import com.hfut.schedule.ui.nav.destination.NotificationsDestination
import com.hfut.schedule.ui.nav.destination.OfficeHallDestination
import com.hfut.schedule.ui.nav.destination.PersonInfoDestination
import com.hfut.schedule.ui.nav.destination.ProgramDestination
import com.hfut.schedule.ui.nav.destination.ScanQrCodeDestination
import com.hfut.schedule.ui.nav.destination.SecondClassDestination
import com.hfut.schedule.ui.nav.destination.SelectCoursesDestination
import com.hfut.schedule.ui.nav.destination.StuTodayCampusDestination
import com.hfut.schedule.ui.nav.destination.SurveyDestination
import com.hfut.schedule.ui.nav.destination.TeacherSearchDestination
import com.hfut.schedule.ui.nav.destination.TermCoursesDestination
import com.hfut.schedule.ui.nav.destination.TrackDestination
import com.hfut.schedule.ui.nav.destination.TransferMajorDestination
import com.hfut.schedule.ui.nav.destination.WebFolderDestination
import com.hfut.schedule.ui.nav.destination.WebVpnDestination
import com.hfut.schedule.ui.nav.destination.WorkAndRestDestination
import com.hfut.schedule.ui.nav.destination.WorkDestination
import com.hfut.schedule.ui.screen.home.search.SearchAppBeanLite
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 全局共享的UI变量放在这里
 */
object GlobalUiStateHolder {
    var postedUse = false
    var excludeJxglstu by mutableStateOf(false)
    var refreshImageCode by mutableIntStateOf(1)

    var globalWebVpn by mutableStateOf(false)
    var webVpn by mutableStateOf(false)

    var isSupabaseRegistering = false
    var casCookies : String? = null

    // 聚焦下拉刷新的进度文案 暂时设置成String，如果需要加进度可以进一步扩展
    val focusRefreshProgressFlow = MutableStateFlow<Pair<String, String>?>(null)

    var cardValue by mutableStateOf<ReturnCard?>(null)
    @Deprecated("LiveData已不再作为本项目主力，请使用UiStateHolder")
    var electricValue = MutableLiveData<String?>()
    @Deprecated("LiveData已不再作为本项目主力，请使用UiStateHolder")
    var webValue = MutableLiveData<WebInfo>()

    // 用过的ID 不要再用了，比如之前删除的功能ID
    val funcDefault = listOf(
        SearchAppBeanLite(1, R.string.navigation_label_school_card ,R.drawable.credit_card),
        SearchAppBeanLite(2, ScanQrCodeDestination.title.resId, ScanQrCodeDestination.icon),
        SearchAppBeanLite(3, R.string.navigation_label_dormitory_electricity_bill ,R.drawable.flash_on),
        SearchAppBeanLite(4, R.string.navigation_label_school_net , R.drawable.net),
        SearchAppBeanLite(5, R.string.navigation_label_school_email ,R.drawable.mail),
        SearchAppBeanLite(6, ExamDestination.TITLE.resId,ExamDestination.ICON),
        SearchAppBeanLite(7, GradeDestination.TITLE.resId,GradeDestination.ICON),
        SearchAppBeanLite(8, FailRateDestination.title.resId, FailRateDestination.icon),
        SearchAppBeanLite(9, TermCoursesDestination.TITLE.resId, TermCoursesDestination.ICON),
        SearchAppBeanLite(10, PersonInfoDestination.title.resId,  PersonInfoDestination.icon),
        SearchAppBeanLite(11, WebFolderDestination.title.resId,WebFolderDestination.icon),
        SearchAppBeanLite(12, R.string.navigation_label_shower,R.drawable.bathtub),
        SearchAppBeanLite(13, SelectCoursesDestination.title.resId,  SelectCoursesDestination.icon),
        SearchAppBeanLite(14, DormitoryDestination.title.resId, DormitoryDestination.icon),
        SearchAppBeanLite(15, NotificationsDestination.title.resId, NotificationsDestination.icon),
        SearchAppBeanLite(16, SurveyDestination.TITLE.resId, SurveyDestination.ICON),
        SearchAppBeanLite(17, NewsDestination.title.resId, NewsDestination.icon),
        SearchAppBeanLite(18, ProgramDestination.TITLE.resId, ProgramDestination.ICON),
        SearchAppBeanLite(19, LibraryDestination.title.resId, LibraryDestination.icon),
        SearchAppBeanLite(20, BusDestination.title.resId, BusDestination.icon),
        SearchAppBeanLite(21, R.string.navigation_label_repair,R.drawable.build),
//        SearchAppBeanLite(22, NextCourseDestination.title.resId, NextCourseDestination.icon),
        SearchAppBeanLite(23, R.string.navigation_label_hot_water,R.drawable.water_voc),
        SearchAppBeanLite(24, ClassroomDestination.TITLE.resId,ClassroomDestination.ICON),
        SearchAppBeanLite(25, R.string.navigation_label_physical_fitness_test,R.drawable.sports_volleyball),
        SearchAppBeanLite(26, WorkAndRestDestination.TITLE.resId, WorkAndRestDestination.ICON),
        SearchAppBeanLite(27, R.string.navigation_label_chsi, R.drawable.school),
        SearchAppBeanLite(28, LifeDestination.title.resId,LifeDestination.icon),
        SearchAppBeanLite(29, TransferMajorDestination.title.resId,  TransferMajorDestination.icon),
        SearchAppBeanLite(30, CourseSearchDestination.title.resId, CourseSearchDestination.icon),
        SearchAppBeanLite(31, TeacherSearchDestination.title.resId, TeacherSearchDestination.icon),
        SearchAppBeanLite(32, FeeDestination.title.resId,FeeDestination.icon),
//        SearchAppBeanLite(33,"实习",R.drawable.work),
        SearchAppBeanLite(34, AlumniDestination.title.resId,AlumniDestination.icon),
        SearchAppBeanLite(35, StuTodayCampusDestination.title.resId,StuTodayCampusDestination.icon),
        SearchAppBeanLite(36, R.string.navigation_label_ietp,R.drawable.groups),
        SearchAppBeanLite(37, WorkDestination.title.resId,WorkDestination.icon),
        SearchAppBeanLite(38, HolidayDestination.title.resId,HolidayDestination.icon),
        SearchAppBeanLite(39, R.string.navigation_label_supabase,R.drawable.cloud),
        SearchAppBeanLite(40, R.string.navigation_label_laundry, R.drawable.local_laundry_service),
        SearchAppBeanLite(41, AdmissionDestination.title.resId,AdmissionDestination.icon),
        SearchAppBeanLite(42, WebVpnDestination.title.resId,  WebVpnDestination.icon),
        SearchAppBeanLite(43, OfficeHallDestination.title.resId,  OfficeHallDestination.icon),
        SearchAppBeanLite(44, R.string.navigation_label_hui_xin ,R.drawable.corporate_fare),
        SearchAppBeanLite(45, SecondClassDestination.title.resId,SecondClassDestination.icon),
        SearchAppBeanLite(46, CommunityAppointmentDestination.title.resId,CommunityAppointmentDestination.icon),
        SearchAppBeanLite(47, AiDestination.title.resId,AiDestination.icon),
        SearchAppBeanLite(48, TrackDestination.title.resId,TrackDestination.icon),
        SearchAppBeanLite(49, R.string.navigation_label_express,R.drawable.package_2),
        SearchAppBeanLite(50, R.string.navigation_label_feedback,R.drawable.voice_selection),
    )
    val funcMaps = funcDefault.toMutableStateList()
}
