package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.Activity.success.main.saved.NoNetWork
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedCoursesActivity : BaseActivity() {
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

