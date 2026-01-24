package com.hfut.schedule.logic.util.sys

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Language
import com.xah.uicommon.util.LogUtil

object LanguageHelper {
    private fun getCurrentAppLanguageTag(context: Context): String? {
        val config = context.resources.configuration
        val currentLanguage: String? = config.locale.language
        LogUtil.debug(currentLanguage ?: "null")
        return currentLanguage
    }

    // 繁体中文也是zh
    fun isChineseLanguage(context: Context = MyApplication.context): Boolean  = getCurrentAppLanguageTag(context) == "zh"
    fun isEnglishLanguage(context: Context = MyApplication.context): Boolean  = getCurrentAppLanguageTag(context) == "en"

    private fun setAppLanguage(languageTag: String) {
        val locales = if (languageTag.isBlank()) {
            // 跟随系统
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun changeAppLanguage(language: Language) {
        LogUtil.debug("change to ${language.label}")
        when(language) {
            Language.ENGLISH -> {
                setAppLanguage("en")        // 英文
            }
            Language.AUTO -> {
                setAppLanguage("")          // 跟随系统
            }
            Language.CHINESE_SIMPLY -> {
                setAppLanguage("zh-CN")     // 简体中文
            }
        }
    }

    fun changeAppLanguage(code: Int) {
        val lng = Language.entries.find { it.code == code }
        lng?.let { changeAppLanguage(it) }
    }
}