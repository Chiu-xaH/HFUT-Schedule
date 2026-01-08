package com.hfut.schedule.ui.component.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp


fun createIconFromString(
    pathData: String,
    sizeDp: Float = 24f,
    viewportSize: Float = 24f,
    color: Color = Color.Black
): ImageVector {
    return ImageVector.Builder(
        defaultWidth = sizeDp.dp,
        defaultHeight = sizeDp.dp,
        viewportWidth = viewportSize,
        viewportHeight = viewportSize
    ).addPath(
        pathData = PathParser().parsePathString(pathData).toNodes(),
        fill = SolidColor(color),
        pathFillType = PathFillType.NonZero
    ).build()
}