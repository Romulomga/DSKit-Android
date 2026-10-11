package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import io.github.romulomga.dskit.components.R

/** Os quatro tamanhos de barra superior do Material 3. */
public enum class DSTopAppBarStyle {
    /** Título pequeno alinhado ao início: o padrão das telas de detalhe e formulário. */
    Small,

    /** Título centralizado: a tela principal de uma aba, com o nome ou a marca do app. */
    CenterAligned,

    /** Título médio que encolhe para a barra pequena ao rolar (dentro do [DSScreen]). */
    Medium,

    /** Título grande que encolhe para a barra pequena ao rolar (dentro do [DSScreen]): telas de topo com pouco conteúdo acima da lista. */
    Large
}

/**
 * Barra superior do Material 3: seta de voltar, título e ações à direita. Normalmente quem a desenha é o
 * [DSScreen], que também a faz encolher ao rolar; use direto só num layout próprio.
 *
 * @param onBack mostra a seta de voltar (espelhada em árabe); quem chama faz o pop. O voltar do sistema
 * continua sendo do app (`BackHandler` ou a biblioteca de navegação).
 * @param actions até três [DSIconButton]s; com mais ações, a terceira é um `DSIconButton(DSIcons.MoreVert, …)` que
 * abre um `DropdownMenu` com o resto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DSTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    style: DSTopAppBarStyle = DSTopAppBarStyle.Small,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBarContent(title, modifier, style, onBack, actions, scrollBehavior = null)
}

/** A barra com o comportamento de rolagem, que é API experimental do Material e fica só dentro do DSKit. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBarContent(
    title: String,
    modifier: Modifier,
    style: DSTopAppBarStyle,
    onBack: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    val titleContent: @Composable () -> Unit = {
        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.semantics { heading() })
    }
    val navigationIcon: @Composable () -> Unit = { if (onBack != null) BackButton(onBack) }

    when (style) {
        DSTopAppBarStyle.Small -> TopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )

        DSTopAppBarStyle.CenterAligned -> CenterAlignedTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )

        DSTopAppBarStyle.Medium -> MediumTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )

        DSTopAppBarStyle.Large -> LargeTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    val haptics = rememberDSHaptics()

    IconButton(
        onClick = {
            haptics.light()
            onBack()
        }
    ) {
        Icon(DSIcons.ArrowBack, contentDescription = stringResource(R.string.dskit_back))
    }
}

@DSPreviews
@Composable
private fun DSTopAppBarPreview() {
    DSPreviewContainer("Top app bar") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTopAppBar("Profile", onBack = {})

            DSTopAppBar("Edit post", onBack = {}) {
                DSIconButton(DSIcons.Check, "Save", onClick = {})
            }

            DSTopAppBar("Homer", style = DSTopAppBarStyle.CenterAligned)

            DSTopAppBar("Messages", style = DSTopAppBarStyle.Large)
        }
    }
}
