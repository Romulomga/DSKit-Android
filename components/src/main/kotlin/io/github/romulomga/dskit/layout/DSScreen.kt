package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Uma tela do app, o `Scaffold` do Material 3 com o que toda tela repete: a barra superior (que encolhe ou
 * muda de cor ao rolar), o FAB, a barra inferior, o snackbar e o "puxar para atualizar".
 *
 * O conteúdo recebe o `PaddingValues` das barras, como no `Scaffold`, e decide como rolar: uma
 * [DSScreenColumn] para formulários e telas curtas, uma `LazyColumn` com [DSScreenDefaults.contentPadding]
 * para listas.
 *
 * ```
 * DSScreen(title = "Publicações", topBarStyle = DSTopAppBarStyle.Large, floatingActionButton = { DSFab(…) }) { padding ->
 *     LazyColumn(contentPadding = DSScreenDefaults.contentPadding(padding)) { items(posts) { PostCard(it) } }
 * }
 * ```
 *
 * @param title título da barra superior; sem título e sem [onBack], a tela não tem barra.
 * @param topBarStyle o tamanho da barra; `Medium` e `Large` encolhem ao rolar.
 * @param onBack mostra a seta de voltar.
 * @param actions botões à direita da barra.
 * @param snackbarHostState onde os snackbars da tela aparecem (acima do FAB e da barra inferior).
 * @param onRefresh liga o "puxar para atualizar"; [isRefreshing] mostra o indicador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DSScreen(
    modifier: Modifier = Modifier,
    title: String? = null,
    topBarStyle: DSTopAppBarStyle = DSTopAppBarStyle.Small,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val showsTopBar = title != null || onBack != null
    val scrollBehavior = when (topBarStyle) {
        DSTopAppBarStyle.Medium, DSTopAppBarStyle.Large -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        DSTopAppBarStyle.Small, DSTopAppBarStyle.CenterAligned -> TopAppBarDefaults.pinnedScrollBehavior()
    }

    Scaffold(
        modifier = modifier.then(if (showsTopBar) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier),
        topBar = {
            if (showsTopBar) {
                TopAppBarContent(
                    title = title.orEmpty(),
                    modifier = Modifier,
                    style = topBarStyle,
                    onBack = onBack,
                    actions = actions,
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = bottomBar,
        snackbarHost = { if (snackbarHostState != null) DSSnackbarHost(snackbarHostState) },
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        if (onRefresh == null) {
            content(innerPadding)
        } else {
            // O indicador desce por baixo da barra superior: a caixa começa onde a barra termina.
            val layoutDirection = LocalLayoutDirection.current

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())
            ) {
                content(
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
            }
        }
    }
}

/** Medidas de conteúdo de tela. */
public object DSScreenDefaults {
    /** Margem lateral do conteúdo, a do Material em janela compacta. */
    public val margin: Dp = DSSpacing.lg

    /**
     * O `PaddingValues` das barras ([innerPadding]) somado às margens de tela, para o `contentPadding` de uma
     * `LazyColumn`: o primeiro e o último item não encostam nas barras e a lista rola por baixo delas.
     *
     * @param horizontalMargin margem lateral; passe `0.dp` numa lista de [DSListItem], que já tem a sua.
     */
    @Composable
    public fun contentPadding(innerPadding: PaddingValues, horizontalMargin: Dp = margin): PaddingValues {
        val layoutDirection = LocalLayoutDirection.current

        return PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection) + horizontalMargin,
            end = innerPadding.calculateEndPadding(layoutDirection) + horizontalMargin,
            top = innerPadding.calculateTopPadding() + DSSpacing.sm,
            bottom = innerPadding.calculateBottomPadding() + DSSpacing.xl
        )
    }
}

/**
 * O conteúdo de um [DSScreen] que rola como um todo (formulário, detalhe): coluna com as margens de tela,
 * espaço entre os blocos, coluna de leitura em tela larga e folga para o teclado, para o campo focado nunca
 * ficar escondido.
 *
 * ```
 * DSScreen(title = "Editar perfil", onBack = ::pop) { padding ->
 *     DSScreenColumn(padding) { DSTextField(…); DSButton(…) }
 * }
 * ```
 */
@Composable
public fun DSScreenColumn(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(DSSpacing.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(innerPadding)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(DSScreenDefaults.contentPadding(innerPadding))
            .dsReadableColumn(),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@DSPreviews
@Composable
private fun DSScreenPreview() {
    DSPreviewContainer("Screen", modifier = Modifier.height(480.dp)) {
        DSScreen(title = "Edit profile", onBack = {}, floatingActionButton = { DSFab(DSIcons.Check, "Save", onClick = {}) }) { padding ->
            DSScreenColumn(padding) {
                DSCard { Text("Content goes here") }
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSScreenLazyPreview() {
    DSPreviewContainer("Lazy screen", modifier = Modifier.height(480.dp)) {
        DSScreen(title = "Messages", topBarStyle = DSTopAppBarStyle.Large) { padding ->
            LazyColumn(contentPadding = DSScreenDefaults.contentPadding(padding, horizontalMargin = 0.dp)) {
                items(20) { index -> DSListItem("Conversation ${index + 1}", supporting = "Last message") }
            }
        }
    }
}
