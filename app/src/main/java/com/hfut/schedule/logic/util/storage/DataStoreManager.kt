package com.hfut.schedule.logic.util.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.util.NavigateAnimationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val dataStore = MyApplication.context.dataStore

    private val ANIMATION_TYPE = intPreferencesKey("animation_types")
    private val STU_COOKIE = stringPreferencesKey("stu_cookie")
    private val PURE_DARK = booleanPreferencesKey("pure_dark")
    private val COLOR_MODE = intPreferencesKey("color_mode")
    private val MOTION_BLUR = booleanPreferencesKey("motion_blur")
    private val HAZE_BLUR = booleanPreferencesKey("haze_blur")
    private val TRANSITION = booleanPreferencesKey("transition")
    private val MOTION_ANIMATION_TYPE = booleanPreferencesKey("motion_animation_type")
    private val SHOW_CLOUD_FOCUS = booleanPreferencesKey("show_cloud_focus")





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
    suspend fun saveMotionBlur(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[MOTION_BLUR] = switch
        }
    }
    suspend fun saveHazeBlur(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAZE_BLUR] = switch
        }
    }
    suspend fun saveMotionAnimation(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[MOTION_ANIMATION_TYPE] = switch
        }
    }
    suspend fun saveTransition(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[TRANSITION] = switch
        }
    }
    suspend fun saveShowCloudFocus(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_CLOUD_FOCUS] = switch
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
    val motionBlurFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[MOTION_BLUR] ?: true
        }
    val hazeBlurFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[HAZE_BLUR] ?: AppVersion.canBlur
        }
    val motionAnimationTypeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[MOTION_ANIMATION_TYPE] ?: false
        }
    val transitionFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[TRANSITION] ?: false
        }
    val showCloudFocusFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SHOW_CLOUD_FOCUS] ?: true
        }
    /* 用法
    val XXX by DataStoreManager.XXX.collectAsState(initial = 默认值)
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