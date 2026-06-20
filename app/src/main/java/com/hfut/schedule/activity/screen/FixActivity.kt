package com.hfut.schedule.activity.screen

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.ui.screen.fix.Fix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * 独立的修读Activity，不要和主App耦合！
 */
class FixActivity : BaseActivity() {
    @Composable
    override fun UI() = Fix(super.networkVm)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            super.networkVm.getMyApi()
        }
    }
}
