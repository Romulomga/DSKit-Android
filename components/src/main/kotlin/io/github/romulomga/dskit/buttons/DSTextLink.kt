package io.github.romulomga.dskit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink

/** Um trecho de [DSTextLink] que vira link: abre um endereço ([Url]) ou chama uma ação do app ([Action]). */
@Immutable
public sealed interface DSLink {
    /** O trecho do texto, exatamente como aparece nele. */
    public val text: String

    /** Abre [url] no navegador (ou no app que a trata). */
    public data class Url(override val text: String, val url: String) : DSLink

    /** Chama [onClick], para abrir uma tela do app. */
    public data class Action(override val text: String, val onClick: () -> Unit) : DSLink
}

/**
 * Texto corrido com links no meio: "Ao continuar, você aceita os Termos e a Política de privacidade". Cada
 * [links] marca a primeira ocorrência do seu trecho em [text], sublinhada na cor primária. O TalkBack lista
 * os links no menu de ações e cada um ganha foco próprio.
 *
 * Link sozinho, fora de uma frase, é um [DSTextButton].
 *
 * @param allowedSchemes esquemas que um [DSLink.Url] pode abrir. Endereço com outro esquema (`intent:`,
 * `javascript:`, `file:`, um deep link vindo de servidor) aparece como texto comum, sem link.
 * @param onOpenFailure chamado quando nenhum app do aparelho abre o endereço (aparelho gerenciado, sem
 * navegador); sem ele, o toque não faz nada, e o app não cai.
 */
@Composable
public fun DSTextLink(
    text: String,
    links: List<DSLink>,
    modifier: Modifier = Modifier,
    style: TextStyle = DSTheme.typography.bodyMedium,
    color: Color = DSTheme.colors.onSurfaceVariant,
    allowedSchemes: Set<String> = DSTextLinkDefaults.allowedSchemes,
    onOpenFailure: (DSLink.Url) -> Unit = {}
) {
    val haptics = rememberDSHaptics()
    val uriHandler = LocalUriHandler.current
    val currentOnOpenFailure by rememberUpdatedState(onOpenFailure)
    val linkColor = DSTheme.colors.primary
    val pressedColor = DSTheme.colors.primaryContainer

    val annotated = remember(text, links, allowedSchemes, linkColor, pressedColor, uriHandler, haptics) {
        val linkStyles = TextLinkStyles(
            style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
            pressedStyle = SpanStyle(color = linkColor, background = pressedColor)
        )

        buildAnnotatedString {
            var cursor = 0
            val ranges = links
                .mapNotNull { link -> text.indexOf(link.text).takeIf { it >= 0 }?.let { it to link } }
                .sortedBy { it.first }

            ranges.forEach { (start, link) ->
                if (start < cursor) return@forEach

                append(text.substring(cursor, start))

                val annotation = when (link) {
                    is DSLink.Url -> if (link.hasSchemeIn(allowedSchemes)) {
                        LinkAnnotation.Url(link.url, linkStyles) {
                            haptics.light()
                            runCatching { uriHandler.openUri(link.url) }.onFailure { currentOnOpenFailure(link) }
                        }
                    } else {
                        null
                    }

                    is DSLink.Action -> LinkAnnotation.Clickable(link.text, linkStyles) {
                        haptics.light()
                        link.onClick()
                    }
                }

                if (annotation != null) {
                    withLink(annotation) { append(link.text) }
                } else {
                    append(link.text)
                }

                cursor = start + link.text.length
            }

            append(text.substring(cursor))
        }
    }

    Text(annotated, modifier = modifier, style = style, color = color)
}

/** Padrões do [DSTextLink]. */
public object DSTextLinkDefaults {
    /** Web segura, e-mail e telefone. Acrescente `http` só para endereços que você controla. */
    public val allowedSchemes: Set<String> = setOf("https", "mailto", "tel")
}

private fun DSLink.Url.hasSchemeIn(allowed: Set<String>): Boolean {
    val scheme = url.substringBefore(':', missingDelimiterValue = "").trim().lowercase()

    return scheme.isNotEmpty() && scheme in allowed.map { it.lowercase() }
}

@DSPreviews
@Composable
private fun DSTextLinkPreview() {
    DSPreviewContainer("Text link") {
        DSTextLink(
            text = "By continuing you accept the Terms of use and the Privacy policy.",
            links = listOf(DSLink.Url("Terms of use", "https://example.com/terms"), DSLink.Action("Privacy policy") {})
        )
    }
}
