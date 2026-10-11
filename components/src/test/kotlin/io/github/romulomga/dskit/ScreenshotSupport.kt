package io.github.romulomga.dskit

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.width
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityCheckOptions
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityChecker
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityChecker.CheckLevel
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.checkRoboAccessibility
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import org.hamcrest.Matcher
import org.hamcrest.Matchers

/**
 * Captura um componente nos cinco modos que o DSKit confere: claro, escuro, fonte a 200 %, da direita para a
 * esquerda (árabe, hebraico) e alto contraste. As imagens ficam em `components/screenshots/<nome>_<modo>.png`.
 *
 * Depois das capturas, o Accessibility Test Framework do Google confere o claro e o escuro (alvo de
 * toque, contraste, rótulo de controle) e falha o teste em erro, como o Now in Android faz. Exceção
 * real entra em `accessibilitySuppressions`, com o motivo ao lado.
 *
 * Gravar: `./gradlew :components:recordRoborazziDebug`. Conferir: `./gradlew :components:verifyRoborazziDebug`.
 */
fun snapshot(
    name: String,
    accessibilitySuppressions: Matcher<in AccessibilityViewCheckResult> = NoSuppressions,
    content: @Composable () -> Unit
) {
    capture("${name}_light", content = content)
    capture("${name}_dark", dark = true, content = content)
    capture("${name}_font200", fontScale = 2f, content = content)
    capture("${name}_rtl", rtl = true, content = content)
    capture("${name}_highcontrast", contrast = DSContrast.High, content = content)
    checkAccessibility("${name}_light", dark = false, accessibilitySuppressions, content)
    checkAccessibility("${name}_dark", dark = true, accessibilitySuppressions, content)
}

/** Nenhuma exceção: todo erro do ATF falha o teste. */
internal val NoSuppressions: Matcher<in AccessibilityViewCheckResult> = Matchers.not(Matchers.anything())

/**
 * Roda o ATF numa composição própria: uma compose rule na classe de teste mudaria a captura do `snapshot`
 * (ver `AnimatedScreenshotTest`).
 */
@OptIn(ExperimentalTestApi::class)
private fun checkAccessibility(
    title: String,
    dark: Boolean,
    suppressions: Matcher<in AccessibilityViewCheckResult>,
    content: @Composable () -> Unit
) = runAndroidComposeUiTest<ComponentActivity> {
    setContent {
        DSPreviewContainer(title = title, modifier = Modifier.width(360.dp), darkTheme = dark) {
            content()
        }
    }
    onRoot().assertAccessible(suppressions)
}

/** O ATF no preset mais recente, falhando em erro (aviso só aparece no log). */
@OptIn(ExperimentalRoborazziApi::class)
internal fun SemanticsNodeInteraction.assertAccessible(suppressions: Matcher<in AccessibilityViewCheckResult> = NoSuppressions) {
    checkRoboAccessibility(
        roborazziATFAccessibilityCheckOptions = RoborazziATFAccessibilityCheckOptions(
            checker = RoborazziATFAccessibilityChecker(preset = AccessibilityCheckPreset.LATEST, suppressions = suppressions),
            failureLevel = CheckLevel.Error
        )
    )
}

private fun capture(
    file: String,
    dark: Boolean = false,
    fontScale: Float = 1f,
    rtl: Boolean = false,
    contrast: DSContrast = DSContrast.Standard,
    content: @Composable () -> Unit
) {
    captureRoboImage("screenshots/$file.png") {
        val density = LocalDensity.current

        CompositionLocalProvider(
            LocalDensity provides Density(density.density, fontScale),
            LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            DSPreviewContainer(
                title = file,
                modifier = Modifier.width(360.dp),
                darkTheme = dark,
                brand = HighContrastTestBrand,
                contrast = contrast,
                content = content
            )
        }
    }
}

/**
 * O esquema base do Material com uma versão de alto contraste feita à mão, para o modo `highcontrast`:
 * os mesmos papéis em tons mais extremos da paleta de base (texto quase preto, contêineres escuros com
 * texto branco), no espírito do que o Material Theme Builder exporta. Nos outros modos vale o padrão.
 */
internal val HighContrastTestBrand = DSBrand.Default.copy(
    lightHighContrast = lightColorScheme(
        primary = Color(0xFF381E72),
        onPrimary = Color.White,
        primaryContainer = Color(0xFF4F378B),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFF332D41),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFF4A4458),
        onSecondaryContainer = Color.White,
        tertiary = Color(0xFF492532),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFF633B48),
        onTertiaryContainer = Color.White,
        error = Color(0xFF601410),
        onError = Color.White,
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color.White,
        onSurface = Color.Black,
        onSurfaceVariant = Color(0xFF322F35),
        outline = Color(0xFF49454F),
        outlineVariant = Color(0xFF49454F)
    )
)

/** Os três modos do `snapshot`, um por vez, para o `stillSnapshot`. */
internal enum class StillMode(val suffix: String, val dark: Boolean, val fontScale: Float) {
    Light("light", false, 1f),
    Dark("dark", true, 1f),
    Font200("font200", false, 2f)
}

/**
 * Captura com o relógio do Compose parado, para componentes com animação infinita (indicador de
 * carregamento), que travariam a espera de ociosidade do `snapshot`. Mesma moldura e mesmo arquivo
 * (`<nome>_<modo>.png`) do `snapshot`.
 */
internal fun androidx.compose.ui.test.junit4.ComposeContentTestRule.stillSnapshot(
    name: String,
    mode: StillMode,
    content: @androidx.compose.runtime.Composable () -> Unit
) {
    mainClock.autoAdvance = false
    setContent {
        val density = androidx.compose.ui.platform.LocalDensity.current
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density, mode.fontScale)
        ) {
            DSPreviewContainer(
                title = "${name}_${mode.suffix}",
                modifier = Modifier.width(360.dp),
                darkTheme = mode.dark,
                content = content
            )
        }
    }
    mainClock.advanceTimeBy(400)
    onRoot().captureRoboImage("screenshots/${name}_${mode.suffix}.png")
    if (mode != StillMode.Font200) onRoot().assertAccessible()
}
