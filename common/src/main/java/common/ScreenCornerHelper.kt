package com.xah.common

import android.os.Build
import android.view.RoundedCorner
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ScreenCornerHelper(
    private val view: View
) {
    companion object {
        var corner : Dp = 0.dp
        val CAN_GET = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    init {
        corner = getCornerDp()
    }

    fun getCornerDp(): Dp {
        return if(CAN_GET) {
            val radiusPx = view.getScreenRoundCornerPx()
            with(view.resources.displayMetrics) {
                (radiusPx / density).dp
            }
        } else {
            0.dp
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun View.getScreenRoundCornerPx(): Int {
        val insets = rootWindowInsets ?: return 0
        return insets
            .getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)
            ?.radius ?: 0
    }
}