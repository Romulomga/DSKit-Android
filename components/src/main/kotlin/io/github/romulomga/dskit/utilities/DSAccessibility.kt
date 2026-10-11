package io.github.romulomga.dskit

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics

/** Ajudas de acessibilidade para o TalkBack. */
public object DSAccessibility {
    /** Junta as partes não vazias com ", ", para o TalkBack ler um bloco como uma frase. */
    public fun combinedLabel(vararg parts: String?): String = parts.filterNot { it.isNullOrBlank() }.joinToString(", ")
}

/**
 * Faz um card ser lido como um elemento só, com rótulo e, se houver, a dica da ação ("toque duas vezes
 * para…"). O `onClick` do próprio card continua tratando o toque.
 */
public fun Modifier.dsAccessibleCard(label: String, hint: String? = null): Modifier = semantics(mergeDescendants = true) {
    contentDescription = label
    if (hint != null) onClick(label = hint, action = null)
}
