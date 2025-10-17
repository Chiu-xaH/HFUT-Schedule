package com.hfut.schedule.logic.util.storage.kv

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface IDataStore {
    suspend fun <T> saveValue(key: Preferences.Key<T>, value: T)
    fun <T> getFlow(key: Preferences.Key<T>, default: T): Flow<T>
    fun <T> getFlowSuspend(
        key: Preferences.Key<T>,
        defaultProvider: suspend () -> T
    ): Flow<T>
}