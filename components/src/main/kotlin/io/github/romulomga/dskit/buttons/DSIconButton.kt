package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/** Estilos do [DSIconButton], os quatro do Material 3. */
public enum class DSIconButtonStyle {
    /** Só o ícone: ações de barra e de linha. Padrão. */
    Standard,

    /** Cheio na cor primária: a ação de destaque de um grupo. */
    Filled,

    /** No contêiner secundário: ação importante sem competir com a principal. */
    Tonal,

    /** Com contorno: ação secundária ao lado de uma cheia. */
    Outlined
}

/**
 * Botão só com ícone, num alvo de 48 dp. `contentDescription` é obrigatório: é o que o TalkBack lê e o que a
 * dica ([DSTooltip]) mostra quando o usuário segura o botão, como nos apps do Google.
 */
@Composable
public fun DSIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DSIconButtonStyle = DSIconButtonStyle.Standard,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()
    val click = {
        haptics.light()
        onClick()
    }
    val content: @Composable () -> Unit = { Icon(icon, contentDescription = contentDescription) }

    DSTooltip(text = contentDescription, modifier = modifier) {
        when (style) {
            DSIconButtonStyle.Standard -> IconButton(onClick = click, enabled = enabled, content = content)
            DSIconButtonStyle.Filled -> FilledIconButton(onClick = click, enabled = enabled, content = content)
            DSIconButtonStyle.Tonal -> FilledTonalIconButton(onClick = click, enabled = enabled, content = content)
            DSIconButtonStyle.Outlined -> OutlinedIconButton(onClick = click, enabled = enabled, content = content)
        }
    }
}

@DSPreviews
@Composable
private fun DSIconButtonPreview() {
    DSPreviewContainer("Icon button") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSIconButton(DSIcons.Share, "Share", onClick = {})

            DSIconButton(DSIcons.Add, "Add", onClick = {}, style = DSIconButtonStyle.Filled)

            DSIconButton(DSIcons.Edit, "Edit", onClick = {}, style = DSIconButtonStyle.Tonal)

            DSIconButton(DSIcons.Delete, "Delete", onClick = {}, style = DSIconButtonStyle.Outlined)

            DSIconButton(DSIcons.Star, "Disabled", onClick = {}, enabled = false)
        }
    }
}
