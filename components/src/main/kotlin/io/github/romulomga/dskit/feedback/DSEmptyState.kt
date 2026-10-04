package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Tamanho do ícone do estado vazio, o mesmo do DSKit iOS (fixo, como lá). */
private val EmptyIconSize = 56.dp

/**
 * Estado "ainda não há nada aqui": ícone, título, mensagem e, com `actionTitle` e `onAction`, um
 * `DSPrimaryButton` com a ação que tira o usuário dali.
 */
@Composable
public fun DSEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionTitle: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DSSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DSSpacing.md)
    ) {
        // O texto é lido como um bloco; o botão fica fora para continuar sendo um alvo do TalkBack.
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DSSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DSTheme.colors.onSurfaceLight,
                modifier = Modifier.size(EmptyIconSize)
            )

            Text(
                text = title,
                style = DSTypography.title3,
                color = DSTheme.colors.onSurfaceHigh,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                style = DSTypography.subheadline,
                color = DSTheme.colors.onSurfaceMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = DSSpacing.lg)
            )
        }

        if (actionTitle != null && onAction != null) {
            DSPrimaryButton(
                title = actionTitle,
                onClick = onAction,
                modifier = Modifier
                    .padding(top = DSSpacing.sm)
                    .padding(horizontal = DSSpacing.lg)
            )
        }
    }
}

@DSPreviews
@Composable
private fun DSEmptyStatePreview() {
    DSPreviewContainer("Empty state") {
        DSEmptyState(
            icon = DSIcons.Tray,
            title = "No participants yet",
            message = "Add at least two names to draw a winner.",
            actionTitle = "Add names",
            onAction = {}
        )
    }
}
