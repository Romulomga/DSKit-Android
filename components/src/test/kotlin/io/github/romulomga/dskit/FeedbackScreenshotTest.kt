package io.github.romulomga.dskit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Test

class FeedbackScreenshotTest : ScreenshotTestBase() {
    /** Um indicador do app parado e fácil de ver (um quadrado na cor recebida), para a captura não depender do relógio. */
    private val squareIndicator: @Composable (Modifier, Color) -> Unit = { modifier, color ->
        Box(modifier.border(2.dp, color))
    }

    @Test
    fun customLoadingIndicator() = snapshot("feedback_loading_indicator_custom") {
        CompositionLocalProvider(LocalDSLoadingIndicator provides squareIndicator) {
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSButton("Publishing", onClick = {}, loading = true)

                DSTonalButton("Saving", onClick = {}, loading = true)

                DSLoadingState(message = "Loading properties…")

                Box(Modifier.fillMaxWidth().height(200.dp)) {
                    DSCard { Text("Form behind the veil") }

                    LoadingVeil(message = "Publishing…")
                }
            }
        }
    }

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

            DSSnackbarForPreview(DSSnackbarVisuals("Listing renewed", DSSnackbarStyle.Info))
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
