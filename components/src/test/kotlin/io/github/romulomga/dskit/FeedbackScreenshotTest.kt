package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import org.junit.Test

class FeedbackScreenshotTest : ScreenshotTestBase() {
    @Test
    fun emptyState() = snapshot("feedback_empty_state") {
        DSEmptyState(
            icon = DSIcons.Inbox,
            title = "No posts yet",
            message = "Publish a property to start finding partners.",
            actionText = "Publish",
            onAction = {}
        )
    }

    @Test
    fun errorState() = snapshot("feedback_error_state") {
        DSErrorState(title = "Couldn't load", message = "Check your connection and try again.", onRetry = {})
    }

    @Test
    fun linearProgress() = snapshot("feedback_linear_progress") {
        DSLinearProgress(progress = 0.4f)
    }

    @Test
    fun snackbars() = snapshot("feedback_snackbar") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSSnackbarForPreview(DSSnackbarVisuals("Draft saved"))

            DSSnackbarForPreview(DSSnackbarVisuals("Property published", DSSnackbarStyle.Success, actionLabel = "View"))

            DSSnackbarForPreview(DSSnackbarVisuals("No connection", DSSnackbarStyle.Error, actionLabel = "Retry"))
        }
    }

    @Test
    fun banners() = snapshot("feedback_banner") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSBanner(message = "Your plan renews in 3 days.", title = "Plan", actionText = "Manage", onAction = {}, onDismiss = {})

            DSBanner(message = "Property published.", style = DSBannerStyle.Success)

            DSBanner(message = "You're offline. Showing saved data.", style = DSBannerStyle.Warning, onDismiss = {})

            DSBanner(message = "Payment failed. Update your card.", style = DSBannerStyle.Error, actionText = "Update", onAction = {})
        }
    }

    @Test
    fun badges() = snapshot("feedback_badge") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.xl)) {
            DSBadgedBox(count = 0) { Icon(DSIcons.Inbox, contentDescription = "Inbox") }

            DSBadgedBox(count = 3, badgeDescription = "3 unread") { Icon(DSIcons.Inbox, contentDescription = "Inbox") }

            DSBadgedBox(count = 120) { Icon(DSIcons.Inbox, contentDescription = "Inbox") }

            DSBadge(count = 7)
        }
    }
}
