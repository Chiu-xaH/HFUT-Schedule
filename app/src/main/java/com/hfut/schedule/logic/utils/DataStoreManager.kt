package com.hfut.schedule.logic.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfut.schedule.App.MyApplication

class DataStoreManager {
    //构建Preferences DataStore
    private val Context.dataStore: DataStore<Preferences> by
    preferencesDataStore(name = "DataStore")//文件名称
    //创建 DataStore 对象
    val dataStore = MyApplication.context.dataStore

    companion object {
        val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
        val EXAMPLE_COUNTER_SET = stringSetPreferencesKey("example_counter_set")
    }

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