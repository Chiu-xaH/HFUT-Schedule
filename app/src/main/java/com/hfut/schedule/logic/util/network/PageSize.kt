package com.hfut.schedule.logic.util.network

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs

fun getPageSize() = prefs.getString("BookRequest", MyApplication.DEFAULT_PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.DEFAULT_PAGE_SIZE
