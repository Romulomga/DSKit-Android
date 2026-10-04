package io.github.romulomga.dskit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.components.R
import kotlinx.coroutines.launch

/**
 * Editor de várias linhas para digitar uma lista (nomes, opções), um item por linha. Mostra a contagem de
 * itens e de repetidos, mas nunca altera o que o usuário digitou. "Colar" anexa a área de transferência numa
 * linha nova; "Limpar" apaga tudo ou, com `onClearRequested`, deixa o app confirmar antes.
 *
 * `placeholder`, `pasteTitle` e `clearTitle` nulos usam os textos do DSKit.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun DSListInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    itemLabel: @Composable (Int) -> String = { count -> pluralStringResource(R.plurals.dskit_items_count, count, count) },
    duplicateLabel: @Composable (Int) -> String = { count -> pluralStringResource(R.plurals.dskit_duplicates_count, count, count) },
    pasteTitle: String? = null,
    clearTitle: String? = null,
    enabled: Boolean = true,
    onClearRequested: (() -> Unit)? = null
) {
    val haptics = rememberDSHaptics()
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val requester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val itemCount = dsListInputLines(value).size
    val duplicateCount = dsListInputDuplicateCount(value)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier.clearAndSetSemantics {},
                style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold),
                color = DSTheme.colors.onSurfaceMedium
            )
        }

        DSInputBox(
            isFocused = isFocused,
            onClick = if (enabled) ({ requester.requestFocus() }) else null
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = ListInputMinHeight)
                    .focusRequester(requester)
                    .onFocusChanged { isFocused = it.isFocused }
                    .semantics { if (title != null) contentDescription = title },
                enabled = enabled,
                textStyle = DSTypography.body.copy(
                    color = if (enabled) DSTheme.colors.onSurfaceHigh else DSTheme.colors.onSurfaceMedium
                ),
                cursorBrush = SolidColor(DSTheme.colors.accent),
                decorationBox = { innerField ->
                    Box(contentAlignment = Alignment.TopStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder ?: stringResource(R.string.dskit_one_item_per_line),
                                style = DSTypography.body,
                                color = DSTheme.colors.onSurfaceMedium
                            )
                        }

                        innerField()
                    }
                }
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(DSSpacing.md),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.xs),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            // O CLDR põe o 0 no "one" em alguns idiomas (pt-BR), e o plural leria "0 item": vazio tem texto próprio.
            ListInputFooterLabel(
                icon = ListBulletIcon,
                text = if (itemCount == 0) stringResource(R.string.dskit_no_items) else itemLabel(itemCount),
                color = DSTheme.colors.onSurfaceMedium
            )

            if (duplicateCount > 0) {
                ListInputFooterLabel(
                    icon = DuplicateIcon,
                    text = duplicateLabel(duplicateCount),
                    color = DSTheme.colors.warningHigh
                )
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md), verticalAlignment = Alignment.CenterVertically) {
                ListInputFooterLabel(
                    icon = PasteIcon,
                    text = pasteTitle ?: stringResource(R.string.dskit_paste),
                    color = if (enabled) DSTheme.colors.accent else DSTheme.colors.onSurfaceMedium,
                    modifier = Modifier.clickable(enabled = enabled, role = Role.Button) {
                        scope.launch {
                            val pasted = clipboard.getClipEntry()?.clipData?.takeIf { it.itemCount > 0 }
                                ?.getItemAt(0)?.text?.toString()
                            if (!pasted.isNullOrEmpty()) {
                                onValueChange(appendPasted(value, pasted))
                                haptics.light()
                            }
                        }
                    }
                )

                val canClear = enabled && value.isNotEmpty()
                ListInputFooterLabel(
                    icon = TrashIcon,
                    text = clearTitle ?: stringResource(R.string.dskit_clear),
                    color = if (canClear) DSTheme.colors.errorHigh else DSTheme.colors.onSurfaceMedium,
                    modifier = Modifier.clickable(enabled = canClear, role = Role.Button) {
                        haptics.light()
                        if (onClearRequested != null) onClearRequested() else onValueChange("")
                    }
                )
            }
        }

        if (helperText != null) {
            Text(helperText, style = DSTypography.footnote, color = DSTheme.colors.onSurfaceMedium)
        }
    }
}

/** Linhas não vazias, sem espaço nas pontas: os itens do `DSListInput`. */
internal fun dsListInputLines(text: String): List<String> = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

/** Quantos itens repetem um anterior, sem diferenciar maiúscula de minúscula. */
internal fun dsListInputDuplicateCount(text: String): Int {
    val lowered = dsListInputLines(text).map { it.lowercase() }
    return lowered.size - lowered.toSet().size
}

private fun appendPasted(current: String, pasted: String): String = when {
    current.isEmpty() -> pasted
    current.endsWith("\n") -> current + pasted
    else -> current + "\n" + pasted
}

@Composable
private fun ListInputFooterLabel(icon: ImageVector, text: String, color: Color, modifier: Modifier = Modifier) {
    val style = DSTypography.footnote.copy(fontFeatureSettings = "tnum")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(dsInputIconSize(style)))

        Text(text, style = style, color = color)
    }
}

private val ListInputMinHeight = 140.dp

// Caminhos dos Material Icons (Google, Apache 2.0): FormatListBulleted, ContentCopy, ContentPaste, Delete.
private val ListBulletIcon: ImageVector by lazy {
    listInputIcon(
        "ListBullet",
        "M4,10.5c-0.83,0 -1.5,0.67 -1.5,1.5s0.67,1.5 1.5,1.5 1.5,-0.67 1.5,-1.5 -0.67,-1.5 -1.5,-1.5zM4,4.5c-0.83,0 -1.5,0.67 " +
            "-1.5,1.5S3.17,7.5 4,7.5 5.5,6.83 5.5,6 4.83,4.5 4,4.5zM4,16.67c-0.74,0 -1.33,0.6 -1.33,1.33s0.6,1.33 1.33,1.33 " +
            "1.33,-0.6 1.33,-1.33 -0.59,-1.33 -1.33,-1.33zM7,19h14v-2H7v2zM7,13h14v-2H7v2zM7,5v2h14V5H7z"
    )
}
private val DuplicateIcon: ImageVector by lazy {
    listInputIcon(
        "Duplicate",
        "M16,1L4,1c-1.1,0 -2,0.9 -2,2v14h2L4,3h12L16,1zM19,5L8,5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h11c1.1,0 2,-0.9 2,-2L21," +
            "7c0,-1.1 -0.9,-2 -2,-2zM19,21L8,21L8,7h11v14z"
    )
}
private val PasteIcon: ImageVector by lazy {
    listInputIcon(
        "Paste",
        "M19,2h-4.18C14.4,0.84 13.3,0 12,0c-1.3,0 -2.4,0.84 -2.82,2L5,2c-1.1,0 -2,0.9 -2,2v16c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 " +
            "2,-2L21,4c0,-1.1 -0.9,-2 -2,-2zM12,2c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM19,20L5,20L5,4h2v3h10L17," +
            "4h2v16z"
    )
}
private val TrashIcon: ImageVector by lazy {
    listInputIcon("Trash", "M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V7H6v12zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14V4z")
}

private fun listInputIcon(name: String, path: String): ImageVector = ImageVector.Builder(
    name = "DSListInput.$name",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).addPath(pathData = addPathNodes(path), fill = SolidColor(Color(0f, 0f, 0f))).build()

// MARK: - Previews

@DSPreviews
@Composable
private fun DSListInputPreview() {
    var text by remember { mutableStateOf("Ana\nBruno\nCarla\nAna") }

    DSPreviewContainer("List input") {
        DSListInput(
            value = text,
            onValueChange = { text = it },
            title = "Participants",
            helperText = "Enter one name per line. Duplicates are kept."
        )
    }
}
