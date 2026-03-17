package com.fantonio.atra.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.unit.dp
import com.fantonio.atra.AppInfo
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun StandarizedAppIcon(app: AppInfo, modifier: Modifier = Modifier) {
    val onBackground = MaterialTheme.colorScheme.onBackground
    val isDark = MaterialTheme.colorScheme.background.run { (red + green + blue ) < 0.5f }

    val colorFilter = remember(app.isNativeMonochrome, onBackground, isDark) {
        if (app.isNativeMonochrome) {
            ColorFilter.tint(onBackground)
        } else {
            val matrix = ColorMatrix().apply {
                setToSaturation(0f)
                if (!isDark) {
                    val invertMatrix = ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    timesAssign(invertMatrix)
                }
            }
            ColorFilter.colorMatrix(matrix)
        }
    }
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        val iconModifier = remember(app.isNativeMonochrome, app.isAdaptive) {
            if (app.isNativeMonochrome) {
                Modifier
                    .fillMaxSize()
                    .padding(if (app.isAdaptive) 0.dp else 8.dp)
            } else {
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
            }
        }

        Image (
            painter = rememberDrawablePainter(drawable = app.icon),
            contentDescription = null,
            modifier = iconModifier,
            colorFilter = colorFilter
        )
    }
}