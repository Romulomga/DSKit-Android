package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import org.junit.Test

class ListsScreenshotTest : ScreenshotTestBase() {
    @Test
    fun listItems() = snapshot("lists_items") {
        Column {
            DSListSectionHeader("Account")

            DSListItem("Profile", supporting = "Name, photo and license", leadingIcon = DSIcons.Person, onClick = {})

            DSListItem("Plan", overline = "Subscription", supporting = "Renews on May 3", trailing = { Text("Pro") })

            DSSwitchListItem("Notifications", supporting = "New partners and messages", checked = true, onCheckedChange = {})

            DSSwitchListItem("Disabled", checked = false, onCheckedChange = {}, enabled = false)
        }
    }

    @Test
    fun selectionItems() = snapshot("lists_selection") {
        Column {
            DSListSectionHeader("Property type")

            Column(Modifier.selectableGroup()) {
                DSRadioListItem("Apartment", selected = true, onClick = {})

                DSRadioListItem("House", selected = false, onClick = {})
            }

            HorizontalDivider()

            DSCheckboxListItem("Pool", checked = true, onCheckedChange = {})

            DSCheckboxListItem("Gym", supporting = "In the building", checked = false, onCheckedChange = {})
        }
    }

    @Test
    fun sheetActions() = snapshot("lists_sheet_actions") {
        Column {
            DSSheetAction(text = "Edit", icon = DSIcons.Edit, onClick = {})

            DSSheetAction(text = "Share", icon = DSIcons.Share, onClick = {})

            DSSheetAction(text = "Delete post", icon = DSIcons.Delete, destructive = true, onClick = {})
        }
    }
}
