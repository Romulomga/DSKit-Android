package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

/**
 * Abas primárias do Material 3, no topo do conteúdo: de duas a cinco seções irmãs da mesma tela
 * ("Minhas", "Parceiros"). Para trocar de seção deslizando, use [DSTabbedPager].
 */
@Composable
public fun DSTabs(tabs: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val haptics = rememberDSHaptics()

    PrimaryTabRow(selectedTabIndex = selectedIndex, modifier = modifier) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedIndex,
                onClick = {
                    if (index != selectedIndex) haptics.light()
                    onSelect(index)
                },
                text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

/**
 * [DSTabs] com as páginas embaixo, que trocam deslizando para o lado ou tocando na aba; as duas coisas ficam
 * sincronizadas.
 */
@Composable
public fun DSTabbedPager(tabs: List<String>, modifier: Modifier = Modifier, page: @Composable (index: Int) -> Unit) {
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        DSTabs(
            tabs = tabs,
            selectedIndex = pagerState.currentPage,
            onSelect = { scope.launch { pagerState.animateScrollToPage(it) } }
        )

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { index ->
            page(index)
        }
    }
}

@DSPreviews
@Composable
private fun DSTabsPreview() {
    var tab by remember { mutableIntStateOf(0) }

    DSPreviewContainer("Tabs") {
        DSTabs(tabs = listOf("Mine", "Partners", "Saved"), selectedIndex = tab, onSelect = { tab = it })

        DSTabbedPager(tabs = listOf("Photos", "Details")) { index ->
            Text("Page ${index + 1}", modifier = Modifier.padding(DSSpacing.lg))
        }
    }
}
