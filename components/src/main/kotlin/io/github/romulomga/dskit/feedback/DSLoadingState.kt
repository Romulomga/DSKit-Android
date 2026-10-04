package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Indicador grande, o `controlSize(.large)` do iOS. */
private val LoadingIndicatorSize = 36.dp

/** Indicador de carregamento centralizado, com uma legenda opcional. Use enquanto a tela carrega. */
@Composable
public fun DSLoadingState(modifier: Modifier = Modifier, message: String? = null) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DSSpacing.xl)
            .semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DSSpacing.md)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(LoadingIndicatorSize),
            color = DSTheme.colors.onSurfaceMedium,
            strokeWidth = 3.dp
        )

        if (message != null) {
            Text(
                text = message,
                style = DSTypography.subheadline,
                color = DSTheme.colors.onSurfaceMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@DSPreviews
@Composable
private fun DSLoadingStatePreview() {
    DSPreviewContainer("Loading state") {
        DSLoadingState(message = "Drawing winner…")
    }
}
