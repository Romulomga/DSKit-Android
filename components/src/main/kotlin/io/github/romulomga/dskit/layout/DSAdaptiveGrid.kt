package io.github.romulomga.dskit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Grade com número de colunas adaptativo, derivado da largura mínima do item: em tablet ou paisagem cabem
 * mais colunas sem calcular breakpoints. Equivale ao `LazyVGrid` com `GridItem(.adaptive)` do iOS.
 *
 * Não é preguiçosa: mede todos os filhos, então pode ficar dentro de uma coluna que rola (`DSScreen`).
 * Para centenas de itens, use `LazyVerticalGrid(GridCells.Adaptive(...))`. Cada item ocupa a largura da
 * coluna e fica centralizado na altura da linha, como no iOS.
 */
@Composable
public fun DSAdaptiveGrid(
    modifier: Modifier = Modifier,
    minItemWidth: Dp = 160.dp,
    spacing: Dp = DSSpacing.md,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val minPx = minItemWidth.roundToPx()
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else max(minPx, constraints.minWidth)
        val columns = max(1, (width + spacingPx) / (minPx + spacingPx))
        val itemWidth = max(0, (width - spacingPx * (columns - 1)) / columns)

        val placeables = measurables.map { it.measure(Constraints.fixedWidth(itemWidth)) }
        val rows = placeables.chunked(columns)
        val rowHeights = rows.map { row -> row.maxOf { it.height } }
        val height = rowHeights.sum() + spacingPx * max(0, rows.size - 1)

        layout(width, height.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                val rowHeight = rowHeights[rowIndex]
                row.forEachIndexed { column, placeable ->
                    placeable.placeRelative(
                        x = column * (itemWidth + spacingPx),
                        y = y + (rowHeight - placeable.height) / 2
                    )
                }
                y += rowHeight + spacingPx
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSAdaptiveGridPreview() {
    DSPreviewContainer("Adaptive grid") {
        DSAdaptiveGrid(minItemWidth = 140.dp) {
            repeat(6) { i ->
                DSCard { Text("Item ${i + 1}", style = DSTypography.headline, color = DSTheme.colors.onSurfaceHigh) }
            }
        }
    }
}
