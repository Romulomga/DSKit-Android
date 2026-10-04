package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Botão cheio de largura total na cor primária do tema. Use para a ação dominante da tela (de preferência
 * uma por tela). Em `isLoading` mostra um indicador no lugar do ícone e não recebe toque.
 */
@Composable
public fun DSPrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val content = DSTheme.colors.onAccent

    // Como no iOS: só o desabilitado esmaece o fundo; carregando mantém a cor e só bloqueia o toque.
    DSButtonFrame(
        onClick = {
            haptics.medium()
            onClick()
        },
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = DSRadius.large,
        minHeight = DSTouchTarget.button,
        background = DSTheme.primary.copy(alpha = if (enabled) 1f else 0.5f)
    ) {
        DSButtonLabel(title = title, icon = icon, color = content, isLoading = isLoading)
    }
}

/**
 * Moldura comum dos botões de largura total do DSKit: forma, fundo, altura mínima, ripple, escala ao
 * pressionar e papel de botão para o TalkBack.
 */
@Composable
internal fun DSButtonFrame(
    onClick: () -> Unit,
    enabled: Boolean,
    shape: Shape,
    minHeight: Dp,
    background: Color,
    modifier: Modifier = Modifier,
    border: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .dsPressScale(interactionSource)
            .clip(shape)
            .background(background)
            .then(border)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = DSSpacing.lg, vertical = DSSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** Ícone (ou indicador de carregamento) e título em headline semibold, centralizados. */
@Composable
internal fun DSButtonLabel(title: String, icon: ImageVector?, color: Color, isLoading: Boolean = false) {
    val style = DSTypography.headline
    val glyph = with(LocalDensity.current) { style.fontSize.toDp() }

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(glyph),
            color = color,
            strokeWidth = 2.dp
        )
    } else if (icon != null) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(glyph))
    }

    Text(
        text = title,
        style = style,
        fontWeight = FontWeight.SemiBold,
        color = color,
        textAlign = TextAlign.Center
    )
}

/** Opacidade de componente desabilitado, a mesma do iOS. */
internal fun Modifier.dsDisabledAlpha(enabled: Boolean): Modifier = alpha(if (enabled) 1f else 0.5f)

@DSPreviews
@Composable
private fun DSPrimaryButtonPreview() {
    DSPreviewContainer("Primary") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSPrimaryButton("Draw", onClick = {}, icon = DSIcons.Star)

            DSPrimaryButton("Loading", onClick = {}, isLoading = true)

            DSPrimaryButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
