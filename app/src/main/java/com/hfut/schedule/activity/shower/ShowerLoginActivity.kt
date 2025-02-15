package com.hfut.schedule.activity.shower

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.shower.login.ShowerLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowerLoginActivity : BaseActivity() {
    @Composable
    override fun UI() {
        ShowerLogin(super.showerVm,super.networkVm)
    }
}

