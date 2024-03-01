package com.hfut.schedule.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UIViewModel : ViewModel()  {
    val findNewCourse = MutableLiveData<Boolean>()
}