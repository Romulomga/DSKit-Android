package io.github.romulomga.dskit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import io.github.romulomga.dskit.components.R

/**
 * Carregamento que bloqueia a tela: o véu (`scrim`) do Material escurece tudo, um painel no centro mostra o
 * indicador e a mensagem, e nada começa duas vezes ("Publicando…", gerando um PDF). Para carregar o conteúdo de
 * uma tela, prefira [DSLoadingState] ou o esqueleto.
 *
 * Abre numa janela própria, por cima de tudo, onde quer que seja chamado: bloqueia o toque, o TalkBack, o
 * teclado e o voltar. Um véu desenhado na mesma janela só pararia o dedo, e quem usa TalkBack ainda alcançaria o
 * "Publicar" por baixo. O TalkBack anuncia a [message] (ou "Carregando…").
 */
@Composable
public fun DSLoadingOverlay(visible: Boolean, modifier: Modifier = Modifier, message: String? = null) {
    if (!visible) return

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        // A janela do diálogo escurece por conta própria; o véu do DSKit é quem escurece, uma vez só.
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { window?.setDimAmount(0f) }

        val appear = remember { MutableTransitionState(false).apply { targetState = true } }

        AnimatedVisibility(visibleState = appear, enter = fadeIn(DSMotion.short())) {
            LoadingVeil(message = message, modifier = modifier)
        }
    }
}

/** O véu e o painel, sem a janela: o que o [DSLoadingOverlay] mostra (e o que os previews e testes desenham). */
@Composable
internal fun LoadingVeil(message: String?, modifier: Modifier = Modifier) {
    val label = message ?: stringResource(R.string.dskit_loading)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DSTheme.colors.scrim.copy(alpha = ScrimAlpha))
            .clearAndSetSemantics {
                contentDescription = label
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(shape = DSTheme.shapes.extraLarge, color = DSTheme.colors.surfaceContainerHigh) {
            Column(
                modifier = Modifier.padding(DSSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)
            ) {
                CircularProgressIndicator()

                if (message != null) {
                    Text(message, style = DSTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

/** A opacidade do véu dos diálogos do Material. */
private const val ScrimAlpha = 0.32f

@DSPreviews
@Composable
private fun DSLoadingOverlayPreview() {
    DSPreviewContainer("Loading overlay") {
        Box(Modifier.height(240.dp)) {
            DSCard { Text("Form behind the veil") }

            LoadingVeil(message = "Publishing…")
        }
    }
}
