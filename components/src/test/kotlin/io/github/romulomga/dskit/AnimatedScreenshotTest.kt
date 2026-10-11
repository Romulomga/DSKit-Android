package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
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
    fun buttonLoadingLight() = composeRule.stillSnapshot("buttons_loading", StillMode.Light) { LoadingButtons() }

    @Test
    fun buttonLoadingDark() = composeRule.stillSnapshot("buttons_loading", StillMode.Dark) { LoadingButtons() }

    @Test
    fun buttonLoadingFont200() = composeRule.stillSnapshot("buttons_loading", StillMode.Font200) { LoadingButtons() }

    @Composable
    private fun LoadingButtons() {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSButton("Publishing", onClick = {}, loading = true)

            DSTonalButton("Saving", onClick = {}, loading = true)
        }
    }

    @Test
    fun loadingStateLight() = composeRule.stillSnapshot("feedback_loading_state", StillMode.Light) {
        DSLoadingState(message = "Loading properties…")
    }

    @Test
    fun loadingStateDark() = composeRule.stillSnapshot("feedback_loading_state", StillMode.Dark) {
        DSLoadingState(message = "Loading properties…")
    }

    @Test
    fun loadingStateFont200() = composeRule.stillSnapshot("feedback_loading_state", StillMode.Font200) {
        DSLoadingState(message = "Loading properties…")
    }

    @Test
    fun skeletonLight() = composeRule.stillSnapshot("feedback_skeleton", StillMode.Light) { SkeletonStates() }

    @Test
    fun skeletonDark() = composeRule.stillSnapshot("feedback_skeleton", StillMode.Dark) { SkeletonStates() }

    @Test
    fun skeletonFont200() = composeRule.stillSnapshot("feedback_skeleton", StillMode.Font200) { SkeletonStates() }

    @Composable
    private fun SkeletonStates() {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSSkeletonRow()

            DSSkeletonRow()

            DSCard {
                Text("Sample title of a post", style = DSTheme.typography.titleMedium, modifier = Modifier.dsSkeleton(true))

                Text("Sample line of the description", style = DSTheme.typography.bodyMedium, modifier = Modifier.dsSkeleton(true))
            }
        }
    }

    @Test
    fun loadingOverlayLight() = composeRule.stillSnapshot("overlays_loading_overlay", StillMode.Light) { OverlayStates() }

    @Test
    fun loadingOverlayDark() = composeRule.stillSnapshot("overlays_loading_overlay", StillMode.Dark) { OverlayStates() }

    @Test
    fun loadingOverlayFont200() = composeRule.stillSnapshot("overlays_loading_overlay", StillMode.Font200) { OverlayStates() }

    @Composable
    private fun OverlayStates() {
        Box(Modifier.fillMaxWidth().height(240.dp)) {
            DSCard { Text("Form behind the veil") }

            DSLoadingOverlay(visible = true, message = "Publishing…")
        }
    }
}
