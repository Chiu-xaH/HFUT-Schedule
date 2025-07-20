package com.hfut.schedule.ui.screen

// 主导航
sealed class AppNavRoute(val route: String) {
    // 用于 composable() 注册 route pattern
    // 得到若干参数的composable("XXX")接受 例如 $route?ifSaved={ifSaved}
    internal fun receiveRoute(vararg argNames: String): String {
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
            append("?")
            append(args.joinToString("&") { "${it.first}=${it.second}" })
        }
    }
    object Grade : AppNavRoute("GRADE") {
        fun receiveRoute() = receiveRoute("ifSaved")
        fun withArgs(ifSaved: Boolean): String = withArgs("ifSaved" to ifSaved)
    }
    object Home : AppNavRoute("HOME")
    object UseAgreement : AppNavRoute("USE_AGREEMENT")
    object Admission : AppNavRoute("ADMISSION")
    object AdmissionRegionDetail : AppNavRoute("ADMISSION_REGION_DETAIL") {
        fun receiveRoute() = receiveRoute("index","type")
        fun withArgs(index: Int,type : String): String = withArgs("index" to index,"type" to type)
    }
}