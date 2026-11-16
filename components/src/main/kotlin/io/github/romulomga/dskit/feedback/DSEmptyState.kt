package io.github.romulomga.dskit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
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

/**
 * Estado vazio com a ilustração do próprio app (um drawable nas cores dele, sem tinta) no lugar do ícone: 120 dp de
 * altura, 80 dp com a fonte nos tamanhos grandes (escala 1,5 ou mais), para o texto não perder espaço. É decorativa:
 * o TalkBack lê o título e a explicação. Gêmeo do `DSEmptyState(illustration:)` do DSKit iOS.
 */
@Composable
public fun DSEmptyState(
    illustration: Painter,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    val large = LocalDensity.current.fontScale >= LargeFontScale

    DSStateLayout(
        artwork = {
            Image(
                painter = illustration,
                contentDescription = null,
                modifier = Modifier.height(if (large) CompactIllustrationHeight else IllustrationHeight)
            )
        },
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
    announce: Boolean = false,
    action: @Composable () -> Unit
) {
    DSStateLayout(
        artwork = {
            Surface(shape = CircleShape, color = iconContainer, contentColor = iconColor) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(DSSpacing.lg).size(32.dp))
            }
        },
        title = title,
        message = message,
        modifier = modifier,
        announce = announce,
        action = action
    )
}

@Composable
internal fun DSStateLayout(
    artwork: @Composable () -> Unit,
    title: String,
    message: String,
    modifier: Modifier,
    announce: Boolean = false,
    action: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = StateMaxWidth).padding(vertical = DSSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)
        ) {
            Column(
                // `announce`: a tela trocou para este estado sem o usuário pedir (um erro), e o TalkBack precisa ouvir.
                modifier = Modifier.semantics(mergeDescendants = true) {
                    if (announce) liveRegion = LiveRegionMode.Polite
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)
            ) {
                artwork()

                Text(
                    title,
                    style = DSTheme.typography.titleLarge,
                    color = DSTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() }
                )

                Text(message, style = DSTheme.typography.bodyMedium, color = DSTheme.colors.onSurfaceVariant, textAlign = TextAlign.Center)
            }

            action()
        }
    }
}

private val StateMaxWidth = 360.dp
private val IllustrationHeight = 120.dp
private val CompactIllustrationHeight = 80.dp
private const val LargeFontScale = 1.5f

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
