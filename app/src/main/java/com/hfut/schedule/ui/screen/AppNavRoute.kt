package com.hfut.schedule.ui.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.one.BuildingBean
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.screen.AppNavRoute.NavArg
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventOrigin

// 主导航
sealed class AppNavRoute(val route: String, val label : String, val icon : Int) {
    interface NavArg {
        val argName: String
        val navType: NavType<out Any?>
        val isNullable : Boolean
        // 如果为空则default为Any?
        val default: Any?

        fun validate() {
            val notNullableTypes = setOf(
                NavType.IntType,
                NavType.BoolType,
                NavType.FloatType,
                NavType.LongType
            )
            if (isNullable && navType in notNullableTypes) {
                throw IllegalArgumentException("$navType 不支持设置 isNullable=true")
            }
        }
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
    internal fun receiveRoutePair(args : Iterable<NavArg>): String = receiveRoute(args.map { it.argName })
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



    object Grade : AppNavRoute("GRADE","成绩",R.drawable.article) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object Home : AppNavRoute("HOME","主页面",R.drawable.home)
    object Login : AppNavRoute("LOGIN","登录",R.drawable.login)
    object UseAgreement : AppNavRoute("USE_AGREEMENT","用户协议",R.drawable.partner_exchange)
    object Empty : AppNavRoute("EMPTY","导航中转空白页",R.drawable.near_me) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            TARGET_ROUTE("target", NavType.StringType, route,true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(targetRoute : String): String = withArgs(
            Args.TARGET_ROUTE.argName to targetRoute,
        )
    }
    object UpdateSuccess : AppNavRoute("UPDATE_SUCCESSFUL","更新完成引导",R.drawable.settings)
    object AddEvent : AppNavRoute("ADD_EVENT","添加",R.drawable.add) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            ID("id", NavType.IntType, -1,false),
            ORIGIN("origin",NavType.StringType, AddEventOrigin.FOCUS_ADD.name,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(id : Int = -1,origin : String = AddEventOrigin.FOCUS_EDITED.name): String = withArgs(
            Args.ID.argName to id,
            Args.ORIGIN.argName to origin
        )
    }
    object Admission : AppNavRoute("ADMISSION","本科招生",R.drawable.publics)
    object VersionInfo : AppNavRoute("VERSION_INFO","本版本新特性",R.drawable.info)
    object AdmissionRegionDetail : AppNavRoute("ADMISSION_REGION_DETAIL","本科招生详情",R.drawable.publics) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            INDEX("index", NavType.IntType,-1,false),
            TYPE("type", NavType.StringType,"本科招生",false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(index: Int,type : String): String = withArgs(
            Args.INDEX.argName to index,
            Args.TYPE.argName to type
        )
    }
    object OpenOuterApplication : AppNavRoute("OPEN_OUTER_APPLICATION","打开外部App",R.drawable.arrow_split) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            PACKAGE_NAME("packageName", NavType.StringType,"",true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(appPackages: Starter.AppPackages): String = withArgs(
            Args.PACKAGE_NAME.argName to appPackages.packageName
        )
    }
    object CourseDetail : AppNavRoute("COURSE_DETAIL","课程详情",R.drawable.category) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            NAME("name", NavType.StringType,"课程详情",false),
            INDEX("id",NavType.StringType,"-1",false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(courseName : String,id : String): String = withArgs(
            Args.NAME.argName to courseName,
            Args.INDEX.argName to id
        )
    }
    object WebVpn : AppNavRoute("WEBVPN","WebVpn",R.drawable.vpn_key)
    object Holiday : AppNavRoute("HOLIDAY","法定假日",R.drawable.beach_access)
    object HolidaySchedule : AppNavRoute("HOLIDAY_SCHEDULE","调休安排", News.icon)
    object News : AppNavRoute("News","通知公告",R.drawable.stream)
    object Alumni : AppNavRoute("ALUMNI","校友平台",R.drawable.person_book)
    object AI : AppNavRoute("LARGE_MODEL","大模型",R.drawable.wand_stars)
    object Appointment : AppNavRoute("APPOINTMENT","社区预约",R.drawable.table_restaurant)
    object WorkAndRest : AppNavRoute("WORK_AND_REST","作息",R.drawable.schedule)  {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any?,override val isNullable: Boolean) : NavArg {
            FRIEND_ID("friend_id", NavType.StringType,null,true),
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(friendId : String? = null): String = withArgs(
            Args.FRIEND_ID.argName to friendId,
        )
    }
    object HaiLeWashing : AppNavRoute("HAILE_WASHING","海乐生活",R.drawable.local_laundry_service)
    object Fee : AppNavRoute("Fee","学费",R.drawable.paid)
    object StuTodayCampus : AppNavRoute("STU","今日校园",R.drawable.handshake)
    object TeacherSearch : AppNavRoute("TEACHER_SEARCH","教师检索",R.drawable.group_search)
    object Work : AppNavRoute("WORK","就业",R.drawable.azm)
    object Person : AppNavRoute("PERSON","个人信息",R.drawable.person)
    object ExamNotifications : AppNavRoute("EXAM_NOTIFICATIONS","全校考试安排", News.icon)
    object Exam : AppNavRoute("EXAM","考试",R.drawable.draw) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            ORIGIN("origin",NavType.StringType, "EXAM",true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(origin : String? = null): String = withArgs(
            Args.ORIGIN.argName to origin
        )
    }
    object DormitoryScore : AppNavRoute("DORMITORY_SCORE","寝室",R.drawable.bed)
    object Notifications : AppNavRoute("NOTIFICATIONS","消息中心",R.drawable.notifications)
    object Survey : AppNavRoute("SURVEY","评教",R.drawable.verified)
    object WebView : AppNavRoute("WEB_VIEW","网页",R.drawable.net) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any?,override val isNullable: Boolean) : NavArg {
            URL("url", NavType.StringType,"",false),
            COOKIES("cookies", NavType.StringType,null,true),
            TITLE("title", NavType.StringType,null,true),
            ICON("icon", NavType.IntType,R.drawable.net,false),
        }
        fun shareRoute(url : String)  = withArgs(Args.URL.argName to url)
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(url : String,title : String?,cookies : String? = null,icon : Int = Args.ICON.default as Int): String = withArgs(
            Args.URL.argName to url,
            Args.COOKIES.argName to cookies,
            Args.TITLE.argName to title,
            Args.ICON.argName to icon,
        )
    }
    object FailRate : AppNavRoute("FAIL_RATE","挂科率",R.drawable.radio_button_partial)
    object Transfer : AppNavRoute("TRANSFER","转专业",R.drawable.compare_arrows)
    object Library : AppNavRoute("LIBRARY","图书馆",R.drawable.book_5)
    object ProgramSearch : AppNavRoute("PROGRAM_SEARCH","全校培养方案",R.drawable.conversion_path) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object TransferDetail : AppNavRoute("TRANSFER_DETAIL","转专业详情",R.drawable.compare_arrows) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            IS_HIDDEN("isHidden", NavType.BoolType,false,false),
            BATCH_ID("batchId", NavType.StringType,"-1",false),
            NAME("name", NavType.StringType,label,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(isHidden: Boolean,batchId : String,name : String): String = withArgs(
            Args.IS_HIDDEN.argName to isHidden,
            Args.BATCH_ID.argName to batchId,
            Args.NAME.argName to name,
        )
    }
    object SelectCourseDetail : AppNavRoute("SELECT_COURSE_DETAIL","选课入口",R.drawable.ads_click) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            COURSE_ID("courseId", NavType.IntType,-1,false),
            NAME("name", NavType.StringType,label,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(courseId: Int,name : String): String = withArgs(
            Args.COURSE_ID.argName to courseId,
            Args.NAME.argName to name
        )
    }
    object DropCourse : AppNavRoute("DROP_COURSE","退课",R.drawable.ads_click) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            COURSE_ID("courseId", NavType.IntType,-1,false),
            NAME("name", NavType.StringType,label,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(courseId: Int,name : String): String = withArgs(
            Args.COURSE_ID.argName to courseId,
            Args.NAME.argName to name
        )
    }
    object Program : AppNavRoute("PROGRAM","培养方案",R.drawable.conversion_path) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object NextCourse : AppNavRoute("NEXT_COURSE","下学期课表",R.drawable.calendar) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object SelectCourse : AppNavRoute("SELECT_COURSE","选课",R.drawable.ads_click)
    object WebNavigation : AppNavRoute("WEB_NAVIGATION","网址导航",R.drawable.explore)
    object NotificationBox : AppNavRoute("BOX","收纳",R.drawable.notifications)
    object Life : AppNavRoute("LIFE","生活服务",R.drawable.near_me) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IN_FOCUS("inFocus", NavType.BoolType,false,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(inFocus: Boolean): String = withArgs(
            Args.IN_FOCUS.argName to inFocus
        )
    }
    object CourseSearch : AppNavRoute("COURSE_SEARCH","开课查询",R.drawable.search)
    object TotalCourse : AppNavRoute("TOTAL_COURSE","课程汇总",R.drawable.category) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false),
            ORIGIN("origin", NavType.StringType,"",true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean,origin : String): String = withArgs(
            Args.IF_SAVED.argName to ifSaved,
            Args.ORIGIN.argName to origin
        )
    }
    object ProgramCompetition : AppNavRoute("PROGRAM_COMPETITION","培养方案完成情况",R.drawable.leaderboard) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object ProgramCompetitionDetail : AppNavRoute("PROGRAM_COMPETITION_DETAIL","培养方案完成情况详情",R.drawable.leaderboard) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            TITLE("title", NavType.StringType,"详情",false),
            INDEX("index", NavType.IntType,-1,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(title : String,index : Int): String = withArgs(
            Args.TITLE.argName to title,
            Args.INDEX.argName to index
        )
    }
    object SecondClass : AppNavRoute("SECOND_CLASS","第二课堂",R.drawable.kid_star)
    object Bus : AppNavRoute("BUS","校车",R.drawable.directions_bus)
    object Classroom : AppNavRoute("CLASSROOM", "教室",R.drawable.meeting_room)
    object ClassroomDetail : AppNavRoute("CLASSROOM_DETAIL","教室详情",R.drawable.meeting_room) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            CODE("code", NavType.StringType,"",false),
            NAME("name", NavType.StringType,"教室",false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(bean : BuildingBean): String = withArgs(
            Args.CODE.argName to bean.code,
            Args.NAME.argName to bean.nameZh
        )
    }
    object ClassroomLessons : AppNavRoute("CLASSROOM_LESSONS","教室课程表",R.drawable.meeting_room) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            ROOM_ID("roomId", NavType.IntType,-1,false),
            NAME("name", NavType.StringType,"教室",false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(roomId : Int,name : String): String = withArgs(
            Args.ROOM_ID.argName to roomId,
            Args.NAME.argName to name
        )
    }
    object OfficeHall : AppNavRoute("OFFICE_HALL", "办事大厅",R.drawable.person_play)
    object Classmates : AppNavRoute("CLASSMATES", "同班同学",R.drawable.sensor_door)
    object Scan : AppNavRoute("SCAN", "扫描二维码",R.drawable.qr_code_scanner_shortcut)
    object SearchEdit : AppNavRoute("SEARCH_EDIT", "排序编辑",R.drawable.edit)
    object CourseSearchCalendar : AppNavRoute("COURSE_SEARCH_Calendar", "开课课程表",R.drawable.calendar) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            CLASSES("classes", NavType.StringType,null,true),
            COURSE_CODE("course_code", NavType.StringType,null,true),
            COURSE_NAME("course_name", NavType.StringType,null,true),
            TERM("term", NavType.IntType,-1,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(classes: String?,code : String?,name : String?,term : Int?): String = withArgs(
            Args.CLASSES.argName to classes,
            Args.COURSE_NAME.argName to name,
            Args.COURSE_CODE.argName to code,
            Args.TERM.argName to term
        )
    }
    object Exception : AppNavRoute("EXCEPTION","错误",R.drawable.warning) {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            EXCEPTION("exception", NavType.StringType,"",false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(exception: String): String = withArgs(
            Args.EXCEPTION.argName to exception
        )
    }
    object LibraryBorrowed : AppNavRoute("LIBRARY_BORROWED","借阅图书",R.drawable.book_5)
    object UiSettings : AppNavRoute("UI_SETTINGS","设置-外观与效果",R.drawable.format_paint)
    object ConfigSettings : AppNavRoute("CONFIG_SETTINGS","设置-应用及配置",R.drawable.joystick)
    object NetworkSettings : AppNavRoute("NETWORK_SETTINGS","设置-网络",R.drawable.net)
    object AboutSettings : AppNavRoute("ABOUT_SETTINGS","设置-维护与关于",R.drawable.partner_exchange)
}

fun getArgs(args : Iterable<NavArg>) : List<NamedNavArgument> = args.map { item ->
    navArgument(item.argName) {
        type = item.navType
        if(item.isNullable) {
            nullable = true
            defaultValue = null
        }
    }
}