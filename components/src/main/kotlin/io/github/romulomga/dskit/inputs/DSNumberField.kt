package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import io.github.romulomga.dskit.components.R

/**
 * Campo de texto para números inteiros. Expõe `Int?`, e `null` representa o vazio ou inválido sem gambiarra.
 * Texto que não é número mostra `invalidNumberMessage`; `errorMessage` (validação do app) tem prioridade.
 * O teclado é numérico, e o "OK" dele é a `ImeAction.Done`.
 */
@Composable
public fun DSNumberField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    placeholder: String = "0",
    errorMessage: String? = null,
    invalidNumberMessage: String = stringResource(R.string.dskit_enter_valid_number),
    enabled: Boolean = true
) {
    var text by rememberSaveable { mutableStateOf(value?.toString().orEmpty()) }
    var hasInternalError by rememberSaveable { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val requester = remember { FocusRequester() }
    val shownError = errorMessage ?: invalidNumberMessage.takeIf { hasInternalError }

    // Valor mudou por fora (reset, outro campo): o texto acompanha. Texto inválido já representa `null`,
    // então não é apagado quando o valor externo é `null`.
    LaunchedEffect(value) {
        if (parseNumber(text) != value) {
            text = value?.toString().orEmpty()
            hasInternalError = false
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier.clearAndSetSemantics {},
                style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold),
                color = DSTheme.colors.onSurfaceMedium
            )
        }

        DSInputBox(
            isFocused = isFocused,
            isInvalid = shownError != null,
            onClick = if (enabled) ({ requester.requestFocus() }) else null
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    val trimmed = newText.trim()
                    val parsed = parseNumber(newText)
                    hasInternalError = trimmed.isNotEmpty() && parsed == null
                    onValueChange(parsed)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(requester)
                    .onFocusChanged { isFocused = it.isFocused }
                    .semantics {
                        if (title != null) contentDescription = title
                        if (shownError != null) error(shownError)
                    },
                enabled = enabled,
                singleLine = true,
                textStyle = DSTypography.body.copy(
                    color = if (enabled) DSTheme.colors.onSurfaceHigh else DSTheme.colors.onSurfaceMedium,
                    fontFeatureSettings = "tnum"
                ),
                cursorBrush = SolidColor(DSTheme.colors.accent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                decorationBox = { innerField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (text.isEmpty()) {
                            Text(placeholder, style = DSTypography.body, color = DSTheme.colors.onSurfaceMedium, maxLines = 1)
                        }

                        innerField()
                    }
                }
            )
        }

        if (shownError != null) {
            DSFieldErrorLabel(shownError)
        }
    }
}

private fun parseNumber(raw: String): Int? = raw.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()

// MARK: - Previews

@DSPreviews
@Composable
private fun DSNumberFieldPreview() {
    var minimum by remember { mutableStateOf<Int?>(1) }
    var maximum by remember { mutableStateOf<Int?>(100) }

    DSPreviewContainer("Number field") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSNumberField(value = minimum, onValueChange = { minimum = it }, modifier = Modifier.weight(1f), title = "Min")

            DSNumberField(value = maximum, onValueChange = { maximum = it }, modifier = Modifier.weight(1f), title = "Max")
        }
    }
}
