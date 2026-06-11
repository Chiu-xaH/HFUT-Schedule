package com.hfut.schedule.ui.nav

import android.net.Uri
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.AdmissionDestination
import com.hfut.schedule.ui.nav.destination.AgreementDestination
import com.hfut.schedule.ui.nav.destination.AllExamDestination
import com.hfut.schedule.ui.nav.destination.AllProgramsDestination
import com.hfut.schedule.ui.nav.destination.AverageGradeDestination
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.ClassroomDestination
import com.hfut.schedule.ui.nav.destination.CourseClassmatesScreen
import com.hfut.schedule.ui.nav.destination.DepartmentsDestination
import com.hfut.schedule.ui.nav.destination.DormitoryDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.nav.destination.ExceptionDestination
import com.hfut.schedule.ui.nav.destination.FailRateApiDestination
import com.hfut.schedule.ui.nav.destination.FailRateDestination
import com.hfut.schedule.ui.nav.destination.FeeDestination
import com.hfut.schedule.ui.nav.destination.GradeDestination
import com.hfut.schedule.ui.nav.destination.HaiLeWashingDestination
import com.hfut.schedule.ui.nav.destination.HolidayDestination
import com.hfut.schedule.ui.nav.destination.HomeDestination
import com.hfut.schedule.ui.nav.destination.LibraryBorrowedDestination
import com.hfut.schedule.ui.nav.destination.LibraryDestination
import com.hfut.schedule.ui.nav.destination.LifeDestination
import com.hfut.schedule.ui.nav.destination.LoginDestination
import com.hfut.schedule.ui.nav.destination.NewsApiDestination
import com.hfut.schedule.ui.nav.destination.NewsDestination
import com.hfut.schedule.ui.nav.destination.NotificationsDestination
import com.hfut.schedule.ui.nav.destination.OfficeHallDestination
import com.hfut.schedule.ui.nav.destination.PersonInfoDestination
import com.hfut.schedule.ui.nav.destination.ProgramCompetitionDestination
import com.hfut.schedule.ui.nav.destination.ProgramDestination
import com.hfut.schedule.ui.nav.destination.ScanQrCodeDestination
import com.hfut.schedule.ui.nav.destination.SettingsAboutDestination
import com.hfut.schedule.ui.nav.destination.SettingsAboutDeveloperDestination
import com.hfut.schedule.ui.nav.destination.SettingsAppearanceDestination
import com.hfut.schedule.ui.nav.destination.SettingsBackupDestination
import com.hfut.schedule.ui.nav.destination.SettingsConfigurationDestination
import com.hfut.schedule.ui.nav.destination.SettingsDeepLinkDestination
import com.hfut.schedule.ui.nav.destination.SettingsNetworkDestination
import com.hfut.schedule.ui.nav.destination.SettingsTipsDestination
import com.hfut.schedule.ui.nav.destination.TeacherSearchApiDestination
import com.hfut.schedule.ui.nav.destination.TeacherSearchDestination
import com.hfut.schedule.ui.nav.destination.TermCoursesDestination
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.nav.destination.TrackDestination
import com.hfut.schedule.ui.nav.destination.UpdateSuccessfullyDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.hfut.schedule.ui.nav.destination.WebFolderDestination
import com.hfut.schedule.ui.nav.destination.WebVpnDestination
import com.hfut.schedule.ui.nav.destination.WorkAndRestDestination
import com.hfut.schedule.ui.nav.destination.WorkDestination
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.navigation.model.dest.DeepLink

private fun createDeepLinkByKey(destination : NavDestination) = with(destination) {
    DeepLink(this.key) { this }
}

/**
 * 用于向外界展示
 */
data class DeepLinkBean(
    val destination: NavDestination,
    val hasArgs: Boolean,
    val deepLink: DeepLink<*>,
    val description: String,
    val exampleUri: Uri
)

/**
 * Copied from SharedNav Dev
 *
 * 为便于管理，不允许使用多级Path进行传参，只允许以query的形式传入基本参数
 *
 * Uri格式：
 *
 * scheme://host
 *
 * scheme://host?query1=xxx
 *
 * scheme://host?query1=xxx&query2=xxx
 *
 * scheme://host?query1=xxx&query2=xxx&query3=xxx
 *
 * ...
 *
 * 虽然格式要求很激进，但这样可以降低开发和管理成本，不让安卓项目趋于网页化
 */
val deepLinks = listOf(
    // 无参链接 -> 无参Dest
    createDeepLinkByKey(AdmissionDestination),
    createDeepLinkByKey(AllExamDestination) ,
    createDeepLinkByKey(BusDestination),
    createDeepLinkByKey(DepartmentsDestination) ,
    createDeepLinkByKey(DormitoryDestination),
    createDeepLinkByKey(FeeDestination),
    createDeepLinkByKey(HaiLeWashingDestination),
    createDeepLinkByKey(HolidayDestination),
    createDeepLinkByKey(LifeDestination),
    createDeepLinkByKey(NotificationsDestination),
    createDeepLinkByKey(OfficeHallDestination),
    createDeepLinkByKey(PersonInfoDestination),
    createDeepLinkByKey(ScanQrCodeDestination),
    createDeepLinkByKey(SettingsAboutDestination),
    createDeepLinkByKey(SettingsAboutDeveloperDestination),
    createDeepLinkByKey(SettingsAppearanceDestination),
    createDeepLinkByKey(SettingsBackupDestination),
    createDeepLinkByKey(SettingsConfigurationDestination),
    createDeepLinkByKey(SettingsDeepLinkDestination),
    createDeepLinkByKey(SettingsTipsDestination),
    createDeepLinkByKey(SettingsNetworkDestination),
    createDeepLinkByKey(TermReportDestination),
    createDeepLinkByKey(TrackDestination),
    createDeepLinkByKey(VersionInfoDestination),
    createDeepLinkByKey(WebFolderDestination),
    createDeepLinkByKey(WebVpnDestination),
    createDeepLinkByKey(WorkDestination),
    createDeepLinkByKey(LibraryDestination),
    createDeepLinkByKey(LibraryBorrowedDestination),
    createDeepLinkByKey(UpdateSuccessfullyDestination),
    createDeepLinkByKey(AgreementDestination),
    createDeepLinkByKey(LoginDestination),
    /* TODO:ClassmatesDestination缺一前序条件后面补全
    createDeepLinkByKey(ClassmatesDestination),
     */

    // 无参链接 -> 有参Dest
    DeepLink(ClassroomDestination.KEY) { ClassroomDestination("deeplink") },
    DeepLink(AverageGradeDestination.KEY) { AverageGradeDestination(true) },
    DeepLink(AllProgramsDestination.KEY) { AllProgramsDestination(true) },
    DeepLink(ExamDestination.KEY) { ExamDestination("deeplink") },
    DeepLink(GradeDestination.KEY) { GradeDestination(true) },
    DeepLink(ProgramCompetitionDestination.KEY) { ProgramCompetitionDestination(true) },
    DeepLink(ProgramDestination.KEY) { ProgramDestination(true) },
    DeepLink(TermCoursesDestination.KEY) { TermCoursesDestination(true,"deeplink") },
    DeepLink(WorkAndRestDestination.KEY) { WorkAndRestDestination(null) },
    DeepLink(AddEventDestination.KEY) { AddEventDestination(null,"deeplink") },
    /* TODO:等合工大教务版评教上线后
    DeepLink(SurveyDestination.KEY) { SurveyDestination(true) },
     */
    /* TODO:等合工大教务版转专业上线后
    DeepLink(TransferMajorDestination.KEY) { TransferMajorDestination(true) },
     */

    // 有参链接 -> 有参Dest
    /* TODO:等合工大教务版开课查询上线后
    DeepLink(CourseSearchDestination.key) { uri ->
        val name = uri.getQueryParameter("course_name")
        val code = uri.getQueryParameter("lesson_code")
        val semster = uri.getQueryParameter("semster")?.toIntOrNull()

        if(name == null && code == null && semster == null) {
            CourseSearchDestination
        } else {
            CourseSearchApiDestination(name,code,semster)
        }
    },
     */
    DeepLink(NewsDestination.key) { uri ->
        val keyword = uri.getQueryParameter("keyword") ?: return@DeepLink NewsDestination
        NewsApiDestination(keyword)
    },
    DeepLink(TeacherSearchDestination.key) { uri ->
        val courseName = uri.getQueryParameter("name") ?: return@DeepLink TeacherSearchDestination
        TeacherSearchApiDestination(courseName)
    },
    DeepLink(FailRateDestination.key) { uri ->
        val courseName = uri.getQueryParameter("course_name") ?: return@DeepLink FailRateDestination
        val filteredLessonCode = uri.getQueryParameter("lesson_code")
        FailRateApiDestination(courseName,filteredLessonCode)
    },
    DeepLink(CourseClassmatesScreen.KEY) { uri ->
        val lessonId = uri.getQueryParameter("lesson_id")?.toIntOrNull() ?: return@DeepLink ExceptionDestination(
            Exception("lesson_id 字段解析失败，要求Int值")
        )
        CourseClassmatesScreen(lessonId,null,false)
    },
    DeepLink(HomeDestination.KEY) { uri ->
        val subPage = uri.getQueryParameter("page") ?: return@DeepLink HomeDestination()
        try {
            val arg = BottomBarItems.valueOf(subPage.uppercase())
            HomeDestination(arg)
        } catch (e : Exception) {
            ExceptionDestination(e)
        }
    },
)

//val deepLinks = deepLinkBeans.map { it }