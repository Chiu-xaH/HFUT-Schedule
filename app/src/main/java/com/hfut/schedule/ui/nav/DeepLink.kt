package com.hfut.schedule.ui.nav

import com.hfut.schedule.ui.nav.destination.AdmissionDestination
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.DepartmentsDestination
import com.hfut.schedule.ui.nav.destination.DormitoryDestination
import com.hfut.schedule.ui.nav.destination.AllExamDestination
import com.hfut.schedule.ui.nav.destination.HomeDestination
import com.xah.navigation.model.dest.DeepLink

private val AdmissionDeepLink = with(AdmissionDestination) {
    DeepLink(this.key) { this }
}

private val AllExamDeepLink = with(AllExamDestination) {
    DeepLink(this.key) { this }
}

private val BusDeepLink = with(BusDestination) {
    DeepLink(this.key) { this }
}

private val DepartmentsDeepLink = with(DepartmentsDestination) {
    DeepLink(this.key) { this }
}

private val DormitoryDeepLink = with(DormitoryDestination) {
    DeepLink(this.key) { this }
}

private val HomeDeepLink = with(HomeDestination) {
    DeepLink(this.key) { this }
}

val deepLinks = listOf(
    AdmissionDeepLink,
    AllExamDeepLink,
    BusDeepLink,
    DepartmentsDeepLink,
    DormitoryDeepLink,
    HomeDeepLink
)