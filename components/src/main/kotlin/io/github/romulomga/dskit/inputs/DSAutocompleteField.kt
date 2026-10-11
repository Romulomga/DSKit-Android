package io.github.romulomga.dskit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction

/**
 * Campo com sugestões enquanto digita, o "exposed dropdown menu" editável do Material: bairro, cidade,
 * destinatário. O app filtra (localmente ou na rede) e passa as [suggestions] do [query] atual; ao escolher
 * uma, recebe [onSuggestionSelect] e normalmente põe o rótulo dela no [query].
 *
 * Para escolher entre poucas opções fixas, use [DSDropdownField]; para uma busca que ocupa a tela, a
 * `SearchBar` do Material.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T> DSAutocompleteField(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<DSOption<T>>,
    onSuggestionSelect: (DSOption<T>) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorText: String? = null,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && suggestions.isNotEmpty(),
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled)
                .then(if (errorText != null) Modifier.semantics { error(errorText) } else Modifier),
            enabled = enabled,
            singleLine = true,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            supportingText = (errorText ?: supportingText)?.let { { Text(it) } },
            isError = errorText != null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        ExposedDropdownMenu(expanded = expanded && suggestions.isNotEmpty(), onDismissRequest = { expanded = false }) {
            suggestions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        haptics.light()
                        onSuggestionSelect(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSAutocompleteFieldPreview() {
    val neighborhoods = listOf("Ipanema", "Leblon", "Lagoa", "Laranjeiras")
    var query by remember { mutableStateOf("La") }

    DSPreviewContainer("Autocomplete field") {
        DSAutocompleteField(
            query = query,
            onQueryChange = { query = it },
            suggestions = neighborhoods.filter { it.startsWith(query, ignoreCase = true) }.map { DSOption(it, it) },
            onSuggestionSelect = { query = it.label },
            label = "Neighborhood",
            leadingIcon = DSIcons.Search
        )
    }
}
