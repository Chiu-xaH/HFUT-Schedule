package com.hfut.schedule.network.api.util

import com.xah.common.logic.model.Campus

private enum class UniAppCampusDto(val code : Int) {
    XC(6),
    TXL(2),
    FCH(3)
}

fun getUinAppCampusId(campus: Campus) = when (campus) {
    Campus.XC -> UniAppCampusDto.XC.code
    Campus.FCH -> UniAppCampusDto.FCH.code
    Campus.TXL -> UniAppCampusDto.TXL.code
}