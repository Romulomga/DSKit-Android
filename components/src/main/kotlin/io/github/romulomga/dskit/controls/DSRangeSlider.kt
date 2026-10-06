package io.github.romulomga.dskit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.romulomga.dskit.components.R
import kotlin.math.roundToInt

/** Qual alça de um `DSRangeSlider` o usuário moveu. */
public enum class DSRangeSliderThumb {
    Lower,
    Upper
}

/**
 * Slider de duas alças sobre passos discretos `0..stepCount`. Quem chama decide o que cada passo significa
 * (uma tabela de preços, de áreas) e mostra isso em `valueText`.
 *
 * Durante o arraste a alça segue o dedo e vibra a cada passo que cruza; ao soltar, encaixa no passo mais
 * próximo. As alças nunca se sobrepõem e o intervalo ainda desce a `minimumDistance` passos: cada alça anda
 * na própria pista, deslocadas de uma largura de alça (a da esquerda em `0..largura - alça`, a da direita em
 * `alça..largura`), então no mesmo passo elas ficam lado a lado. Para o TalkBack cada alça é um controle ajustável (deslize para cima ou
 * para baixo).
 *
 * O `RangeSlider` do Material não serve aqui: não impõe distância mínima entre as alças nem tem o visual do
 * DSKit iOS, então o trilho é desenhado à mão.
 *
 * @param lowerIndex passo da alça da esquerda.
 * @param upperIndex passo da alça da direita.
 * @param onLowerIndexChange chamado quando a alça da esquerda muda de passo.
 * @param onUpperIndexChange chamado quando a alça da direita muda de passo.
 * @param stepCount último passo (o trilho vai de 0 a `stepCount`); mínimo 1.
 * @param title legenda opcional acima do trilho.
 * @param minimumDistance passos mínimos entre as alças.
 * @param valueText texto opcional sob o trilho, com o intervalo escolhido.
 * @param lowerLabel nome da alça da esquerda para o TalkBack.
 * @param upperLabel nome da alça da direita para o TalkBack.
 * @param enabled desligado, o slider fica a 50 % e não responde.
 * @param onEditingChanged `true` quando um arraste começa, `false` quando termina.
 * @param onThumbMoved chamado ao soltar uma alça (ou a cada ajuste do TalkBack), com a alça que mudou.
 */
@Composable
public fun DSRangeSlider(
    lowerIndex: Int,
    upperIndex: Int,
    onLowerIndexChange: (Int) -> Unit,
    onUpperIndexChange: (Int) -> Unit,
    stepCount: Int,
    modifier: Modifier = Modifier,
    title: String? = null,
    minimumDistance: Int = 1,
    valueText: String? = null,
    lowerLabel: String = stringResource(R.string.dskit_minimum),
    upperLabel: String = stringResource(R.string.dskit_maximum),
    enabled: Boolean = true,
    onEditingChanged: ((Boolean) -> Unit)? = null,
    onThumbMoved: ((DSRangeSliderThumb) -> Unit)? = null
) {
    val steps = stepCount.coerceAtLeast(1)
    val minDistance = minimumDistance.coerceAtLeast(0)

    Column(
        modifier = modifier.alpha(if (enabled) 1f else SliderDisabledAlpha),
        verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)
    ) {
        if (title != null) {
            Text(
                title,
                style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold),
                color = DSTheme.colors.onSurfaceMedium
            )
        }

        RangeTrack(
            lowerIndex = lowerIndex,
            upperIndex = upperIndex,
            onLowerIndexChange = onLowerIndexChange,
            onUpperIndexChange = onUpperIndexChange,
            stepCount = steps,
            minimumDistance = minDistance,
            lowerLabel = lowerLabel,
            upperLabel = upperLabel,
            enabled = enabled,
            onEditingChanged = onEditingChanged,
            onThumbMoved = onThumbMoved
        )

        if (valueText != null) {
            Text(valueText, style = DSTypography.subheadline, color = DSTheme.colors.onSurfaceMedium)
        }
    }
}

@Composable
private fun RangeTrack(
    lowerIndex: Int,
    upperIndex: Int,
    onLowerIndexChange: (Int) -> Unit,
    onUpperIndexChange: (Int) -> Unit,
    stepCount: Int,
    minimumDistance: Int,
    lowerLabel: String,
    upperLabel: String,
    enabled: Boolean,
    onEditingChanged: ((Boolean) -> Unit)?,
    onThumbMoved: ((DSRangeSliderThumb) -> Unit)?
) {
    val density = LocalDensity.current
    val haptics = rememberDSHaptics()
    val primary = DSTheme.primary
    val thumbPx = with(density) { ThumbSize.toPx() }

    var activeThumb by remember { mutableStateOf<DSRangeSliderThumb?>(null) }
    // Onde está o dedo enquanto uma alça é arrastada; null depois do encaixe.
    var dragX by remember { mutableStateOf<Float?>(null) }

    val lower by rememberUpdatedState(lowerIndex)
    val upper by rememberUpdatedState(upperIndex)
    val setLower by rememberUpdatedState(onLowerIndexChange)
    val setUpper by rememberUpdatedState(onUpperIndexChange)
    val editingChanged by rememberUpdatedState(onEditingChanged)
    val thumbMoved by rememberUpdatedState(onThumbMoved)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(DSTouchTarget.minimum)
            .padding(horizontal = ThumbSize / 2),
        contentAlignment = Alignment.CenterStart
    ) {
        val width = constraints.maxWidth.toFloat()

        // Comprimento da pista de cada alça: o trilho menos a largura de alça que as mantém separadas.
        val lane = (width - thumbPx).coerceAtLeast(0f)

        // Onde a alça fica num passo: a pista da direita começa uma alça para dentro.
        fun position(index: Int, which: DSRangeSliderThumb): Float {
            val start = if (which == DSRangeSliderThumb.Upper) thumbPx else 0f
            return lane * index.coerceIn(0, stepCount) / stepCount + start
        }

        fun index(atX: Float, which: DSRangeSliderThumb): Int {
            if (lane <= 0f) return if (which == DSRangeSliderThumb.Lower) 0 else stepCount
            val laneX = atX - if (which == DSRangeSliderThumb.Upper) thumbPx else 0f
            return (laneX / lane * stepCount).roundToInt()
        }

        fun set(which: DSRangeSliderThumb, target: Int): Boolean {
            val gap = minimumDistance
            return when (which) {
                DSRangeSliderThumb.Lower -> {
                    val value = target.coerceAtMost((upper - gap).coerceAtLeast(0)).coerceAtLeast(0)
                    if (value != lower) setLower(value)
                    value != lower
                }

                DSRangeSliderThumb.Upper -> {
                    val value = target.coerceAtLeast((lower + gap).coerceAtMost(stepCount)).coerceAtMost(stepCount)
                    if (value != upper) setUpper(value)
                    value != upper
                }
            }
        }

        // A alça arrastada segue o dedo, presa à própria pista e ao passo mais distante que a outra alça permite.
        fun thumbX(which: DSRangeSliderThumb): Float {
            val finger = dragX
            if (activeThumb != which || finger == null) {
                return position(if (which == DSRangeSliderThumb.Lower) lowerIndex else upperIndex, which)
            }
            return when (which) {
                DSRangeSliderThumb.Lower ->
                    finger.coerceIn(0f, position((upperIndex - minimumDistance).coerceAtLeast(0), DSRangeSliderThumb.Lower))

                DSRangeSliderThumb.Upper ->
                    finger.coerceIn(position((lowerIndex + minimumDistance).coerceAtMost(stepCount), DSRangeSliderThumb.Upper), width)
            }
        }

        val lowerX =
            animatedThumbX(thumbX(DSRangeSliderThumb.Lower), dragging = activeThumb == DSRangeSliderThumb.Lower && dragX != null, width)
        val upperX =
            animatedThumbX(thumbX(DSRangeSliderThumb.Upper), dragging = activeThumb == DSRangeSliderThumb.Upper && dragX != null, width)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .background(primary.copy(alpha = TrackAlpha), DSRadius.capsule)
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(lowerX().roundToInt(), 0) }
                .width(with(density) { (upperX() - lowerX()).coerceAtLeast(0f).toDp() })
                .height(TrackHeight)
                .background(primary, DSRadius.capsule)
        )

        listOf(DSRangeSliderThumb.Lower, DSRangeSliderThumb.Upper).forEach { which ->
            val isLower = which == DSRangeSliderThumb.Lower
            val x = if (isLower) lowerX else upperX
            val value = if (isLower) lowerIndex else upperIndex
            val scale by animateFloatAsState(
                targetValue = if (activeThumb == which && !DSTheme.reduceMotion) ActiveThumbScale else 1f,
                animationSpec = DSMotion.short(),
                label = "DSRangeSlider.thumbScale"
            )

            Box(
                modifier = Modifier
                    .offset { IntOffset((x() - thumbPx / 2).roundToInt() - with(density) { TouchInset.roundToPx() }, 0) }
                    .zIndex(if (activeThumb == which) 1f else 0f)
                    .size(DSTouchTarget.minimum)
                    .semantics {
                        contentDescription = if (isLower) lowerLabel else upperLabel
                        stateDescription = "$value / $stepCount"
                        progressBarRangeInfo = ProgressBarRangeInfo(value.toFloat(), 0f..stepCount.toFloat(), steps = stepCount - 1)
                        if (enabled) {
                            setProgress { target ->
                                val changed = set(which, target.roundToInt())
                                if (changed) thumbMoved?.invoke(which)
                                changed
                            }
                        }
                    }
                    .pointerInput(enabled, width) {
                        if (!enabled) return@pointerInput
                        detectHorizontalDragGestures(
                            onDragStart = {
                                activeThumb = which
                                dragX = x()
                                editingChanged?.invoke(true)
                            },
                            onDragEnd = {
                                dragX = null
                                activeThumb = null
                                editingChanged?.invoke(false)
                                thumbMoved?.invoke(which)
                            },
                            onDragCancel = {
                                dragX = null
                                activeThumb = null
                                editingChanged?.invoke(false)
                            },
                            onHorizontalDrag = { change, delta ->
                                change.consume()
                                val finger = (dragX ?: x()) + delta
                                dragX = finger
                                val target = index(finger, which)
                                val current = if (which == DSRangeSliderThumb.Lower) lower else upper
                                if (target != current && set(which, target)) haptics.light()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(ThumbSize)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .dsShadow(ThumbShadow, CircleShape)
                        .background(DSTheme.colors.onAccent, CircleShape)
                        .border(ThumbBorder, primary, CircleShape)
                )
            }
        }
    }
}

/**
 * Posição animada de uma alça: segue o dedo sem atraso durante o arraste e encaixa no passo com a mola
 * `snappy` (sem movimento com "Remover animações").
 */
@Composable
private fun animatedThumbX(target: Float, dragging: Boolean, width: Float): () -> Float {
    val reduceMotion = DSTheme.reduceMotion
    val animatable = remember(width) { Animatable(target) }

    LaunchedEffect(target, dragging) {
        if (dragging || reduceMotion) animatable.snapTo(target) else animatable.animateTo(target, DSMotion.snappy())
    }

    return { animatable.value }
}

private val ThumbSize = 28.dp
private val TrackHeight = 4.dp
private val ThumbBorder = 2.dp
private val TouchInset = (DSTouchTarget.minimum - ThumbSize) / 2
private const val TrackAlpha = 0.16f
private const val ActiveThumbScale = 1.12f
private const val SliderDisabledAlpha = 0.5f

// A mesma sombra da alça do DSKit iOS (preto a 18 %, desfoque 3, 1 para baixo).
private val ThumbShadow = DSShadow(Color.Black.copy(alpha = 0.18f), radius = 3.dp, y = 1.dp)

@DSPreviews
@Composable
private fun DSRangeSliderPreview() {
    DSPreviewContainer("Range slider") {
        val prices = listOf(0, 90_000, 100_000, 120_000, 150_000, 200_000, 250_000, 300_000, 400_000, 500_000, 750_000, 1_000_000)
        var lower by remember { mutableStateOf(3) }
        var upper by remember { mutableStateOf(9) }

        DSRangeSlider(
            title = "Preço",
            lowerIndex = lower,
            upperIndex = upper,
            onLowerIndexChange = { lower = it },
            onUpperIndexChange = { upper = it },
            stepCount = prices.lastIndex,
            valueText = "De R$ ${prices[lower]} até ${if (upper == prices.lastIndex) "qualquer" else "R$ ${prices[upper]}"}"
        )
    }
}
