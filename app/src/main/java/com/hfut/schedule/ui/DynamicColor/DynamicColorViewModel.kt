package com.hfut.schedule.ui.DynamicColor

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hfut.schedule.MyApplication.Companion.DEFAULT_THEME
import com.hfut.schedule.ui.DynamicColor.PrefHelper.get
import com.hfut.schedule.ui.DynamicColor.PrefHelper.operation
import com.hfut.schedule.ui.DynamicColor.PrefHelper.put
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DynamicColorViewModel  @Inject constructor(private val app: Application) : ViewModel() {



    val currentTheme = mutableStateOf(
        PrefHelper.prefs(app.applicationContext)
            .get(PrefHelper.KEY_CURRENT_THEME, DEFAULT_THEME) as String
    )

    fun setCurrentTheme(theme: String) {
        currentTheme.value = theme
        PrefHelper.prefs(app.applicationContext).operation {
            it.put(Pair(PrefHelper.KEY_CURRENT_THEME, theme))
        }
    }
}