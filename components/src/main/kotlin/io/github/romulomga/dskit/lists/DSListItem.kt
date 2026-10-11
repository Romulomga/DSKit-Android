package io.github.romulomga.dskit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

/**
 * Linha de lista do Material 3: título, linha de apoio, sobretítulo, um ícone ou conteúdo à esquerda e algo
 * à direita (um valor, um ícone). É a peça de toda lista e tela de ajustes. Com [onClick], a linha inteira
 * é tocável e o TalkBack a lê como um botão só.
 *
 * O fundo é transparente: a linha assume a cor de onde está (a tela, um card).
 *
 * @param leadingIcon ícone à esquerda; para avatar ou imagem, use [leading].
 * @param trailing conteúdo à direita: um texto de valor, um ícone, um selo.
 */
@Composable
public fun DSListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    overline: String? = null,
    leadingIcon: ImageVector? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val haptics = rememberDSHaptics()

    val interaction = if (onClick != null) {
        Modifier.clickable(enabled = enabled, role = Role.Button) {
            haptics.light()
            onClick()
        }
    } else {
        Modifier
    }

    BaseListItem(
        headline = headline,
        modifier = modifier.then(interaction),
        supporting = supporting,
        overline = overline,
        leading = leading ?: leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailing = trailing,
        enabled = enabled
    )
}

/** Linha com interruptor à direita: um ajuste liga/desliga. A linha inteira alterna; o TalkBack a lê como interruptor. */
@Composable
public fun DSSwitchListItem(
    headline: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    BaseListItem(
        headline = headline,
        modifier = modifier.toggleable(value = checked, enabled = enabled, role = Role.Switch) {
            haptics.light()
            onCheckedChange(it)
        },
        supporting = supporting,
        leading = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        // A linha cuida do toque; o interruptor só desenha o estado.
        trailing = { Switch(checked = checked, onCheckedChange = null, enabled = enabled) },
        enabled = enabled
    )
}

/** Linha com caixa de seleção à esquerda: uma das várias opções que se marcam juntas. */
@Composable
public fun DSCheckboxListItem(
    headline: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    BaseListItem(
        headline = headline,
        modifier = modifier.toggleable(value = checked, enabled = enabled, role = Role.Checkbox) {
            haptics.light()
            onCheckedChange(it)
        },
        supporting = supporting,
        leading = { Checkbox(checked = checked, onCheckedChange = null, enabled = enabled) },
        enabled = enabled
    )
}

/**
 * Linha com botão de opção à esquerda: uma opção de escolha única. Empilhe numa coluna com
 * `Modifier.selectableGroup()` para o TalkBack anunciar o grupo.
 */
@Composable
public fun DSRadioListItem(
    headline: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    enabled: Boolean = true
) {
    val haptics = rememberDSHaptics()

    BaseListItem(
        headline = headline,
        modifier = modifier.selectable(selected = selected, enabled = enabled, role = Role.RadioButton) {
            haptics.light()
            onClick()
        },
        supporting = supporting,
        leading = { RadioButton(selected = selected, onClick = null, enabled = enabled) },
        enabled = enabled
    )
}

/**
 * Título de seção de lista, na cor primária, como nas telas de ajustes do Android. Use antes de um grupo de
 * [DSListItem]s (numa `Column` ou como `item` de uma `LazyColumn`).
 */
@Composable
public fun DSListSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = DSTheme.typography.titleSmall,
        color = DSTheme.colors.primary,
        modifier = modifier
            .padding(start = DSSpacing.lg, end = DSSpacing.lg, top = DSSpacing.lg, bottom = DSSpacing.sm)
            .semantics { heading() }
    )
}

@Composable
private fun BaseListItem(
    headline: String,
    modifier: Modifier,
    supporting: String? = null,
    overline: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    enabled: Boolean
) {
    ListItem(
        headlineContent = { Text(headline) },
        modifier = modifier.dsDisabledAlpha(enabled),
        overlineContent = overline?.let { { Text(it) } },
        supportingContent = supporting?.let { { Text(it) } },
        leadingContent = leading,
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@DSPreviews
@Composable
private fun DSListItemPreview() {
    var notifications by remember { mutableStateOf(true) }
    var kind by remember { mutableStateOf("Apartment") }

    DSPreviewContainer("List items") {
        Column {
            DSListSectionHeader("Account")

            DSListItem("Profile", supporting = "Name, photo and CRECI", leadingIcon = DSIcons.Person, onClick = {})

            DSSwitchListItem("Notifications", checked = notifications, onCheckedChange = { notifications = it })

            HorizontalDivider()

            DSListSectionHeader("Property type")

            Column(Modifier.selectableGroup()) {
                listOf("Apartment", "House").forEach { option ->
                    DSRadioListItem(option, selected = kind == option, onClick = { kind = option })
                }
            }

            DSCheckboxListItem("Pool", checked = true, onCheckedChange = {})
        }
    }
}
