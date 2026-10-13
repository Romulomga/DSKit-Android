package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test

/**
 * Os componentes com marcas geradas por `DSBrand.fromSeed` (módulo `seed`), em claro e escuro: uma cor comum, uma
 * quente e uma difícil (verde neon), para conferir que a marca gerada fica legível em tudo.
 */
class SeedScreenshotTest : ScreenshotTestBase() {
    @Test
    fun purple() = seeded("seed_purple", Color(0xFF3C0079))

    @Test
    fun orange() = seeded("seed_orange", Color(0xFFE66406))

    @Test
    fun neon() = seeded("seed_neon", Color(0xFF03FF00))

    private fun seeded(name: String, seed: Color) {
        val brand = DSBrand.fromSeed(seed)

        listOf(false to "light", true to "dark").forEach { (dark, mode) ->
            captureRoboImage("screenshots/${name}_$mode.png") {
                DSPreviewContainer(title = "${name}_$mode", modifier = Modifier.width(360.dp), darkTheme = dark, brand = brand) {
                    Sample()
                }
            }
        }
    }

    @Composable
    private fun Sample() {
        DSButton("Publish", onClick = {}, icon = DSIcons.Add)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTonalButton("Save draft", onClick = {})

            DSOutlinedButton("Cancel", onClick = {})
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSFilterChip("Parking", selected = true, onSelectedChange = {})

            DSFilterChip("Pet friendly", selected = false, onSelectedChange = {})
        }

        DSTextField(value = "Ipanema", onValueChange = {}, label = "Neighborhood")

        DSSwitchListItem("Notifications", checked = true, onCheckedChange = {})

        DSBanner(message = "Property published.", style = DSBannerStyle.Success)

        DSBanner(message = "You're offline.", style = DSBannerStyle.Warning)
    }
}
