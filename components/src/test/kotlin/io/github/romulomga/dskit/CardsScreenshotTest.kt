package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import org.junit.Test

class CardsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun cards() = snapshot("cards_variants") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSCard {
                Text("Filled card", style = DSTheme.typography.titleMedium)

                Text("The default card", style = DSTheme.typography.bodyMedium)
            }

            DSCard(variant = DSCardVariant.Elevated, onClick = {}) { Text("Elevated, tappable") }

            DSCard(variant = DSCardVariant.Outlined) { Text("Outlined") }
        }
    }

    @Test
    fun adaptiveGrid() = snapshot("cards_adaptive_grid") {
        DSAdaptiveGrid(minItemWidth = 140.dp) {
            repeat(5) { DSCard { Text("Item ${it + 1}", style = DSTheme.typography.titleMedium) } }
        }
    }
}
