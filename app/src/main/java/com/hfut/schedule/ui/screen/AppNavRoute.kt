package com.hfut.schedule.ui.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
    object Admission : AppNavRoute("ADMISSION")
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
}

fun getArgs(args : Iterable<NavArg>) : List<NamedNavArgument> = args.map { item ->
    navArgument(item.argName) {
        type = item.navType
    }
}