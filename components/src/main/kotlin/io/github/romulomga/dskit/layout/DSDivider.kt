package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Recuo do [DSDivider], os três do Material 3. */
public enum class DSDividerInset {
    /** De ponta a ponta: separa seções. Padrão. */
    None,

    /** Recuado no começo, alinhado ao texto de um [DSListItem] sem ícone: separa itens de uma mesma lista. */
    Start,

    /** Recuado dos dois lados: separa conteúdo dentro de um cartão ou painel. */
    Middle
}

/**
 * Linha fina do Material 3 (`outlineVariant`) entre grupos de conteúdo. Use pouco: espaço e
 * [DSListSectionHeader] separam melhor que linha. Lado a lado, use [DSVerticalDivider].
 */
@Composable
public fun DSDivider(modifier: Modifier = Modifier, inset: DSDividerInset = DSDividerInset.None) {
    val padding = when (inset) {
        DSDividerInset.None -> Modifier
        DSDividerInset.Start -> Modifier.padding(start = DSSpacing.lg)
        DSDividerInset.Middle -> Modifier.padding(horizontal = DSSpacing.lg)
    }

    HorizontalDivider(modifier = modifier.then(padding))
}

/** Linha vertical do Material 3 entre itens lado a lado (ações de um cartão, números de um resumo). */
@Composable
public fun DSVerticalDivider(modifier: Modifier = Modifier) {
    VerticalDivider(modifier = modifier)
}

@DSPreviews
@Composable
private fun DSDividerPreview() {
    DSPreviewContainer("Divider") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSDivider()

            DSDivider(inset = DSDividerInset.Start)

            DSDivider(inset = DSDividerInset.Middle)

            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                Text("12 posts")

                DSVerticalDivider()

                Text("3 drafts")
            }
        }
    }
}
