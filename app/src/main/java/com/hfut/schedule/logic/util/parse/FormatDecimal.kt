package com.hfut.schedule.logic.util.parse

import java.math.BigDecimal
import java.math.RoundingMode


fun formatDecimal(res : Double, weiShu : Int) : String {
    return try {
        val bd = BigDecimal(res.toString())
        bd.setScale(weiShu, RoundingMode.HALF_UP).toString()
    } catch (_ : Exception) {
        "0"
    }
}