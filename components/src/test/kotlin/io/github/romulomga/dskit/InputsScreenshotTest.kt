package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import org.junit.Test

class InputsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun inputBox() = snapshot("inputs_input_box") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSFieldLabel("Custom input")
            DSInputBox(leadingIcon = DSIcons.Lock) { Text("Type", color = DSTheme.colors.onSurfaceMedium) }
            DSInputBox(isFocused = true) {
                BasicTextField(value = "Focused", onValueChange = {
                }, textStyle = DSTypography.body.copy(color = DSTheme.colors.onSurfaceHigh))
            }
            DSFieldLabel("Invalid")
            DSInputBox(isInvalid = true) { Text("Type", color = DSTheme.colors.onSurfaceMedium) }
            DSFieldCaption(errorMessage = "Something is off")
            DSFieldCaption(helperText = "Helper text")
        }
    }

    @Test
    fun inputBoxProminent() = snapshot("inputs_input_box_prominent") {
        Prominent {
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSFieldLabel("Custom input")
                DSInputBox(isInvalid = true) { Text("Type", color = DSTheme.colors.onSurfaceMedium) }
                DSFieldCaption(errorMessage = "Something is off")
                DSFieldCaption(helperText = "Helper text")
            }
        }
    }

    @Test
    fun textField() = snapshot("inputs_text_field") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            TextFieldStates()
        }
    }

    @Test
    fun textFieldProminent() = snapshot("inputs_text_field_prominent") {
        Prominent {
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                TextFieldStates()
            }
        }
    }

    @Test
    fun textFieldFocusedAndInCard() = snapshot("inputs_text_field_focused_card") {
        val requester = remember { FocusRequester() }
        LaunchedEffect(Unit) { requester.requestFocus() }
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSTextField(value = "Focused", onValueChange = {}, title = "Focused", focusRequester = requester, showsClearButton = true)
            Card {
                DSTextField(value = "", onValueChange = {}, title = "Inside a card", placeholder = "Surface level 1")
            }
        }
    }

    @Test
    fun pickerField() = snapshot("inputs_picker_field") {
        val options = listOf("RJ", "SP", "MG").map { DSPickerOption(it, it) }
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSPickerField(placeholder = "Choose", selection = null, onSelectionChange = {}, options = options, title = "State")
            DSPickerField(placeholder = "Choose", selection = "SP", onSelectionChange = {}, options = options, title = "Selected")
            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSTextField(value = "", onValueChange = {}, modifier = Modifier.weight(1f), placeholder = "Number")
                DSPickerField(
                    placeholder = "UF",
                    selection = null,
                    onSelectionChange = {},
                    options = options,
                    isInvalid = true,
                    hugsContent = true
                )
            }
            DSPickerField(
                placeholder = "Choose",
                selection = null,
                onSelectionChange = {},
                options = options,
                title = "Error",
                errorMessage = "Pick a state"
            )
            DSPickerField(placeholder = "Choose", selection = "RJ", onSelectionChange = {
            }, options = options, title = "Disabled", enabled = false)
        }
    }

    @Test
    fun numberField() = snapshot("inputs_number_field") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                DSNumberField(value = 1, onValueChange = {}, modifier = Modifier.weight(1f), title = "Min")
                DSNumberField(value = 100, onValueChange = {}, modifier = Modifier.weight(1f), title = "Max")
            }
            DSNumberField(value = null, onValueChange = {}, title = "Empty")
            DSNumberField(value = 5, onValueChange = {}, title = "Error", errorMessage = "Must be at most 3")
            DSNumberField(value = 7, onValueChange = {}, title = "Disabled", enabled = false)
        }
    }

    @Test
    fun listInput() = snapshot("inputs_list_input") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSListInput(
                value = "Ana\nBruno\nCarla\nAna",
                onValueChange = {},
                title = "Participants",
                helperText = "Enter one name per line. Duplicates are kept."
            )
            DSListInput(value = "", onValueChange = {}, title = "Empty")
        }
    }

    @Composable
    private fun TextFieldStates() {
        DSTextField(value = "", onValueChange = {}, title = "Name", placeholder = "Your name", showsClearButton = true)
        DSTextField(value = "Ana", onValueChange = {}, title = "Filled", showsClearButton = true)
        DSTextField(
            value = "bad@",
            onValueChange = {},
            title = "Email",
            placeholder = "you@example.com",
            errorMessage = "Invalid email",
            leadingIcon = DSIcons.Lock
        )
        DSTextField(
            value = "secret",
            onValueChange = {},
            title = "Password",
            placeholder = "Password",
            helperText = "At least 4 characters",
            isSecure = true,
            leadingIcon = DSIcons.Lock
        )
        DSTextField(value = "Read only", onValueChange = {}, title = "Disabled", enabled = false)
    }

    @Composable
    private fun Prominent(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalDSFieldLabelStyle provides DSFieldLabelStyle.Prominent, content = content)
    }

    @Composable
    private fun Card(content: @Composable () -> Unit) {
        Column(
            Modifier
                .clip(DSRadius.large)
                .background(DSTheme.colors.surface(DSTheme.surfaceLevel))
                .padding(DSSpacing.lg)
        ) {
            DSSurfaceContainer(content)
        }
    }
}
