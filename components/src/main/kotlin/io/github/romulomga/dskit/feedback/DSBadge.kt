package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Selo do Material 3: um ponto ([count] 0) ou um número pequeno no canto de um ícone. Acima de 99 mostra
 * "99+". Quase sempre vai num [DSBadgedBox]; nos itens de navegação, use o `badgeCount` do
 * [DSNavigationItem].
 */
@Composable
public fun DSBadge(modifier: Modifier = Modifier, count: Int = 0) {
    Badge(modifier = modifier) {
        if (count > 0) Text(badgeText(count))
    }
}

/**
 * Um ícone (ou outro conteúdo pequeno) com [DSBadge] no canto: mensagens não lidas, novidade num ajuste.
 * [count] `null` esconde o selo; 0 mostra só o ponto.
 *
 * @param badgeDescription o que o TalkBack lê no lugar do número ("3 mensagens não lidas"); sem ele, lê só
 * o número.
 */
@Composable
public fun DSBadgedBox(
    count: Int?,
    modifier: Modifier = Modifier,
    badgeDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    BadgedBox(
        badge = {
            if (count != null) {
                DSBadge(
                    count = count,
                    modifier = if (badgeDescription != null) Modifier.semantics { contentDescription = badgeDescription } else Modifier
                )
            }
        },
        modifier = modifier,
        content = content
    )
}

private const val MaxBadge = 99

private fun badgeText(count: Int): String = if (count > MaxBadge) "$MaxBadge+" else count.toString()

@DSPreviews
@Composable
private fun DSBadgePreview() {
    DSPreviewContainer("Badge") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.xl)) {
            DSBadgedBox(count = 0) { Icon(DSIcons.Inbox, contentDescription = "Inbox") }

            DSBadgedBox(count = 3) { Icon(DSIcons.Inbox, contentDescription = "Inbox") }

            DSBadgedBox(count = 120) { Icon(DSIcons.Inbox, contentDescription = "Inbox") }
        }
    }
}
