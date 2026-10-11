package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/**
 * Caixa de seleção do Material ao lado de uma frase ("Não mostrar de novo", "Li e aceito os termos"), fora
 * de uma lista. A frase também é tocável. Numa lista de opções, use [DSCheckboxListItem].
 */
@Composable
public fun DSCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    Row(
        modifier = modifier
            .heightIn(min = DSTouchTarget.minimum)
            .toggleable(value = checked, enabled = enabled, role = Role.Checkbox) {
                haptics.light()
                onCheckedChange(it)
            },
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)

        Text(
            text = label,
            style = DSTheme.typography.bodyMedium,
            color = DSTheme.colors.onSurface,
            modifier = Modifier.dsDisabledAlpha(enabled)
        )
    }
}

@DSPreviews
@Composable
private fun DSCheckboxPreview() {
    var accepted by remember { mutableStateOf(true) }

    DSPreviewContainer("Checkbox") {
        DSCheckbox("I accept the terms", checked = accepted, onCheckedChange = { accepted = it })

        DSCheckbox("Don't show again", checked = false, onCheckedChange = {})
    }
}
