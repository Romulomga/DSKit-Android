package io.github.romulomga.dskit

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

    return this
        .clearAndSetSemantics {}
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitPointerEvent().changes.forEach { it.consume() }
            }
        }
        .then(SkeletonElement(skeletonColor(), shape, pulse = !DSTheme.reduceMotion))
}

/**
 * O bloco do esqueleto, desenhado por um `Modifier.Node`: o pulso só invalida o desenho, nunca a composição
 * de quem usa. A opacidade sai do relógio de quadros, então todos os blocos da tela pulsam juntos sem uma
 * animação por elemento.
 */
private data class SkeletonElement(val color: Color, val shape: Shape, val pulse: Boolean) : ModifierNodeElement<SkeletonNode>() {
    override fun create(): SkeletonNode = SkeletonNode(color, shape, pulse)

    override fun update(node: SkeletonNode) {
        node.color = color
        node.shape = shape
        node.updatePulse(pulse)
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "dsSkeleton"
        properties["shape"] = shape
    }
}

private class SkeletonNode(var color: Color, var shape: Shape, private var pulse: Boolean) :
    Modifier.Node(),
    DrawModifierNode {
    private var alpha = 1f
    private var job: Job? = null

    override fun onAttach() {
        if (pulse) start()
    }

    override fun onDetach() {
        job = null
    }

    fun updatePulse(pulse: Boolean) {
        if (this.pulse == pulse) return

        this.pulse = pulse
        if (pulse && isAttached) {
            start()
        } else {
            job?.cancel()
            job = null
            alpha = 1f
        }
    }

    private fun start() {
        job?.cancel()
        job = coroutineScope.launch {
            while (true) {
                withFrameMillis { frameMillis ->
                    alpha = pulseAlpha(frameMillis)
                    invalidateDraw()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawOutline(shape.createOutline(size, layoutDirection, this), color = color, alpha = alpha)
    }
}

/** Vai de 1 a [PulseMinAlpha] e volta em `2 × PulseMillis`, com a curva padrão do Material nos dois sentidos. */
private fun pulseAlpha(frameMillis: Long): Float {
    val position = (frameMillis % (PulseMillis * 2)).toFloat() / PulseMillis
    val fraction = if (position <= 1f) position else 2f - position

    return 1f - (1f - PulseMinAlpha) * FastOutSlowInEasing.transform(fraction)
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
