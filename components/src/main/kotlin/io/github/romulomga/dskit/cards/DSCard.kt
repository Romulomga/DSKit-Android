package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Os três cards do Material 3. */
public enum class DSCardVariant {
    /** Fundo `surfaceContainerHighest`, sem sombra: o card padrão. */
    Filled,

    /** Fundo `surfaceContainerLow` com sombra: quando o card precisa se destacar do que está atrás. */
    Elevated,

    /** Fundo da tela com contorno: listas de cards lado a lado, sem peso visual. */
    Outlined
}

/**
 * Card do Material 3: agrupa o conteúdo de um item (uma publicação, um resumo). Com [onClick], o card
 * inteiro é tocável e mostra o ripple; o TalkBack lê o conteúdo junto, como um botão.
 *
 * @param contentPadding folga interna; o padrão é `DSSpacing.lg`. Passe `PaddingValues(0.dp)` para uma
 * imagem que vai até a borda.
 */
@Composable
public fun DSCard(
    modifier: Modifier = Modifier,
    variant: DSCardVariant = DSCardVariant.Filled,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(DSSpacing.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    val haptics = rememberDSHaptics()
    val body: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth().padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.sm),
            content = content
        )
    }
    val click = onClick?.let {
        {
            haptics.light()
            it()
        }
    }

    when (variant) {
        DSCardVariant.Filled ->
            if (click != null) Card(onClick = click, modifier = modifier, content = body) else Card(modifier = modifier, content = body)

        DSCardVariant.Elevated ->
            if (click != null) {
                ElevatedCard(onClick = click, modifier = modifier, content = body)
            } else {
                ElevatedCard(modifier = modifier, content = body)
            }

        DSCardVariant.Outlined ->
            if (click != null) {
                OutlinedCard(onClick = click, modifier = modifier, content = body)
            } else {
                OutlinedCard(modifier = modifier, content = body)
            }
    }
}

@DSPreviews
@Composable
private fun DSCardPreview() {
    DSPreviewContainer("Cards") {
        DSCard {
            Text("Filled card", style = DSTheme.typography.titleMedium)

            Text("The default card", style = DSTheme.typography.bodyMedium)
        }

        DSCard(variant = DSCardVariant.Elevated, onClick = {}) { Text("Elevated, tappable") }

        DSCard(variant = DSCardVariant.Outlined) { Text("Outlined") }
    }
}
