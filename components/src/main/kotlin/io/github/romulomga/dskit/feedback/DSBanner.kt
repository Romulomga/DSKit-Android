package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.components.R

/** O tom do [DSBanner]: as cores de status do [DSTheme] e o ícone de cada um. */
public enum class DSBannerStyle {
    Info,
    Success,
    Warning,
    Error
}

/**
 * Aviso que fica no conteúdo até ser resolvido ou dispensado: "Seu plano vence em 3 dias", "Sem conexão,
 * mostrando dados salvos". Usa o contêiner da cor de status do [DSTheme] (no erro, o `errorContainer` do Material), o ícone dela e, se houver, uma ação e um "x".
 *
 * Para confirmar algo que acabou de acontecer, use o snackbar ([showDSSnackbar]); para pedir uma decisão
 * antes de seguir, o [DSAlertDialog]. O Material 3 não tem banner no Compose; este segue o desenho do
 * Material (contêiner tonal, texto e ação no tom do contêiner).
 */
@Composable
public fun DSBanner(
    message: String,
    modifier: Modifier = Modifier,
    style: DSBannerStyle = DSBannerStyle.Info,
    title: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    val (container, content, icon) = bannerColors(style)

    Surface(modifier = modifier.fillMaxWidth(), shape = DSTheme.shapes.medium, color = container, contentColor = content) {
        Row(
            modifier = Modifier.padding(start = DSSpacing.lg, top = DSSpacing.md, bottom = DSSpacing.md, end = DSSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(top = DSSpacing.xxs))

            Column(modifier = Modifier.weight(1f).padding(end = if (onDismiss == null) DSSpacing.md else 0.dp)) {
                if (title != null) {
                    Text(title, style = DSTheme.typography.titleSmall)
                }

                Text(message, style = DSTheme.typography.bodyMedium)

                if (actionText != null && onAction != null) {
                    TextButton(
                        onClick = onAction,
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current)
                    ) {
                        Text(actionText)
                    }
                }
            }

            if (onDismiss != null) {
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.Top)) {
                    Icon(DSIcons.Close, contentDescription = stringResource(R.string.dskit_dismiss))
                }
            }
        }
    }
}

private data class BannerColors(val container: Color, val content: Color, val icon: ImageVector)

@Composable
private fun bannerColors(style: DSBannerStyle): BannerColors = when (style) {
    DSBannerStyle.Info -> BannerColors(DSTheme.status.info.container, DSTheme.status.info.onContainer, DSIcons.Info)
    DSBannerStyle.Success -> BannerColors(DSTheme.status.success.container, DSTheme.status.success.onContainer, DSIcons.CheckCircle)
    DSBannerStyle.Warning -> BannerColors(DSTheme.status.warning.container, DSTheme.status.warning.onContainer, DSIcons.Warning)
    DSBannerStyle.Error -> BannerColors(DSTheme.colors.errorContainer, DSTheme.colors.onErrorContainer, DSIcons.Error)
}

@DSPreviews
@Composable
private fun DSBannerPreview() {
    DSPreviewContainer("Banner") {
        DSBanner(
            message = "Your plan renews in 3 days.",
            title = "Plan",
            actionText = "Manage",
            onAction = {},
            onDismiss = {}
        )

        DSBanner(message = "You're offline. Showing saved data.", style = DSBannerStyle.Warning)
    }
}
