package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.junit.Test

class FoundationScreenshotTest : ScreenshotTestBase() {
    @Test
    fun typography() = snapshot("foundation_typography") {
        val type = DSTheme.typography

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
            Text("Headline large", style = type.headlineLarge)
            Text("Headline small", style = type.headlineSmall)
            Text("Title large", style = type.titleLarge)
            Text("Title medium", style = type.titleMedium)
            Text("Body large", style = type.bodyLarge)
            Text("Body medium", style = type.bodyMedium)
            Text("Label large", style = type.labelLarge)
            Text("Label small", style = type.labelSmall)
        }
    }

    @Test
    fun colors() = snapshot("foundation_colors") {
        val colors = DSTheme.colors
        val status = DSTheme.status

        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm), verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            Swatch("primary", colors.primary, colors.onPrimary)
            Swatch("primaryContainer", colors.primaryContainer, colors.onPrimaryContainer)
            Swatch("secondaryContainer", colors.secondaryContainer, colors.onSecondaryContainer)
            Swatch("tertiary", colors.tertiary, colors.onTertiary)
            Swatch("error", colors.error, colors.onError)
            Swatch("success", status.success.color, status.success.onColor)
            Swatch("warning", status.warning.container, status.warning.onContainer)
            Swatch("info", status.info.container, status.info.onContainer)
        }
    }

    @Test
    fun icons() = snapshot("foundation_icons") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm), verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            listOf(
                DSIcons.ArrowBack, DSIcons.ChevronRight, DSIcons.ArrowDropDown, DSIcons.Check, DSIcons.Close, DSIcons.Cancel,
                DSIcons.Add, DSIcons.Remove, DSIcons.Search, DSIcons.MoreVert, DSIcons.Visibility, DSIcons.VisibilityOff,
                DSIcons.Error, DSIcons.Info, DSIcons.Warning, DSIcons.CheckCircle, DSIcons.Refresh, DSIcons.Inbox,
                DSIcons.CloudOff, DSIcons.Home, DSIcons.Person, DSIcons.Edit, DSIcons.Delete, DSIcons.Share, DSIcons.Star,
                DSIcons.Lock
            ).forEach { Icon(it, contentDescription = null, tint = DSTheme.colors.onSurface) }
        }
    }

    @androidx.compose.runtime.Composable
    private fun Swatch(name: String, color: Color, onColor: Color) {
        Row(Modifier.background(color, DSTheme.shapes.small).padding(DSSpacing.sm)) {
            Text(name, style = DSTheme.typography.labelMedium, color = onColor)
        }
    }
}
