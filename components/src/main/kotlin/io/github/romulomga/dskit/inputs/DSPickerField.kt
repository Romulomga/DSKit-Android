package io.github.romulomga.dskit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import io.github.romulomga.dskit.components.R

/** Uma opção do `DSPickerField`: o valor e o texto que aparece. */
@Immutable
public data class DSPickerOption<T>(val value: T, val label: String)

/**
 * Escolha única com cara de `DSTextField` que abre um menu (`DropdownMenu`): título opcional, placeholder
 * enquanto nada foi escolhido, chevron (ou outro ícone à direita) e legenda de ajuda ou erro. A opção
 * escolhida aparece com um check no menu. `hugsContent` deixa o campo da largura do texto, para seletores
 * ao lado de um campo de texto (UF ao lado do número).
 */
@Composable
public fun <T> DSPickerField(
    placeholder: String,
    selection: T?,
    onSelectionChange: (T) -> Unit,
    options: List<DSPickerOption<T>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    errorMessage: String? = null,
    helperText: String? = null,
    isInvalid: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    hugsContent: Boolean = false,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selection }?.label
    val notSelected = stringResource(R.string.dskit_not_selected)
    val hasError = isInvalid || errorMessage != null
    val chevronStyle = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold)

    Column(
        modifier = if (hugsContent) modifier.width(IntrinsicSize.Max) else modifier,
        verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)
    ) {
        if (title != null) {
            DSFieldLabel(title, modifier = Modifier.clearAndSetSemantics {})
        }

        Box {
            DSInputBox(
                modifier = Modifier
                    .clip(DSRadius.medium)
                    .clickable(enabled = enabled, role = Role.DropdownList) { expanded = true }
                    .clearAndSetSemantics {
                        contentDescription = title ?: placeholder
                        stateDescription = selectedLabel ?: notSelected
                        if (errorMessage != null) error(errorMessage)
                    },
                leadingIcon = leadingIcon,
                isFocused = expanded,
                isInvalid = hasError
            ) {
                Text(
                    text = selectedLabel ?: placeholder,
                    modifier = if (hugsContent) Modifier else Modifier.weight(1f),
                    style = DSTypography.body,
                    color = if (selectedLabel == null || !enabled) DSTheme.colors.onSurfaceMedium else DSTheme.colors.onSurfaceHigh,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = trailingIcon ?: DSIcons.ChevronDown,
                    contentDescription = null,
                    tint = DSTheme.colors.onSurfaceMedium,
                    modifier = Modifier.size(dsInputIconSize(chevronStyle))
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = DSTheme.colors.surfaceElevated
            ) {
                options.forEach { option ->
                    val isSelected = option.value == selection
                    DropdownMenuItem(
                        text = { Text(option.label, style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) },
                        onClick = {
                            expanded = false
                            onSelectionChange(option.value)
                            haptics.light()
                        },
                        modifier = Modifier.semantics { selected = isSelected },
                        trailingIcon = if (isSelected) {
                            { Icon(DSIcons.Check, contentDescription = null, tint = DSTheme.colors.accent) }
                        } else {
                            null
                        }
                    )
                }
            }
        }

        DSFieldCaption(errorMessage = errorMessage, helperText = helperText)
    }
}

// MARK: - Previews

@Composable
private fun DSPickerFieldPreviewContent() {
    var state by remember { mutableStateOf<String?>(null) }
    var number by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
        DSPickerField(
            placeholder = "Choose",
            selection = state,
            onSelectionChange = { state = it },
            options = listOf("RJ", "SP", "MG").map { DSPickerOption(it, it) },
            title = "State"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTextField(value = number, onValueChange = { number = it }, modifier = Modifier.weight(1f), placeholder = "Number")

            DSPickerField(
                placeholder = "UF",
                selection = state,
                onSelectionChange = { state = it },
                options = listOf("RJ", "SP").map { DSPickerOption(it, it) },
                isInvalid = true,
                hugsContent = true
            )
        }

        DSFieldCaption(errorMessage = "Pick a state")
    }
}

@DSPreviews
@Composable
private fun DSPickerFieldPreview() {
    DSPreviewContainer("Picker field") { DSPickerFieldPreviewContent() }
}
