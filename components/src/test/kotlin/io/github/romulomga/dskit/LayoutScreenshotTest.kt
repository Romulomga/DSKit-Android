package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.junit.Test

class LayoutScreenshotTest : ScreenshotTestBase() {
    @Test
    fun screenForm() = snapshot("layout_screen_form") {
        Box(Modifier.height(480.dp)) {
            DSScreen(
                title = "Edit profile",
                onBack = {},
                actions = { DSIconButton(DSIcons.MoreVert, "More options", onClick = {}) }
            ) { padding ->
                DSScreenColumn(padding) {
                    DSTextField(value = "Rômulo", onValueChange = {}, label = "Name")

                    DSTextField(value = "", onValueChange = {}, label = "License", supportingText = "CRECI number")

                    DSButton("Save", onClick = {})
                }
            }
        }
    }

    @Test
    fun screenList() = snapshot("layout_screen_list") {
        Box(Modifier.height(520.dp)) {
            DSScreen(
                title = "Messages",
                topBarStyle = DSTopAppBarStyle.Large,
                floatingActionButton = { DSFab(DSIcons.Edit, "New message", onClick = {}) }
            ) { padding ->
                LazyColumn(contentPadding = DSScreenDefaults.contentPadding(padding, horizontalMargin = 0.dp)) {
                    items(12) { DSListItem("Conversation ${it + 1}", supporting = "Last message", leadingIcon = DSIcons.Person) }
                }
            }
        }
    }

    @Test
    fun adaptiveColumns() = snapshot("layout_adaptive_columns") {
        DSAdaptiveColumns(
            primary = { DSCard { Text("Photos and description") } },
            secondary = { DSCard { Text("Price and contact") } }
        )
    }

    @Test
    fun dividers() = snapshot("layout_divider") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSDivider()

            DSDivider(inset = DSDividerInset.Start)

            DSDivider(inset = DSDividerInset.Middle)

            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                Text("12 posts")

                DSVerticalDivider()

                Text("3 drafts")
            }
        }
    }
}
