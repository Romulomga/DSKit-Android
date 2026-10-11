package io.github.romulomga.dskit

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

/** Opacidade de conteúdo desabilitado no Material 3. */
internal const val DisabledContentAlpha: Float = 0.38f

/** Esmaece o que não tem estado de desabilitado próprio (uma linha de lista inteira). */
internal fun Modifier.dsDisabledAlpha(enabled: Boolean): Modifier = if (enabled) this else alpha(DisabledContentAlpha)
