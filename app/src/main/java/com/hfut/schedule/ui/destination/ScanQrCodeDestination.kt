package com.hfut.schedule.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.scan.ScanScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object ScanQrCodeDestination : NavDestination() {
    override val key = "scan_qr_code"
    override val title = res(R.string.navigation_label_scan_qr_code)
    override val icon = R.drawable.qr_code_scanner_shortcut
    override val PlaceHolder = @Composable {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Icon(
                painterResource(R.drawable.monochrome_photos),
                null,
                modifier = Modifier.size(100.dp).align(Alignment.Center),
                tint = Color.White
            )
        }
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ScanScreen(vm)
    }
}