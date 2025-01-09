package com.hfut.schedule.ui.utils

import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import kotlin.reflect.typeOf

object BlurManager : PrefsBooleanManager("SWITCHMOTIONBLUR",false)

interface PrefsIn<T> {
    fun setValue(value: T)
    fun getValue(): T
}


open class PrefsBooleanManager(title : String, default: Boolean = true) : PrefsIn<Boolean> {
    private val titles = title
    private val defaults = default


    override fun  setValue(value: Boolean) {
        SharePrefs.saveBoolean(titles, prefs.getBoolean(titles,defaults),value)
    }

    override fun  getValue(): Boolean {
        return prefs.getBoolean(titles,defaults)
    }
}
