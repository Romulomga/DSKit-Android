package io.github.romulomga.dskit

import android.app.UiModeManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

public val LocalDSStatusColors: ProvidableCompositionLocal<DSStatusColors> = staticCompositionLocalOf { DSStatusColors.Light }

/** Liga e desliga a vibração dos componentes do DSKit na subárvore. */
public val LocalDSHapticsEnabled: ProvidableCompositionLocal<Boolean> = compositionLocalOf { true }

/**
 * `true` quando o usuário ligou "Remover animações" (escala de animação 0). As animações do Compose já
 * respeitam a escala; isto é para o que o componente decide sozinho, como um pulso infinito. Lido quando o
 * tema entra na tela.
 */
public val LocalDSReduceMotion: ProvidableCompositionLocal<Boolean> = compositionLocalOf { false }

/**
 * Raiz do DSKit: um `MaterialTheme` com as cores da marca, a fonte do app e as formas do Material 3. Tudo dentro dele, inclusive componentes do Material que o DSKit não embrulha (`DatePicker`,
 * `DropdownMenu`), já sai com a cara do app.
 *
 * ```
 * DSTheme(brand = HomerBrand, fontFamily = Raleway) { App() }
 * ```
 *
 * @param dynamicColor usa as cores do papel de parede (Material You) no Android 12+; abaixo disso, a marca.
 * As cores de status continuam as da marca. As cores dinâmicas do sistema já seguem o contraste dele.
 * @param contrast o nível de contraste; o padrão segue o "Contraste de cor" do Android 14+ e escolhe o
 * esquema correspondente do [brand].
 * @param shapes as formas do Material (`extraSmall`…`extraLarge`); o padrão é o do Material 3.
 */
@Composable
public fun DSTheme(
    brand: DSBrand = DSBrand.Default,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    contrast: DSContrast = rememberSystemContrast(),
    fontFamily: FontFamily? = null,
    shapes: Shapes = Shapes(),
    hapticsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        else -> brand.colorScheme(darkTheme, contrast)
    }

    val typography = remember(fontFamily) { DSTypography.create(fontFamily) }

    CompositionLocalProvider(
        LocalDSStatusColors provides if (darkTheme) brand.darkStatus else brand.lightStatus,
        LocalDSHapticsEnabled provides hapticsEnabled,
        LocalDSReduceMotion provides rememberReduceMotion()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = typography,
            content = content
        )
    }
}

/**
 * Acesso ao tema atual num lugar só: `DSTheme.colors.primary`, `DSTheme.typography.titleLarge`,
 * `DSTheme.status.success.container`. As três primeiras são as do `MaterialTheme`.
 */
public object DSTheme {
    public val colors: ColorScheme
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    public val typography: Typography
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.typography

    public val shapes: Shapes
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.shapes

    public val status: DSStatusColors
        @Composable @ReadOnlyComposable
        get() = LocalDSStatusColors.current

    public val reduceMotion: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalDSReduceMotion.current

    public val hapticsEnabled: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalDSHapticsEnabled.current
}

/**
 * O "Contraste de cor" do sistema (Android 14+), atualizado quando o usuário muda a opção. Antes do 14, o padrão.
 * O sistema dá um valor de -1 a 1; o Material chama 0,5 de médio e 1 de alto.
 */
@Composable
public fun rememberSystemContrast(): DSContrast {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return DSContrast.Standard

    val context = LocalContext.current
    val uiModeManager = remember(context) { context.getSystemService(UiModeManager::class.java) }
    var level by remember(uiModeManager) { mutableFloatStateOf(uiModeManager?.contrast ?: 0f) }

    DisposableEffect(uiModeManager) {
        val listener = UiModeManager.ContrastChangeListener { level = it }
        uiModeManager?.addContrastChangeListener(context.mainExecutor, listener)
        onDispose { uiModeManager?.removeContrastChangeListener(listener) }
    }

    return when {
        level >= 1f -> DSContrast.High
        level >= 0.5f -> DSContrast.Medium
        else -> DSContrast.Standard
    }
}

@Composable
private fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}
