package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import org.junit.Test

class ControlsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun segmentedButton() = snapshot("controls_segmented_button") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSSegmentedButton(selection = "sale", onSelectionChange = {
            }, options = listOf(DSOption("sale", "Sale"), DSOption("rent", "Rent")))

            DSMultiSegmentedButton(
                selection = setOf("mon", "wed"),
                onSelectionChange = {},
                options = listOf(DSOption("mon", "Mon"), DSOption("tue", "Tue"), DSOption("wed", "Wed"))
            )
        }
    }

    @Test
    fun chips() = snapshot("controls_chips") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSFilterChip("Parking", selected = true, onSelectedChange = {})

            DSFilterChip("Pet friendly", selected = false, onSelectedChange = {})

            DSInputChip("Ipanema", onRemove = {})

            DSAssistChip("Call", onClick = {}, icon = DSIcons.Share)
        }
    }

    @Test
    fun checkbox() = snapshot("controls_checkbox") {
        Column {
            DSCheckbox("I accept the terms", checked = true, onCheckedChange = {})

            DSCheckbox("Don't show again", checked = false, onCheckedChange = {})
        }
    }

    @Test
    fun tabs() = snapshot("controls_tabs") {
        DSTabs(tabs = listOf("Mine", "Partners", "Saved"), selectedIndex = 1, onSelect = {})
    }
}
