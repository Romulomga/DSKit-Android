package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.junit.Test

class NavigationScreenshotTest : ScreenshotTestBase() {
    private enum class Tab { Home, Messages, Posts, Profile }

    private val items = listOf(
        DSNavigationItem(Tab.Home, "Home", DSIcons.Home),
        DSNavigationItem(Tab.Messages, "Messages", DSIcons.Inbox, badgeCount = 3),
        DSNavigationItem(Tab.Posts, "Posts", DSIcons.Add, badgeCount = 0),
        DSNavigationItem(Tab.Profile, "Profile", DSIcons.Person)
    )

    @Test
    fun topAppBars() = snapshot("navigation_top_app_bar") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTopAppBar("Profile", onBack = {})

            DSTopAppBar("Edit post", onBack = {}) {
                DSIconButton(DSIcons.Check, "Save", onClick = {})
            }

            DSTopAppBar("Homer", style = DSTopAppBarStyle.CenterAligned)

            DSTopAppBar("Messages", style = DSTopAppBarStyle.Large)
        }
    }

    @Test
    fun navigationBar() = snapshot("navigation_bar") {
        DSNavigationBar(items = items, selection = Tab.Messages, onSelectionChange = {})
    }

    @Test
    fun navigationRail() = snapshot("navigation_rail") {
        DSNavigationRail(items = items, selection = Tab.Home, onSelectionChange = {}, modifier = Modifier.height(400.dp))
    }
}
