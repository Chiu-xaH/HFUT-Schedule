package com.hfut.schedule.logic.utils.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData

fun <T> reEmptyLiveDta(liveData: MutableLiveData<T?>) {
    Handler(Looper.getMainLooper()).post { liveData.value = null }
}