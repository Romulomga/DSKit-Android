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
 * Componentes com animação infinita (indicador de carregamento). O `captureDSComponent` espera a UI ficar ociosa,
 * o que nunca acontece aqui; estes passam pelo relógio parado do `captureDSAnimatedComponent`, um modo por teste. Ficam numa
 * classe própria porque a regra do Compose muda a captura do `captureDSComponent` das outras classes.
 */
class AnimatedScreenshotTest : ScreenshotTestBase() {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun buttonLoadingLight() = composeRule.captureDSAnimatedComponent("buttons_loading", DSScreenshotMode.Light) { LoadingButtons() }

    @Test
    fun buttonLoadingDark() = composeRule.captureDSAnimatedComponent("buttons_loading", DSScreenshotMode.Dark) { LoadingButtons() }

    @Test
    fun buttonLoadingFont200() = composeRule.captureDSAnimatedComponent("buttons_loading", DSScreenshotMode.Font200) { LoadingButtons() }

    @Composable
    private fun LoadingButtons() {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSButton("Publishing", onClick = {}, loading = true)

            DSTonalButton("Saving", onClick = {}, loading = true)
        }
    }

    @Test
    fun loadingStateLight() = composeRule.captureDSAnimatedComponent("feedback_loading_state", DSScreenshotMode.Light) {
        DSLoadingState(message = "Loading properties…")
    }

    @Test
    fun loadingStateDark() = composeRule.captureDSAnimatedComponent("feedback_loading_state", DSScreenshotMode.Dark) {
        DSLoadingState(message = "Loading properties…")
    }

    @Test
    fun loadingStateFont200() = composeRule.captureDSAnimatedComponent("feedback_loading_state", DSScreenshotMode.Font200) {
        DSLoadingState(message = "Loading properties…")
    }

    @Test
    fun skeletonLight() = composeRule.captureDSAnimatedComponent("feedback_skeleton", DSScreenshotMode.Light) { SkeletonStates() }

    @Test
    fun skeletonDark() = composeRule.captureDSAnimatedComponent("feedback_skeleton", DSScreenshotMode.Dark) { SkeletonStates() }

    @Test
    fun skeletonFont200() = composeRule.captureDSAnimatedComponent("feedback_skeleton", DSScreenshotMode.Font200) { SkeletonStates() }

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
    fun loadingOverlayLight() = composeRule.captureDSAnimatedComponent("overlays_loading_overlay", DSScreenshotMode.Light) {
        OverlayStates()
    }

    @Test
    fun loadingOverlayDark() = composeRule.captureDSAnimatedComponent("overlays_loading_overlay", DSScreenshotMode.Dark) { OverlayStates() }

    @Test
    fun loadingOverlayFont200() = composeRule.captureDSAnimatedComponent("overlays_loading_overlay", DSScreenshotMode.Font200) {
        OverlayStates()
    }

    @Composable
    private fun OverlayStates() {
        Box(Modifier.fillMaxWidth().height(240.dp)) {
            DSCard { Text("Form behind the veil") }

            LoadingVeil(message = "Publishing…")
        }
    }
}
