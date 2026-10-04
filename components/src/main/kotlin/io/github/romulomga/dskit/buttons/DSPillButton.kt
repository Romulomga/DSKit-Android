package io.github.romulomga.dskit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Variantes do `DSPillButton`. */
public enum class DSPillButtonVariant {
    /** Cheio na cor primária, texto em `onAccent`. Ação dominante. */
    Primary,

    /** Fundo tingido da cor primária, texto na cor primária. Ação de apoio. */
    Secondary,

    /**
     * Fundo transparente com contorno e texto na cor primária. Ação de apoio sobre foto ou fundo
     * atmosférico, onde o tingido ficaria turvo.
     */
    Outlined
}

/** Altura mínima da pílula, a mesma do DSKit iOS (mais alta que o botão retangular). */
private val PillMinHeight = 56.dp

/** Espessura do contorno da variante `Outlined`, a mesma do DSKit iOS. */
private val PillOutlineWidth = 1.5.dp

/**
 * CTA de largura total em forma de pílula (raio igual a metade da altura). Use em onboarding, CTAs de
 * destaque ou onde um formato mais suave combine com o conteúdo. Em `isLoading` mostra um indicador no
 * lugar do ícone e não recebe toque.
 */
@Composable
public fun DSPillButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    variant: DSPillButtonVariant = DSPillButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val primary = DSTheme.primary

    val foreground = when (variant) {
        DSPillButtonVariant.Primary -> DSTheme.colors.onAccent
        DSPillButtonVariant.Secondary, DSPillButtonVariant.Outlined -> primary
    }

    val background = when (variant) {
        DSPillButtonVariant.Primary -> primary
        DSPillButtonVariant.Secondary -> primary.copy(alpha = 0.12f)
        DSPillButtonVariant.Outlined -> Color.Transparent
    }

    val outline = if (variant == DSPillButtonVariant.Outlined) {
        Modifier.border(PillOutlineWidth, primary, DSRadius.capsule)
    } else {
        Modifier
    }

    DSButtonFrame(
        onClick = {
            haptics.medium()
            onClick()
        },
        modifier = modifier.dsDisabledAlpha(enabled),
        enabled = enabled && !isLoading,
        shape = DSRadius.capsule,
        minHeight = PillMinHeight,
        background = background,
        border = outline
    ) {
        DSButtonLabel(title = title, icon = icon, color = foreground, isLoading = isLoading)
    }
}

@DSPreviews
@Composable
private fun DSPillButtonPreview() {
    DSPreviewContainer("Pill") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSPillButton("Continuar", onClick = {})

            DSPillButton("Começar jornada", onClick = {}, icon = DSIcons.Star)

            DSPillButton("Loading", onClick = {}, isLoading = true)

            DSPillButton("Secundário", onClick = {}, variant = DSPillButtonVariant.Secondary)

            DSPillButton("Outlined", onClick = {}, variant = DSPillButtonVariant.Outlined)

            DSPillButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
