package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** O tom de um snackbar do DSKit. */
public enum class DSSnackbarStyle {
    /** A superfície inversa do Material: avisos neutros ("Rascunho salvo"). Padrão. */
    Default,

    /** Cor de sucesso: uma ação que deu certo e merece destaque ("Imóvel publicado"). */
    Success,

    /** Cor de erro: algo falhou e não toma a tela ("Sem conexão"). */
    Error
}

/** O conteúdo de um snackbar do DSKit: o `SnackbarVisuals` do Material com o tom. */
public data class DSSnackbarVisuals(
    override val message: String,
    val style: DSSnackbarStyle = DSSnackbarStyle.Default,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
) : SnackbarVisuals

/**
 * Mostra um snackbar do DSKit neste host e espera ele sumir. Devolve se o usuário tocou na ação.
 *
 * ```
 * scope.launch { snackbarHostState.showDSSnackbar("Sem conexão", DSSnackbarStyle.Error, actionLabel = "Tentar") }
 * ```
 */
public suspend fun SnackbarHostState.showDSSnackbar(
    message: String,
    style: DSSnackbarStyle = DSSnackbarStyle.Default,
    actionLabel: String? = null,
    withDismissAction: Boolean = false
): SnackbarResult = showSnackbar(DSSnackbarVisuals(message, style, actionLabel, withDismissAction))

/**
 * Onde os snackbars aparecem, embaixo da tela e acima do FAB, com o tom de cada um. O [DSScreen] já o põe
 * quando recebe um `snackbarHostState`; use direto só num layout próprio.
 */
@Composable
public fun DSSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data -> DSSnackbar(data) }
}

@Composable
private fun DSSnackbar(data: SnackbarData) {
    val style = (data.visuals as? DSSnackbarVisuals)?.style ?: DSSnackbarStyle.Default
    val colors = DSTheme.colors
    val success = DSTheme.status.success

    when (style) {
        DSSnackbarStyle.Default -> Snackbar(data)

        DSSnackbarStyle.Success -> Snackbar(
            snackbarData = data,
            containerColor = success.color,
            contentColor = success.onColor,
            actionColor = success.onColor,
            dismissActionContentColor = success.onColor
        )

        DSSnackbarStyle.Error -> Snackbar(
            snackbarData = data,
            containerColor = colors.error,
            contentColor = colors.onError,
            actionColor = colors.onError,
            dismissActionContentColor = colors.onError
        )
    }
}

@DSPreviews
@Composable
private fun DSSnackbarPreview() {
    DSPreviewContainer("Snackbar") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSSnackbarStyle.entries.forEach { style ->
                DSSnackbar(PreviewSnackbarData(DSSnackbarVisuals("Property published", style, actionLabel = "View")))
            }
        }
    }
}

/** Um `SnackbarData` parado, para previews e screenshots. */
internal class PreviewSnackbarData(override val visuals: SnackbarVisuals) : SnackbarData {
    override fun performAction() {}

    override fun dismiss() {}
}

/** O snackbar sem host, para screenshots. */
@Composable
internal fun DSSnackbarForPreview(visuals: DSSnackbarVisuals) {
    DSSnackbar(PreviewSnackbarData(visuals))
}
