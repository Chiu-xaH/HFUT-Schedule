package com.hfut.schedule.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ViewModel.FWDTViewMoodel
import com.hfut.schedule.ui.ComposeUI.TransparentSystemBars
import com.hfut.schedule.ui.ComposeUI.FWDT.FWDTLoginUI
import com.hfut.schedule.ui.ComposeUI.FWDT.FWDTNotLoginUI
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.theme.MonetColor

class FWDTActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val vm by lazy { ViewModelProvider(this).get(FWDTViewMoodel::class.java) }
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            SettingsProvider {
                val stickerUuid = LocalCurrentStickerUuid.current
                LaunchedEffect(stickerUuid) {
                    viewModel.sendUiIntent(MainIntent.UpdateThemeColor(stickerUuid))
                }
                CompositionLocalProvider {
                    MonetColor {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            TransparentSystemBars()
                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            titleContentColor = MaterialTheme.colorScheme.primary,
                                        ),
                                        title = { Text("服务大厅") },
                                        )
                                }
                            ) { innerPadding ->
                                Column(modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()) {

                                    val boolean = intent.getStringExtra("boolean")
                                    when (boolean) {
                                        "1" -> FWDTLoginUI(vm)
                                        "0" -> FWDTNotLoginUI(vm)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
