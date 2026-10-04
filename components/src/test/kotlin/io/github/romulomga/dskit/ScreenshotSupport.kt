package io.github.romulomga.dskit

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage

/**
 * Captura um componente nos três modos que o DSKit confere: claro, escuro e fonte a 200 %.
 * As imagens ficam em `components/screenshots/<nome>_<modo>.png`.
 *
 * Gravar: `./gradlew :components:recordRoborazziDebug`. Conferir: `./gradlew :components:verifyRoborazziDebug`.
 */
fun snapshot(name: String, content: @Composable () -> Unit) {
    capture("${name}_light", dark = false, fontScale = 1f, content)
    capture("${name}_dark", dark = true, fontScale = 1f, content)
    capture("${name}_font200", dark = false, fontScale = 2f, content)
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
}
