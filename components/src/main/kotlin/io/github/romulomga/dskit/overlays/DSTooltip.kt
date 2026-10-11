package io.github.romulomga.dskit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Dica do Material 3 que aparece ao tocar e segurar (ou passar o mouse) no [content]. Sem [title] é a dica
 * simples, uma linha curta que some sozinha, para dizer o que um ícone faz ([DSIconButton] já a mostra com o
 * `contentDescription`). Com [title] é a dica rica, que fica até o usuário tocar fora, para explicar um
 * recurso em duas ou três linhas.
 *
 * Não envolva um [DSIconButton] (ele já tem a dica simples); a dica rica vai num ícone de informação ou num
 * elemento sem dica própria. Não esconda informação necessária numa dica: quem não sabe que pode segurar
 * nunca a vê.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DSTooltip(text: String, modifier: Modifier = Modifier, title: String? = null, content: @Composable () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = {
            if (title == null) {
                PlainTooltip { Text(text) }
            } else {
                RichTooltip(title = { Text(title) }) { Text(text) }
            }
        },
        state = rememberTooltipState(isPersistent = title != null),
        modifier = modifier,
        content = content
    )
}

@DSPreviews
@Composable
private fun DSTooltipPreview() {
    DSPreviewContainer("Tooltip") {
        DSTooltip(text = "Pinned posts stay at the top of the feed.", title = "Pin") {
            Icon(DSIcons.Info, contentDescription = "About pins")
        }
    }
}
