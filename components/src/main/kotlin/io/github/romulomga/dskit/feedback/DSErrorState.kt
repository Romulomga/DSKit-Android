package io.github.romulomga.dskit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.romulomga.dskit.components.R

/**
 * Estado de erro de uma tela que não carregou: ícone no contêiner de erro, título, explicação e o botão de
 * tentar de novo. Para um erro que não toma a tela, use um snackbar.
 */
@Composable
public fun DSErrorState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = DSIcons.CloudOff,
    retryText: String = stringResource(R.string.dskit_try_again),
    onRetry: (() -> Unit)? = null
) {
    DSStateLayout(
        icon = icon,
        iconContainer = DSTheme.colors.errorContainer,
        iconColor = DSTheme.colors.onErrorContainer,
        title = title,
        message = message,
        modifier = modifier
    ) {
        if (onRetry != null) DSButton(retryText, onClick = onRetry, icon = DSIcons.Refresh)
    }
}

@DSPreviews
@Composable
private fun DSErrorStatePreview() {
    DSPreviewContainer("Error state") {
        DSErrorState(title = "Couldn't load", message = "Check your connection and try again.", onRetry = {})
    }
}
