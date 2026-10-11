package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.romulomga.dskit.components.R

/**
 * Chip de filtro do Material 3: liga e desliga um filtro ("Com vaga", "Aceita pet"). Marcado, ganha o
 * check à esquerda e o fundo tonal. Use vários num `FlowRow`.
 */
@Composable
public fun DSFilterChip(
    label: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    FilterChip(
        selected = selected,
        onClick = {
            haptics.light()
            onSelectedChange(!selected)
        },
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = when {
            selected -> {
                { Icon(DSIcons.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
            }

            icon != null -> {
                { Icon(icon, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
            }

            else -> null
        }
    )
}

/**
 * Chip de entrada: um valor que o usuário adicionou e pode tirar (um bairro escolhido, um destinatário).
 * Tocar no chip o remove (o "x" é só o sinal, como no Material); o TalkBack o lê como "Remover <rótulo>".
 */
@Composable
public fun DSInputChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val removeLabel = stringResource(R.string.dskit_remove_item, label)

    InputChip(
        selected = false,
        onClick = {
            haptics.light()
            onRemove()
        },
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = icon?.let { { Icon(it, contentDescription = null, modifier = Modifier.size(InputChipDefaults.IconSize)) } },
        trailingIcon = { Icon(DSIcons.Close, contentDescription = removeLabel, modifier = Modifier.size(InputChipDefaults.IconSize)) }
    )
}

/** Chip de ação: um atalho ligado ao conteúdo ("Ligar", "Ver no mapa"). */
@Composable
public fun DSAssistChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    AssistChip(
        onClick = {
            haptics.light()
            onClick()
        },
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = icon?.let { { Icon(it, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) } }
    )
}

@DSPreviews
@Composable
private fun DSChipsPreview() {
    DSPreviewContainer("Chips") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSFilterChip("Parking", selected = true, onSelectedChange = {})

            DSFilterChip("Pet friendly", selected = false, onSelectedChange = {})

            DSInputChip("Ipanema", onRemove = {})

            DSAssistChip("Call", onClick = {}, icon = DSIcons.Share)
        }
    }
}
