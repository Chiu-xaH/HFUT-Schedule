package com.hfut.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginSuccessViewModelFactory(private val webVpn: Boolean) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NetWorkViewModel::class.java)) {
            return NetWorkViewModel(webVpn) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
