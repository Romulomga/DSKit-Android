package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Estilos do `DSIconButton`. */
public enum class DSIconButtonStyle {
    /** Só o ícone na cor primária, para affordances em linha. */
    Plain,

    /** Ícone na cor primária sobre fundo tingido a 15 %, para ações de barra. Padrão. */
    Tinted,

    /** Ícone em `onAccent` sobre a cor primária, para CTAs circulares com destaque. */
    Filled
}

/** Forma do fundo do `DSIconButton`. */
public enum class DSIconButtonShape {
    Circle,

    /** Quadrado de cantos arredondados (`DSRadius.md`). */
    Rounded
}

/** Tamanho do glifo, o equivalente dos 17 pt semibold do iOS (não escala com a fonte, como lá). */
private val IconGlyphSize = 20.dp

/**
 * Botão compacto só com ícone, num alvo de 48 dp. `Tinted` para ações de barra, `Filled` para CTAs
 * circulares com destaque, `Plain` para ícones em linha. `contentDescription` é obrigatório: é o que o
 * TalkBack lê.
 */
@Composable
public fun DSIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DSIconButtonStyle = DSIconButtonStyle.Tinted,
    shape: DSIconButtonShape = DSIconButtonShape.Circle,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val primary = DSTheme.primary

    val foreground = when (style) {
        DSIconButtonStyle.Plain, DSIconButtonStyle.Tinted -> primary
        DSIconButtonStyle.Filled -> DSTheme.colors.onAccent
    }

    val background = when (style) {
        DSIconButtonStyle.Plain -> Color.Transparent
        DSIconButtonStyle.Tinted -> primary.copy(alpha = 0.15f)
        DSIconButtonStyle.Filled -> primary
    }

    val clipShape = when (shape) {
        DSIconButtonShape.Circle -> CircleShape
        DSIconButtonShape.Rounded -> DSRadius.medium
    }

    Box(
        modifier = modifier
            .dsDisabledAlpha(enabled)
            .size(DSTouchTarget.minimum)
            .dsPressScale(interactionSource)
            .clip(clipShape)
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = {
                    haptics.light()
                    onClick()
                }
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(IconGlyphSize))
    }
}

@DSPreviews
@Composable
private fun DSIconButtonPreview() {
    DSPreviewContainer("Icon button") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSIconButton(DSIcons.ArrowClockwise, "Share", onClick = {}, style = DSIconButtonStyle.Plain)

            DSIconButton(DSIcons.Check, "Copy", onClick = {}, style = DSIconButtonStyle.Tinted)

            DSIconButton(DSIcons.Plus, "Add", onClick = {}, style = DSIconButtonStyle.Filled)

            DSIconButton(DSIcons.Lock, "Settings", onClick = {}, shape = DSIconButtonShape.Rounded)

            DSIconButton(DSIcons.Plus, "Disabled", onClick = {}, enabled = false)
        }
    }
}
