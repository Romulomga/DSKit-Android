package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.junit.Test

class ControlsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun toggleRow() = snapshot("controls_toggle_row") {
        Column {
            DSToggleRow(
                title = "Haptics",
                subtitle = "Subtle vibration on actions",
                icon = DSIcons.Star,
                checked = true,
                onCheckedChange = {}
            )
            DSToggleRow(title = "Sound", icon = DSIcons.InfoCircle, checked = false, onCheckedChange = {})
            DSToggleRow(title = "Disabled", checked = true, onCheckedChange = {}, enabled = false)
        }
    }

    @Test
    fun chip() = snapshot("controls_chip") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSChip(title = "Primeira viagem", emoji = "🌱", selected = true, onClick = {}, modifier = Modifier.fillMaxWidth())
            DSChip(title = "Pós-parto recente", emoji = "💜", selected = false, onClick = {}, modifier = Modifier.fillMaxWidth())
            DSChip(title = "Hug content", icon = DSIcons.Star, selected = true, onClick = {})
            DSChip(title = "Unselected", icon = DSIcons.Star, selected = false, onClick = {})
            DSChip(title = "Disabled", selected = false, onClick = {}, enabled = false)
        }
    }

    @Test
    fun optionChip() = snapshot("controls_option_chip") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)
        ) {
            DSOptionChip(title = "Duplicates", icon = DSIcons.Plus, selected = false, onSelectedChange = {})
            DSOptionChip(title = "No repeats", icon = DSIcons.Check, selected = true, onSelectedChange = {})
            DSOptionChip(title = "Shuffle", selected = true, onSelectedChange = {})
            DSOptionChip(title = "Disabled", selected = false, onSelectedChange = {}, enabled = false)
        }
    }

    @Test
    fun segmentedPicker() = snapshot("controls_segmented_picker") {
        val modes = listOf(DSSegmentedOption("single", "Single"), DSSegmentedOption("list", "List"), DSSegmentedOption("teams", "Teams"))
        val lists = listOf(DSSegmentedOption("active", "4 Active items"), DSSegmentedOption("inactive", "1 Inactive item"))

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSSegmentedPicker(title = "Mode", selection = "list", onSelectionChange = {}, options = modes)
            DSSegmentedPicker(selection = "active", onSelectionChange = {}, options = lists, style = DSSegmentedStyle.Filled)
            DSSegmentedPicker(selection = "inactive", onSelectionChange = {}, options = lists, style = DSSegmentedStyle.Filled)
            DSSegmentedPicker(selection = "single", onSelectionChange = {}, options = modes, enabled = false)
        }
    }

    @Test
    fun pagedTabs() = snapshot("controls_paged_tabs") {
        val pages = listOf("4 Active items", "1 Inactive item")
        val many = listOf("All", "Houses", "Apartments", "Land", "Commercial")

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSPagedTabs(
                sections = pages,
                selection = pages[0],
                onSelectionChange = {},
                label = { it },
                style = DSPagedTabsStyle.Segmented,
                modifier = Modifier.height(220.dp),
                header = { Text("Header", style = DSTypography.footnote, color = DSTheme.colors.onSurfaceMedium) }
            ) { page -> PageBody(page) }

            DSPagedTabs(
                sections = pages,
                selection = pages[1],
                onSelectionChange = {},
                label = { it },
                modifier = Modifier.height(160.dp)
            ) { page -> PageBody(page) }

            DSPagedTabs(
                sections = many,
                selection = many[2],
                onSelectionChange = {},
                label = { it },
                modifier = Modifier.height(160.dp)
            ) { page -> PageBody(page) }
        }
    }

    @Test
    fun rangeSlider() = snapshot("controls_range_slider") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSRangeSlider(
                title = "Preço",
                lowerIndex = 3,
                upperIndex = 9,
                onLowerIndexChange = {},
                onUpperIndexChange = {},
                stepCount = 11,
                valueText = "De R$ 120.000 até R$ 500.000"
            )
            DSRangeSlider(lowerIndex = 0, upperIndex = 11, onLowerIndexChange = {}, onUpperIndexChange = {}, stepCount = 11)
            DSRangeSlider(
                lowerIndex = 60,
                upperIndex = 61,
                onLowerIndexChange = {},
                onUpperIndexChange = {},
                stepCount = 116,
                valueText = "Um passo"
            )
            DSRangeSlider(
                lowerIndex = 116,
                upperIndex = 116,
                onLowerIndexChange = {},
                onUpperIndexChange = {},
                stepCount = 116,
                minimumDistance = 0,
                valueText = "Mesmo passo, no fim"
            )
            DSRangeSlider(
                lowerIndex = 5,
                upperIndex = 6,
                onLowerIndexChange = {},
                onUpperIndexChange = {},
                stepCount = 11,
                valueText = "Disabled",
                enabled = false
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun PageBody(page: String) {
    Text("$page · page", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh, modifier = Modifier.padding(DSSpacing.md))
}
