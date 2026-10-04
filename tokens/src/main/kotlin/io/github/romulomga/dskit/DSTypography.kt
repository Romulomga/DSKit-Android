package io.github.romulomga.dskit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Papéis tipográficos do DSKit, com os tamanhos dos text styles do iOS no tamanho padrão.
 *
 * Os tamanhos estão em `sp`, então escalam com a fonte do sistema (até 200 %, com escala não linear
 * no Android 14+), como o Dynamic Type. Use `DSTypography.headline` em vez de `TextStyle(fontSize = …)`.
 */
@Immutable
public data class DSTextStyles(
    val largeTitle: TextStyle,
    val title: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val headline: TextStyle,
    val body: TextStyle,
    val callout: TextStyle,
    val subheadline: TextStyle,
    val footnote: TextStyle,
    val caption: TextStyle,
    val caption2: TextStyle,
    /** Destaque do `DSResultCard`. */
    val resultPrimary: TextStyle,
    val resultSecondary: TextStyle,
    val fontFamily: FontFamily
) {
    public companion object {
        /**
         * Escala com a família do app. Sem família, usa a fonte do sistema (Roboto), o equivalente da
         * San Francisco no DSKit iOS.
         */
        public fun create(fontFamily: FontFamily? = null): DSTextStyles {
            val family = fontFamily ?: FontFamily.Default

            fun style(size: Int, lineHeight: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
                fontFamily = family,
                fontWeight = weight,
                fontSize = size.sp,
                lineHeight = lineHeight.sp
            )

            return DSTextStyles(
                largeTitle = style(34, 41, FontWeight.Bold),
                title = style(28, 34, FontWeight.SemiBold),
                title2 = style(22, 28, FontWeight.SemiBold),
                title3 = style(20, 25, FontWeight.SemiBold),
                headline = style(17, 22, FontWeight.SemiBold),
                body = style(17, 22),
                callout = style(16, 21),
                subheadline = style(15, 20),
                footnote = style(13, 18),
                caption = style(12, 16),
                caption2 = style(11, 13),
                resultPrimary = style(34, 41, FontWeight.Bold),
                resultSecondary = style(22, 28, FontWeight.SemiBold),
                fontFamily = family
            )
        }
    }
}

/** Acesso aos papéis tipográficos do tema atual: `DSTypography.headline`, `DSTypography.body`… */
public object DSTypography {
    public val largeTitle: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.largeTitle
    public val title: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.title
    public val title2: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.title2
    public val title3: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.title3
    public val headline: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.headline
    public val body: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.body
    public val callout: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.callout
    public val subheadline: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.subheadline
    public val footnote: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.footnote
    public val caption: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.caption
    public val caption2: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.caption2
    public val resultPrimary: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.resultPrimary
    public val resultSecondary: TextStyle
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current.resultSecondary

    /**
     * Texto de exibição gigante (afirmação, resposta de uma palavra) em tamanho **fixo**: não escala com
     * a fonte do sistema, porque o layout não comporta crescer. Para texto que escala, use `largeTitle`.
     */
    @Composable
    @ReadOnlyComposable
    public fun display(size: Dp = 44.dp, weight: FontWeight = FontWeight.Bold): TextStyle {
        val density = LocalDensity.current
        val fixed = (size.value / density.fontScale).sp
        return TextStyle(
            fontFamily = LocalDSTextStyles.current.fontFamily,
            fontWeight = weight,
            fontSize = fixed,
            lineHeight = fixed * 1.2f
        )
    }
}
