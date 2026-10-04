package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test

/**
 * Componentes com animação infinita (indicador de carregamento). O `snapshot` comum espera a UI ficar ociosa,
 * o que nunca acontece aqui; estes passam pelo relógio parado do `stillSnapshot`, um modo por teste. Ficam numa
 * classe própria porque a regra do Compose muda a captura do `snapshot` das outras classes.
 */
class AnimatedScreenshotTest : ScreenshotTestBase() {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun primaryLight() = composeRule.stillSnapshot("buttons_primary", StillMode.Light) { PrimaryStates() }

    @Test
    fun primaryDark() = composeRule.stillSnapshot("buttons_primary", StillMode.Dark) { PrimaryStates() }

    @Test
    fun primaryFont200() = composeRule.stillSnapshot("buttons_primary", StillMode.Font200) { PrimaryStates() }

    @Composable
    private fun PrimaryStates() {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSPrimaryButton("Draw", onClick = {}, icon = DSIcons.Star)

            DSPrimaryButton("Loading", onClick = {}, isLoading = true)

            DSPrimaryButton("Disabled", onClick = {}, enabled = false)
        }
    }

    @Test
    fun pillLight() = composeRule.stillSnapshot("buttons_pill", StillMode.Light) { PillStates() }

    @Test
    fun pillDark() = composeRule.stillSnapshot("buttons_pill", StillMode.Dark) { PillStates() }

    @Test
    fun pillFont200() = composeRule.stillSnapshot("buttons_pill", StillMode.Font200) { PillStates() }

    @Composable
    private fun PillStates() {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSPillButton("Continuar", onClick = {})

            DSPillButton("Começar jornada", onClick = {}, icon = DSIcons.Star)

            DSPillButton("Loading", onClick = {}, isLoading = true)

            DSPillButton("Secundário", onClick = {}, variant = DSPillButtonVariant.Secondary)

            DSPillButton("Outlined", onClick = {}, variant = DSPillButtonVariant.Outlined)

            DSPillButton("Disabled", onClick = {}, enabled = false)
        }
    }

    @Test
    fun loadingStateLight() = composeRule.stillSnapshot("feedback_loading_state", StillMode.Light) {
        DSLoadingState(message = "Drawing winner…")
    }

    @Test
    fun loadingStateDark() = composeRule.stillSnapshot("feedback_loading_state", StillMode.Dark) {
        DSLoadingState(message = "Drawing winner…")
    }

    @Test
    fun loadingStateFont200() = composeRule.stillSnapshot("feedback_loading_state", StillMode.Font200) {
        DSLoadingState(message = "Drawing winner…")
    }
}
