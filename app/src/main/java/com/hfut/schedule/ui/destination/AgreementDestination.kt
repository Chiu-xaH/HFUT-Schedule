package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.welcome.UseAgreementScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

object AgreementDestination : NavDestination() {
    override val key: String = "agreement"
    override val title: UiText = res(R.string.navigation_label_agreement)
    override val icon = R.drawable.partner_exchange

    @Composable
    override fun Content() {
        UseAgreementScreen()
    }
}