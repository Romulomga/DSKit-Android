package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.roundToInt

/**
 * Slider do Material 3 com rótulo e o valor atual escrito ao lado (o slider sozinho não diz o número).
 *
 * @param steps quantos pontos intermediários entre as pontas; `0` é contínuo.
 * @param valueText o valor formatado à direita do rótulo ("3 quartos", "R$ 500 mil").
 */
@Composable
public fun DSSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    valueText: String? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        SliderHeader(label, valueText)

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
            enabled = enabled,
            valueRange = valueRange,
            steps = steps
        )
    }
}

/**
 * Intervalo com duas alças, o `RangeSlider` do Material 3, com rótulo e o intervalo escrito ao lado.
 *
 * @param minimumDistance a menor distância entre as alças, no valor; o intervalo nunca encolhe abaixo disso.
 */
@Composable
public fun DSRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    minimumDistance: Float = 0f,
    valueText: String? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        SliderHeader(label, valueText)

        RangeSlider(
            value = value,
            onValueChange = { range ->
                if (range.endInclusive - range.start >= minimumDistance) onValueChange(range)
            },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
            enabled = enabled,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
private fun SliderHeader(label: String, valueText: String?) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = DSTheme.typography.bodyLarge, color = DSTheme.colors.onSurface, modifier = Modifier.weight(1f))

        if (valueText != null) {
            Text(valueText, style = DSTheme.typography.labelLarge, color = DSTheme.colors.primary)
        }
    }
}

@DSPreviews
@Composable
private fun DSSliderPreview() {
    var rooms by remember { mutableFloatStateOf(3f) }
    var price by remember { mutableStateOf(2f..6f) }

    DSPreviewContainer("Slider") {
        DSSlider(
            value = rooms,
            onValueChange = { rooms = it },
            label = "Bedrooms",
            valueRange = 1f..5f,
            steps = 3,
            valueText = rooms.roundToInt().toString()
        )

        DSRangeSlider(
            value = price,
            onValueChange = { price = it },
            label = "Price",
            valueRange = 0f..10f,
            steps = 9,
            minimumDistance = 1f,
            valueText = "${price.start.roundToInt()} – ${price.endInclusive.roundToInt()}"
        )
    }
}
