package com.hfut.schedule.activity.screen

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hfut.schedule.R
import com.hfut.schedule.activity.screen.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.StartAppIconButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.sub.FocusWidgetSettingsUI
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.theme.AppTheme
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

class WidgetConfigureActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
                val hazeState = rememberHazeState(blurEnabled = blur)
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        MediumTopAppBar(
                            scrollBehavior = scrollBehavior,
                            modifier = Modifier.topBarBlur(hazeState, MaterialTheme.colorScheme.surfaceContainer),
                            colors = topBarTransplantColor(),
                            title = { Text("聚焦课程组件-配置") },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        this.finish()
                                    }
                                ) {
                                    Icon(painterResource(R.drawable.close),null, tint = MaterialTheme.colorScheme.primary)
                                }
                            },
                        )
                    }
                ) { innerPadding ->
                    FocusWidgetSettingsUI(
                        innerPadding,
                        modifier = Modifier.hazeSource(hazeState)
                    )
                }
            }
        }
    }
}

