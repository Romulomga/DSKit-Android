package io.github.romulomga.dskit

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Barra de progresso fina na cor primária do tema, para indicador de passos de onboarding e contextos
 * parecidos onde o `LinearProgressIndicator` do Material fica grosso demais.
 *
 * `value` é limitado a `0..1`. O preenchimento anima quando o valor muda; passe `animationSpec = null`
 * para saltar direto. Com "Remover animações", sempre salta.
 */
@Composable
public fun DSProgressBar(
    value: Float,
    modifier: Modifier = Modifier,
    height: Dp = 4.dp,
    animationSpec: AnimationSpec<Float>? = DSMotion.spring()
) {
    val clamped = value.coerceIn(0f, 1f)
    val reduceMotion = DSTheme.reduceMotion
    val primary = DSTheme.primary

    val fraction by animateFloatAsState(
        targetValue = clamped,
        animationSpec = if (reduceMotion || animationSpec == null) snap() else animationSpec,
        label = "DSProgressBar"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(DSRadius.capsule)
            .background(primary.copy(alpha = 0.16f))
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(clamped, 0f..1f)
                stateDescription = "${(value * 100).toInt()}%"
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction)
                .clip(DSRadius.capsule)
                .background(primary)
        )
    }
}

@DSPreviews
@Composable
private fun DSProgressBarPreview() {
    DSPreviewContainer("Progress bar") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSProgressBar(value = 3f / 8f)

            Text("Passo 3 de 8", style = DSTypography.footnote, color = DSTheme.colors.onSurfaceMedium)
        }
    }
}
