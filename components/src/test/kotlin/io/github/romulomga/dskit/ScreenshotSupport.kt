package io.github.romulomga.dskit

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.unit.Density
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
 * Captura um componente nos três modos que o DSKit confere: claro, escuro e fonte a 200 %.
 * As imagens ficam em `components/screenshots/<nome>_<modo>.png`.
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
    capture("${name}_light", dark = false, fontScale = 1f, content)
    capture("${name}_dark", dark = true, fontScale = 1f, content)
    capture("${name}_font200", dark = false, fontScale = 2f, content)
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

private fun capture(file: String, dark: Boolean, fontScale: Float, content: @Composable () -> Unit) {
    captureRoboImage("screenshots/$file.png") {
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
            DSPreviewContainer(title = file, modifier = Modifier.width(360.dp), darkTheme = dark) {
                content()
            }
        }
    }
}

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
