package io.github.romulomga.dskit

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

/** Identidade do app no tema: o equivalente do `DSTheme` do DSKit iOS. */
@Immutable
public data class DSThemeValues(
    val appName: String,
    /** Cor das ações primárias (`DSPrimaryButton`, seleção). Padrão: o `accent` da marca. */
    val primary: Color,
    val secondary: Color,
    val accent: Color
)

/** Estilo de rótulo e erro dos campos, como o `dsFieldLabelStyle` do DSKit iOS. */
public enum class DSFieldLabelStyle {
    /** Rótulo em footnote secundário; erro com ícone. Padrão. */
    Compact,

    /** Rótulo em subheadline negrito na cor primária; erro em itálico. */
    Prominent
}

public val LocalDSColors: androidx.compose.runtime.ProvidableCompositionLocal<DSColors> =
    staticCompositionLocalOf { DSColorTokens.Light }

public val LocalDSTextStyles: androidx.compose.runtime.ProvidableCompositionLocal<DSTextStyles> =
    staticCompositionLocalOf { DSTextStyles.create() }

public val LocalDSThemeValues: androidx.compose.runtime.ProvidableCompositionLocal<DSThemeValues> =
    staticCompositionLocalOf {
        DSThemeValues(
            appName = "MicroTool",
            primary = DSColorTokens.Light.accent,
            secondary = DSColorTokens.Light.accent,
            accent = DSColorTokens.Light.accent
        )
    }

/**
 * Quantas superfícies do DSKit envolvem o ponto atual. `0` é o canvas. Componentes que pintam superfície
 * somam um para o conteúdo (`DSSurfaceContainer`), então um campo dentro de um card fica em
 * `surfaceElevated` sem sumir superfície sobre superfície.
 */
public val LocalDSSurfaceLevel: androidx.compose.runtime.ProvidableCompositionLocal<Int> = compositionLocalOf { 0 }

/** Liga e desliga a vibração dos componentes do DSKit na subárvore. */
public val LocalDSHapticsEnabled: androidx.compose.runtime.ProvidableCompositionLocal<Boolean> =
    compositionLocalOf { true }

/**
 * `true` quando o usuário ligou "Remover animações" (escala de animação 0). Componentes trocam movimento
 * por crossfade ou por nada, como o Reduce Motion no iOS.
 */
public val LocalDSReduceMotion: androidx.compose.runtime.ProvidableCompositionLocal<Boolean> =
    compositionLocalOf { false }

public val LocalDSFieldLabelStyle: androidx.compose.runtime.ProvidableCompositionLocal<DSFieldLabelStyle> =
    compositionLocalOf { DSFieldLabelStyle.Compact }

/**
 * Raiz do DSKit: escolhe a paleta clara ou escura, aplica a marca e a família de fonte do app e monta um
 * `MaterialTheme` com os mesmos tokens, para que componentes Material (seletor de data, bottom sheet,
 * snackbar) herdem cor e fonte.
 *
 * ```
 * DSTheme(appName = "Homer", brand = HomerBrand, fontFamily = Raleway) { App() }
 * ```
 */
@Composable
public fun DSTheme(
    appName: String = "MicroTool",
    brand: DSBrand = DSBrand.Default,
    fontFamily: FontFamily? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    primary: Color? = null,
    secondary: Color? = null,
    hapticsEnabled: Boolean = true,
    fieldLabelStyle: DSFieldLabelStyle = DSFieldLabelStyle.Compact,
    content: @Composable () -> Unit
) {
    val colors = remember(brand, darkTheme) {
        (if (darkTheme) DSColorTokens.Dark else DSColorTokens.Light).withBrand(brand, darkTheme)
    }
    val textStyles = remember(fontFamily) { DSTextStyles.create(fontFamily) }
    val values = DSThemeValues(
        appName = appName,
        primary = primary ?: colors.accent,
        secondary = secondary ?: colors.accent,
        accent = colors.accent
    )

    CompositionLocalProvider(
        LocalDSColors provides colors,
        LocalDSTextStyles provides textStyles,
        LocalDSThemeValues provides values,
        LocalDSHapticsEnabled provides hapticsEnabled,
        LocalDSReduceMotion provides rememberReduceMotion(),
        LocalDSFieldLabelStyle provides fieldLabelStyle
    ) {
        MaterialTheme(
            colorScheme = colors.toColorScheme(darkTheme, values.primary),
            typography = textStyles.toMaterialTypography(),
            content = content
        )
    }
}

/** Acesso ao tema atual, como `MaterialTheme.colorScheme`: `DSTheme.colors.surface`, `DSTheme.primary`. */
public object DSTheme {
    public val colors: DSColors
        @Composable @ReadOnlyComposable
        get() = LocalDSColors.current
    public val typography: DSTextStyles
        @Composable @ReadOnlyComposable
        get() = LocalDSTextStyles.current
    public val values: DSThemeValues
        @Composable @ReadOnlyComposable
        get() = LocalDSThemeValues.current
    public val appName: String
        @Composable @ReadOnlyComposable
        get() = LocalDSThemeValues.current.appName
    public val primary: Color
        @Composable @ReadOnlyComposable
        get() = LocalDSThemeValues.current.primary
    public val secondary: Color
        @Composable @ReadOnlyComposable
        get() = LocalDSThemeValues.current.secondary
    public val surfaceLevel: Int
        @Composable @ReadOnlyComposable
        get() = LocalDSSurfaceLevel.current
    public val reduceMotion: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalDSReduceMotion.current
    public val hapticsEnabled: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalDSHapticsEnabled.current
}

/** Marca o conteúdo como dentro de uma superfície: os filhos leem um nível de aninhamento a mais. */
@Composable
public fun DSSurfaceContainer(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDSSurfaceLevel provides LocalDSSurfaceLevel.current + 1, content = content)
}

/**
 * Canvas agrupado, o visual de Ajustes: pinta `backgroundGrouped` e começa o nível de superfície em 1, então
 * cards e campos ficam em `surfaceElevated` (células brancas sobre cinza claro).
 */
@Composable
public fun DSGroupedCanvas(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.background(DSTheme.colors.backgroundGrouped)) {
        CompositionLocalProvider(LocalDSSurfaceLevel provides 1, content = content)
    }
}

@Composable
private fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

private fun DSColors.toColorScheme(dark: Boolean, primary: Color): ColorScheme {
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onAccent,
        primaryContainer = appColorLight,
        secondary = accentSecondary,
        onSecondary = onAccent,
        tertiary = accentSecondary,
        background = background,
        onBackground = onSurfaceHigh,
        surface = background,
        onSurface = onSurfaceHigh,
        surfaceVariant = surface,
        onSurfaceVariant = onSurfaceMedium,
        surfaceContainerLowest = background,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surfaceElevated,
        surfaceContainerHighest = surfaceElevated,
        outline = border,
        outlineVariant = hairline,
        error = errorHigh,
        onError = Color.White,
        errorContainer = errorLight
    )
}

private fun DSTextStyles.toMaterialTypography(): Typography = Typography(
    displayLarge = largeTitle,
    displayMedium = largeTitle,
    displaySmall = title,
    headlineLarge = largeTitle,
    headlineMedium = title,
    headlineSmall = title2,
    titleLarge = title3,
    titleMedium = headline,
    titleSmall = subheadline,
    bodyLarge = body,
    bodyMedium = subheadline,
    bodySmall = footnote,
    labelLarge = subheadline,
    labelMedium = caption,
    labelSmall = caption2
)
