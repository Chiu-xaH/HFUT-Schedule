package com.hfut.schedule.activity.funiction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.ui.activity.nologin.NoLoginUI

class AnonymityActivity : BaseActivity() {
    @Composable
    override fun UI() {
        NoLoginUI(super.networkVm,super.loginVm,super.uiVm)
    }
}

