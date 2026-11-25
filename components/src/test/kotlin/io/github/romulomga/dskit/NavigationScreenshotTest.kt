package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    fun topAppBars() = captureDSComponent("navigation_top_app_bar") {
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
    fun topAppBarWithNavigationIcon() = captureDSComponent("navigation_top_app_bar_navigation_icon") {
        DSTopAppBar("Today", navigationIcon = { DSIconButton(DSIcons.Home, "Home", onClick = {}) })
    }

    @Test
    fun topAppBarWithBrandColors() = captureDSComponent("navigation_top_app_bar_brand") {
        CompositionLocalProvider(LocalDSTopAppBarBrandColors provides DSTopAppBarBrandColors(Color(0xFFD0212E), Color.White)) {
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSTopAppBar("Profile", onBack = {}) {
                    DSIconButton(DSIcons.Check, "Save", onClick = {})
                }

                DSTopAppBar(
                    "Home",
                    style = DSTopAppBarStyle.CenterAligned,
                    navigationIcon = { DSIconButton(DSIcons.Person, "Profile", onClick = {}) },
                    actions = { DSIconButton(DSIcons.MoreVert, "More", onClick = {}) },
                    titleContent = { Text("homer", fontWeight = FontWeight.Bold) }
                )
            }
        }
    }

    @Test
    fun navigationBar() = captureDSComponent("navigation_bar") {
        DSNavigationBar(items = items, selection = Tab.Messages, onSelectionChange = {})
    }

    @Test
    fun navigationRail() = captureDSComponent("navigation_rail") {
        DSNavigationRail(items = items, selection = Tab.Home, onSelectionChange = {}, modifier = Modifier.height(400.dp))
    }
}
