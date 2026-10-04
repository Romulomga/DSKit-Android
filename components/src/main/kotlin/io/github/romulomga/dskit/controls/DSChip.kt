package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * Chip em pílula que alterna entre selecionado e não selecionado. Use em escolhas múltiplas de onboarding,
 * seletores de tags ou qualquer escolha rápida de "tocar para marcar".
 *
 * Leva um emoji **ou** um ícone à esquerda (passe só um). Selecionado, ganha fundo e contorno na cor
 * primária e um check à direita, para o estado não depender só da cor; o TalkBack lê "selecionado".
 *
 * No Android o chip abraça o conteúdo (no iOS ele ocupa a largura toda). Para a lista empilhada do iOS,
 * passe `Modifier.fillMaxWidth()`: o check vai para a borda direita.
 *
 * @param title rótulo do chip; é o que o TalkBack lê (o emoji fica de fora).
 * @param selected se está marcado.
 * @param onClick chamado no toque; quem chama inverte `selected`.
 * @param emoji emoji opcional à esquerda.
 * @param icon ícone opcional à esquerda, usado quando não há emoji.
 * @param enabled desligado, o chip fica a 50 % e não responde.
 */
@Composable
public fun DSChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val primary = DSTheme.primary
    val titleStyle = DSTypography.subheadline.copy(fontWeight = FontWeight.SemiBold)

    Row(
        modifier = modifier
            .dsPressScale(interactionSource)
            .alpha(if (enabled) 1f else ChipDisabledAlpha)
            .clip(DSRadius.capsule)
            .background(if (selected) primary.copy(alpha = ChipSelectedFillAlpha) else DSTheme.colors.surface(DSTheme.surfaceLevel))
            .border(ChipBorderWidth, if (selected) primary else Color.Transparent, DSRadius.capsule)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = {
                    haptics.light()
                    onClick()
                }
            )
            .heightIn(min = DSTouchTarget.minimum)
            .padding(horizontal = DSSpacing.md, vertical = ChipVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (emoji != null) {
                Text(emoji, style = DSTypography.body, modifier = Modifier.clearAndSetSemantics {})
            } else if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) primary else DSTheme.colors.onSurfaceMedium,
                    modifier = Modifier.size(dsControlIconSize(titleStyle.fontSize))
                )
            }

            Text(title, style = titleStyle, color = if (selected) primary else DSTheme.colors.onSurfaceHigh)
        }

        if (selected) {
            Icon(
                DSIcons.Check,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.padding(start = DSSpacing.sm).size(dsControlIconSize(DSTypography.caption.fontSize))
            )
        }
    }
}

/** Ícone do tamanho do texto ao lado, para crescer junto com a fonte do sistema. */
@Composable
internal fun dsControlIconSize(fontSize: TextUnit): Dp = with(LocalDensity.current) { fontSize.toDp() } + DSSpacing.xxs

private val ChipVerticalPadding = 14.dp
private val ChipBorderWidth = 1.5.dp
private const val ChipSelectedFillAlpha = 0.12f
private const val ChipDisabledAlpha = 0.5f

@DSPreviews
@Composable
private fun DSChipPreview() {
    DSPreviewContainer("Chip") {
        val selected = remember { mutableStateListOf("primeira-viagem", "trabalho") }
        val options = listOf(
            Triple("primeira-viagem", "Primeira viagem", "🌱"),
            Triple("pos-parto", "Pós-parto recente", "💜"),
            Triple("solo", "Mãe solo", "🦁"),
            Triple("trabalho", "Voltando ao trabalho", "💼")
        )

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            options.forEach { (id, label, emoji) ->
                DSChip(
                    title = label,
                    emoji = emoji,
                    selected = id in selected,
                    onClick = { if (id in selected) selected.remove(id) else selected.add(id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSChip(title = "Hug", icon = DSIcons.Star, selected = true, onClick = {})
                DSChip(title = "Disabled", selected = false, onClick = {}, enabled = false)
            }
        }
    }
}
