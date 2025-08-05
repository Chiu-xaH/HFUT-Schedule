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



    object Grade : AppNavRoute("GRADE") {
        val icon = R.drawable.article
        val title = "成绩"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
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
    object CourseDetail : AppNavRoute("COURSE_DETAIL") {
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default: Any,override val isNullable: Boolean) : NavArg {
            NAME("name", NavType.StringType,"课程详情",false),
            INDEX("index",NavType.IntType,-1,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(courseName : String,index : Int): String = withArgs(
            Args.NAME.argName to courseName,
            Args.INDEX.argName to index
        )
    }
    object WebVpn : AppNavRoute("WEBVPN") {
        val icon = R.drawable.vpn_key
        val title = "WebVpn"
    }
    object Holiday : AppNavRoute("HOLIDAY") {
        val icon = R.drawable.beach_access
        val title = "法定假日"
    }
    object News : AppNavRoute("News") {
        val icon = R.drawable.stream
        val title = "通知公告"
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
    object Exam : AppNavRoute("EXAM") {
        val icon = R.drawable.draw
        val title = "考试"
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
    object FailRate : AppNavRoute("FAIL_RATE") {
        val icon = R.drawable.monitoring
        val title = "挂科率"
    }
    object Transfer : AppNavRoute("TRANSFER") {
        val icon = R.drawable.compare_arrows
        val title = "转专业"
    }
    object Library : AppNavRoute("LIBRARY") {
        val icon = R.drawable.book_5
        val title = "图书馆"
    }
    object ProgramSearch : AppNavRoute("PROGRAM_SEARCH") {
        val title = "全校培养方案"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>,override val default : Any,override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object Program : AppNavRoute("PROGRAM") {
        val title = "培养方案"
        val icon = R.drawable.conversion_path
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object NextCourse : AppNavRoute("NEXT_COURSE") {
        val icon = R.drawable.calendar
        val title = "下学期课表"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object SelectCourse : AppNavRoute("SELECT_COURSE") {
        val icon = R.drawable.ads_click
        val title = "选课"
    }
    object WebNavigation : AppNavRoute("WEB_NAVIGATION") {
        val icon = R.drawable.explore
        val title = "网址导航"
    }
    object NotificationBox : AppNavRoute("BOX") {
        val icon = R.drawable.notifications
        val title = "收纳"
    }
    object Life : AppNavRoute("LIFE") {
        val icon = R.drawable.near_me
        val title = "生活服务"
    }
    object CourseSearch : AppNavRoute("COURSE_SEARCH") {
        val icon = R.drawable.search
        val title = "开课查询"
    }
    object TotalCourse : AppNavRoute("TOTAL_COURSE") {
        val icon = R.drawable.category
        val title = "课程汇总"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object ProgramCompetition : AppNavRoute("PROGRAM_COMPETITION") {
        val title = "培养方案完成情况"
        val icon = R.drawable.leaderboard
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            IF_SAVED("ifSaved", NavType.BoolType,true,false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(ifSaved: Boolean): String = withArgs(
            Args.IF_SAVED.argName to ifSaved
        )
    }
    object ProgramCompetitionDetail : AppNavRoute("PROGRAM_COMPETITION_DETAIL") {
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
    object EmptyRoom : AppNavRoute("EMPTY_ROOM") {
        val icon = R.drawable.meeting_room
        val title = "空教室"
    }
    object Exception : AppNavRoute("EXCEPTION") {
        val icon = R.drawable.warning
        val title = "错误"
        enum class Args(override val argName: String, override val navType: NavType<out Any?>, override val default : Any?, override val isNullable: Boolean) : NavArg {
            EXCEPTION("exception", NavType.StringType,"",false)
        }
        fun receiveRoute() = receiveRoutePair(Args.entries)
        fun withArgs(exception: String): String = withArgs(
            Args.EXCEPTION.argName to exception
        )
    }
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