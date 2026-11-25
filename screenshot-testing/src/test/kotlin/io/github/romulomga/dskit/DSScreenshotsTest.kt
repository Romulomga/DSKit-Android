package io.github.romulomga.dskit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.google.android.apps.common.testing.accessibility.framework.integrations.espresso.AccessibilityViewCheckException
import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalRoborazziApi::class)
class DSScreenshotsTest : DSScreenshotTest() {
    private val directory: File = Files.createTempDirectory("dskit-screenshots").toFile()

    /** Gravando de verdade, sem depender da tarefa do Gradle: um arquivo por modo, com o sufixo do modo. */
    private val recording = RoborazziOptions(taskType = RoborazziTaskType.Record)

    @Test
    fun aScreenIsCapturedOnceForEachMode() {
        captureDSScreen("Screen", roborazziOptions = recording, directory = directory.path, theme = { DSTheme(content = it) }) {
            DSScreen(title = "Feriados de 2026") { }
        }

        assertEquals(setOf("Screen_light.png", "Screen_dark.png", "Screen_font200.png"), directory.list()!!.toSet())
    }

    @Test
    fun theModesOfAScreenAreTheCallers() {
        captureDSScreen(
            "Screen",
            modes = listOf(DSScreenshotMode.Rtl),
            roborazziOptions = recording,
            directory = directory.path,
            theme = { DSTheme(content = it) }
        ) {
            Text("Olá")
        }

        assertEquals(setOf("Screen_rtl.png"), directory.list()!!.toSet())
    }

    /** O controle positivo do ATF: um alvo de 12 dp sem rótulo falha a captura. */
    @Test
    fun aScreenWithAnAccessibilityErrorFails() {
        val error = assertThrows(Throwable::class.java) {
            captureDSScreen("Broken", directory = directory.path, theme = { DSTheme(content = it) }) {
                Box(Modifier.size(12.dp).clickable { })
            }
        }

        assertTrue(error.toString(), error is AccessibilityViewCheckException)
    }

    @Test
    fun highContrastIsOnlyForComponents() {
        assertThrows(IllegalArgumentException::class.java) {
            captureDSScreen("Screen", modes = listOf(DSScreenshotMode.HighContrast), theme = { DSTheme(content = it) }) { }
        }
    }
}
