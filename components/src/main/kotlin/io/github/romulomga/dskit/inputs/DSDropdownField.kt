package io.github.romulomga.dskit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics

/**
 * Uma opção de escolha ([DSDropdownField], [DSAutocompleteField], [DSSegmentedButton], [DSMultiSegmentedButton]):
 * o valor e o rótulo que aparece.
 */
@Immutable
public data class DSOption<T>(val value: T, val label: String)

/**
 * Escolha única num menu que desce do campo, o "exposed dropdown menu" do Material: parece um
 * [DSTextField] só de leitura, com a seta à direita. Para até cinco opções curtas sempre visíveis, prefira
 * [DSSegmentedButton]; para muitas, uma tela de busca.
 *
 * @param selection o valor escolhido, ou `null` enquanto nada foi escolhido.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T> DSDropdownField(
    selection: T?,
    onSelectionChange: (T) -> Unit,
    options: List<DSOption<T>>,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selection }?.label.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
                .then(if (errorText != null) Modifier.semantics { error(errorText) } else Modifier),
            enabled = enabled,
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            supportingText = (errorText ?: supportingText)?.let { { Text(it) } },
            isError = errorText != null
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        haptics.light()
                        onSelectionChange(option.value)
                        expanded = false
                    },
                    trailingIcon = if (option.value == selection) {
                        { Icon(DSIcons.Check, contentDescription = null) }
                    } else {
                        null
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSDropdownFieldPreview() {
    var state by remember { mutableStateOf<String?>("RJ") }

    DSPreviewContainer("Dropdown field") {
        DSDropdownField(
            selection = state,
            onSelectionChange = { state = it },
            options = listOf("RJ", "SP", "MG").map { DSOption(it, it) },
            label = "State"
        )

        DSDropdownField(selection = null, onSelectionChange = {}, options = listOf(DSOption(1, "One")), label = "Not chosen")
    }
}
