package com.hfut.schedule.ui.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.AppNavRoute.NavArg

// 主导航
sealed class AppNavRoute(val route: String) {
    interface NavArg {
        val argName: String
        val navType: NavType<out Any?>
        val default : Any
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
            append("?")
            append(args.joinToString("&") { "${it.first}=${it.second}" })
        }
    }


    object Grade : AppNavRoute("GRADE") {
        val icon = R.drawable.article
        val title = "成绩"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object Home : AppNavRoute("HOME")
    object UseAgreement : AppNavRoute("USE_AGREEMENT")
    object Admission : AppNavRoute("ADMISSION") {
        val icon = R.drawable.publics
        val title = "本科招生"
    }
    object AdmissionRegionDetail : AppNavRoute("ADMISSION_REGION_DETAIL") {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any) : NavArg {
            INDEX("index", NavType.IntType,-1),
            TYPE("type", NavType.StringType,"本科招生")
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(index: Int,type : String): String = withArgs(
            Args.INDEX.argName to index,
            Args.TYPE.argName to type
        )
    }
    object CourseDetail : AppNavRoute("COURSE_DETAIL") {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any) : NavArg {
            NAME("name", NavType.StringType,"课程详情"),
            INDEX("index",NavType.IntType,-1)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(courseName : String,index : Int): String = withArgs(
            Args.NAME.argName to courseName,
            Args.INDEX.argName to index
        )
    }
    object Holiday : AppNavRoute("HOLIDAY") {
        val icon = R.drawable.beach_access
        val title = "法定假日"
    }
    object Wechat : AppNavRoute("WECHAT") {
        val icon = R.drawable.wechat
        val title = "微信专区"
    }
    object TimeTable : AppNavRoute("TIME_TABLE") {
        val icon = R.drawable.schedule
        val title = "作息"
    }
    object HaiLeWashing : AppNavRoute("HAILE_WASHING") {
        val title = "海乐生活"
    }
    object Fee : AppNavRoute("Fee") {
        val icon = R.drawable.paid
        val title = "学费"
    }
    object StuTodayCampus : AppNavRoute("STU") {
        val icon = R.drawable.handshake
        val title = "学工系统"
    }
    object TeacherSearch : AppNavRoute("TEACHER_SEARCH") {
        val icon = R.drawable.group
        val title = "教师检索"
    }
    object Work : AppNavRoute("WORK") {
        val icon = R.drawable.azm
        val title = "就业"
    }
    object Person : AppNavRoute("PERSON") {
        val icon = R.drawable.person
        val title = "个人信息"
    }
    object FailRate : AppNavRoute("FAIL_RATE") {
        val icon = R.drawable.monitoring
        val title = "挂科率"
    }
    object Exam : AppNavRoute("EXAM") {
        val icon = R.drawable.draw
        val title = "考试"
    }
    object WebNavigation : AppNavRoute("WEB_NAVIGATION") {
        val icon = R.drawable.net
        val title = "网址导航"
    }
    object SelectCourse : AppNavRoute("SELECT_COURSE") {
        val icon = R.drawable.ads_click
        val title = "选课"
    }
    object DormitoryScore : AppNavRoute("DORMITORY_SCORE") {
        val icon = R.drawable.psychiatry
        val title = "寝室评分"
    }
    object Notifications : AppNavRoute("NOTIFICATIONS") {
        val icon = R.drawable.notifications
        val title = "消息中心"
    }
    object Survey : AppNavRoute("SURVEY") {
        val icon = R.drawable.verified
        val title = "评教"
    }
    object WebView : AppNavRoute("WEB_VIEW") {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any) : NavArg {
            URL("url", NavType.StringType,""),
            COOKIES("cookies", NavType.StringType,""),
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(url : String,cookies : String): String = withArgs(
            Args.URL.argName to url,
            Args.COOKIES.argName to cookies
        )
    }
    object Life : AppNavRoute("LIFE") {
        val icon = R.drawable.near_me
        val title = "生活服务"
    }
    object Transfer : AppNavRoute("TRANSFER") {
        val icon = R.drawable.compare_arrows
        val title = "转专业"
    }
    object CourseSearch : AppNavRoute("COURSE_SEARCH") {
        val icon = R.drawable.search
        val title = "开课查询"
    }
    object EmptyRoom : AppNavRoute("EMPTY_ROOM") {
        val icon = R.drawable.meeting_room
        val title = "空教室"
    }
    object NextCourse : AppNavRoute("NEXT_COURSE") {
        val icon = R.drawable.calendar
        val title = "下学期课表"
    }
    object Library : AppNavRoute("LIBRARY") {
        val icon = R.drawable.book_5
        val title = "图书馆"
    }
    object TotalCourse : AppNavRoute("TOTAL_COURSE") {
        val icon = R.drawable.category
        val title = "课程汇总"
    }
    object ProgramSearch : AppNavRoute("PROGRAM_SEARCH") {
        val title = "全校培养方案"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object Program : AppNavRoute("PROGRAM") {
        val title = "培养方案"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object ProgramCompetition : AppNavRoute("PROGRAM_COMPETITION") {
        val title = "培养方案完成情况"
    }
}

fun getArgs(args : Iterable<NavArg>) : List<NamedNavArgument> = args.map { item ->
    navArgument(item.argName) {
        type = item.navType
    }
}