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

/**
 * Moldura padrão de preview e de screenshot: tema do DSKit, fundo `surface` e uma legenda. Passe a marca do
 * app em [brand] para ver o componente com as cores dele.
 */
@Composable
public fun DSPreviewContainer(
    title: String,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    brand: DSBrand = DSBrand.Default,
    contrast: DSContrast = DSContrast.Standard,
    content: @Composable () -> Unit
) {
    DSTheme(brand = brand, darkTheme = darkTheme, contrast = contrast) {
        Column(
            modifier = modifier.background(DSTheme.colors.surface).padding(DSSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.md)
        ) {
            Text(title, style = DSTheme.typography.labelMedium, color = DSTheme.colors.onSurfaceVariant)

            content()
        }
    }
}
