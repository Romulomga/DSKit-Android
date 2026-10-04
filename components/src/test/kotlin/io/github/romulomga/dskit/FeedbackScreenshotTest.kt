package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test

class FeedbackScreenshotTest : ScreenshotTestBase() {
    @Test
    fun toast() = snapshot("feedback_toast") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSToast("Info notice", style = DSToastStyle.Info)

            DSToast("Saved successfully", style = DSToastStyle.Success)

            DSToast("Something went wrong", style = DSToastStyle.Error)

            DSToast("Heads up", style = DSToastStyle.Warning)

            DSToast("A much longer message that has to wrap onto a second line and then gets cut", style = DSToastStyle.Error)
        }
    }

    @Test
    fun toastHost() = snapshot("feedback_toast_host") {
        Box(Modifier.fillMaxWidth().heightIn(min = DSTouchTarget.minimum * 3)) {
            Text("Content", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)

            DSToastHost(DSToastMessage("Copied to clipboard", style = DSToastStyle.Success), onDismiss = {})
        }
    }

    @Test
    fun errorState() = snapshot("feedback_error_state") {
        DSErrorState(
            title = "Couldn't load",
            message = "Check your connection and try again.",
            onRetry = {}
        )
    }

    @Test
    fun emptyState() = snapshot("feedback_empty_state") {
        DSEmptyState(
            icon = DSIcons.Tray,
            title = "No participants yet",
            message = "Add at least two names to draw a winner.",
            actionTitle = "Add names",
            onAction = {}
        )
    }

    @Test
    fun progressBar() = snapshot("feedback_progress_bar") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSProgressBar(value = 0f)

            DSProgressBar(value = 3f / 8f)

            DSProgressBar(value = 1.4f, height = 8.dp)
        }
    }
}
