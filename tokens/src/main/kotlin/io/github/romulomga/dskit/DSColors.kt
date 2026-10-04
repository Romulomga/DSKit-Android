package io.github.romulomga.dskit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Cores semânticas do DSKit, os mesmos tokens do DSKit iOS (`Color.surface`, `Color.onSurfaceHigh`…).
 *
 * Leia sempre pelo tema, `DSTheme.colors.surface`, nunca por `DSColorTokens` direto: o tema escolhe
 * a paleta clara ou escura e aplica as cores de marca do app.
 */
@Immutable
public data class DSColors(
    /** Fundo de tela. Claro #FFFFFF, escuro #121212. */
    val background: Color,
    /** Canvas agrupado, o fundo de Ajustes. Combine com células em `surfaceElevated`. */
    val backgroundGrouped: Color,
    /** Superfície de cards e campos sobre o fundo. */
    val surface: Color,
    /** Um degrau acima de `surface`: célula no canvas agrupado, campo dentro de card. */
    val surfaceElevated: Color,
    val onSurfaceHigh: Color,
    /** Texto secundário legível. Para leitura, nunca `onSurfaceLight`. */
    val onSurfaceMedium: Color,
    val onSurfaceLight: Color,
    /** Divisória em força total. Para o contorno em repouso de uma superfície, use `hairline`. */
    val border: Color,
    /** Contorno discreto de cards e campos: linha escura no claro, linha clara a 10 % no escuro. */
    val hairline: Color,
    val appColorLight: Color,
    /** Cor de marca. O app define pelo `DSBrand` do `DSTheme`. */
    val accent: Color,
    val accentSecondary: Color,
    /** Texto e ícone sobre `accent`/`accentSecondary`. */
    val onAccent: Color,
    val successLight: Color,
    val successHigh: Color,
    val warningLight: Color,
    val warningHigh: Color,
    val infoLight: Color,
    val infoHigh: Color,
    val errorLight: Color,
    val errorHigh: Color,
    val disabled: Color,
    val onDisabled: Color,
) {
    /**
     * Superfície para um nível de aninhamento (`LocalDSSurfaceLevel`): o nível 0 está no canvas e usa
     * `surface`; qualquer nível acima usa `surfaceElevated`.
     */
    public fun surface(level: Int): Color = if (level > 0) surfaceElevated else surface
}

/**
 * Cores de marca do app, nas duas aparências. Equivale aos colorsets `Accent`, `AccentSecondary` e
 * `OnAccent` que o app iOS põe no próprio asset catalog.
 */
@Immutable
public data class DSBrand(
    val accent: Color,
    val accentDark: Color = accent,
    val accentSecondary: Color,
    val accentSecondaryDark: Color = accentSecondary,
    val onAccent: Color = Color(0xFFFFFFFF),
    val onAccentDark: Color = onAccent,
) {
    public companion object {
        /** As cores padrão do DSKit (indigo e rosa do sistema da Apple). */
        public val Default: DSBrand = DSBrand(
            accent = DSColorTokens.Light.accent,
            accentDark = DSColorTokens.Dark.accent,
            accentSecondary = DSColorTokens.Light.accentSecondary,
            accentSecondaryDark = DSColorTokens.Dark.accentSecondary,
            onAccent = DSColorTokens.Light.onAccent,
            onAccentDark = DSColorTokens.Dark.onAccent,
        )
    }
}

internal fun DSColors.withBrand(brand: DSBrand, dark: Boolean): DSColors = copy(
    accent = if (dark) brand.accentDark else brand.accent,
    accentSecondary = if (dark) brand.accentSecondaryDark else brand.accentSecondary,
    onAccent = if (dark) brand.onAccentDark else brand.onAccent,
)
