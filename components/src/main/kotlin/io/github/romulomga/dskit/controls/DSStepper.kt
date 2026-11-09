package io.github.romulomga.dskit

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import io.github.romulomga.dskit.components.R
import kotlin.math.roundToLong

/**
 * Ajuste de um número em passos: o rótulo e os botões − e +, que somam [step] dentro de [range]. O Material 3
 * não tem esta peça; ela é montada com os botões de ícone dele. Use para o ajuste fino de um valor que já tem um
 * ponto de partida (dose, peso, parcelas, quantidade); para digitar o número, use o [DSDecimalField] ou o
 * [DSNumberField], e para escolher numa faixa arrastando, o [DSSlider].
 *
 * Cada toque vibra de leve. Segurar o botão repete o passo, acelerando, até soltar ou chegar ao limite; no
 * limite, o botão daquele lado desliga. A soma não acumula ruído de ponto flutuante (0,1 + 0,2 dá 0,3).
 *
 * @param label o que está sendo ajustado ("Parcelas"). Também nomeia os botões para o TalkBack.
 * @param valueText o valor como deve ser lido e mostrado entre os botões ("2,5 mg", "3x"); `null` não mostra
 *   nada ali, para quando o valor já aparece no rótulo ou num campo ao lado.
 * @param style o estilo dos dois botões; o padrão é o botão de ícone sem fundo.
 * @param decreaseDescription o que o TalkBack lê no −; por padrão "Diminuir [label]".
 * @param increaseDescription o que o TalkBack lê no +; por padrão "Aumentar [label]".
 */
@Composable
public fun DSStepper(
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Double>,
    step: Double,
    label: String,
    modifier: Modifier = Modifier,
    valueText: String? = null,
    style: DSIconButtonStyle = DSIconButtonStyle.Standard,
    enabled: Boolean = true,
    decreaseDescription: String = stringResource(R.string.dskit_decrease_item, label),
    increaseDescription: String = stringResource(R.string.dskit_increase_item, label)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f).dsDisabledAlpha(enabled),
            color = DSTheme.colors.onSurface,
            style = DSTheme.typography.bodyLarge
        )

        StepButton(DSIcons.Remove, decreaseDescription, value, -step, range, onValueChange, style, enabled, valueText)

        if (valueText != null) {
            Text(
                text = valueText,
                modifier = Modifier.dsDisabledAlpha(enabled),
                color = DSTheme.colors.onSurface,
                style = DSTheme.typography.titleMedium
            )
        }

        StepButton(DSIcons.Add, increaseDescription, value, step, range, onValueChange, style, enabled, valueText)
    }
}

/**
 * O mesmo ajuste para contagens (parcelas, unidades, dias). O valor aparece entre os botões; passe
 * `valueText = null` se ele já estiver no rótulo.
 */
@Composable
public fun DSStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String,
    modifier: Modifier = Modifier,
    valueText: String? = value.toString(),
    step: Int = 1,
    style: DSIconButtonStyle = DSIconButtonStyle.Standard,
    enabled: Boolean = true,
    decreaseDescription: String = stringResource(R.string.dskit_decrease_item, label),
    increaseDescription: String = stringResource(R.string.dskit_increase_item, label)
) {
    DSStepper(
        value = value.toDouble(),
        onValueChange = { onValueChange(it.roundToLong().toInt()) },
        range = range.first.toDouble()..range.last.toDouble(),
        step = step.toDouble(),
        label = label,
        modifier = modifier,
        valueText = valueText,
        style = style,
        enabled = enabled,
        decreaseDescription = decreaseDescription,
        increaseDescription = increaseDescription
    )
}

/** As contas do [DSStepper], puras para o teste. */
internal object DSStepperMath {
    private const val Precision = 10_000.0

    /** Quanto o dedo fica no botão antes de começar a repetir; menos que um toque longo do sistema. */
    const val HoldDelayMillis: Long = 350L

    private const val RepeatStartMillis = 150L
    private const val RepeatMinMillis = 40L
    private const val RepeatDecay = 0.85

    /** Soma [delta] sem o ruído de ponto flutuante e sem sair de [range]. */
    fun step(value: Double, delta: Double, range: ClosedFloatingPointRange<Double>): Double {
        val next = ((value + delta) * Precision).roundToLong() / Precision

        return next.coerceIn(range.start, range.endInclusive)
    }

    /** O intervalo da [index]-ésima repetição ao segurar: começa em 150 ms e encurta 15% a cada passo, até 40 ms. */
    fun repeatInterval(index: Int): Long {
        var interval = RepeatStartMillis.toDouble()
        repeat(index) { interval *= RepeatDecay }

        return maxOf(RepeatMinMillis, interval.toLong())
    }
}

/**
 * Um − ou um +: o toque soma [delta] uma vez; segurar repete com aceleração. Não é o [DSIconButton] porque o toque
 * longo dele abre a dica, e aqui o toque longo é repetir.
 */
@Composable
private fun StepButton(
    icon: ImageVector,
    contentDescription: String,
    value: Double,
    delta: Double,
    range: ClosedFloatingPointRange<Double>,
    onValueChange: (Double) -> Unit,
    style: DSIconButtonStyle,
    enabled: Boolean,
    valueText: String?
) {
    val haptics = rememberDSHaptics()
    val latest by rememberUpdatedState(value)
    val change by rememberUpdatedState(onValueChange)
    val active = enabled && if (delta < 0) value > range.start else value < range.endInclusive
    val click = {
        haptics.light()
        onValueChange(DSStepperMath.step(value, delta, range))
    }

    // Um passo por repetição; para (e não vibra) quando o limite prende o valor.
    val stepOnce by rememberUpdatedState {
        val next = DSStepperMath.step(latest, delta, range)

        if (next != latest) {
            haptics.light()
            change(next)
            true
        } else {
            false
        }
    }
    val modifier = Modifier
        .semantics { if (valueText != null) stateDescription = valueText }
        .then(if (active) Modifier.repeatWhileHeld { stepOnce() } else Modifier)
    val content: @Composable () -> Unit = { Icon(icon, contentDescription = contentDescription) }

    when (style) {
        DSIconButtonStyle.Standard -> IconButton(onClick = click, modifier = modifier, enabled = active, content = content)
        DSIconButtonStyle.Filled -> FilledIconButton(onClick = click, modifier = modifier, enabled = active, content = content)
        DSIconButtonStyle.Tonal -> FilledTonalIconButton(onClick = click, modifier = modifier, enabled = active, content = content)
        DSIconButtonStyle.Outlined -> OutlinedIconButton(onClick = click, modifier = modifier, enabled = active, content = content)
    }
}

/**
 * Segurar repete [onRepeat] (que devolve `false` no limite) depois de [DSStepperMath.HoldDelayMillis]. Observa os
 * toques antes do botão (passe `Initial`): um toque curto segue para o `onClick`; depois de repetir, o soltar é
 * consumido, para o botão não somar mais um passo.
 */
private fun Modifier.repeatWhileHeld(onRepeat: () -> Boolean): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

        val endedEarly = withTimeoutOrNull(DSStepperMath.HoldDelayMillis) {
            waitForUpOrCancellation(PointerEventPass.Initial)
            true
        }
        if (endedEarly != null) return@awaitEachGesture

        var index = 0

        while (onRepeat()) {
            val ended = withTimeoutOrNull(DSStepperMath.repeatInterval(index)) {
                waitForUpOrCancellation(PointerEventPass.Initial)?.consume()
                true
            }
            if (ended != null) return@awaitEachGesture

            index++
        }

        // No limite: só espera soltar, sem somar no soltar.
        waitForUpOrCancellation(PointerEventPass.Initial)?.consume()
    }
}

@DSPreviews
@Composable
private fun DSStepperPreview() {
    var dose by remember { mutableDoubleStateOf(2.5) }
    var installments by remember { mutableIntStateOf(1) }

    DSPreviewContainer("Stepper") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSStepper(value = installments, onValueChange = { installments = it }, range = 1..12, label = "Installments")

            DSStepper(
                value = dose,
                onValueChange = { dose = it },
                range = 0.0..40.0,
                step = 0.25,
                label = "Dose",
                valueText = "$dose mg",
                style = DSIconButtonStyle.Tonal
            )

            DSStepper(value = 3, onValueChange = {}, range = 1..12, label = "Disabled", enabled = false)
        }
    }
}
