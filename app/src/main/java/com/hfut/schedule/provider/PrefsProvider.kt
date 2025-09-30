package com.hfut.schedule.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.hfut.schedule.logic.util.storage.SharedPrefs.PREFS
import org.json.JSONObject

class PrefsProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val prefs = context?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val allPrefs = prefs?.all ?: emptyMap()

        // 过滤 null，避免 JSONObject.NULL
        val safePrefs = allPrefs.mapValues { it.value ?: "" }
        val json = JSONObject(safePrefs).toString()
        Log.d("结果发送JSON", json)

        val cursor = MatrixCursor(arrayOf("prefs"))
        cursor.addRow(arrayOf<Any>(json))
        return cursor
    }


    override fun getType(uri: Uri): String? = "vnd.android.cursor.item/prefs"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}
