package io.github.romulomga.dskit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

/**
 * Botões segmentados do Material 3, escolha única: de duas a cinco opções curtas sempre visíveis ("Venda",
 * "Aluguel"). Ocupa a largura toda; a opção marcada ganha o check.
 */
@Composable
public fun <T> DSSegmentedButton(
    selection: T,
    onSelectionChange: (T) -> Unit,
    options: List<DSOption<T>>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option.value == selection,
                onClick = {
                    if (option.value != selection) haptics.light()
                    onSelectionChange(option.value)
                },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                enabled = enabled,
                label = { Text(option.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

/** Botões segmentados de escolha múltipla: cada opção liga e desliga sozinha ("Seg", "Ter", "Qua"…). */
@Composable
public fun <T> DSMultiSegmentedButton(
    selection: Set<T>,
    onSelectionChange: (Set<T>) -> Unit,
    options: List<DSOption<T>>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    MultiChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val checked = option.value in selection

            SegmentedButton(
                checked = checked,
                onCheckedChange = {
                    haptics.light()
                    onSelectionChange(if (it) selection + option.value else selection - option.value)
                },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                enabled = enabled,
                label = { Text(option.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

@DSPreviews
@Composable
private fun DSSegmentedButtonPreview() {
    var deal by remember { mutableStateOf("sale") }
    var days by remember { mutableStateOf(setOf("mon", "wed")) }

    DSPreviewContainer("Segmented button") {
        DSSegmentedButton(
            selection = deal,
            onSelectionChange = { deal = it },
            options = listOf(DSOption("sale", "Sale"), DSOption("rent", "Rent"))
        )

        DSMultiSegmentedButton(
            selection = days,
            onSelectionChange = { days = it },
            options = listOf(DSOption("mon", "Mon"), DSOption("tue", "Tue"), DSOption("wed", "Wed"))
        )
    }
}
