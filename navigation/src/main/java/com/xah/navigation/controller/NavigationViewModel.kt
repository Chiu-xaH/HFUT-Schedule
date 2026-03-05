package com.xah.navigation.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xah.navigation.model.StackEntry

class NavigationViewModel() : ViewModel() {
    val stack = mutableStateListOf<StackEntry>()

    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NavigationViewModel() as T
        }
    }
}