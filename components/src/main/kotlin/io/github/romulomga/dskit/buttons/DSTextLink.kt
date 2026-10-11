package io.github.romulomga.dskit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
 */
@Composable
public fun DSTextLink(
    text: String,
    links: List<DSLink>,
    modifier: Modifier = Modifier,
    style: TextStyle = DSTheme.typography.bodyMedium,
    color: Color = DSTheme.colors.onSurfaceVariant
) {
    val haptics = rememberDSHaptics()
    val uriHandler = LocalUriHandler.current
    val linkStyles = TextLinkStyles(
        style = SpanStyle(color = DSTheme.colors.primary, textDecoration = TextDecoration.Underline),
        pressedStyle = SpanStyle(color = DSTheme.colors.primary, background = DSTheme.colors.primaryContainer)
    )

    val annotated = buildAnnotatedString {
        var cursor = 0
        val ranges = links
            .mapNotNull { link -> text.indexOf(link.text).takeIf { it >= 0 }?.let { it to link } }
            .sortedBy { it.first }

        ranges.forEach { (start, link) ->
            if (start < cursor) return@forEach

            append(text.substring(cursor, start))

            val annotation = when (link) {
                is DSLink.Url -> LinkAnnotation.Url(link.url, linkStyles) {
                    haptics.light()
                    uriHandler.openUri(link.url)
                }

                is DSLink.Action -> LinkAnnotation.Clickable(link.text, linkStyles) {
                    haptics.light()
                    link.onClick()
                }
            }

            withLink(annotation) { append(link.text) }

            cursor = start + link.text.length
        }

        append(text.substring(cursor))
    }

    Text(annotated, modifier = modifier, style = style, color = color)
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
