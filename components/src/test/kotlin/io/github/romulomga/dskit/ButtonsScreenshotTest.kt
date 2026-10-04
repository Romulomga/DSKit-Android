package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import org.junit.Test

class ButtonsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun secondary() = snapshot("buttons_secondary") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSSecondaryButton("Share", onClick = {}, icon = DSIcons.ArrowClockwise)

            DSSecondaryButton("Draw again", onClick = {})

            DSSecondaryButton("Disabled", onClick = {}, enabled = false)
        }
    }

    @Test
    fun destructive() = snapshot("buttons_destructive") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSDestructiveButton("Clear list", onClick = {}, icon = DSIcons.Close)

            DSDestructiveButton("Disabled", onClick = {}, enabled = false)
        }
    }

    @Test
    fun icon() = snapshot("buttons_icon") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSIconButton(DSIcons.ArrowClockwise, "Share", onClick = {}, style = DSIconButtonStyle.Plain)

            DSIconButton(DSIcons.Check, "Copy", onClick = {}, style = DSIconButtonStyle.Tinted)

            DSIconButton(DSIcons.Plus, "Add", onClick = {}, style = DSIconButtonStyle.Filled)

            DSIconButton(DSIcons.Lock, "Settings", onClick = {}, shape = DSIconButtonShape.Rounded)

            DSIconButton(DSIcons.Plus, "Disabled", onClick = {}, enabled = false)
        }
    }
}
