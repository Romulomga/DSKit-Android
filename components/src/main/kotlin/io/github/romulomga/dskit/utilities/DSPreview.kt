package io.github.romulomga.dskit

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/** Os três modos que todo componente confere: claro, escuro e fonte a 200 %. */
@Preview(name = "Claro", showBackground = true)
@Preview(name = "Escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Fonte 200 %", showBackground = true, fontScale = 2f)
public annotation class DSPreviews

/** Moldura padrão de preview e de screenshot: tema do DSKit, fundo e uma legenda. */
@Composable
public fun DSPreviewContainer(
    title: String,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    DSTheme(darkTheme = darkTheme) {
        Column(
            modifier = modifier.background(DSTheme.colors.background).padding(DSSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.md),
        ) {
            Text(title, style = DSTypography.footnote, color = DSTheme.colors.onSurfaceMedium)
            content()
        }
    }
}
