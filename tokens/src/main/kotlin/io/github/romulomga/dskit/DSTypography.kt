package io.github.romulomga.dskit

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

/**
 * A escala de tipo do Material 3 (display, headline, title, body, label) com a família de fonte do app.
 * Tamanhos, pesos e entrelinhas são os do Material; só a família muda. Em `sp`, então escala com a fonte do
 * sistema.
 */
public object DSTypography {
    /** A escala do Material com [fontFamily] em todos os estilos; `null` mantém a fonte do sistema (Roboto). */
    public fun create(fontFamily: FontFamily? = null): Typography {
        val base = Typography()
        if (fontFamily == null) return base

        fun TextStyle.withFamily() = copy(fontFamily = fontFamily)

        return Typography(
            displayLarge = base.displayLarge.withFamily(),
            displayMedium = base.displayMedium.withFamily(),
            displaySmall = base.displaySmall.withFamily(),
            headlineLarge = base.headlineLarge.withFamily(),
            headlineMedium = base.headlineMedium.withFamily(),
            headlineSmall = base.headlineSmall.withFamily(),
            titleLarge = base.titleLarge.withFamily(),
            titleMedium = base.titleMedium.withFamily(),
            titleSmall = base.titleSmall.withFamily(),
            bodyLarge = base.bodyLarge.withFamily(),
            bodyMedium = base.bodyMedium.withFamily(),
            bodySmall = base.bodySmall.withFamily(),
            labelLarge = base.labelLarge.withFamily(),
            labelMedium = base.labelMedium.withFamily(),
            labelSmall = base.labelSmall.withFamily()
        )
    }
}
