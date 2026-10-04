package io.github.romulomga.dskit

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
