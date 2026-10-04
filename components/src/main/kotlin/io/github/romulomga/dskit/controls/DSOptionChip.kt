package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

/**
 * Chip em cápsula que liga e desliga uma opção. Use vários lado a lado (numa `Row` ou `FlowRow`) para
 * grupos de filtros.
 *
 * Selecionado, o chip fica cheio na cor primária com texto claro; desmarcado, fundo primário suave com
 * texto na cor primária. O TalkBack o lê como caixa de seleção, com o estado.
 *
 * @param title rótulo do chip.
 * @param selected se está marcado.
 * @param onSelectedChange chamado com o novo valor no toque.
 * @param icon ícone opcional antes do texto.
 * @param enabled desligado, o chip fica a 50 % e não responde.
 */
@Composable
public fun DSOptionChip(
    title: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val primary = DSTheme.primary
    val contentColor = if (selected) DSTheme.colors.onAccent else primary
    val style = DSTypography.subheadline.copy(fontWeight = FontWeight.SemiBold)

    Row(
        modifier = modifier
            // A cápsula tem 36 dp como no iOS; o alvo de toque cresce para 48 dp em volta dela.
            .minimumInteractiveComponentSize()
            .dsPressScale(interactionSource)
            .alpha(if (enabled) 1f else OptionChipDisabledAlpha)
            .clip(DSRadius.capsule)
            .background(if (selected) primary else primary.copy(alpha = OptionChipFillAlpha))
            .toggleable(
                value = selected,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = ripple(),
                onValueChange = {
                    onSelectedChange(it)
                    haptics.light()
                }
            )
            .padding(horizontal = DSSpacing.md, vertical = DSSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(dsControlIconSize(style.fontSize)))
        }

        Text(title, style = style, color = contentColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private const val OptionChipFillAlpha = 0.12f
private const val OptionChipDisabledAlpha = 0.5f

@DSPreviews
@Composable
private fun DSOptionChipPreview() {
    DSPreviewContainer("Option chip") {
        var duplicates by remember { mutableStateOf(false) }
        var noRepeats by remember { mutableStateOf(true) }

        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSOptionChip(title = "Duplicates", icon = DSIcons.Plus, selected = duplicates, onSelectedChange = { duplicates = it })
            DSOptionChip(title = "No repeats", icon = DSIcons.Check, selected = noRepeats, onSelectedChange = { noRepeats = it })
            DSOptionChip(title = "Off", selected = false, onSelectedChange = {}, enabled = false)
        }
    }
}
