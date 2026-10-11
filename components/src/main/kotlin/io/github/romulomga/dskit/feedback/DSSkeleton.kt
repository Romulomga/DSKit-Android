package io.github.romulomga.dskit

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp

/**
 * Esqueleto de carregamento: enquanto [isLoading], o elemento vira um bloco na forma [shape] que pulsa
 * devagar, no lugar do conteúdo.
 *
 * Monte a tela com valores de exemplo e aplique em cada texto e imagem: o esqueleto fica com a forma
 * final e nada pula quando o conteúdo chega. Enquanto carrega, o elemento não recebe toque e sai do
 * TalkBack; anuncie o carregamento no contêiner da tela.
 *
 * Com "Remover animações", o bloco fica parado.
 */
@Composable
public fun Modifier.dsSkeleton(isLoading: Boolean, shape: Shape = DSTheme.shapes.extraSmall): Modifier {
    if (!isLoading) return this

    val color = skeletonColor()
    val reduceMotion = DSTheme.reduceMotion
    val pulse = if (reduceMotion) {
        1f
    } else {
        val transition = rememberInfiniteTransition(label = "dsSkeleton")
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = PulseMinAlpha,
            animationSpec = infiniteRepeatable(tween(PulseMillis), RepeatMode.Reverse),
            label = "dsSkeletonPulse"
        )
        alpha
    }

    return this
        .clearAndSetSemantics {}
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitPointerEvent().changes.forEach { it.consume() }
            }
        }
        .drawWithContent {
            drawOutline(shape.createOutline(size, layoutDirection, this), color = color, alpha = pulse)
        }
}

/**
 * Linha de esqueleto genérica: um círculo e duas linhas, o formato de uma linha de lista com avatar
 * (conversas, contatos) enquanto ela carrega. O TalkBack não a lê.
 */
@Composable
public fun DSSkeletonRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(SkeletonAvatar).dsSkeleton(true, CircleShape))

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            Spacer(Modifier.width(160.dp).height(14.dp).dsSkeleton(true))

            Spacer(Modifier.width(100.dp).height(12.dp).dsSkeleton(true))
        }
    }
}

/** `outlineVariant` aparece tanto sobre a tela quanto sobre um card, no claro e no escuro. */
@Composable
private fun skeletonColor(): Color = DSTheme.colors.outlineVariant

private const val PulseMinAlpha = 0.55f
private const val PulseMillis = 900
private val SkeletonAvatar = 44.dp

@DSPreviews
@Composable
private fun DSSkeletonPreview() {
    DSPreviewContainer("Skeleton") {
        DSSkeletonRow()

        DSSkeletonRow()

        DSCard {
            Text("Sample title of a post", style = DSTheme.typography.titleMedium, modifier = Modifier.dsSkeleton(true))

            Text("Sample line of the description", style = DSTheme.typography.bodyMedium, modifier = Modifier.dsSkeleton(true))
        }
    }
}
