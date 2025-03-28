package com.hfut.schedule.logic.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.utils.NavigateAnimationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val dataStore = MyApplication.context.dataStore

    private val ANIMATION_TYPE = intPreferencesKey("animation_types")
    private val STU_COOKIE = stringPreferencesKey("stu_cookie")
    private val PURE_DARK = booleanPreferencesKey("pure_dark")
    private val COLOR_MODE = intPreferencesKey("color_mode")


    enum class ColorMode(val code : Int) {
        LIGHT(1),DARK(2),AUTO(0)
    }

    suspend fun saveAnimationType(type: Int) {
        dataStore.edit { preferences ->
            preferences[ANIMATION_TYPE] = type
        }
    }
    suspend fun saveStuCookie(cookie: String) {
        dataStore.edit { preferences ->
            preferences[STU_COOKIE] = cookie
        }
    }
    suspend fun savePureDark(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[PURE_DARK] = switch
        }
    }
    suspend fun saveColorMode(switch: ColorMode) {
        dataStore.edit { preferences ->
            preferences[COLOR_MODE] = switch.code
        }
    }


    val animationTypeFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[ANIMATION_TYPE] ?: NavigateAnimationManager.AnimationTypes.FadeAnimation.code
        }
    val stuCookieFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[STU_COOKIE] ?: ""
        }
    val pureDarkFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PURE_DARK] ?: false
        }
    val colorModeFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[COLOR_MODE] ?: ColorMode.AUTO.code
        }
    /* 用法
    val currentAnimationIndex by DataStoreManager.XXX.collectAsState(initial = 默认值)
     */
}




//
//fun main() {
//    val dataStore = DataStoreManager().dataStore
//
//    suspend fun incrementCounter() {
//        dataStore.edit { settings ->
//            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
//            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
//        }
//    }
//    CoroutineScope(Job()).launch {
//        async { incrementCounter() }
//    }
//
//    val exampleCounterFlow: Flow<Int> = dataStore.data.map { preferences ->
//        // 无类型安全
//        preferences[EXAMPLE_COUNTER] ?: 0
//    }
//    println(exampleCounterFlow)
//}