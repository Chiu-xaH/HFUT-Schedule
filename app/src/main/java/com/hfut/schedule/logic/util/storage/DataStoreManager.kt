package com.hfut.schedule.logic.util.storage

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.other.AppVersion.CAN_PREDICTIVE
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.materialkolor.PaletteStyle
import com.xah.transition.style.TransitionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    enum class ColorStyle(val code : Int,val description: String,val style : PaletteStyle) {
        DEFAULT(2,"正常", PaletteStyle.TonalSpot),
        LIGHT(1,"淡雅", PaletteStyle.Neutral),
        DEEP(3,"艳丽", PaletteStyle.Vibrant),
        BLACK(0,"黑白", PaletteStyle.Monochrome),
    }

    val SEARCH_DEFAULT_STR = GlobalUIStateHolder.funcDefault.map { it.id }.joinToString(",")

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

    data class HefeiElectricStorage(
        val buildingNumber : String,
        val roomNumber : String,
        val name : String
    )


    private val ANIMATION_TYPE = intPreferencesKey("animation_types")
    private val STU_COOKIE = stringPreferencesKey("stu_cookie")
    private val PURE_DARK = booleanPreferencesKey("pure_dark")
    private val COLOR_MODE = intPreferencesKey("color_mode")
    private val MOTION_BLUR = booleanPreferencesKey("motion_blur_2")
    private val HAZE_BLUR = intPreferencesKey("haze_blur_3")
    private val TRANSITION = intPreferencesKey("transitions_2")
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
    private val WEB_VIEW_DARK = booleanPreferencesKey("web_view_dark")
    private val PREDICTIVE = booleanPreferencesKey("predictive")
    private val CONTROL_CENTER = booleanPreferencesKey("control_center")
    private val WX_AUTH = stringPreferencesKey("wx_auth")
    private val CUSTOM_COLOR = longPreferencesKey("custom_color")
    private val CUSTOM_BACKGROUND = stringPreferencesKey("custom_background")
    private val CUSTOM_COLOR_STYLE = intPreferencesKey("custom_color_style_2")
    private val CUSTOM_BACKGROUND_ALPHA = floatPreferencesKey("custom_background_alpha")
    private val SEARCH_SORT = stringPreferencesKey("search_sort")
    private val MAX_FLOW = intPreferencesKey("max_flow")
    private val SHOW_BOTTOM_BAR_LABEL = booleanPreferencesKey("show_bottom_bar_label")
    private val USUALLY_ITEMS = stringPreferencesKey("usually_items")
    private val HIDE_EMPTY_CALENDAR_SQUARE = booleanPreferencesKey("hide_empry_calendar_square")
    private val HEFEI_ROOM_NUMBER = stringPreferencesKey("hefei_room_number")
    private val HEFEI_BUILDING_NUMBER = stringPreferencesKey("hefei_building_number")
    private val HEFEI_ELECTRIC = stringPreferencesKey("hefei_electric")
    private val HEFEI_ELECTRIC_FEE = stringPreferencesKey("hefei_electric_fee")
    private val USE_HEFEI_ELECTRIC = booleanPreferencesKey("use_hefei_electric")
    private val LIQUID_GLASS = booleanPreferencesKey("liquid_glass")

    suspend fun saveAnimationType(value: Int) = saveValue(ANIMATION_TYPE,value)
    suspend fun saveStuCookie(value: String) = saveValue(STU_COOKIE,value)
    suspend fun savePureDark(value: Boolean) = saveValue(PURE_DARK,value)
    suspend fun saveColorMode(mode: ColorMode) = saveValue(COLOR_MODE,mode.code)
    suspend fun saveMotionBlur(value: Boolean) = saveValue(MOTION_BLUR,value)
    suspend fun saveHazeBlur(value: HazeBlurLevel) = saveValue(HAZE_BLUR, value.code)
    suspend fun saveTransition(value: TransitionLevel) = saveValue(TRANSITION,value.code)
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
    suspend fun saveCustomColorStyle(value: ColorStyle) = saveValue(CUSTOM_COLOR_STYLE,value.code)
    suspend fun saveWebViewDark(value: Boolean) = saveValue(WEB_VIEW_DARK,value)
    suspend fun savePredict(value: Boolean) = saveValue(PREDICTIVE,value)
    suspend fun saveControlCenter(value: Boolean) = saveValue(CONTROL_CENTER,value)
    suspend fun saveWxAuth(value: String) = saveValue(WX_AUTH, "Bearer $value")
    suspend fun saveCustomColor(value: Long) = saveValue(CUSTOM_COLOR, value)
    suspend fun saveCustomBackground(value: Uri?) = saveValue(CUSTOM_BACKGROUND, value?.toString() ?: EMPTY_STRING)
    suspend fun saveCustomBackgroundAlpha(value: Float) = saveValue(CUSTOM_BACKGROUND_ALPHA,value)
    suspend fun saveSearchSort(value: List<Int>) = saveValue(SEARCH_SORT, value.joinToString(","))
    suspend fun saveMaxFlow(value: Int) = saveValue(MAX_FLOW, value)
    suspend fun saveShowBottomBarLabel(value: Boolean) = saveValue(SHOW_BOTTOM_BAR_LABEL,value)
    suspend fun saveUsuallyItems(value: List<String>) = saveValue(USUALLY_ITEMS, value.joinToString(","))
    suspend fun saveHideEmptyCalendarSquare(value: Boolean) = saveValue(HIDE_EMPTY_CALENDAR_SQUARE,value)
    private suspend fun saveHefeiBuildingNumber(value: String) = saveValue(HEFEI_BUILDING_NUMBER, value)
    private suspend fun saveHefeiElectricName(value: String) = saveValue(HEFEI_ELECTRIC, value)
    private suspend fun saveHefeiRoomNumber(value: String) = saveValue(HEFEI_ROOM_NUMBER, value)
    suspend fun saveHefeiElectricFee(value: String) = saveValue(HEFEI_ELECTRIC_FEE, value)
    suspend fun saveUseHefeiElectric(value: Boolean) = saveValue(USE_HEFEI_ELECTRIC, value)
    suspend fun saveLiquidGlass(value: Boolean) = saveValue(LIQUID_GLASS, value)
    suspend fun saveHefeiElectric(bean : HefeiElectricStorage)  = withContext(Dispatchers.IO) {
        with(bean) {
            launch { saveHefeiRoomNumber(roomNumber) }
            launch { saveHefeiBuildingNumber(buildingNumber) }
            launch { saveHefeiElectricName(name) }
        }
    }
    suspend fun emptyHefeiElectric() = withContext(Dispatchers.IO) {
        launch { saveHefeiRoomNumber(EMPTY_STRING) }
        launch { saveHefeiBuildingNumber(EMPTY_STRING) }
        launch { saveHefeiElectricName(EMPTY_STRING) }
    }


    val animationType = getFlow(ANIMATION_TYPE,AppAnimationManager.AnimationTypes.CenterAnimation.code)
    val stuCookies = getFlow(STU_COOKIE,EMPTY_STRING)
    val enablePureDark = getFlow(PURE_DARK,false)
    val colorMode = getFlow(COLOR_MODE,ColorMode.AUTO.code)
    val enableMotionBlur = getFlow(MOTION_BLUR,AppVersion.CAN_MOTION_BLUR)
    val enableHazeBlur = getFlow(HAZE_BLUR, HazeBlurLevel.MID.code)
    val transitionLevel = getFlow(TRANSITION, TransitionLevel.MEDIUM.code)
    val supabaseJwt = getFlow(SUPABASE_JWT,EMPTY_STRING)
    val supabaseRefreshToken = getFlow(SUPABASE_REFRESH_TOKEN,EMPTY_STRING)
    val enableSupabaseFilterEvent = getFlow(SUPABASE_FILTER_EVENT,true)
    val enableSupabaseAutoCheck = getFlow(SUPABASE_AUTO_CHECK,true)
    val enableShowFocusShower = getFlow(FOCUS_SHOW_SHOWER,true)
    val enableShowFocusWeatherWarn = getFlow(FOCUS_SHOW_WEATHER_WARN,false)
    val customCardPassword = getFlow(CARD_PASSWORD,EMPTY_STRING)
    val enableUseDefaultCardPassword = getFlow(USE_DEFAULT_CARD_PASSWORD,true)
    val defaultCalendarAccountId = getFlow(DEFAULT_CALENDAR_ACCOUNT,1)
    val courseTableTimeValue = getFlow(COURSE_TABLE_TIME,EMPTY_STRING)
    val courseTableTimeNextValue = getFlow(COURSE_TABLE_TIME_NEXT,EMPTY_STRING)
    val webVpnCookies = getFlow(WEBVPN_COOKIE,EMPTY_STRING)
    val enableAutoTerm = getFlow(AUTO_TERM,true)
    val showTodayCampusTip = getFlow(TODAY_CAMPUS_TIP,true)
    val enablePredictive = getFlow(PREDICTIVE,CAN_PREDICTIVE)
    val enableForceWebViewDark = getFlow(WEB_VIEW_DARK,true)
    val enableControlCenter = getFlow(CONTROL_CENTER,false)
    val courseBookJson = getFlow(COURSE_BOOK,EMPTY_STRING)
    val wxAuth = getFlow(WX_AUTH,EMPTY_STRING)
    val searchSort = getFlow(SEARCH_SORT, SEARCH_DEFAULT_STR)
    val customColor = getFlow(CUSTOM_COLOR,-1)
    val customBackground = getFlow(CUSTOM_BACKGROUND,EMPTY_STRING)
    val customBackgroundAlpha = getFlow(CUSTOM_BACKGROUND_ALPHA,1f)
    val customColorStyle = getFlow(CUSTOM_COLOR_STYLE, ColorStyle.DEFAULT.code)
    val customTermValue: Flow<Int> =  dataStore.data.map { it[AUTO_TERM_VALUE] ?: getSemseter() }
    val enableQuickStart = getFlow(FIRST_USE,prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false))
    val maxFlow = getFlow(MAX_FLOW, MyApplication.DEFAULT_MAX_FREE_FLOW)
    val showBottomBarLabel = getFlow(SHOW_BOTTOM_BAR_LABEL,true)
    val enableLiquidGlass = getFlow(LIQUID_GLASS, AppVersion.CAN_LIQUID_GLASS)
    val enableHideEmptyCalendarSquare = getFlow(HIDE_EMPTY_CALENDAR_SQUARE,false)
    val usuallyItems = getFlow(USUALLY_ITEMS,EMPTY_STRING)
    val hefeiElectricFee = getFlow(HEFEI_ELECTRIC_FEE,"0.0")
    val useHefeiElectric = getFlow(USE_HEFEI_ELECTRIC,getCampusRegion() == CampusRegion.HEFEI)
    private val hefeiBuildingNumber = getFlow(HEFEI_BUILDING_NUMBER,EMPTY_STRING)
    private val hefeiRoomNumber = getFlow(HEFEI_ROOM_NUMBER,EMPTY_STRING)
    private val hefeiElectric = getFlow(HEFEI_ELECTRIC,EMPTY_STRING)
    suspend fun getHefeiElectric(): HefeiElectricStorage? = withContext(Dispatchers.IO) {
        val hefeiBuildingNumber = hefeiBuildingNumber.first()
        val hefeiRoomNumber = hefeiRoomNumber.first()
        val hefeiElectric = hefeiElectric.first()
        if(hefeiRoomNumber == EMPTY_STRING || hefeiElectric == EMPTY_STRING || hefeiBuildingNumber == EMPTY_STRING) {
            return@withContext null
        }
        return@withContext HefeiElectricStorage(hefeiBuildingNumber,hefeiRoomNumber,hefeiElectric)
    }
}
