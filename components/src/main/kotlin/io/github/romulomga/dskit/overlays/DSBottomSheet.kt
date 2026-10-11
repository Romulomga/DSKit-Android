package io.github.romulomga.dskit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

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
 * @param visible se o painel está aberto. Ao virar `false`, o painel desce antes de sair da tela.
 * @param onDismissRequest o usuário arrastou para baixo, tocou fora ou usou o voltar: desligue [visible].
 * @param title título opcional no topo do painel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DSBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shown by remember { mutableStateOf(visible) }

    // O painel só sai da composição depois de descer; fechar pelo gesto já desceu, e o `hide` não faz nada.
    LaunchedEffect(visible) {
        if (visible) {
            shown = true
        } else if (shown) {
            sheetState.hide()
            shown = false
        }
    }

    if (!shown) return

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                haptics.light()
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
