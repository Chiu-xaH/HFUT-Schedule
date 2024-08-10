package com.hfut.schedule.logic.utils

import java.math.BigDecimal
import java.math.RoundingMode

object ReservDecimal {
    fun reservDecimal(res : Double,weiShu : Int) : String {
        return try {
            val bd = BigDecimal(res.toString())
            bd.setScale(weiShu, RoundingMode.HALF_UP).toString()
        } catch (_ : Exception) {
            "0"
        }
    }
}