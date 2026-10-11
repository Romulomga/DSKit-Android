package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import org.junit.Test

class ButtonsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun buttons() = snapshot("buttons_variants") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSButton("Publish", onClick = {}, icon = DSIcons.Add)

            DSTonalButton("Save draft", onClick = {})

            DSOutlinedButton("Cancel", onClick = {})

            DSTextButton("Forgot password", onClick = {})
        }
    }

    @Test
    fun destructiveAndDisabled() = snapshot("buttons_destructive") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSButton("Delete account", onClick = {}, destructive = true)

            DSTonalButton("Remove partner", onClick = {}, destructive = true)

            DSOutlinedButton("Leave group", onClick = {}, destructive = true)

            DSButton("Disabled", onClick = {}, enabled = false)
        }
    }

    @Test
    fun iconButtons() = snapshot("buttons_icon") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSIconButton(DSIcons.Share, "Share", onClick = {})

            DSIconButton(DSIcons.Add, "Add", onClick = {}, style = DSIconButtonStyle.Filled)

            DSIconButton(DSIcons.Edit, "Edit", onClick = {}, style = DSIconButtonStyle.Tonal)

            DSIconButton(DSIcons.Delete, "Delete", onClick = {}, style = DSIconButtonStyle.Outlined)
        }
    }

    @Test
    fun fab() = snapshot("buttons_fab") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSFab(DSIcons.Edit, "Compose", onClick = {})

            DSExtendedFab("New post", DSIcons.Add, onClick = {})
        }
    }

    @Test
    fun textLink() = snapshot("buttons_text_link") {
        DSTextLink(
            text = "By continuing you accept the Terms of use and the Privacy policy.",
            links = listOf(DSLink.Url("Terms of use", "https://example.com/terms"), DSLink.Action("Privacy policy") {})
        )
    }
}
