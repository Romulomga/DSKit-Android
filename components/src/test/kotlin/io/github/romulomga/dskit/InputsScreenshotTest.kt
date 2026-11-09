package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import org.junit.Test

class InputsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun textField() = snapshot("inputs_text_field") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTextField(value = "", onValueChange = {}, label = "Name", supportingText = "As it shows on your profile")

            DSTextField(value = "Ipanema", onValueChange = {}, label = "Neighborhood", leadingIcon = DSIcons.Search, clearable = true)

            DSTextField(value = "secret", onValueChange = {}, label = "Password", password = true)

            DSTextField(value = "broker@", onValueChange = {}, label = "Email", errorText = "Invalid email")

            DSTextField(value = "Locked", onValueChange = {}, label = "Disabled", enabled = false)

            DSTextField(
                value = "11 98765-4321",
                onValueChange = {},
                label = "Phone",
                leadingContent = { DSTextButton("+55", onClick = {}) },
                trailingContent = { Text("mobile", modifier = Modifier.padding(end = DSSpacing.lg)) }
            )
        }
    }

    @Test
    fun numberField() = snapshot("inputs_number_field") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSNumberField(value = 2, onValueChange = {}, label = "Min", modifier = androidx.compose.ui.Modifier.weight(1f))

            DSNumberField(value = null, onValueChange = {}, label = "Max", modifier = androidx.compose.ui.Modifier.weight(1f))
        }
    }

    @Test
    fun decimalField() = snapshot("inputs_decimal_field") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSDecimalField(value = 82.5, onValueChange = {}, label = "Weight", supportingText = "In kilograms", maxFractionDigits = 1)

            DSDecimalField(value = null, onValueChange = {}, label = "Dose", placeholder = "0.25", errorText = "Enter the dose")
        }
    }

    @Test
    fun dropdownField() = snapshot("inputs_dropdown_field") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSDropdownField(selection = "RJ", onSelectionChange = {
            }, options = listOf("RJ", "SP").map { DSOption(it, it) }, label = "State")

            DSDropdownField(
                selection = null,
                onSelectionChange = {},
                options = listOf(DSOption(1, "One")),
                label = "Property type",
                errorText = "Choose a type"
            )
        }
    }

    @Test
    fun sliders() = snapshot("inputs_slider") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSSlider(value = 3f, onValueChange = {}, label = "Bedrooms", valueRange = 1f..5f, steps = 3, valueText = "3")

            DSRangeSlider(
                value = 2f..6f,
                onValueChange = {},
                label = "Price",
                valueRange = 0f..10f,
                steps = 9,
                valueText = "2 – 6"
            )
        }
    }

    @Test
    fun autocompleteField() = snapshot("inputs_autocomplete_field") {
        DSAutocompleteField(
            query = "Lagoa",
            onQueryChange = {},
            suggestions = emptyList<DSOption<String>>(),
            onSuggestionSelect = {},
            label = "Neighborhood",
            leadingIcon = DSIcons.Search,
            supportingText = "Type to see suggestions"
        )
    }
}
