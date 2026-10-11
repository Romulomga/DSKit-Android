package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import io.github.romulomga.dskit.components.R

/**
 * Campo para um número inteiro, sobre o [DSTextField]: teclado numérico e o valor já convertido. Texto que
 * não é número vira `null` e mostra [invalidNumberMessage]; campo vazio também é `null`, sem erro.
 *
 * @param value o número atual, ou `null` se vazio ou inválido.
 * @param onValueChange chamado a cada edição com o número convertido.
 */
@Composable
public fun DSNumberField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    errorText: String? = null,
    invalidNumberMessage: String = stringResource(R.string.dskit_enter_valid_number),
    enabled: Boolean = true
) {
    var text by rememberSaveable { mutableStateOf(value?.toString().orEmpty()) }
    val invalid = remember(text) { text.isNotBlank() && parseNumber(text) == null }

    // Valor mudou por fora (reset, outro campo): o texto acompanha. Texto inválido já representa `null`.
    LaunchedEffect(value) {
        if (parseNumber(text) != value) text = value?.toString().orEmpty()
    }

    DSTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            onValueChange(parseNumber(newText))
        },
        label = label,
        modifier = modifier,
        supportingText = supportingText,
        errorText = errorText ?: invalidNumberMessage.takeIf { invalid },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
    )
}

private fun parseNumber(raw: String): Int? = raw.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()

@DSPreviews
@Composable
private fun DSNumberFieldPreview() {
    var minimum by remember { mutableStateOf<Int?>(1) }
    var maximum by remember { mutableStateOf<Int?>(100) }

    DSPreviewContainer("Number field") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSNumberField(value = minimum, onValueChange = { minimum = it }, label = "Min", modifier = Modifier.weight(1f))

            DSNumberField(value = maximum, onValueChange = { maximum = it }, label = "Max", modifier = Modifier.weight(1f))
        }
    }
}
