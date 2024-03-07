package com.hfut.schedule.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.datamodel.zjgd.ReturnCard

class UIViewModel : ViewModel()  {
    val findNewCourse = MutableLiveData<Boolean>()
    var CardValue = MutableLiveData<ReturnCard>()
}