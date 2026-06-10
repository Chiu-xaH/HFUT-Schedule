package com.hfut.schedule.ui.nav

import android.net.Uri
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.ui.nav.destination.AdmissionDestination
import com.hfut.schedule.ui.nav.destination.AllExamDestination
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.DepartmentsDestination
import com.hfut.schedule.ui.nav.destination.DormitoryDestination
import com.hfut.schedule.ui.nav.destination.ExceptionDestination
import com.hfut.schedule.ui.nav.destination.HomeDestination
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
    createDeepLinkByKey(AdmissionDestination),
    createDeepLinkByKey(AllExamDestination) ,
    createDeepLinkByKey(BusDestination),
    createDeepLinkByKey(DepartmentsDestination) ,
    createDeepLinkByKey(DormitoryDestination),
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