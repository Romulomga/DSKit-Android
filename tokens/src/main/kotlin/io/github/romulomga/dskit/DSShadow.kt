package io.github.romulomga.dskit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.dropShadow

/**
 * Sombras muito discretas, os mesmos presets do DSKit iOS (cor, raio de desfoque, deslocamento).
 * Use pouco: cards elevados e elementos flutuantes.
 */
@Immutable
public data class DSShadow(
    val color: Color,
    val radius: Dp,
    val x: Dp = 0.dp,
    val y: Dp = 0.dp,
) {
    public companion object {
        public val none: DSShadow = DSShadow(Color.Transparent, 0.dp)
        public val subtle: DSShadow = DSShadow(Color.Black.copy(alpha = 0.04f), radius = 6.dp, y = 2.dp)
        public val card: DSShadow = DSShadow(Color.Black.copy(alpha = 0.06f), radius = 10.dp, y = 4.dp)
        public val floating: DSShadow = DSShadow(Color.Black.copy(alpha = 0.10f), radius = 16.dp, y = 6.dp)
    }
}

/** Aplica um preset de `DSShadow` atrás da forma. */
public fun Modifier.dsShadow(shadow: DSShadow, shape: Shape = DSRadius.large): Modifier =
    if (shadow.radius == 0.dp) {
        this
    } else {
        dropShadow(
            shape = shape,
            shadow = Shadow(radius = shadow.radius, color = shadow.color, offset = DpOffset(shadow.x, shadow.y)),
        )
    }
