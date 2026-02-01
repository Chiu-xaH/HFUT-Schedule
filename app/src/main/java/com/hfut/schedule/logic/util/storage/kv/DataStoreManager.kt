package com.hfut.schedule.logic.util.storage.kv

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.Language
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.cube.sub.getJxglstuDefaultPassword
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getDefaultStartTerm
import com.hfut.schedule.ui.util.color.ColorMode
import com.hfut.schedule.ui.util.color.ColorStyle
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res
import com.xah.uicommon.util.language.text
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.xah.transition.style.TransitionLevel
import com.xah.uicommon.util.language.BaseChoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object DataStoreManager : IDataStore {

    /* 用法
    val XXX by DataStoreManager.XXX.collectAsState(initial = 默认值)
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val dataStore = MyApplication.Companion.context.dataStore

    val SEARCH_DEFAULT_STR = GlobalUIStateHolder.funcDefault.map { it.id }.joinToString(",")

    private const val EMPTY_STRING = ""

    override suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }
    override fun <T> getFlow(key: Preferences.Key<T>, default: T): Flow<T> = dataStore.data.map { it[key] ?: default }
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun <T> getFlowSuspend(
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

    enum class ShowTeacherConfig(override val code : Int,override val label: UiText) : BaseChoice {
        ONLY_MULTI(0,res(R.string.appearance_settings_choice_display_teachers_only_multi)),
        ALL(1,res(R.string.appearance_settings_choice_display_teachers_all)),
        NONE(2, res(R.string.appearance_settings_choice_display_teachers_none))
    }


    private val ANIMATION_TYPE = intPreferencesKey("animation_types")
    private val PURE_DARK = booleanPreferencesKey("pure_dark")
    private val COLOR_MODE = intPreferencesKey("color_mode")
    private val MOTION_BLUR = booleanPreferencesKey("motion_blur_2")
    private val HAZE_BLUR = booleanPreferencesKey("haze_blur_4")
    private val TRANSITION = intPreferencesKey("transitions_2")
    private val SUPABASE_JWT = stringPreferencesKey("supabase_jwt")
    private val SUPABASE_REFRESH_TOKEN = stringPreferencesKey("supabase_refresh_token")
    private val SUPABASE_FILTER_EVENT = booleanPreferencesKey("supabase_filter_event")
    private val SUPABASE_AUTO_CHECK = booleanPreferencesKey("supabase_auto_check")
    private val FOCUS_SHOW_SHOWER = booleanPreferencesKey("focus_show_shower")
    private val FOCUS_SHOW_WEATHER_WARN = booleanPreferencesKey("focus_show_weather_warn")
    private val CARD_PASSWORD = stringPreferencesKey("card_password")
    private val USE_DEFAULT_CARD_PASSWORD = booleanPreferencesKey("use_default_card_password")
    private val USE_DEFAULT_JXGLSTU_PASSWORD = booleanPreferencesKey("use_default_jxglstu_password")
    private val DEFAULT_CALENDAR_ACCOUNT = longPreferencesKey("default_calendar_account")
    private val COURSE_TABLE_TIME = stringPreferencesKey("course_table_time")
    private val WEBVPN_COOKIE = stringPreferencesKey("webvpn_cookie")
    private val AUTO_TERM = booleanPreferencesKey("auto_term")
    private val AUTO_TERM_VALUE = intPreferencesKey("auto_term_value")
    private val COURSE_BOOK = stringPreferencesKey("course_book")
    private val WEB_VIEW_DARK = booleanPreferencesKey("web_view_dark")
    private val PREDICTIVE = booleanPreferencesKey("predictive")
    private val CONTROL_CENTER = booleanPreferencesKey("control_center")
    private val WX_AUTH = stringPreferencesKey("wx_auth")
    private val CUSTOM_COLOR = longPreferencesKey("custom_color")
    private val CUSTOM_BACKGROUND = stringPreferencesKey("custom_background")
    private val CUSTOM_COLOR_STYLE = intPreferencesKey("custom_color_style_2")
    private val CUSTOM_CALENDAR_SQUARE_ALPHA = floatPreferencesKey("custom_calendar_square_alpha")
    private val SEARCH_SORT = stringPreferencesKey("search_sort")
    private val MAX_FLOW = intPreferencesKey("max_flow")
    private val SHOW_BOTTOM_BAR_LABEL = booleanPreferencesKey("show_bottom_bar_label")
    private val HEFEI_ROOM_NUMBER = stringPreferencesKey("hefei_room_number")
    private val HEFEI_BUILDING_NUMBER = stringPreferencesKey("hefei_building_number")
    private val HEFEI_ELECTRIC = stringPreferencesKey("hefei_electric")
    private val HEFEI_ELECTRIC_FEE = stringPreferencesKey("hefei_electric_fee")
    private val USE_HEFEI_ELECTRIC = booleanPreferencesKey("use_hefei_electric")
    private val LIQUID_GLASS = booleanPreferencesKey("liquid_glass")
    private val CAMERA_DYNAMIC_RECORD = booleanPreferencesKey("camera_dynamic_record_2")
    private val SHOW_OUT_OF_DATE_EVENT = booleanPreferencesKey("show_out_of_date_event")
    private val CALENDAR_SHOW_TEACHER = intPreferencesKey("calendar_show_teacher_2")
    private val CALENDAR_SQUARE_HEIGHT = floatPreferencesKey("calendar_square_height")
    private val CALENDAR_SQUARE_HEIGHT_NEW = floatPreferencesKey("calendar_square_height_new")
    private val MERGE_SQUARE = booleanPreferencesKey("merge_square")
    private val CALENDAR_SQUARE_TEXT_SIZE = floatPreferencesKey("calendar_square_test_size")
    private val CALENDAR_SQUARE_TEXT_PADDING = floatPreferencesKey("calendar_square_test_padding_2")
    private val FOCUS_WIDGET_TEXT_SIZE = floatPreferencesKey("focus_widget_test_size")
    private val XWX_PASSWORD = stringPreferencesKey("xwx_password")
    private val JXGLSTU_PASSWORD = stringPreferencesKey("jxglstu_password")
    private val UNI_APP_JWT = stringPreferencesKey("uni_app_jwt")
    private val TERM_START_DATE = stringPreferencesKey("term_start_date")
    private val DEFAULT_CALENDAR = intPreferencesKey("default_calendar")
    private val READ_NOTIFICATIONS = stringPreferencesKey("read_notifications")
    private val LANGUAGE = intPreferencesKey("language")

    suspend fun saveAnimationType(value: Int) = saveValue(ANIMATION_TYPE,value)
    suspend fun savePureDark(value: Boolean) = saveValue(PURE_DARK,value)
    suspend fun saveColorMode(mode: ColorMode) = saveValue(COLOR_MODE,mode.code)
    suspend fun saveMotionBlur(value: Boolean) = saveValue(MOTION_BLUR,value)
    suspend fun saveHazeBlur(value: Boolean) = saveValue(HAZE_BLUR, value)
    suspend fun saveTransition(value: TransitionLevel) = saveValue(TRANSITION,value.code)
    suspend fun saveSupabaseJwt(value: String) = saveValue(SUPABASE_JWT,value)
    suspend fun saveSupabaseRefreshToken(value: String) = saveValue(SUPABASE_REFRESH_TOKEN,value)
    suspend fun saveSupabaseFilterEvent(value: Boolean) = saveValue(SUPABASE_FILTER_EVENT,value)
    suspend fun saveSupabaseAutoCheck(value: Boolean) = saveValue(SUPABASE_AUTO_CHECK,value)
    suspend fun saveFocusShowWeatherWarn(value: Boolean) = saveValue(FOCUS_SHOW_WEATHER_WARN,value)
    suspend fun saveCardPassword(value: String) = saveValue(CARD_PASSWORD,value)
    suspend fun saveUseDefaultCardPassword(value: Boolean) = saveValue(USE_DEFAULT_CARD_PASSWORD,value)
    suspend fun saveUseDefaultJxglstuPassword(value: Boolean) = saveValue(USE_DEFAULT_JXGLSTU_PASSWORD,value)
    suspend fun saveDefaultCalendarAccount(value: Long) = saveValue(DEFAULT_CALENDAR_ACCOUNT,value)
    suspend fun saveCourseTable(value: String) = saveValue(COURSE_TABLE_TIME,value)
    suspend fun saveWebVpnCookie(value: String) = saveValue(WEBVPN_COOKIE,value)
    suspend fun saveAutoTerm(value: Boolean) = saveValue(AUTO_TERM,value)
    suspend fun saveAutoTermValue(value: Int) = saveValue(AUTO_TERM_VALUE,value)
    suspend fun saveCourseBook(value: String) = saveValue(COURSE_BOOK,value)
    suspend fun saveCustomColorStyle(value: ColorStyle) = saveValue(CUSTOM_COLOR_STYLE,value.code)
    suspend fun saveWebViewDark(value: Boolean) = saveValue(WEB_VIEW_DARK,value)
    suspend fun savePredict(value: Boolean) = saveValue(PREDICTIVE,value)
    suspend fun saveControlCenter(value: Boolean) = saveValue(CONTROL_CENTER,value)
    suspend fun saveWxAuth(value: String) = saveValue(WX_AUTH, "Bearer $value")
    suspend fun saveCustomColor(value: Long) = saveValue(CUSTOM_COLOR, value)
    suspend fun saveCustomBackground(value: String?) = saveValue(CUSTOM_BACKGROUND, value ?: EMPTY_STRING)
    suspend fun saveCustomSquareAlpha(value: Float) = saveValue(CUSTOM_CALENDAR_SQUARE_ALPHA,value)
    suspend fun saveSearchSort(value: List<Int>) = saveValue(SEARCH_SORT, value.joinToString(","))
    suspend fun saveReadNotifications(value: List<Int>) = saveValue(READ_NOTIFICATIONS, value.joinToString(","))
    suspend fun saveMaxFlow(value: Int) = saveValue(MAX_FLOW, value)
    suspend fun saveShowBottomBarLabel(value: Boolean) = saveValue(SHOW_BOTTOM_BAR_LABEL,value)
    private suspend fun saveHefeiBuildingNumber(value: String) = saveValue(HEFEI_BUILDING_NUMBER, value)
    private suspend fun saveHefeiElectricName(value: String) = saveValue(HEFEI_ELECTRIC, value)
    private suspend fun saveHefeiRoomNumber(value: String) = saveValue(HEFEI_ROOM_NUMBER, value)
    suspend fun saveHefeiElectricFee(value: String) = saveValue(HEFEI_ELECTRIC_FEE, value)
    suspend fun saveUseHefeiElectric(value: Boolean) = saveValue(USE_HEFEI_ELECTRIC, value)
    suspend fun saveLiquidGlass(value: Boolean) = saveValue(LIQUID_GLASS, value)
    suspend fun saveCalendarShowTeacher(value: ShowTeacherConfig) = saveValue(CALENDAR_SHOW_TEACHER, value.code)
    suspend fun saveCameraDynamicRecord(value: Boolean) = saveValue(CAMERA_DYNAMIC_RECORD, value)
    suspend fun saveShowOutOdDateEvent(value: Boolean) = saveValue(SHOW_OUT_OF_DATE_EVENT, value)
    suspend fun saveCalendarSquareHeight(value: Float) = saveValue(CALENDAR_SQUARE_HEIGHT, value)
    suspend fun saveCalendarSquareHeightNew(value: Float) = saveValue(CALENDAR_SQUARE_HEIGHT_NEW, value)
    suspend fun saveCalendarSquareTextSize(value: Float) = saveValue(CALENDAR_SQUARE_TEXT_SIZE, value)
    suspend fun saveCalendarSquareTextPadding(value: Float) = saveValue(CALENDAR_SQUARE_TEXT_PADDING, value)
    suspend fun saveFocusWidgetTextSize(value: Float) = saveValue(FOCUS_WIDGET_TEXT_SIZE, value)
    suspend fun saveDefaultCalendar(value: CourseType) = saveValue(DEFAULT_CALENDAR, value.code)
    suspend fun saveLanguage(value: Language) = saveValue(LANGUAGE, value.code)
    suspend fun saveHefeiElectric(bean : HefeiElectricStorage)  = withContext(Dispatchers.IO) {
        with(bean) {
            launch { saveHefeiRoomNumber(roomNumber) }
            launch { saveHefeiBuildingNumber(buildingNumber) }
            launch { saveHefeiElectricName(name) }
        }
    }
    suspend fun saveMergeSquare(value: Boolean) = saveValue(MERGE_SQUARE,value)
    suspend fun saveXwxPassword(value: String) = saveValue(XWX_PASSWORD,value)
    suspend fun saveJxglstuPassword(value: String) = saveValue(JXGLSTU_PASSWORD,value)
    suspend fun saveTermStartDate(value: String)  {
        saveValue(TERM_START_DATE, value)
        DateTimeManager.initCurrentWeekValue()
    }
    suspend fun saveUniAppJwt(value: String) = saveValue(UNI_APP_JWT,value)


    val animationType = getFlow(ANIMATION_TYPE, AppAnimationManager.AnimationTypes.CenterAnimation.code)
    val enablePureDark = getFlow(PURE_DARK,false)
    val colorMode = getFlow(COLOR_MODE,ColorMode.AUTO.code)
    val enableMotionBlur = getFlow(MOTION_BLUR, AppVersion.CAN_MOTION_BLUR)
    val enableHazeBlur = getFlow(HAZE_BLUR, true)
    val transitionLevel = getFlow(TRANSITION, TransitionLevel.MEDIUM.code)
    val supabaseJwt = getFlow(SUPABASE_JWT,EMPTY_STRING)
    val supabaseRefreshToken = getFlow(SUPABASE_REFRESH_TOKEN,EMPTY_STRING)
    val enableSupabaseFilterEvent = getFlow(SUPABASE_FILTER_EVENT,false)
    val enableSupabaseAutoCheck = getFlow(SUPABASE_AUTO_CHECK,true)
    val enableShowFocusShower = getFlow(FOCUS_SHOW_SHOWER,true)
    val enableShowFocusWeatherWarn = getFlow(FOCUS_SHOW_WEATHER_WARN,false)
    val customCardPassword = getFlow(CARD_PASSWORD,EMPTY_STRING)
    val enableUseDefaultCardPassword = getFlow(USE_DEFAULT_CARD_PASSWORD,true)
    val enableUseDefaultJxglstuPassword = getFlow(USE_DEFAULT_JXGLSTU_PASSWORD,true)
    val defaultCalendarAccountId = getFlow(DEFAULT_CALENDAR_ACCOUNT,1)
    val webVpnCookies = getFlow(WEBVPN_COOKIE,EMPTY_STRING)
    val enableAutoTerm = getFlow(AUTO_TERM,true)
    val enablePredictive = getFlow(PREDICTIVE, AppVersion.CAN_PREDICTIVE)
    val enableForceWebViewDark = getFlow(WEB_VIEW_DARK,true)
    val enableControlCenterGesture = getFlow(CONTROL_CENTER,false)
    val courseBookJson = getFlow(COURSE_BOOK,EMPTY_STRING)
    val wxAuth = getFlow(WX_AUTH,EMPTY_STRING)
    val searchSort = getFlow(SEARCH_SORT, SEARCH_DEFAULT_STR)
    val readNotifications = getFlow(READ_NOTIFICATIONS, EMPTY_STRING)
    val customColor = getFlow(CUSTOM_COLOR,-1)
    val customBackground = getFlow(CUSTOM_BACKGROUND,EMPTY_STRING)
    val customCalendarSquareAlpha = getFlow(CUSTOM_CALENDAR_SQUARE_ALPHA,MyApplication.CALENDAR_SQUARE_ALPHA)
    val customColorStyle = getFlow(CUSTOM_COLOR_STYLE, ColorStyle.DEFAULT.code)
    val customTermValue: Flow<Int> =  dataStore.data.map { it[AUTO_TERM_VALUE] ?: SemesterParser.getSemester() }
    val maxFlow = getFlow(MAX_FLOW, MyApplication.Companion.DEFAULT_MAX_FREE_FLOW)
    val showBottomBarLabel = getFlow(SHOW_BOTTOM_BAR_LABEL,true)
    val enableCameraDynamicRecord = getFlow(CAMERA_DYNAMIC_RECORD,false)
    val enableCalendarShowTeacher = getFlow(CALENDAR_SHOW_TEACHER,ShowTeacherConfig.ONLY_MULTI.code)
    val enableLiquidGlass = getFlow(LIQUID_GLASS, AppVersion.CAN_SHADER)
    val hefeiElectricFee = getFlow(HEFEI_ELECTRIC_FEE,"0.0")
    val useHefeiElectric = getFlow(USE_HEFEI_ELECTRIC, getCampusRegion() == CampusRegion.HEFEI)
    val enableShowOutOfDateEvent = getFlow(SHOW_OUT_OF_DATE_EVENT, false)
    val calendarSquareHeight = getFlow(CALENDAR_SQUARE_HEIGHT, MyApplication.CALENDAR_SQUARE_HEIGHT)
    val calendarSquareHeightNew = getFlow(CALENDAR_SQUARE_HEIGHT_NEW, MyApplication.CALENDAR_SQUARE_HEIGHT_NEW)
    val calendarSquareTextSize = getFlow(CALENDAR_SQUARE_TEXT_SIZE, 1f)
    val focusWidgetTextSize = getFlow(FOCUS_WIDGET_TEXT_SIZE, 1f)
    val language = getFlow(LANGUAGE, Language.AUTO.code)
    val calendarSquareTextPadding = getFlow(CALENDAR_SQUARE_TEXT_PADDING, MyApplication.CALENDAR_SQUARE_TEXT_PADDING)
    val xwxPassword = getFlow(XWX_PASSWORD, EMPTY_STRING)
    val jxglstuPassword = getFlow(JXGLSTU_PASSWORD, getJxglstuDefaultPassword() ?: EMPTY_STRING)
    val uniAppJwt = getFlow(UNI_APP_JWT,  EMPTY_STRING)
    val defaultCalendar = getFlow(DEFAULT_CALENDAR, CourseType.JXGLSTU.code)
    private val hefeiBuildingNumber = getFlow(HEFEI_BUILDING_NUMBER,EMPTY_STRING)
    private val hefeiRoomNumber = getFlow(HEFEI_ROOM_NUMBER,EMPTY_STRING)
    private val hefeiElectric = getFlow(HEFEI_ELECTRIC,EMPTY_STRING)
    val termStartDate = getFlow(TERM_START_DATE, getDefaultStartTerm())
    suspend fun getHefeiElectric(): HefeiElectricStorage? = withContext(Dispatchers.IO) {
        val hefeiBuildingNumber = hefeiBuildingNumber.first()
        val hefeiRoomNumber = hefeiRoomNumber.first()
        val hefeiElectric = hefeiElectric.first()
        if (hefeiRoomNumber == EMPTY_STRING || hefeiElectric == EMPTY_STRING || hefeiBuildingNumber == EMPTY_STRING) {
            return@withContext null
        }
        return@withContext HefeiElectricStorage(hefeiBuildingNumber, hefeiRoomNumber, hefeiElectric)
    }
    val enableMergeSquare = getFlow(MERGE_SQUARE,false)

    fun getSyncDefaultCalendar(): Int? {
        return runBlocking {
            val preferences = dataStore.data.first() // 获取第一次的值
            preferences[DEFAULT_CALENDAR] // 返回你需要的值
        }
    }
}