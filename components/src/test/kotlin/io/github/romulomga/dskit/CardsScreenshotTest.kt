package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.junit.Test

class CardsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun cardVariants() = snapshot("cards_card_variants") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSCard { Body("Default — subtle border + soft shadow") }

            DSCard(variant = DSCardVariant.Elevated) { Body("Elevated — stronger shadow") }

            DSCard(variant = DSCardVariant.Material) { Body("Material — no border") }
        }
    }

    @Test
    fun cardGroupedCanvas() = snapshot("cards_card_grouped") {
        DSGroupedCanvas {
            Column(Modifier.padding(DSSpacing.lg), verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                DSCard { Body("Elevated cell on grouped canvas") }

                DSCard { Body("Settings look") }
            }
        }
    }

    @Test
    fun cardNested() = snapshot("cards_card_nested") {
        DSCard {
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                Text("Nested surface reads on surfaceElevated", style = DSTypography.headline, color = DSTheme.colors.onSurfaceHigh)

                DSCard { Body("Inner card at level 1") }

                DSResultCard(title = "Nested result", primaryResult = "42")
            }
        }
    }

    @Test
    fun featureCard() = snapshot("cards_feature_card") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSFeatureCard(title = "Number Picker", description = "Pick random numbers from a range.", icon = DSIcons.Plus, onClick = {})

            DSFeatureCard(
                title = "Name Picker",
                description = "Draw a winner from a list of names.",
                icon = DSIcons.Star,
                badge = "NEW",
                onClick = {}
            )

            DSFeatureCard(
                title = "Wheel",
                description = "Spin a roulette wheel.",
                icon = DSIcons.Circle,
                badge = "PRO",
                isLocked = true,
                lockedDescription = "Locked",
                onClick = {}
            )
        }
    }

    @Test
    fun resultSingle() = snapshot("cards_result_single") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSResultCard(title = "Winner", primaryResult = "Ana", subtitle = "Picked from 12 participants")

            DSResultCard(title = "Long result", primaryResult = "Maria Eduarda Albuquerque Figueiredo")
        }
    }

    @Test
    fun resultList() = snapshot("cards_result_list") {
        DSResultCard(title = "Top 3", results = listOf("Ana", "Bruno", "Carla"), subtitle = "Out of 12 participants")
    }

    @Test
    fun resultGrouped() = snapshot("cards_result_grouped") {
        DSResultCard(
            title = "Teams",
            groupedResults = listOf(
                DSResultSection("Team A", listOf("Ana", "Bruno")),
                DSResultSection("Team B", listOf("Carla", "Diego"))
            )
        )
    }

    @Test
    fun badges() = snapshot("cards_badges") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSBadge("7 days free", tint = DSTheme.colors.successHigh)

                DSBadge("Save 70%", tint = DSTheme.colors.warningHigh)

                DSBadge("New")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSProBadge()

                DSProBadge(text = "NEW")

                DSProBadge(text = "BETA")
            }
        }
    }

    @Composable
    private fun Body(text: String) {
        Text(text, style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)
    }
}
