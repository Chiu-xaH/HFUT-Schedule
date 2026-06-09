package com.hfut.schedule.ui.nav

import android.net.Uri
import com.hfut.schedule.ui.nav.destination.AdmissionDestination
import com.hfut.schedule.ui.nav.destination.AllExamDestination
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.DepartmentsDestination
import com.hfut.schedule.ui.nav.destination.DormitoryDestination
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

val deepLinks = listOf(
    createDeepLinkByKey(AdmissionDestination),
    createDeepLinkByKey(AllExamDestination) ,
    createDeepLinkByKey(BusDestination),
    createDeepLinkByKey(DepartmentsDestination) ,
    createDeepLinkByKey(DormitoryDestination),
    createDeepLinkByKey(HomeDestination),
)

//val deepLinks = deepLinkBeans.map { it }