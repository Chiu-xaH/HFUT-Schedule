package com.hfut.schedule.logic.util.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.network.parse.ParseJsons.useCaptcha
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.util.MyAnimationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val dataStore = MyApplication.context.dataStore

    private val ANIMATION_TYPE = intPreferencesKey("animation_types")
    private val STU_COOKIE = stringPreferencesKey("stu_cookie")
    private val PURE_DARK = booleanPreferencesKey("pure_dark")
    private val COLOR_MODE = intPreferencesKey("color_mode")
    private val MOTION_BLUR = booleanPreferencesKey("motion_blur_2")
    private val HAZE_BLUR = booleanPreferencesKey("haze_blur_2")
    private val TRANSITION = booleanPreferencesKey("transition")
    private val MOTION_ANIMATION_TYPE = booleanPreferencesKey("motion_animation_type")
    private val SHOW_CLOUD_FOCUS = booleanPreferencesKey("show_cloud_focus")
    private val SHOW_FOCUS = booleanPreferencesKey("show_focus")
    private val SUPABASE_JWT = stringPreferencesKey("supabase_jwt")
    private val SUPABASE_REFRESH_TOKEN = stringPreferencesKey("supabase_refresh_token")
    private val SUPABASE_FILTER_EVENT = booleanPreferencesKey("supabase_filter_event")
    private val SUPABASE_AUTO_CHECK = booleanPreferencesKey("supabase_auto_check")
    private val USE_CAPTCHA = booleanPreferencesKey("use_captcha")
    private val USE_CAPTCHA_AUTO = booleanPreferencesKey("use_captcha_auto")
    private val FOCUS_SHOW_SHOWER = booleanPreferencesKey("focus_show_shower")
    private val FOCUS_SHOW_WEATHER_WARN = booleanPreferencesKey("focus_show_weather_warn")
    private val FOCUS_SHOW_SPECIAL = booleanPreferencesKey("focus_show_special")
    private val CARD_PASSWORD = stringPreferencesKey("card_password")
    private val USE_DEFAULT_CARD_PASSWORD = booleanPreferencesKey("use_default_card_password")

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
    suspend fun saveShowFocus(switch: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_FOCUS] = switch
        }
    }
    suspend fun saveSupabaseJwt(value: String) {
        dataStore.edit { preferences ->
            preferences[SUPABASE_JWT] = value
        }
    }
    suspend fun saveSupabaseRefreshToken(value: String) {
        dataStore.edit { preferences ->
            preferences[SUPABASE_REFRESH_TOKEN] = value
        }
    }
    suspend fun saveSupabaseFilterEvent(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUPABASE_FILTER_EVENT] = value
        }
    }
    suspend fun saveSupabaseAutoCheck(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUPABASE_AUTO_CHECK] = value
        }
    }
    suspend fun saveUseCaptcha(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_CAPTCHA] = value
        }
    }
    suspend fun saveUseCaptchaAuto(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_CAPTCHA_AUTO] = value
        }
    }
    suspend fun saveFocusShowShower(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOCUS_SHOW_SHOWER] = value
        }
    }
    suspend fun saveFocusShowWeatherWarn(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOCUS_SHOW_WEATHER_WARN] = value
        }
    }
    suspend fun saveFocusShowSpecial(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOCUS_SHOW_SPECIAL] = value
        }
    }
    suspend fun saveCardPassword(value: String) {
        dataStore.edit { preferences ->
            preferences[CARD_PASSWORD] = value
        }
    }
    suspend fun saveUseDefaultCardPassword(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_DEFAULT_CARD_PASSWORD] = value
        }
    }


    val animationTypeFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[ANIMATION_TYPE] ?: MyAnimationManager.AnimationTypes.CenterAnimation.code
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
            preferences[MOTION_BLUR] ?: AppVersion.CAN_MOTION_BLUR
        }
    val hazeBlurFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[HAZE_BLUR] ?: true
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
    val showFocusFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SHOW_FOCUS] ?: true
        }
    val supabaseJwtFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SUPABASE_JWT] ?: ""
        }
    val supabaseRefreshTokenFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SUPABASE_REFRESH_TOKEN] ?: ""
        }
    val supabaseFilterEventFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SUPABASE_FILTER_EVENT] ?: true
        }
    val supabaseAutoCheck: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SUPABASE_AUTO_CHECK] ?: true
        }
    val useCaptcha: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[USE_CAPTCHA] ?: useCaptcha()
        }
    val useCaptchaAuto: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[USE_CAPTCHA_AUTO] ?: true
        }
    val showFocusShower: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[FOCUS_SHOW_SHOWER] ?: true
        }
    val showFocusWeatherWarn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[FOCUS_SHOW_WEATHER_WARN] ?: true
        }
//    val showFocusSpecial: Flow<Boolean> = dataStore.data
//        .map { preferences ->
//            preferences[FOCUS_SHOW_SPECIAL] ?: true
//        }
    val cardPassword: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[CARD_PASSWORD] ?: ""
        }
    val useDefaultCardPassword: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[USE_DEFAULT_CARD_PASSWORD] ?: true
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