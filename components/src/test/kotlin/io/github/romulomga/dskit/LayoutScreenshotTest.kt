package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.junit.Test

class LayoutScreenshotTest : ScreenshotTestBase() {
    @Test
    fun screen() = snapshot("layout_screen") {
        Box(Modifier.height(520.dp)) {
            DSScreen(
                title = "Name Picker",
                subtitle = "Add names and pick a winner",
                trailing = { Text("Edit", style = DSTypography.body, color = DSTheme.colors.accent) },
                bottomAction = { FakeButton("Pick winner") }
            ) {
                DSCard { Body("Content goes here") }

                DSCard { Body("More content") }
            }
        }
    }

    @Test
    fun screenWithoutBottomAction() = snapshot("layout_screen_plain") {
        Box(Modifier.height(320.dp)) {
            DSScreen(title = "Settings") {
                DSCard { Body("Content goes here") }
            }
        }
    }

    @Test
    fun section() = snapshot("layout_section") {
        DSSection(title = "Recent") {
            DSCard { Body("Item 1") }

            DSCard { Body("Item 2") }
        }
    }

    @Test
    fun settingsSection() = snapshot("layout_settings_section") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSSettingsSection(title = "Help") {
                DSSettingsRow(
                    title = "Account and profile",
                    titleStyle = DSSettingsRowTitleStyle.Prominent,
                    subtitle = "Manage your profile",
                    onClick = {}
                )

                DSSettingsRow(
                    title = "Billing",
                    titleStyle = DSSettingsRowTitleStyle.Prominent,
                    subtitle = "Invoices and receipts",
                    showsDivider = false,
                    onClick = {}
                )
            }

            DSSettingsSection(title = "Preferences") {
                DSSettingsRow(title = "Haptics", icon = DSIcons.Check) {
                    Switch(checked = true, onCheckedChange = {})
                }

                DSSettingsRow(
                    title = "Notifications",
                    icon = DSIcons.InfoCircle,
                    iconColor = DSTheme.colors.warningHigh,
                    showsDivider = false
                ) {
                    Switch(checked = false, onCheckedChange = {})
                }
            }

            DSSettingsSection(title = "About", footer = "Version 1.0.0") {
                DSSettingsRow(title = "Contact support", icon = DSIcons.Tray, subtitle = "Get help by email", onClick = {})

                DSSettingsRow(
                    title = "Rate the app",
                    icon = DSIcons.Star,
                    iconColor = DSTheme.colors.warningHigh,
                    showsDivider = false,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun settingsOnGroupedCanvas() = snapshot("layout_settings_grouped") {
        DSGroupedCanvas {
            Column(Modifier.padding(DSSpacing.lg)) {
                DSSettingsSection(title = "Preferences") {
                    DSSettingsRow(title = "Haptics", icon = DSIcons.Check, showsDivider = false) {
                        Switch(checked = true, onCheckedChange = {})
                    }
                }
            }
        }
    }

    @Test
    fun adaptiveGrid() = snapshot("layout_adaptive_grid") {
        DSAdaptiveGrid(minItemWidth = 140.dp) {
            repeat(5) { i -> DSCard { Text("Item ${i + 1}", style = DSTypography.headline, color = DSTheme.colors.onSurfaceHigh) } }
        }
    }

    @Composable
    private fun Body(text: String) {
        Text(text, style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)
    }

    /** Botão de mentira: o `DSPrimaryButton` é de outro grupo. */
    @Composable
    private fun FakeButton(text: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DSTouchTarget.button)
                .clip(DSRadius.large)
                .background(DSTheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = DSTypography.headline, color = DSTheme.colors.onAccent)
        }
    }
}
