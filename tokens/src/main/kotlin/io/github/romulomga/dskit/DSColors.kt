package io.github.romulomga.dskit

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * A marca do app: os esquemas de cor do Material 3, claro e escuro, e as cores de status.
 *
 * Gere os esquemas no [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
 * a partir da cor da marca e exporte para Jetpack Compose: o arquivo `Color.kt`/`Theme.kt` exportado traz o
 * `lightScheme` e o `darkScheme` prontos, com todos os papéis (primary, primaryContainer, surfaceContainer…)
 * calculados em tons que passam no contraste. As cores de status entram como "custom colors" do mesmo builder.
 *
 * O mesmo arquivo traz os esquemas de contraste médio e alto (`mediumContrastLightColorScheme`…). Passe-os
 * para o `DSTheme` seguir o "Contraste de cor" do Android 14+; sem eles, vale o esquema padrão.
 *
 * ```
 * val HomerBrand = DSBrand(light = lightScheme, dark = darkScheme)
 * DSTheme(brand = HomerBrand) { App() }
 * ```
 */
@Immutable
public data class DSBrand(
    val light: ColorScheme,
    val dark: ColorScheme,
    val lightStatus: DSStatusColors = DSStatusColors.Light,
    val darkStatus: DSStatusColors = DSStatusColors.Dark,
    val lightMediumContrast: ColorScheme? = null,
    val darkMediumContrast: ColorScheme? = null,
    val lightHighContrast: ColorScheme? = null,
    val darkHighContrast: ColorScheme? = null
) {
    /** O esquema para o modo e o contraste pedidos; sem o de alto, cai no médio, e sem o médio, no padrão. */
    public fun colorScheme(darkTheme: Boolean, contrast: DSContrast): ColorScheme {
        val standard = if (darkTheme) dark else light
        val medium = (if (darkTheme) darkMediumContrast else lightMediumContrast) ?: standard
        val high = (if (darkTheme) darkHighContrast else lightHighContrast) ?: medium

        return when (contrast) {
            DSContrast.Standard -> standard
            DSContrast.Medium -> medium
            DSContrast.High -> high
        }
    }

    public companion object {
        /** O esquema base do Material 3, para previews e para começar antes da marca existir. */
        public val Default: DSBrand = DSBrand(light = lightColorScheme(), dark = darkColorScheme())
    }
}

/** O nível de contraste de cor: o padrão, o médio e o alto do Material 3 (Configurações > Acessibilidade > Contraste). */
public enum class DSContrast {
    Standard,
    Medium,
    High
}

/**
 * Um papel de cor no formato do Material: a cor, o conteúdo sobre ela, o contêiner suave e o conteúdo sobre
 * o contêiner. É o mesmo formato das "custom colors" do Material Theme Builder.
 */
@Immutable
public data class DSColorRole(val color: Color, val onColor: Color, val container: Color, val onContainer: Color)

/**
 * Cores de status que o Material não tem (ele só tem `error`): sucesso, aviso e informação. Use o contêiner
 * para fundos suaves (banner, selo) e a cor cheia para ícones, textos curtos e superfícies fortes (snackbar).
 */
@Immutable
public data class DSStatusColors(val success: DSColorRole, val warning: DSColorRole, val info: DSColorRole) {
    public companion object {
        /** Tons 40/100/90/10 de verde, âmbar e azul, como o Theme Builder gera para o tema claro. */
        public val Light: DSStatusColors = DSStatusColors(
            success = DSColorRole(Color(0xFF2E6B30), Color(0xFFFFFFFF), Color(0xFFB0F2AB), Color(0xFF002106)),
            warning = DSColorRole(Color(0xFF795900), Color(0xFFFFFFFF), Color(0xFFFFDEA3), Color(0xFF261900)),
            info = DSColorRole(Color(0xFF0061A4), Color(0xFFFFFFFF), Color(0xFFD1E4FF), Color(0xFF001D36))
        )

        /** Tons 80/20/30/90, os mesmos papéis no tema escuro. */
        public val Dark: DSStatusColors = DSStatusColors(
            success = DSColorRole(Color(0xFF95D691), Color(0xFF00390D), Color(0xFF13521A), Color(0xFFB0F2AB)),
            warning = DSColorRole(Color(0xFFF5BF48), Color(0xFF402D00), Color(0xFF5C4200), Color(0xFFFFDEA3)),
            info = DSColorRole(Color(0xFF9ECAFF), Color(0xFF003258), Color(0xFF00497D), Color(0xFFD1E4FF))
        )
    }
}
