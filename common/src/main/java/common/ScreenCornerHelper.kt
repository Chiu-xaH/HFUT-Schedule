package com.xah.common

import android.os.Build
import android.view.RoundedCorner
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ScreenCornerHelper(
    private val view: View,
    private val defaultCorner : CornerBasedShape
) {
    companion object {
        var shape : CornerBasedShape = RoundedCornerShape(0.dp)
        val CAN_GET = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    init {
        shape = getCornerShape()
    }

    private fun getCornerDp(): Dp {
        return if(CAN_GET) {
            val radiusPx = view.getScreenRoundCornerPx()
            with(view.resources.displayMetrics) {
                (radiusPx / density).dp
            }
        } else {
            0.dp
        }
    }


    private fun getCornerShape() : CornerBasedShape {
        return if(CAN_GET) {
            RoundedCornerShape(getCornerDp())
        } else {
            defaultCorner
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