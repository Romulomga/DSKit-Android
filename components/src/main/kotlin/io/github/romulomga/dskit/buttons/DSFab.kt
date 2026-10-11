package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Botão de ação flutuante: a ação principal de uma tela de conteúdo (criar uma publicação, escrever uma
 * mensagem). Vai no `floatingActionButton` do [DSScreen], que o põe acima da barra de navegação.
 */
@Composable
public fun DSFab(icon: ImageVector, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val haptics = rememberDSHaptics()

    FloatingActionButton(
        onClick = {
            haptics.light()
            onClick()
        },
        modifier = modifier
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

/**
 * FAB com texto, para quando o ícone sozinho não diz a ação ("Nova publicação"). Com [expanded] `false`
 * encolhe para só o ícone, o padrão ao rolar a lista para baixo.
 */
@Composable
public fun DSExtendedFab(text: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier, expanded: Boolean = true) {
    val haptics = rememberDSHaptics()

    ExtendedFloatingActionButton(
        text = { Text(text) },
        icon = { Icon(icon, contentDescription = null) },
        onClick = {
            haptics.light()
            onClick()
        },
        modifier = modifier,
        expanded = expanded
    )
}

@DSPreviews
@Composable
private fun DSFabPreview() {
    DSPreviewContainer("FAB") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSFab(DSIcons.Edit, "Compose", onClick = {})

            DSExtendedFab("New post", DSIcons.Add, onClick = {})
        }
    }
}
