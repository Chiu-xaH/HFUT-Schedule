package com.hfut.schedule.logic.util.parse

import com.xah.common.logic.util.LogUtil
import java.math.BigDecimal
import java.math.RoundingMode

fun Float.roundOff(weiShu: Int): Float {
    return try {
        roundOffBd(weiShu)!!.toFloat()
    } catch (e: Exception) {
        LogUtil.error(e)
        0f
    }
}

fun Double.roundOff(weiShu: Int): Double {
    return try {
        roundOffBd(weiShu)!!.toDouble()
    } catch (e: Exception) {
        LogUtil.error(e)
        0.0
    }
}

fun Float.roundOffString(weiShu : Int) : String {
    return try {
        roundOffBd(weiShu).toString()
    } catch (_ : Exception) {
        "0"
    }
}

fun Double.roundOffString(weiShu : Int) : String {
    return try {
        roundOffBd(weiShu).toString()
    } catch (_ : Exception) {
        "0"
    }
}

private fun Float.roundOffBd(weiShu : Int) : BigDecimal? {
    return BigDecimal(this.toString()).setScale(weiShu, RoundingMode.HALF_UP)
}

private fun Double.roundOffBd(weiShu : Int) : BigDecimal? {
    return BigDecimal(this.toString()).setScale(weiShu, RoundingMode.HALF_UP)
}

