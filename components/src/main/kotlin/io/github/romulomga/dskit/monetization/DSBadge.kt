package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * Cápsula pequena e tingida para marcar atributos curtos ("7 dias grátis", "Economize 70%", "Novo").
 * Para o selo "PRO" do app inteiro, use [DSProBadge].
 *
 * @param tint cor do texto e, a 14 %, do fundo. Padrão: `DSTheme.colors.accent`.
 */
@Composable
public fun DSBadge(text: String, modifier: Modifier = Modifier, tint: Color = DSTheme.colors.accent) {
    Text(
        text = text,
        style = DSTypography.caption2.copy(fontWeight = FontWeight.SemiBold),
        color = tint,
        modifier = modifier
            .clip(DSRadius.capsule)
            .background(tint.copy(alpha = 0.14f))
            .padding(horizontal = DSSpacing.xs, vertical = DSSpacing.xxs)
    )
}

@DSPreviews
@Composable
private fun DSBadgePreview() {
    DSPreviewContainer("Badge") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSBadge("7 days free", tint = DSTheme.colors.successHigh)

            DSBadge("Save 70%", tint = DSTheme.colors.warningHigh)

            DSBadge("New")
        }
    }
}
