package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import org.junit.Test

class FoundationScreenshotTest : ScreenshotTestBase() {
    @Test
    fun typography() = snapshot("foundation_typography") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
            Text("Large title", style = DSTypography.largeTitle, color = DSTheme.colors.onSurfaceHigh)
            Text("Title", style = DSTypography.title, color = DSTheme.colors.onSurfaceHigh)
            Text("Headline", style = DSTypography.headline, color = DSTheme.colors.onSurfaceHigh)
            Text("Body", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)
            Text("Subheadline", style = DSTypography.subheadline, color = DSTheme.colors.onSurfaceMedium)
            Text("Caption", style = DSTypography.caption, color = DSTheme.colors.onSurfaceMedium)
        }
    }

    @Test
    fun icons() = snapshot("foundation_icons") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            listOf(
                DSIcons.Check, DSIcons.Close, DSIcons.ChevronRight, DSIcons.ChevronDown, DSIcons.Eye, DSIcons.EyeSlash,
                DSIcons.ExclamationCircle, DSIcons.InfoCircle, DSIcons.CheckCircle, DSIcons.XCircle, DSIcons.Lock
            ).forEach { Icon(it, contentDescription = null, tint = DSTheme.colors.onSurfaceHigh) }
        }
    }
}
