package com.hfut.schedule.ui.MonetColor

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.theme.extractTonalPalettesFromWallpaper
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import java.lang.Long


@Composable
fun MonetUI() {
    val tonalPalettesFromWallpaper = extractTonalPalettesFromWallpaper()
    Palettes(palettes = tonalPalettesFromWallpaper)
}

@Composable
fun Palettes(
    palettes: Map<String, TonalPalettes>,
    themeName: String = LocalThemeName.current,
) {
    val customPrimaryColor = LocalCustomPrimaryColor.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tonalPalettes = (customPrimaryColor.toColorOrNull() ?: Color.Transparent).toTonalPalettes()
    var addDialogVisible by rememberSaveable { mutableStateOf(false) }
    var customColorValue by rememberSaveable { mutableStateOf(customPrimaryColor) }

    if (palettes.isEmpty()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(74.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {},
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
                    "无调色板"
                else "8.1 +",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
        }
    } else {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            palettes.forEach { (t, u) ->
                val isCustom = t == ThemeNamePreference.CUSTOM_THEME_NAME
                SelectableMiniPalette(
                    selected = t == themeName,
                    isCustom = isCustom,
                    onClick = {
                        if (isCustom) {
                            customColorValue = customPrimaryColor
                            addDialogVisible = true
                        } else {
                            ThemeNamePreference.put(context, scope, t)
                        }
                    },
                    palette = if (isCustom) tonalPalettes else u
                )
            }
        }
    }

}

@Composable
fun SelectableMiniPalette(
    modifier: Modifier = Modifier,
    selected: Boolean,
    isCustom: Boolean = false,
    onClick: () -> Unit,
    palette: TonalPalettes,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isCustom) {
            MaterialTheme.colorScheme.primaryContainer
            //  .copy(0.5f) onDark MaterialTheme.colorScheme.onPrimaryContainer.copy(0.3f)
        } else {
            MaterialTheme.colorScheme.inverseOnSurface
        },
    ) {
        Surface(
            modifier = Modifier
                .clickable { onClick() }
                .padding(12.dp)
                .size(50.dp),
            shape = CircleShape,
            color = palette accent1 90.0,
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .offset((-25).dp, 25.dp),
                    color = palette accent3 90.0,
                ) {}
                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(25.dp, 25.dp),
                    color = palette accent2 60.0,
                ) {}
                val animationSpec = spring<Float>(stiffness = Spring.StiffnessMedium)
                AnimatedVisibility(
                    visible = selected,
                    enter = scaleIn(animationSpec) + fadeIn(animationSpec),
                    exit = scaleOut(animationSpec) + fadeOut(animationSpec),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Checked",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}

