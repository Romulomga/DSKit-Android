package io.github.romulomga.dskit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Barra de progresso linear do Material 3. Com [progress], mostra quanto já foi (envio de fotos, passo de
 * um cadastro); sem ele, é indeterminada (um carregamento curto no topo de uma lista).
 *
 * @param progress de 0 a 1, ou `null` para indeterminada.
 */
@Composable
public fun DSLinearProgress(modifier: Modifier = Modifier, progress: Float? = null) {
    if (progress == null) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    } else {
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = modifier.fillMaxWidth())
    }
}

@DSPreviews
@Composable
private fun DSLinearProgressPreview() {
    DSPreviewContainer("Linear progress") {
        DSLinearProgress(progress = 0.4f)
    }
}
