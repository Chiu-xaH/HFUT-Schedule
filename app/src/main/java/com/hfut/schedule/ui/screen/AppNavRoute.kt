package com.hfut.schedule.ui.screen

import androidx.navigation.NavType
import com.hfut.schedule.R

/* 本kt文件已完成多语言文案适配 */
// 主导航
sealed class AppNavRoute(val route: String, val label: Int, val icon: Int) {
    interface NavArg {
        val argName: String
        val navType: NavType<out Any?>
        val isNullable : Boolean
        // 如果为空则default为Any?
        val default: Any?
    }
    // 用于 composable() 注册 route pattern
    // 得到若干参数的composable("XXX")接受 例如 $route?ifSaved={ifSaved}
    internal fun receiveRoute(argNames: List<String>): String {
        return if (argNames.isEmpty()) route
        else buildString {
            append(route)
            append("?")
            append(argNames.joinToString("&") { "$it={$it}" })
        }
    }
    // 用于 navController.navigate() 跳转实际路由
    // 发送若凡参数navigate("XXX") 例如 $route?ifSaved=true
    internal fun withArgs(vararg args: Pair<String, Any?>): String {
        return if (args.isEmpty()) route
        else buildString {
            append(route)
            val filteredArgs = args.filter { it.second != null }
            if (filteredArgs.isNotEmpty()) {
                append("?")
                append(filteredArgs.joinToString("&") { "${it.first}=${it.second}" })
            }
        }
    }

    object Grade : AppNavRoute("GRADE", R.string.navigation_label_grade,R.drawable.article)
    object Home : AppNavRoute("HOME", R.string.navigation_label_home,R.drawable.home)
    object Login : AppNavRoute("LOGIN", R.string.navigation_label_login,R.drawable.login)
    object Agreement : AppNavRoute("USE_AGREEMENT", R.string.navigation_label_agreement,R.drawable.partner_exchange)
    object Empty : AppNavRoute("EMPTY", R.string.navigation_label_empty,R.drawable.near_me)
    object UpdateSuccessfully : AppNavRoute("UPDATE_SUCCESSFUL", R.string.navigation_label_update_successfully,R.drawable.settings)
    object AddEvent : AppNavRoute("ADD_EVENT", R.string.navigation_label_add_event,R.drawable.add)
    object Admission : AppNavRoute("ADMISSION", R.string.navigation_label_admission,R.drawable.publics)
    object VersionInfo : AppNavRoute("VERSION_INFO", R.string.navigation_label_version_info,R.drawable.info)
    object AdmissionDetail : AppNavRoute("ADMISSION_REGION_DETAIL", R.string.navigation_label_admission_detail,R.drawable.publics)
    object ToOuterApplication : AppNavRoute("OPEN_OUTER_APPLICATION", R.string.navigation_label_to_outer,R.drawable.arrow_split)
    object CourseDetail : AppNavRoute("COURSE_DETAIL", R.string.navigation_label_course_detail,R.drawable.category)
    object WebVpn : AppNavRoute("WEBVPN", R.string.navigation_label_webvpn,R.drawable.vpn_key)
    object Holiday : AppNavRoute("HOLIDAY", R.string.navigation_label_holiday,R.drawable.beach_access)
    object ExamNews : AppNavRoute("ExamNotifications", R.string.navigation_label_exam_news, News.icon)
    object NewsApi : AppNavRoute("NEWS_API", R.string.navigation_label_news,R.drawable.stream)  {
        enum class Keyword(val keyword : String) {
            EXAM_SCHEDULE_HEFEI("周考试安排"),HOLIDAY_SCHEDULE("放假安排"),SELECT_COURSE("选课"),TRANSFER_MAJOR("转专业")
        }
    }
    object News : AppNavRoute("News", R.string.navigation_label_news,R.drawable.stream)
    object Alumni : AppNavRoute("ALUMNI", R.string.navigation_label_alumni,R.drawable.person_book)
    object AI : AppNavRoute("LARGE_MODEL", R.string.navigation_label_ai,R.drawable.wand_stars)
    object Track : AppNavRoute("TRACK", R.string.navigation_label_track,R.drawable.track_changes)
    object CommunityAppointment : AppNavRoute("APPOINTMENT", R.string.navigation_label_community_appointment,R.drawable.table_restaurant)
    object WorkAndRest : AppNavRoute("WORK_AND_REST", R.string.navigation_label_work_and_rest,R.drawable.schedule)
    object HaiLeWashing : AppNavRoute("HAILE_WASHING", R.string.navigation_label_washing,R.drawable.local_laundry_service)
    object Fee : AppNavRoute("Fee", R.string.navigation_label_fee,R.drawable.paid)
    object StuTodayCampus : AppNavRoute("STU", R.string.navigation_label_stu,R.drawable.handshake)
    object TeacherSearch : AppNavRoute("TEACHER_SEARCH",R.string.navigation_label_teacher_search,R.drawable.group_search)
    object Work : AppNavRoute("WORK", R.string.navigation_label_work,R.drawable.azm)
    object PersonInfo : AppNavRoute("PERSON", R.string.navigation_label_person_info,R.drawable.person)
    object Exam : AppNavRoute("EXAM", R.string.navigation_label_exam,R.drawable.draw)
    object Dormitory : AppNavRoute("DORMITORY_SCORE", R.string.navigation_label_dormitory,R.drawable.bed)
    object Notifications : AppNavRoute("NOTIFICATIONS", R.string.navigation_label_notifications,R.drawable.notifications)
    object Survey : AppNavRoute("SURVEY", R.string.navigation_label_survey,R.drawable.verified)
    object WebView : AppNavRoute("WEB_VIEW", R.string.navigation_label_web,R.drawable.net) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any?,override val isNullable: Boolean) : NavArg {
            URL("url", NavType.StringType,"",false),
            COOKIES("cookies", NavType.StringType,null,true),
            TITLE("title", NavType.StringType,null,true),
            ICON("icon", NavType.IntType,R.drawable.net,false),
        }
        fun shareRoute(url : String)  = withArgs(Args.URL.argName to url)
        fun withArgs(url : String,title : String?,cookies : String? = null,icon : Int = Args.ICON.default as Int): String = withArgs(
            Args.URL.argName to url,
            Args.COOKIES.argName to cookies,
            Args.TITLE.argName to title,
            Args.ICON.argName to icon,
        )
    }
    object FailRate : AppNavRoute("FAIL_RATE", R.string.navigation_label_fail_rate,R.drawable.radio_button_partial)
    object TransferMajor : AppNavRoute("TRANSFER", R.string.navigation_label_transfer_major,R.drawable.compare_arrows)
    object Library : AppNavRoute("LIBRARY",R.string.navigation_label_library,R.drawable.book_5)
    object AllPrograms : AppNavRoute("PROGRAM_SEARCH", R.string.navigation_label_all_programs,R.drawable.conversion_path)
    object TransferMajorDetail : AppNavRoute("TRANSFER_DETAIL", R.string.navigation_label_transfer_major_detail,R.drawable.compare_arrows)
    object SelectCoursesDetail : AppNavRoute("SELECT_COURSE_DETAIL", R.string.navigation_label_select_courses_detail,R.drawable.ads_click)
    object DropCourses : AppNavRoute("DROP_COURSE", R.string.navigation_label_drop_courses,R.drawable.ads_click)
    object Program : AppNavRoute("PROGRAM", R.string.navigation_label_program,R.drawable.conversion_path)
    object SelectCourses : AppNavRoute("SELECT_COURSE", R.string.navigation_label_select_courses,R.drawable.ads_click)
    object WebFolder : AppNavRoute("WEB_NAVIGATION", R.string.navigation_label_web_folder,R.drawable.explore)
    object NotificationBox : AppNavRoute("BOX", R.string.navigation_label_notification_box,R.drawable.notifications)
    object Life : AppNavRoute("LIFE", R.string.navigation_label_life,R.drawable.near_me)
    object GradeDetail : AppNavRoute("GRADE_DETAIL", R.string.navigation_label_grade_detail,R.drawable.article)
    object CourseSearch : AppNavRoute("COURSE_SEARCH",R.string.navigation_label_course_search,R.drawable.search)
    object TermCourses : AppNavRoute("TOTAL_COURSE", R.string.navigation_label_term_courses,R.drawable.category)
    object ProgramCompetition : AppNavRoute("PROGRAM_COMPETITION", R.string.navigation_label_program_competition,R.drawable.leaderboard)
    object AverageGrade : AppNavRoute("AVERAGE_GRADE", R.string.navigation_label_average_grade,R.drawable.leaderboard)
    object ProgramCompetitionDetail : AppNavRoute("PROGRAM_COMPETITION_DETAIL", R.string.navigation_label_program_competition_detail,R.drawable.leaderboard)
    object SecondClass : AppNavRoute("SECOND_CLASS", R.string.navigation_label_second_class,R.drawable.kid_star)
    object Bus : AppNavRoute("BUS", R.string.navigation_label_bus,R.drawable.directions_bus)
    object Classroom : AppNavRoute("CLASSROOM", R.string.navigation_label_classroom,R.drawable.meeting_room)
    object ClassroomDetail : AppNavRoute("CLASSROOM_DETAIL", R.string.navigation_label_classroom_detail,R.drawable.meeting_room)
    object ClassroomCourseTable : AppNavRoute("CLASSROOM_LESSONS", R.string.navigation_label_classroom_course_table,R.drawable.meeting_room)
    object OfficeHall : AppNavRoute("OFFICE_HALL", R.string.navigation_label_office_hall,R.drawable.person_play)
    object Classmates : AppNavRoute("CLASSMATES", R.string.navigation_label_classmates,R.drawable.sensor_door)
    object ScanQrCode : AppNavRoute("SCAN", R.string.navigation_label_scan_qr_code,R.drawable.qr_code_scanner_shortcut)
    object FunctionsSort : AppNavRoute("SEARCH_EDIT", R.string.navigation_label_functions_sort,R.drawable.edit)
    object CourseSearchTable : AppNavRoute("COURSE_SEARCH_Calendar", R.string.navigation_label_course_search_table,R.drawable.calendar)
    object Exception : AppNavRoute("EXCEPTION", R.string.navigation_label_exception,R.drawable.warning)
    object LibraryBorrowed : AppNavRoute("LIBRARY_BORROWED",R.string.navigation_label_library_borrowed,R.drawable.book_5)
    object AppearanceSettings : AppNavRoute("UI_SETTINGS", R.string.navigation_label_settings_appearance,R.drawable.format_paint)
    object ConfigurationsSettings : AppNavRoute("CONFIG_SETTINGS", R.string.navigation_label_settings_configurations,R.drawable.joystick)
    object NetworkSettings : AppNavRoute("NETWORK_SETTINGS", R.string.navigation_label_settings_network,R.drawable.net)
    object AboutSettings : AppNavRoute("ABOUT_SETTINGS", R.string.navigation_label_settings_about,R.drawable.partner_exchange)
}
