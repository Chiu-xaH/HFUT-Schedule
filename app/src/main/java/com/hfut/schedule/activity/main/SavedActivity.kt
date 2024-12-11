package com.hfut.schedule.activity.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.home.main.saved.NoNetWork
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        NoNetWork(super.networkVm,super.loginVm,super.uiVm)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

