package io.github.romulomga.dskit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Leve redução de escala ao pressionar (0,97), o `DSPressableButtonStyle` do DSKit iOS. Desligada com
 * "Remover animações". Combine com o ripple, não no lugar dele.
 */
@Composable
public fun Modifier.dsPressScale(interactionSource: InteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val reduceMotion = DSTheme.reduceMotion
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduceMotion) 0.97f else 1f,
        animationSpec = DSMotion.snappy(),
        label = "dsPressScale",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
