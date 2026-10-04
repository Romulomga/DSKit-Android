package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

/** Pilha vertical com título: agrupa conteúdo relacionado sob um só cabeçalho. */
@Composable
public fun DSSection(modifier: Modifier = Modifier, title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
        if (title != null) {
            Text(
                text = title,
                style = DSTypography.headline,
                color = DSTheme.colors.onSurfaceHigh,
                modifier = Modifier.semantics { heading() }
            )
        }

        content()
    }
}

@DSPreviews
@Composable
private fun DSSectionPreview() {
    DSPreviewContainer("Section") {
        DSSection(title = "Recent") {
            DSCard { Text("Item 1", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }

            DSCard { Text("Item 2", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }
        }
    }
}
