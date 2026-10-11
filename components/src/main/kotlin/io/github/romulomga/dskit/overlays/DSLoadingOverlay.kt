package io.github.romulomga.dskit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.components.R

/**
 * Carregamento que bloqueia a tela: o véu (`scrim`) do Material escurece tudo, um painel no centro mostra o
 * indicador e a mensagem, e os toques param nele, para nada começar duas vezes ("Publicando…", gerando um
 * PDF). Para carregar o conteúdo de uma tela, prefira [DSLoadingState] ou o esqueleto.
 *
 * Ponha por cima da tela, num `Box` que ocupa tudo. O TalkBack anuncia a [message] (ou "Carregando…").
 */
@Composable
public fun DSLoadingOverlay(visible: Boolean, modifier: Modifier = Modifier, message: String? = null) {
    val label = message ?: stringResource(R.string.dskit_loading)

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(DSMotion.short()),
        exit = fadeOut(DSMotion.short())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DSTheme.colors.scrim.copy(alpha = ScrimAlpha))
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
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
}

/** A opacidade do véu dos diálogos do Material. */
private const val ScrimAlpha = 0.32f

@DSPreviews
@Composable
private fun DSLoadingOverlayPreview() {
    DSPreviewContainer("Loading overlay") {
        Box(Modifier.height(240.dp)) {
            DSCard { Text("Form behind the veil") }

            DSLoadingOverlay(visible = true, message = "Publishing…")
        }
    }
}
