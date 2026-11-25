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
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.v2.runAndroidComposeUiTest as runAndroidComposeUiTestV2
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityCheckOptions
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityChecker
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityChecker.CheckLevel
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.checkRoboAccessibility
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// MARK: - Base

/**
 * A base de um teste de captura: Robolectric com renderização nativa (sem emulador), numa tela de telefone de
 * 360 × 800 dp, no SDK 35 (o 36 do Robolectric 4.17 não sobe no JDK 21: "Failed to interact with raw FileDescriptor
 * internals"). O teste estende esta classe e chama [captureDSScreen] (uma tela do app) ou [captureDSComponent] (um
 * componente numa moldura). Outra tela ou outro SDK é um `@Config(…)` na classe que estende.
 *
 * As imagens ficam em `screenshots/` do módulo. Gravar: `./gradlew recordRoborazziDebug`; conferir:
 * `./gradlew verifyRoborazziDebug`, sempre no Mac (o Linux desenha a fonte diferente). O `testDebugUnitTest` comum
 * não compara pixel, mas o Accessibility Test Framework falha em erro do mesmo jeito.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w360dp-h800dp-xhdpi")
public abstract class DSScreenshotTest

// MARK: - Modos

/** Um jeito de ver a mesma tela; o [suffix] vai no fim do nome do arquivo (`<nome>_<suffix>.png`). */
public enum class DSScreenshotMode(public val suffix: String) {
    Light("light"),
    Dark("dark"),

    /** Fonte do sistema a 200 %. */
    Font200("font200"),

    /** Da direita para a esquerda (árabe, hebraico): ícone de direção espelha, texto alinha à direita. */
    Rtl("rtl"),

    /** O esquema de alto contraste do `DSBrand` (o "Contraste de cor" do Android 14+). Só para [captureDSComponent]. */
    HighContrast("highcontrast")
}

/** Os padrões das capturas. */
public object DSScreenshotDefaults {
    /** Os modos de uma tela do app: claro, escuro e fonte a 200 %. */
    public val ScreenModes: List<DSScreenshotMode> = listOf(DSScreenshotMode.Light, DSScreenshotMode.Dark, DSScreenshotMode.Font200)

    /** Nenhuma exceção: todo erro do ATF falha o teste. */
    public val NoAccessibilitySuppressions: Matcher<in AccessibilityViewCheckResult> = Matchers.not(Matchers.anything())

    /** Meia escala, como o Now in Android: o repositório guarda dezenas de PNG, e a metade ainda mostra o layout. */
    public val ScreenOptions: RoborazziOptions = RoborazziOptions(recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5))

    /** O bastante para a entrada da tela e os `AnimatedVisibility` terminarem com o relógio parado. */
    public const val SETTLE_MILLIS: Long = 1_000L

    /**
     * O esquema base do Material com uma versão de alto contraste feita à mão, para o modo [DSScreenshotMode.HighContrast]
     * de [captureDSComponent]: os mesmos papéis em tons mais extremos da paleta de base (texto quase preto, contêineres
     * escuros com texto branco), no espírito do que o Material Theme Builder exporta. Nos outros modos vale o padrão.
     */
    public val HighContrastBrand: DSBrand = DSBrand.Default.copy(
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
}

// MARK: - Tela

/**
 * Captura uma tela inteira do app dentro do tema dele, um arquivo por modo (`<directory>/<nome>_<modo>.png`), e roda o
 * Accessibility Test Framework em cada modo, falhando em erro (alvo de toque, contraste, rótulo de controle).
 *
 * O escuro vem do aparelho (`+night`), porque o tema do app segue o sistema; a fonte e a direção vêm por
 * `CompositionLocal`. O relógio do Compose fica parado e anda [settleMillis]: o indicador de carregamento gira para
 * sempre e travaria a espera de ociosidade, e o cursor de um campo com foco piscaria entre uma gravação e outra.
 *
 * ```kotlin
 * class HolidaysScreenScreenshotTest : DSScreenshotTest() {
 *     @Test
 *     fun holidays() = captureDSScreen("HolidaysScreen_holidays", theme = { AppTheme(it) }) {
 *         HolidaysScreen(HolidaysSamples.holidays, onRefresh = {})
 *     }
 * }
 * ```
 *
 * @param modes sem [DSScreenshotMode.HighContrast]: o alto contraste é do componente, e o DSKit já o captura.
 * @param theme o tema do app em volta da tela, `{ AppTheme(it) }`.
 */
@OptIn(ExperimentalTestApi::class)
public fun captureDSScreen(
    name: String,
    modes: List<DSScreenshotMode> = DSScreenshotDefaults.ScreenModes,
    roborazziOptions: RoborazziOptions = DSScreenshotDefaults.ScreenOptions,
    settleMillis: Long = DSScreenshotDefaults.SETTLE_MILLIS,
    accessibilitySuppressions: Matcher<in AccessibilityViewCheckResult> = DSScreenshotDefaults.NoAccessibilitySuppressions,
    directory: String = "screenshots",
    theme: @Composable (content: @Composable () -> Unit) -> Unit,
    content: @Composable () -> Unit
) {
    require(DSScreenshotMode.HighContrast !in modes) { "O alto contraste é do componente: use captureDSComponent." }

    modes.forEach { mode ->
        // Vale para a atividade criada a seguir.
        RuntimeEnvironment.setQualifiers(if (mode == DSScreenshotMode.Dark) "+night" else "+notnight")

        // A v2 (`StandardTestDispatcher`), como o esqueleto do reference-app gravou as telas dele.
        runAndroidComposeUiTestV2<ComponentActivity> {
            mainClock.autoAdvance = false

            setContent {
                val density = LocalDensity.current

                CompositionLocalProvider(
                    LocalDensity provides Density(density.density, if (mode == DSScreenshotMode.Font200) 2f else 1f),
                    LocalLayoutDirection provides if (mode == DSScreenshotMode.Rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    theme(content)
                }
            }

            mainClock.advanceTimeBy(settleMillis)

            onRoot().captureRoboImage("$directory/${name}_${mode.suffix}.png", roborazziOptions = roborazziOptions)
            onRoot().assertDSAccessible(accessibilitySuppressions)
        }
    }
}

// MARK: - Componente

/**
 * Captura um componente numa moldura de 360 dp (`DSPreviewContainer`) nos cinco modos: claro, escuro, fonte a 200 %, da
 * direita para a esquerda e alto contraste, em `<directory>/<nome>_<modo>.png`. Depois, o Accessibility Test Framework
 * confere o claro e o escuro e falha em erro. É a captura dos componentes do próprio DSKit; o app usa para os
 * componentes do `designsystem` dele.
 *
 * Espera a UI ficar ociosa: componente com animação infinita (indicador, esqueleto) vai por
 * [captureDSAnimatedComponent].
 *
 * @param accessibilitySuppressions exceção real do ATF, com o motivo ao lado de quem chama.
 * @param brand a marca da moldura; a padrão traz um esquema de alto contraste ([DSScreenshotDefaults.HighContrastBrand]).
 */
public fun captureDSComponent(
    name: String,
    accessibilitySuppressions: Matcher<in AccessibilityViewCheckResult> = DSScreenshotDefaults.NoAccessibilitySuppressions,
    brand: DSBrand = DSScreenshotDefaults.HighContrastBrand,
    directory: String = "screenshots",
    content: @Composable () -> Unit
) {
    capture(directory, "${name}_light", brand = brand, content = content)
    capture(directory, "${name}_dark", dark = true, brand = brand, content = content)
    capture(directory, "${name}_font200", fontScale = 2f, brand = brand, content = content)
    capture(directory, "${name}_rtl", rtl = true, brand = brand, content = content)
    capture(directory, "${name}_highcontrast", contrast = DSContrast.High, brand = brand, content = content)
    checkAccessibility("${name}_light", dark = false, brand, accessibilitySuppressions, content)
    checkAccessibility("${name}_dark", dark = true, brand, accessibilitySuppressions, content)
}

/**
 * Captura um componente com animação infinita (indicador de carregamento, esqueleto) com o relógio do Compose parado,
 * um modo por chamada: a espera de ociosidade de [captureDSComponent] nunca terminaria. Mesma moldura e mesmo arquivo
 * (`<directory>/<nome>_<modo>.png`). O ATF roda no claro e no escuro.
 *
 * A regra do Compose (`createComposeRule()`) muda a captura de [captureDSComponent]: deixe estes testes numa classe
 * própria.
 *
 * @param mode [DSScreenshotMode.Light], [DSScreenshotMode.Dark] ou [DSScreenshotMode.Font200].
 */
public fun ComposeContentTestRule.captureDSAnimatedComponent(
    name: String,
    mode: DSScreenshotMode,
    directory: String = "screenshots",
    content: @Composable () -> Unit
) {
    require(mode in DSScreenshotDefaults.ScreenModes) { "Só claro, escuro e fonte a 200 %." }

    mainClock.autoAdvance = false
    setContent {
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalDensity provides Density(density.density, if (mode == DSScreenshotMode.Font200) 2f else 1f)
        ) {
            DSPreviewContainer(
                title = "${name}_${mode.suffix}",
                modifier = Modifier.width(360.dp),
                darkTheme = mode == DSScreenshotMode.Dark,
                content = content
            )
        }
    }
    mainClock.advanceTimeBy(400)
    onRoot().captureRoboImage("$directory/${name}_${mode.suffix}.png")
    if (mode != DSScreenshotMode.Font200) onRoot().assertDSAccessible()
}

// MARK: - Acessibilidade

/** O Accessibility Test Framework no preset mais recente, falhando em erro (aviso só aparece no log). */
@OptIn(ExperimentalRoborazziApi::class)
public fun SemanticsNodeInteraction.assertDSAccessible(
    suppressions: Matcher<in AccessibilityViewCheckResult> = DSScreenshotDefaults.NoAccessibilitySuppressions
) {
    checkRoboAccessibility(
        roborazziATFAccessibilityCheckOptions = RoborazziATFAccessibilityCheckOptions(
            checker = RoborazziATFAccessibilityChecker(preset = AccessibilityCheckPreset.LATEST, suppressions = suppressions),
            failureLevel = CheckLevel.Error
        )
    )
}

// MARK: - Privado

/**
 * Roda o ATF numa composição própria: uma compose rule na classe de teste mudaria a captura de [captureDSComponent].
 */
@OptIn(ExperimentalTestApi::class)
private fun checkAccessibility(
    title: String,
    dark: Boolean,
    brand: DSBrand,
    suppressions: Matcher<in AccessibilityViewCheckResult>,
    content: @Composable () -> Unit
) = runAndroidComposeUiTest<ComponentActivity> {
    setContent {
        DSPreviewContainer(title = title, modifier = Modifier.width(360.dp), darkTheme = dark, brand = brand) {
            content()
        }
    }
    onRoot().assertDSAccessible(suppressions)
}

private fun capture(
    directory: String,
    file: String,
    dark: Boolean = false,
    fontScale: Float = 1f,
    rtl: Boolean = false,
    contrast: DSContrast = DSContrast.Standard,
    brand: DSBrand,
    content: @Composable () -> Unit
) {
    captureRoboImage("$directory/$file.png") {
        val density = LocalDensity.current

        CompositionLocalProvider(
            LocalDensity provides Density(density.density, fontScale),
            LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            DSPreviewContainer(
                title = file,
                modifier = Modifier.width(360.dp),
                darkTheme = dark,
                brand = brand,
                contrast = contrast,
                content = content
            )
        }
    }
}
