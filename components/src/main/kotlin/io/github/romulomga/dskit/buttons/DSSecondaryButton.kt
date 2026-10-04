package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Botão tingido de largura total para ações secundárias, como Compartilhar, Copiar ou Sortear de novo:
 * fundo na cor primária a 12 % e texto na cor primária.
 */
@Composable
public fun DSSecondaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val primary = DSTheme.primary

    DSButtonFrame(
        onClick = {
            haptics.light()
            onClick()
        },
        modifier = modifier.dsDisabledAlpha(enabled),
        enabled = enabled,
        shape = DSRadius.large,
        minHeight = DSTouchTarget.button,
        background = primary.copy(alpha = 0.12f)
    ) {
        DSButtonLabel(title = title, icon = icon, color = primary)
    }
}

@DSPreviews
@Composable
private fun DSSecondaryButtonPreview() {
    DSPreviewContainer("Secondary") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSSecondaryButton("Share", onClick = {}, icon = DSIcons.ArrowClockwise)

            DSSecondaryButton("Draw again", onClick = {})

            DSSecondaryButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
