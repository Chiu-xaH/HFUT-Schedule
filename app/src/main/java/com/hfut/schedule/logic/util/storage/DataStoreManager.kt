package com.hfut.schedule.logic.util.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.other.AppVersion.CAN_PREDICTIVE
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.util.AppAnimationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

object DataStoreManager {
    /* 用法
    val XXX by DataStoreManager.XXX.collectAsState(initial = 默认值)
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val dataStore = MyApplication.context.dataStore
    enum class ColorMode(val code : Int) {
        LIGHT(1),DARK(2),AUTO(0)
    }
    enum class AnimationSpeed(val code : Int,val speed : Int,val title : String) {
        SLOW(3,550,"慢"),NORMAL(0,400,"正常"),FAST(1,250,"快"),NONE(2,0,"无")
    }
    private const val EMPTY_STRING = ""

    private suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) = dataStore.edit { it[key] = value }
    private fun <T> getFlow(key: Preferences.Key<T>, default: T): Flow<T> = dataStore.data.map { it[key] ?: default }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> getFlowSuspend(
        key: Preferences.Key<T>,
        defaultProvider: suspend () -> T
    ): Flow<T> {
        return dataStore.data.flatMapLatest { preferences ->
            val value = preferences[key]
            if (value != null) {
                flowOf(value)
            } else {
                flow {
                    defaultProvider()?.let { emit(it) }
                }
            }
        }
    }


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
    private val FOCUS_SHOW_SHOWER = booleanPreferencesKey("focus_show_shower")
    private val FOCUS_SHOW_WEATHER_WARN = booleanPreferencesKey("focus_show_weather_warn")
    private val CARD_PASSWORD = stringPreferencesKey("card_password")
    private val USE_DEFAULT_CARD_PASSWORD = booleanPreferencesKey("use_default_card_password")
    private val DEFAULT_CALENDAR_ACCOUNT = longPreferencesKey("default_calendar_account")
    private val COURSE_TABLE_TIME = stringPreferencesKey("course_table_time")
    private val COURSE_TABLE_TIME_NEXT = stringPreferencesKey("course_table_time_next")
    private val WEBVPN_COOKIE = stringPreferencesKey("webvpn_cookie")
    private val FIRST_USE = booleanPreferencesKey("first_use")
    private val AUTO_TERM = booleanPreferencesKey("auto_term")
    private val AUTO_TERM_VALUE = intPreferencesKey("auto_term_value")
    private val TODAY_CAMPUS_TIP = booleanPreferencesKey("today_campus_tip")
    private val COURSE_BOOK = stringPreferencesKey("course_book")
    private val ANIMATION_SPEED = intPreferencesKey("animation_speed")
    private val WEB_VIEW_DARK = booleanPreferencesKey("web_view_dark")
    private val PREDICTIVE = booleanPreferencesKey("predictive")


    suspend fun saveAnimationType(value: Int) = saveValue(ANIMATION_TYPE,value)
    suspend fun saveStuCookie(value: String) = saveValue(STU_COOKIE,value)
    suspend fun savePureDark(value: Boolean) = saveValue(PURE_DARK,value)
    suspend fun saveColorMode(mode: ColorMode) = saveValue(COLOR_MODE,mode.code)
    suspend fun saveMotionBlur(value: Boolean) = saveValue(MOTION_BLUR,value)
    suspend fun saveHazeBlur(value: Boolean) = saveValue(HAZE_BLUR,value)
    suspend fun saveTransition(value: Boolean) = saveValue(TRANSITION,value)
    suspend fun saveShowCloudFocus(value: Boolean) = saveValue(SHOW_CLOUD_FOCUS,value)
    suspend fun saveShowFocus(value: Boolean) = saveValue(SHOW_FOCUS,value)
    suspend fun saveSupabaseJwt(value: String) = saveValue(SUPABASE_JWT,value)
    suspend fun saveSupabaseRefreshToken(value: String) = saveValue(SUPABASE_REFRESH_TOKEN,value)
    suspend fun saveSupabaseFilterEvent(value: Boolean) = saveValue(SUPABASE_FILTER_EVENT,value)
    suspend fun saveSupabaseAutoCheck(value: Boolean) = saveValue(SUPABASE_AUTO_CHECK,value)
    suspend fun saveFocusShowShower(value: Boolean) = saveValue(FOCUS_SHOW_SHOWER,value)
    suspend fun saveFocusShowWeatherWarn(value: Boolean) = saveValue(FOCUS_SHOW_WEATHER_WARN,value)
    suspend fun saveCardPassword(value: String) = saveValue(CARD_PASSWORD,value)
    suspend fun saveUseDefaultCardPassword(value: Boolean) = saveValue(USE_DEFAULT_CARD_PASSWORD,value)
    suspend fun saveDefaultCalendarAccount(value: Long) = saveValue(DEFAULT_CALENDAR_ACCOUNT,value)
    suspend fun saveCourseTable(value: String) = saveValue(COURSE_TABLE_TIME,value)
    suspend fun saveWebVpnCookie(value: String) = saveValue(WEBVPN_COOKIE,value)
    suspend fun saveFastStart(value: Boolean) = saveValue(FIRST_USE,value)
    suspend fun saveAutoTerm(value: Boolean) = saveValue(AUTO_TERM,value)
    suspend fun saveAutoTermValue(value: Int) = saveValue(AUTO_TERM_VALUE,value)
    suspend fun saveTodayCampusTip(value: Boolean) = saveValue(TODAY_CAMPUS_TIP,value)
    suspend fun saveCourseBook(value: String) = saveValue(COURSE_BOOK,value)
    suspend fun saveAnimationSpeed(value: AnimationSpeed) = saveValue(ANIMATION_SPEED,value.code)
    suspend fun saveWebViewDark(value: Boolean) = saveValue(WEB_VIEW_DARK,value)
    suspend fun savePredict(value: Boolean) = saveValue(PREDICTIVE,value)


    val animationTypeFlow = getFlow(ANIMATION_TYPE,AppAnimationManager.AnimationTypes.CenterAnimation.code)
    val stuCookieFlow = getFlow(STU_COOKIE,EMPTY_STRING)
    val pureDarkFlow = getFlow(PURE_DARK,false)
    val colorModeFlow = getFlow(COLOR_MODE,ColorMode.AUTO.code)
    val motionBlurFlow = getFlow(MOTION_BLUR,AppVersion.CAN_MOTION_BLUR)
    val hazeBlurFlow = getFlow(HAZE_BLUR,true)
    val transitionFlow = getFlow(TRANSITION,false)
    val showCloudFocusFlow = getFlow(SHOW_CLOUD_FOCUS,true)
    val showFocusFlow = getFlow(SHOW_FOCUS,true)
    val supabaseJwtFlow = getFlow(SUPABASE_JWT,EMPTY_STRING)
    val supabaseRefreshTokenFlow = getFlow(SUPABASE_REFRESH_TOKEN,EMPTY_STRING)
    val supabaseFilterEventFlow = getFlow(SUPABASE_FILTER_EVENT,true)
    val supabaseAutoCheck = getFlow(SUPABASE_AUTO_CHECK,true)
    val showFocusShower = getFlow(FOCUS_SHOW_SHOWER,true)
    val showFocusWeatherWarn = getFlow(FOCUS_SHOW_WEATHER_WARN,true)
    val cardPassword = getFlow(CARD_PASSWORD,EMPTY_STRING)
    val useDefaultCardPassword = getFlow(USE_DEFAULT_CARD_PASSWORD,true)
    val defaultCalendarAccount = getFlow(DEFAULT_CALENDAR_ACCOUNT,1)
    val courseTableTime = getFlow(COURSE_TABLE_TIME,EMPTY_STRING)
    val courseTableTimeNext = getFlow(COURSE_TABLE_TIME_NEXT,EMPTY_STRING)
    val webVpnCookie = getFlow(WEBVPN_COOKIE,EMPTY_STRING)
    val autoTerm = getFlow(AUTO_TERM,true)
    val todayCampusTip = getFlow(TODAY_CAMPUS_TIP,true)
    val enablePredictive = getFlow(PREDICTIVE,CAN_PREDICTIVE)
    val webViewDark = getFlow(WEB_VIEW_DARK,true)
    val courseBookJson = getFlow(COURSE_BOOK,EMPTY_STRING)
    val animationSpeedType = getFlow(ANIMATION_SPEED, AnimationSpeed.NORMAL.code)
    val autoTermValue: Flow<Int> =  dataStore.data.map { it[AUTO_TERM_VALUE] ?: getSemseter() }
    val firstStart = getFlow(FIRST_USE,prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false))
}