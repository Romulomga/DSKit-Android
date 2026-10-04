package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Botão de ação destrutiva de largura total, em `errorHigh` sobre fundo do mesmo tom a 12 %. Combine com
 * um diálogo de confirmação em quem chama.
 */
@Composable
public fun DSDestructiveButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val error = DSTheme.colors.errorHigh

    DSButtonFrame(
        onClick = {
            haptics.warning()
            onClick()
        },
        modifier = modifier.dsDisabledAlpha(enabled),
        enabled = enabled,
        shape = DSRadius.large,
        minHeight = DSTouchTarget.button,
        background = error.copy(alpha = 0.12f)
    ) {
        DSButtonLabel(title = title, icon = icon, color = error)
    }
}

@DSPreviews
@Composable
private fun DSDestructiveButtonPreview() {
    DSPreviewContainer("Destructive") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSDestructiveButton("Clear list", onClick = {}, icon = DSIcons.Close)

            DSDestructiveButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
