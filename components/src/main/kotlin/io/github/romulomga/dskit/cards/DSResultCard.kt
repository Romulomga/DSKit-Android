package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** Uma seção com nome dentro do [DSResultCard] agrupado. */
@Immutable
public data class DSResultSection(val title: String, val items: List<String>)

/**
 * Destaca um resultado (sorteio, conversão) com um resultado único em destaque, por exemplo o vencedor.
 * O texto encolhe até metade do tamanho antes de quebrar (no máximo duas linhas).
 */
@Composable
public fun DSResultCard(title: String, primaryResult: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    ResultCardFrame(title, modifier) {
        // Altura de linha proporcional: com a fixa (41 sp), o texto encolhido ficaria com vão entre as linhas.
        val style = DSTypography.resultPrimary.tabular().copy(lineHeight = 1.2.em)

        BasicText(
            text = primaryResult,
            style = style.copy(color = DSTheme.colors.onSurfaceHigh),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = DSTypography.resultPrimary.fontSize * 0.5f,
                maxFontSize = DSTypography.resultPrimary.fontSize,
                stepSize = 1.sp
            )
        )

        if (subtitle != null) {
            Text(subtitle, style = DSTypography.subheadline, color = DSTheme.colors.onSurfaceMedium)
        }
    }
}

/** Destaca uma lista ordenada de resultados ("1. Ana", "2. Bruno"…). */
@Composable
public fun DSResultCard(title: String, results: List<String>, modifier: Modifier = Modifier, subtitle: String? = null) {
    ResultCardFrame(title, modifier) {
        val style = DSTypography.title3.tabular()

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            results.forEachIndexed { index, item ->
                Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                    Text("${index + 1}.", style = style, color = DSTheme.colors.onSurfaceMedium)

                    Text(item, style = style, color = DSTheme.colors.onSurfaceHigh)
                }
            }
        }

        if (subtitle != null) {
            Text(subtitle, style = DSTypography.footnote, color = DSTheme.colors.onSurfaceMedium)
        }
    }
}

/** Destaca resultados divididos em seções com nome (times, grupos). */
@JvmName("DSResultCardGrouped")
@Composable
public fun DSResultCard(title: String, groupedResults: List<DSResultSection>, modifier: Modifier = Modifier) {
    ResultCardFrame(title, modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            groupedResults.forEach { section ->
                Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
                    Text(
                        text = section.title,
                        style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold),
                        color = DSTheme.colors.onSurfaceMedium
                    )

                    section.items.forEach { item ->
                        Text(item, style = DSTypography.body.tabular(), color = DSTheme.colors.onSurfaceHigh)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCardFrame(title: String, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(DSRadius.large)
            .background(DSTheme.colors.surface(DSTheme.surfaceLevel))
            .semantics(mergeDescendants = true) {}
            .padding(DSSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(DSSpacing.md)
    ) {
        Text(
            text = title.uppercase(),
            style = DSTypography.caption.copy(letterSpacing = 0.5.sp),
            color = DSTheme.colors.onSurfaceMedium
        )

        content()
    }
}

/** Algarismos de largura fixa, o `monospacedDigit()` do SwiftUI. */
private fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = "tnum")

@DSPreviews
@Composable
private fun DSResultCardPreview() {
    DSPreviewContainer("Result card") {
        DSResultCard(title = "Winner", primaryResult = "Ana", subtitle = "Picked from 12 participants")

        DSResultCard(title = "Top 3", results = listOf("Ana", "Bruno", "Carla"), subtitle = "Out of 12 participants")

        DSResultCard(
            title = "Teams",
            groupedResults = listOf(
                DSResultSection("Team A", listOf("Ana", "Bruno")),
                DSResultSection("Team B", listOf("Carla", "Diego"))
            )
        )
    }
}
