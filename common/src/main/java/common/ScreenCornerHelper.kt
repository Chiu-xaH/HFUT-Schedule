package com.xah.common

import android.os.Build
import android.view.RoundedCorner
import android.view.View
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ScreenCornerHelper(
    private val view: View
) {
    companion object {
        var corner : Dp = 0.dp
    }

    init {
        corner = getCornerDp()
    }

    fun getCornerDp(): Dp {
        val radiusPx = view.getScreenRoundCornerPx()
        return with(view.resources.displayMetrics) {
            (radiusPx / density).dp
        }
    }

    private fun View.getScreenRoundCornerPx(): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return 0
        }

        val insets = rootWindowInsets ?: return 0
        return insets
            .getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)
            ?.radius ?: 0
    }
}