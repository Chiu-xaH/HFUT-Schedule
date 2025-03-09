package com.hfut.schedule.ui.utils.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.round

@Composable
fun MenuChip(
    onClick: () -> Unit = {},
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    shape: Shape = AssistChipDefaults. shape,
    colors: ChipColors = AssistChipDefaults. assistChipColors(),
    interactionSource: MutableInteractionSource? = null,
    onOfferSet : (DpOffset) -> Unit,
) {
    val onOffsetUpdated by rememberUpdatedState(onOfferSet)
    var coordinates : LayoutCoordinates? by remember { mutableStateOf(null) }
    val density = LocalDensity.current

    AssistChip(
        onClick = {
            with(density) {
                coordinates?.let {
                    val windowOffset = it.localToWindow(Offset.Zero).round()
                    val dpOffset = DpOffset(windowOffset.x.toDp(), windowOffset.y.toDp())
                    onOffsetUpdated(dpOffset)
                }
            }

            onClick.invoke()
        },
        label = label,
        modifier = modifier.onGloballyPositioned { coordinates = it },
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource
    )
}