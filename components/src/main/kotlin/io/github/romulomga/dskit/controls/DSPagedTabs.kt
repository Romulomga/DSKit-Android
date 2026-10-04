package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

/** Como a faixa de abas de um `DSPagedTabs` é desenhada. */
public enum class DSPagedTabsStyle {
    /** Rótulos sobre uma linha fina, com um indicador em cápsula sob o atual. */
    Underline,

    /** A pílula do `DSSegmentedPicker(style = Filled)`: a cápsula primária desliza com as páginas. */
    Segmented
}

/**
 * Abas cujas páginas deslizam na horizontal, uma página por seção, com a faixa de rótulos em cima.
 *
 * O indicador na cor primária acompanha o dedo durante o deslize; o rótulo atual ganha peso e a cor primária;
 * o TalkBack anuncia a aba em que a página parou. Até três seções dividem a largura por igual; com mais, a
 * faixa rola.
 *
 * `header` fica entre a faixa e as páginas (uma busca, filtros) e não desliza. Dê ao `DSPagedTabs` uma altura
 * (`Modifier.weight(1f)` ou `fillMaxSize()`): as páginas ocupam o espaço que sobra.
 *
 * @param sections as seções, uma por página.
 * @param selection a seção atual.
 * @param onSelectionChange chamado quando uma aba é tocada ou quando o deslize para numa página nova.
 * @param label rótulo de cada seção na faixa.
 * @param style `Underline` (padrão) ou `Segmented`.
 * @param stripInset margem horizontal da faixa; `null` usa a do estilo (0 no `Underline`, `DSSpacing.lg` no `Segmented`).
 * @param header conteúdo fixo entre a faixa e as páginas.
 * @param content a página de cada seção.
 */
@Composable
public fun <T> DSPagedTabs(
    sections: List<T>,
    selection: T,
    onSelectionChange: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    style: DSPagedTabsStyle = DSPagedTabsStyle.Underline,
    stripInset: Dp? = null,
    header: @Composable () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    val reduceMotion = DSTheme.reduceMotion
    val selectedIndex = sections.indexOf(selection).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = selectedIndex) { sections.size }
    val progress by remember(pagerState) {
        derivedStateOf { pagerState.currentPage + pagerState.currentPageOffsetFraction }
    }
    val currentSections by rememberUpdatedState(sections)
    val currentSelection by rememberUpdatedState(selection)
    val currentOnSelectionChange by rememberUpdatedState(onSelectionChange)

    // Toque numa aba (ou seleção trocada por fora): as páginas vão até ela.
    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex || pagerState.currentPageOffsetFraction != 0f) {
            if (reduceMotion) {
                pagerState.scrollToPage(selectedIndex)
            } else {
                pagerState.animateScrollToPage(selectedIndex, animationSpec = DSMotion.medium())
            }
        }
    }

    // Deslize que parou numa página nova: ela vira a seleção.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val landed = currentSections.getOrNull(page) ?: return@collect
            if (landed != currentSelection && !pagerState.isScrollInProgress) currentOnSelectionChange(landed)
        }
    }

    val onTabClick: (Int) -> Unit = { index -> sections.getOrNull(index)?.let(onSelectionChange) }
    val labels = sections.map(label)

    Column(modifier = modifier) {
        when (style) {
            DSPagedTabsStyle.Underline -> DSTabStrip(
                labels = labels,
                selectedIndex = selectedIndex,
                progress = { progress },
                onTabClick = onTabClick,
                modifier = Modifier.padding(horizontal = stripInset ?: 0.dp)
            )

            DSPagedTabsStyle.Segmented -> DSPillTrack(
                count = labels.size,
                capsuleProgress = { progress },
                modifier = Modifier
                    .padding(horizontal = stripInset ?: DSSpacing.lg)
                    .padding(vertical = DSSpacing.sm),
                semantics = { collectionInfo = CollectionInfo(rowCount = 1, columnCount = labels.size) }
            ) {
                labels.forEachIndexed { index, text ->
                    DSPillSegment(
                        label = text,
                        isCurrent = abs(progress - index) < 0.5f,
                        selected = index == selectedIndex,
                        drawsOwnCapsule = false,
                        enabled = true,
                        onClick = { onTabClick(index) },
                        semantics = { collectionItemInfo = CollectionItemInfo(0, 1, index, 1) }
                    )
                }
            }
        }

        header()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                // O título do painel muda com a aba: o TalkBack anuncia a aba em que a página parou.
                .semantics { paneTitle = labels.getOrElse(selectedIndex) { "" } }
        ) { page ->
            val hidden = page != selectedIndex

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (hidden) Modifier.clearAndSetSemantics {} else Modifier)
            ) {
                sections.getOrNull(page)?.let { content(it) }
            }
        }
    }
}

/** Posição e largura de um elemento dentro da faixa, em px. */
private data class StripSlot(val left: Float, val width: Float)

/**
 * A faixa de rótulos: um botão por seção e o indicador interpolado entre o centro e a largura dos rótulos
 * das duas seções mais próximas de `progress`.
 */
@Composable
private fun DSTabStrip(
    labels: List<String>,
    selectedIndex: Int,
    progress: () -> Float,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val fill = labels.size <= MaxFixedTabs
    val tabSlots = remember { mutableStateMapOf<Int, StripSlot>() }
    val labelWidths = remember { mutableStateMapOf<Int, Float>() }
    val primary = DSTheme.primary
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (fill) Modifier else Modifier.horizontalScroll(rememberScrollState()))
    ) {
        Row(
            modifier = Modifier
                .then(if (fill) Modifier.fillMaxWidth() else Modifier)
                .selectableGroup()
                .semantics { collectionInfo = CollectionInfo(rowCount = 1, columnCount = labels.size) }
        ) {
            labels.forEachIndexed { index, text ->
                val activeness = (1f - abs(progress() - index)).coerceAtLeast(0f)
                val isCurrent = activeness > 0.5f
                val style = if (isCurrent) {
                    DSTypography.subheadline.copy(fontWeight = FontWeight.SemiBold)
                } else {
                    DSTypography.subheadline
                }
                val color = if (isCurrent) primary else DSTheme.colors.onSurfaceMedium

                Box(
                    modifier = Modifier
                        .then(if (fill) Modifier.weight(1f) else Modifier)
                        .onPlaced { tabSlots[index] = StripSlot(it.positionInParent().x, it.size.width.toFloat()) }
                        .selectable(selected = index == selectedIndex, role = Role.Tab, onClick = { onTabClick(index) })
                        .semantics { collectionItemInfo = CollectionItemInfo(0, 1, index, 1) }
                        .heightIn(min = DSTouchTarget.minimum)
                        .padding(horizontal = DSSpacing.md, vertical = DSSpacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = text,
                        style = style,
                        color = { color },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = style.fontSize * TabMinimumScale,
                            maxFontSize = style.fontSize,
                            stepSize = style.fontSize * TabAutoSizeStep
                        ),
                        modifier = Modifier.onSizeChanged { labelWidths[index] = it.width.toFloat() }
                    )
                }
            }
        }

        // Linha fina sob a faixa inteira.
        Box(
            modifier = Modifier
                .matchParentSize()
                .wrapContentHeight(Alignment.Bottom)
                .height(StripHairline)
                .background(DSTheme.colors.hairline)
        )

        val geometry = indicatorGeometry(labels.size, progress(), tabSlots, labelWidths)
        if (geometry != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset { IntOffset(geometry.left.roundToInt(), 0) }
                    .width(with(density) { geometry.width.toDp() })
                    .height(IndicatorHeight)
                    .background(primary, DSRadius.capsule)
            )
        }
    }
}

/** Centro e largura entre as duas seções mais próximas de `progress`. */
private fun indicatorGeometry(count: Int, progress: Float, tabSlots: Map<Int, StripSlot>, labelWidths: Map<Int, Float>): StripSlot? {
    if (count == 0) return null
    val clamped = progress.coerceIn(0f, (count - 1).toFloat())
    val lower = floor(clamped).toInt()
    val upper = (lower + 1).coerceAtMost(count - 1)
    val fraction = clamped - lower
    val lowerSlot = tabSlots[lower] ?: return null
    val upperSlot = tabSlots[upper] ?: return null
    val lowerWidth = labelWidths[lower] ?: (lowerSlot.width / 2)
    val upperWidth = labelWidths[upper] ?: (upperSlot.width / 2)
    val width = lowerWidth + (upperWidth - lowerWidth) * fraction
    val lowerCenter = lowerSlot.left + lowerSlot.width / 2
    val upperCenter = upperSlot.left + upperSlot.width / 2
    val center = lowerCenter + (upperCenter - lowerCenter) * fraction
    return StripSlot(center - width / 2, width)
}

private const val MaxFixedTabs = 3
private val StripHairline = 1.dp
private val IndicatorHeight = 3.dp
private const val TabMinimumScale = 0.8f
private const val TabAutoSizeStep = 0.05f

@DSPreviews
@Composable
private fun DSPagedTabsPreview() {
    DSPreviewContainer("Paged tabs") {
        val pages = listOf("4 Active items", "1 Inactive item")
        var selection by remember { mutableStateOf(pages.first()) }

        DSPagedTabs(
            sections = pages,
            selection = selection,
            onSelectionChange = { selection = it },
            label = { it },
            style = DSPagedTabsStyle.Segmented,
            modifier = Modifier.height(320.dp),
            header = {
                Text(
                    "Header between the strip and the pages",
                    style = DSTypography.footnote,
                    color = DSTheme.colors.onSurfaceMedium,
                    modifier = Modifier.padding(DSSpacing.md)
                )
            }
        ) { page ->
            Column(
                modifier = Modifier.padding(DSSpacing.md),
                verticalArrangement = Arrangement.spacedBy(DSSpacing.md)
            ) {
                repeat(3) { row ->
                    Text("$page · row ${row + 1}", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)
                }
            }
        }
    }
}
