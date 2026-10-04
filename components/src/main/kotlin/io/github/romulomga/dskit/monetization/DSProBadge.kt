package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import io.github.romulomga.dskit.components.R

/**
 * Pílula "PRO" para marcar recursos premium em listas e cabeçalhos. O fundo é um degradê da cor primária
 * do tema (`DSTheme.primary`), então acompanha a marca do app.
 */
@Composable
public fun DSProBadge(modifier: Modifier = Modifier, text: String = stringResource(R.string.dskit_pro)) {
    val primary = DSTheme.primary

    Text(
        text = text,
        style = DSTypography.caption2.copy(fontWeight = FontWeight.Bold),
        color = DSTheme.colors.onAccent,
        modifier = modifier
            .clip(DSRadius.capsule)
            .background(Brush.linearGradient(listOf(primary, primary.copy(alpha = 0.78f))))
            .padding(horizontal = DSSpacing.xs + DSSpacing.xxs, vertical = DSSpacing.xxs)
    )
}

@DSPreviews
@Composable
private fun DSProBadgePreview() {
    DSPreviewContainer("Pro badge") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSProBadge()

            DSProBadge(text = "NEW")

            DSProBadge(text = "BETA")
        }
    }
}
