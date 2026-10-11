package io.github.romulomga.dskit

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Um destino da navegação principal do app (uma aba).
 *
 * @param key o valor que identifica a aba (um enum, uma rota).
 * @param label rótulo curto, sempre visível; é o que o TalkBack lê.
 * @param icon ícone da aba; [selectedIcon] troca pela versão preenchida quando a aba está ativa.
 * @param badgeCount número no selo (conversas não lidas); `0` mostra só o ponto, `null` não mostra nada.
 */
@Immutable
public data class DSNavigationItem<T>(
    val key: T,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val badgeCount: Int? = null
)

/**
 * A navegação principal do app, o `NavigationSuiteScaffold` do Material 3: barra embaixo no celular, barra
 * lateral em tablet e dobrável aberto, escolhida pelo tamanho da janela. O conteúdo é a tela da aba (um
 * [DSScreen]), que já recebe os insets descontados da barra.
 *
 * ```
 * DSNavigationScaffold(items = tabs, selection = tab, onSelectionChange = { tab = it }) {
 *     when (tab) { Tab.Home -> HomeScreen(); Tab.Chat -> ChatScreen() }
 * }
 * ```
 *
 * @param showsNavigation `false` esconde a navegação (uma tela de tela cheia dentro da aba).
 */
@Composable
public fun <T> DSNavigationScaffold(
    items: List<DSNavigationItem<T>>,
    selection: T,
    onSelectionChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    showsNavigation: Boolean = true,
    content: @Composable () -> Unit
) {
    val haptics = rememberDSHaptics()
    val adaptiveType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach { item ->
                val selected = item.key == selection

                item(
                    selected = selected,
                    onClick = {
                        if (!selected) haptics.light()
                        onSelectionChange(item.key)
                    },
                    icon = { ItemIcon(item, selected) },
                    label = { Text(item.label, maxLines = 1) }
                )
            }
        },
        modifier = modifier,
        layoutType = if (showsNavigation) adaptiveType else NavigationSuiteType.None,
        content = content
    )
}

/**
 * Barra de navegação inferior do Material 3, solta: de três a cinco destinos principais. Normalmente quem a
 * desenha é o [DSNavigationScaffold]; use direto só num layout próprio.
 */
@Composable
public fun <T> DSNavigationBar(
    items: List<DSNavigationItem<T>>,
    selection: T,
    onSelectionChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberDSHaptics()

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val selected = item.key == selection

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) haptics.light()
                    onSelectionChange(item.key)
                },
                icon = { ItemIcon(item, selected) },
                label = { Text(item.label, maxLines = 1) }
            )
        }
    }
}

/** Barra lateral de navegação do Material 3, solta: os mesmos destinos da [DSNavigationBar], para tela larga. */
@Composable
public fun <T> DSNavigationRail(
    items: List<DSNavigationItem<T>>,
    selection: T,
    onSelectionChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null
) {
    val haptics = rememberDSHaptics()

    NavigationRail(modifier = modifier, header = header?.let { { it() } }) {
        items.forEach { item ->
            val selected = item.key == selection

            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (!selected) haptics.light()
                    onSelectionChange(item.key)
                },
                icon = { ItemIcon(item, selected) },
                label = { Text(item.label, maxLines = 1) }
            )
        }
    }
}

@Composable
private fun <T> ItemIcon(item: DSNavigationItem<T>, selected: Boolean) {
    val icon = if (selected) item.selectedIcon ?: item.icon else item.icon

    DSBadgedBox(count = item.badgeCount) {
        Icon(icon, contentDescription = null)
    }
}

private enum class PreviewTab { Home, Messages, Profile }

@DSPreviews
@Composable
private fun DSNavigationBarPreview() {
    var tab by remember { mutableStateOf(PreviewTab.Home) }

    DSPreviewContainer("Navigation bar") {
        DSNavigationBar(
            items = listOf(
                DSNavigationItem(PreviewTab.Home, "Home", DSIcons.Home),
                DSNavigationItem(PreviewTab.Messages, "Messages", DSIcons.Inbox, badgeCount = 3),
                DSNavigationItem(PreviewTab.Profile, "Profile", DSIcons.Person)
            ),
            selection = tab,
            onSelectionChange = { tab = it }
        )
    }
}
