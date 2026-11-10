package io.github.romulomga.dskit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import io.github.romulomga.dskit.components.R

/**
 * Painel modal que sobe da base da tela, o `ModalBottomSheet` do Material 3: escolhas rápidas, um
 * formulário curto, as ações de um item.
 *
 * Abre na altura do conteúdo: um painel curto não para no meio da tela.
 * Conteúdo maior que a tela, como em fonte grande, rola. Trata a barra de navegação do sistema.
 *
 * Declarativo: [visible] abre e fecha, com a animação nos dois sentidos.
 * ```
 * DSBottomSheet(visible = showOptions, onDismissRequest = { showOptions = false }, title = "Opções") {
 *     DSSheetAction("Excluir", onClick = { showOptions = false; delete() }, destructive = true)
 * }
 * ```
 *
 * Fechado por código, o painel desce em 250 ms com o véu apagando junto (a saída do Material chamada por código
 * dura um instante e parece um corte). A alça é lida "Fechar" pelo TalkBack, e tocar nela com o leitor fecha.
 *
 * @param visible se o painel está aberto. Ao virar `false`, o painel desce antes de sair da tela.
 * @param onDismissRequest o usuário arrastou para baixo, tocou fora ou usou o voltar: desligue [visible].
 * @param title título opcional no topo do painel.
 * @param scrollable `true` faz o painel rolar o conteúdo inteiro (o padrão, para conteúdo comum). Passe `false`
 * quando o conteúdo já rola sozinho, como uma `LazyColumn` com uma lista longa (países, contas): uma lista
 * preguiçosa dentro de um conteúdo que rola não tem altura máxima e trava o app.
 * @param onDismissBlocked com ele, arrastar para baixo e tocar no véu não fecham o painel e chamam este lambda:
 * para o painel que guarda algo digitado perguntar antes (`guard::requestExit` do [DSDiscardGuard]). O voltar
 * continua em [onDismissRequest]. `null` (o padrão) deixa o gesto fechar.
 * @param closeDescription o que o TalkBack lê na alça, por padrão "Fechar". Passe o texto do app quando ele tem
 * línguas que o DSKit não tem.
 */
@Composable
public fun DSBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    scrollable: Boolean = true,
    onDismissBlocked: (() -> Unit)? = null,
    closeDescription: String = stringResource(R.string.dskit_close),
    content: @Composable ColumnScope.() -> Unit
) {
    val state = rememberDSBottomSheetState()
    var shown by remember { mutableStateOf(visible) }

    // O painel só sai da composição depois de descer; fechado pelo gesto, já desceu, e o `hide` não faz nada.
    LaunchedEffect(visible) {
        if (visible) {
            state.reset()
            shown = true
        } else if (shown) {
            state.hide()
            shown = false
        }
    }

    if (!shown) return

    DSBottomSheet(state, onDismissRequest, modifier, title, scrollable, onDismissBlocked, closeDescription, content)
}

/**
 * O mesmo painel para quem o tira da composição por conta própria, como uma navegação que mostra uma tela num
 * painel: ele fica aberto enquanto estiver na composição. Antes de tirá-lo, chame [DSBottomSheetState.hide] para
 * ele descer; tirado direto, some sem animação.
 *
 * Com o Navigation 3, use o `DSBottomSheetSceneStrategy` do módulo opcional `navigation3`, que já faz isso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DSBottomSheet(
    state: DSBottomSheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    scrollable: Boolean = true,
    onDismissBlocked: (() -> Unit)? = null,
    closeDescription: String = stringResource(R.string.dskit_close),
    content: @Composable ColumnScope.() -> Unit
) {
    val blocked by rememberUpdatedState(onDismissBlocked)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            val onBlocked = blocked

            if (value == SheetValue.Hidden && onBlocked != null) {
                onBlocked()
                false
            } else {
                true
            }
        }
    )

    DisposableEffect(state, sheetState) {
        state.sheet = sheetState
        onDispose { if (state.sheet === sheetState) state.sheet = null }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.graphicsLayer { translationY = state.exit.value * size.height },
        sheetState = sheetState,
        scrimColor = BottomSheetDefaults.ScrimColor.copy(alpha = BottomSheetDefaults.ScrimColor.alpha * (1f - state.exit.value)),
        dragHandle = { SheetHandle(label = closeDescription, onClose = onDismissRequest) },
        // O voltar do sistema vai direto a quem chama (o BackHandler abaixo), que decide: fechar ou perguntar antes.
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false)
    ) {
        BackHandler(onBack = onDismissRequest)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(bottom = DSSpacing.lg)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = DSTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = DSSpacing.xl, vertical = DSSpacing.sm)
                        .semantics { heading() }
                )
            }

            content()
        }
    }
}

/**
 * O estado de um [DSBottomSheet] que quem chama tira da composição: [hide] faz o painel descer antes. Crie com
 * [rememberDSBottomSheetState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
public class DSBottomSheetState internal constructor() {
    internal var sheet: SheetState? = null
    internal val exit = Animatable(0f)

    /** Se o painel está na tela: aberto ou abrindo, e ainda não descido, nem pelo gesto nem pelo [hide]. */
    public val isVisible: Boolean
        get() = sheet.let { it != null && (it.isVisible || it.targetValue != SheetValue.Hidden) } && exit.value < 1f

    /** Desce o painel, com o véu apagando junto, e volta quando ele saiu da tela. Já descido, volta na hora. */
    public suspend fun hide() {
        if (isVisible) exit.animateTo(1f, DSMotion.exit())
    }

    internal suspend fun reset() {
        exit.snapTo(0f)
    }
}

/** O estado de um [DSBottomSheet] controlado por quem chama. */
@Composable
public fun rememberDSBottomSheetState(): DSBottomSheetState = remember { DSBottomSheetState() }

/**
 * A alça do Material com o rótulo certo para o TalkBack: a do Material é lida "Fechar planilha" em português
 * (tradução de *sheet*). Tocar nela, com o leitor, fecha o painel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetHandle(label: String, onClose: () -> Unit) {
    Box(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = label
            onClick(label) {
                onClose()
                true
            }
        }
    ) {
        BottomSheetDefaults.DragHandle(modifier = Modifier.clearAndSetSemantics {})
    }
}

/**
 * Uma ação de um [DSBottomSheet] de ações (editar, compartilhar, excluir), numa linha de lista do Material:
 * ícone e rótulo, a linha inteira tocável. `destructive` pinta na cor de erro.
 */
@Composable
public fun DSSheetAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    destructive: Boolean = false,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val color = if (destructive) DSTheme.colors.error else DSTheme.colors.onSurface

    ListItem(
        headlineContent = { Text(text) },
        modifier = modifier
            .clickable(enabled = enabled, role = Role.Button) {
                if (destructive) haptics.warning() else haptics.light()
                onClick()
            }
            .padding(horizontal = DSSpacing.sm)
            .dsDisabledAlpha(enabled),
        leadingContent = icon?.let { { Icon(it, contentDescription = null) } },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = color,
            leadingIconColor = if (destructive) color else DSTheme.colors.onSurfaceVariant
        )
    )
}

@DSPreviews
@Composable
private fun DSSheetActionPreview() {
    DSPreviewContainer("Sheet actions") {
        Column {
            DSSheetAction(text = "Edit", icon = DSIcons.Edit, onClick = {})

            DSSheetAction(text = "Share", icon = DSIcons.Share, onClick = {})

            DSSheetAction(text = "Delete post", icon = DSIcons.Delete, destructive = true, onClick = {})
        }
    }
}
