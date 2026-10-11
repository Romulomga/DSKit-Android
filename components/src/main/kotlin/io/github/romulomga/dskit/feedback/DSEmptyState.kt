package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Estado vazio: a lista não tem nada ainda. Ícone num círculo tonal, título, explicação e, se houver, a
 * ação que resolve ("Publicar imóvel"). Centralizado e com largura de leitura.
 */
@Composable
public fun DSEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    DSStateLayout(
        icon = icon,
        iconContainer = DSTheme.colors.secondaryContainer,
        iconColor = DSTheme.colors.onSecondaryContainer,
        title = title,
        message = message,
        modifier = modifier
    ) {
        if (actionText != null && onAction != null) DSTonalButton(actionText, onClick = onAction)
    }
}

/** O desenho comum aos estados vazio e de erro. O texto é lido junto; o botão continua tocável à parte. */
@Composable
internal fun DSStateLayout(
    icon: ImageVector,
    iconContainer: Color,
    iconColor: Color,
    title: String,
    message: String,
    modifier: Modifier,
    action: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = StateMaxWidth).padding(vertical = DSSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)
        ) {
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {},
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)
            ) {
                Surface(shape = CircleShape, color = iconContainer, contentColor = iconColor) {
                    Icon(icon, contentDescription = null, modifier = Modifier.padding(DSSpacing.lg).size(32.dp))
                }

                Text(title, style = DSTheme.typography.titleLarge, color = DSTheme.colors.onSurface, textAlign = TextAlign.Center)

                Text(message, style = DSTheme.typography.bodyMedium, color = DSTheme.colors.onSurfaceVariant, textAlign = TextAlign.Center)
            }

            action()
        }
    }
}

private val StateMaxWidth = 360.dp

@DSPreviews
@Composable
private fun DSEmptyStatePreview() {
    DSPreviewContainer("Empty state") {
        DSEmptyState(
            icon = DSIcons.Inbox,
            title = "No posts yet",
            message = "Publish a property to start finding partners.",
            actionText = "Publish",
            onAction = {}
        )
    }
}
