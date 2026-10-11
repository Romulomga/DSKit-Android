package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign

/**
 * Carregamento de uma tela inteira: o indicador circular do Material e uma legenda opcional. Quando a forma
 * do conteúdo é conhecida, prefira o esqueleto (`Modifier.dsSkeleton`), que não pula quando os dados chegam.
 */
@Composable
public fun DSLoadingState(modifier: Modifier = Modifier, message: String? = null) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DSSpacing.xl)
            .semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)
    ) {
        CircularProgressIndicator()

        if (message != null) {
            Text(message, style = DSTheme.typography.bodyMedium, color = DSTheme.colors.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@DSPreviews
@Composable
private fun DSLoadingStatePreview() {
    DSPreviewContainer("Loading state") {
        DSLoadingState(message = "Loading properties…")
    }
}
