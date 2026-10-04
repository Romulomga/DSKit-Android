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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.components.R

/** Tamanho do ícone do estado de erro, o mesmo do DSKit iOS (fixo, como lá). */
private val ErrorIconSize = 48.dp

/**
 * Placeholder de erro de tela inteira: ícone, título, mensagem e, com `onRetry`, um botão "Tentar de
 * novo". Use quando a tela não carregou.
 */
@Composable
public fun DSErrorState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = DSIcons.ExclamationTriangle,
    retryTitle: String? = stringResource(R.string.dskit_try_again),
    onRetry: (() -> Unit)? = null
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
                tint = DSTheme.colors.errorHigh,
                modifier = Modifier.size(ErrorIconSize)
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

        if (onRetry != null && retryTitle != null) {
            DSSecondaryButton(
                title = retryTitle,
                onClick = onRetry,
                icon = DSIcons.ArrowClockwise,
                modifier = Modifier
                    .padding(top = DSSpacing.sm)
                    .padding(horizontal = DSSpacing.lg)
            )
        }
    }
}

@DSPreviews
@Composable
private fun DSErrorStatePreview() {
    DSPreviewContainer("Error state") {
        DSErrorState(
            title = "Couldn't load",
            message = "Check your connection and try again.",
            onRetry = {}
        )
    }
}
