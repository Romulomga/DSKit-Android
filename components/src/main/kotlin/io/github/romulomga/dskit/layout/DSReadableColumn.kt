package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Medidas de layout de tela larga (tablet, dobrável aberto, paisagem). */
public object DSLayout {
    /** Largura máxima da coluna de leitura: um celular grande, com espaço para dois cards lado a lado. */
    public val readableWidth: Dp = 600.dp

    /** A partir desta largura, o [DSAdaptiveColumns] põe as duas colunas lado a lado. */
    public val twoColumnBreakpoint: Dp = 680.dp
}

/**
 * Centra o conteúdo numa coluna de no máximo [maxWidth] quando sobra largura, e não faz nada no celular.
 * A [DSScreenColumn] já aplica a coluna; numa `LazyColumn`, aplique no item ou no conteúdo.
 *
 * A largura vem das restrições do pai, nunca do conteúdo, então a margem não realimenta a medida.
 */
public fun Modifier.dsReadableColumn(maxWidth: Dp = DSLayout.readableWidth): Modifier = layout { measurable, constraints ->
    val limit = maxWidth.roundToPx()

    if (!constraints.hasBoundedWidth || constraints.maxWidth <= limit) {
        val placeable = measurable.measure(constraints)
        return@layout layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    val placeable = measurable.measure(constraints.copy(minWidth = minOf(constraints.minWidth, limit), maxWidth = limit))
    val width = constraints.maxWidth

    layout(width, placeable.height) { placeable.place((width - placeable.width) / 2, 0) }
}

/**
 * Duas colunas de conteúdo: a principal no começo e a secundária no fim (espelhadas da direita para a
 * esquerda) quando a largura comporta as duas ([breakpoint]), uma embaixo da outra abaixo disso, na mesma
 * ordem. Para o detalhe de tablet (o conteúdo de um lado, o resumo e as ações do outro). Empilhadas, coluna
 * vazia não ocupa espaço; lado a lado, cada uma fica com metade da largura.
 *
 * Dentro de uma [DSScreenColumn] as duas colunas nunca cabem, porque ela limita a largura a
 * [DSLayout.readableWidth]; use numa coluna própria, com `Modifier.verticalScroll`.
 */
@Composable
public fun DSAdaptiveColumns(
    modifier: Modifier = Modifier,
    breakpoint: Dp = DSLayout.twoColumnBreakpoint,
    spacing: Dp = DSSpacing.xl,
    primary: @Composable ColumnScope.() -> Unit,
    secondary: @Composable ColumnScope.() -> Unit
) {
    Layout(
        modifier = modifier.fillMaxWidth(),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing), content = primary)

            Column(verticalArrangement = Arrangement.spacedBy(spacing), content = secondary)
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = spacing.roundToPx()

        if (constraints.hasBoundedWidth && width >= breakpoint.roundToPx()) {
            val columnWidth = (width - gap) / 2
            val placeables = measurables.map { it.measure(Constraints.fixedWidth(columnWidth)) }

            layout(width, placeables.maxOf { it.height }) {
                placeables.forEachIndexed { index, placeable -> placeable.placeRelative(index * (columnWidth + gap), 0) }
            }
        } else {
            val placeables = measurables.map { it.measure(constraints.copy(minHeight = 0)) }
            val visible = placeables.filter { it.height > 0 }
            val height = visible.sumOf { it.height } + gap * (visible.size - 1).coerceAtLeast(0)

            layout(width, height) {
                var y = 0

                placeables.forEach { placeable ->
                    placeable.placeRelative(0, y)
                    if (placeable.height > 0) y += placeable.height + gap
                }
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSAdaptiveColumnsPreview() {
    DSPreviewContainer("Adaptive columns") {
        DSAdaptiveColumns(
            primary = { DSCard { Text("Photos and description") } },
            secondary = { DSCard { Text("Price and contact") } }
        )
    }
}
