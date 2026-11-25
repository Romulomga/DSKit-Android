package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import io.github.romulomga.dskit.components.R

/**
 * As cores de marca da barra superior do app inteiro: o fundo (parado e rolado) e a cor do título e dos ícones. O app
 * liga uma vez no tema, `CompositionLocalProvider(LocalDSTopAppBarBrandColors provides DSTopAppBarBrandColors(…))`, e
 * toda [DSTopAppBar] (as do [DSScreen] inclusive) passa a usá-las, como a barra colorida do Android clássico. Sem nada
 * ligado (o padrão), a barra segue a cor da tela.
 */
public data class DSTopAppBarBrandColors(val container: Color, val content: Color)

/** As [DSTopAppBarBrandColors] do app, ou `null` (o padrão): a barra com a cor da tela. */
public val LocalDSTopAppBarBrandColors: ProvidableCompositionLocal<DSTopAppBarBrandColors?> = staticCompositionLocalOf { null }

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
 * @param title o nome da tela, numa linha com reticências. Com a fonte do sistema aumentada, nas barras
 * [DSTopAppBarStyle.Small] e [DSTopAppBarStyle.CenterAligned], quebra em até duas linhas e a barra cresce junto.
 * @param onBack mostra a seta de voltar (espelhada em árabe); quem chama faz o pop. O voltar do sistema
 * continua sendo do app (`BackHandler` ou a biblioteca de navegação).
 * @param navigationIcon o que fica no lugar da seta numa tela sem [onBack]: a marca do app na tela de topo de uma
 * aba, ou um [DSIconButton] (menu). Com [onBack], a seta vence.
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
    navigationIcon: (@Composable () -> Unit)? = null,
    // Antes das `actions`, que ficam por último: o bloco no fim da chamada continua sendo as ações.
    titleContent: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBarContent(title, modifier, style, onBack, navigationIcon, actions, scrollBehavior = null, titleContent = titleContent)
}

/** A barra com o comportamento de rolagem, que é API experimental do Material e fica só dentro do DSKit. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBarContent(
    title: String,
    modifier: Modifier,
    style: DSTopAppBarStyle,
    onBack: (() -> Unit)?,
    navigationIcon: (@Composable () -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    // Com a cor da tela, a barra parada tem a mesma cor dela; rolada, sobe um degrau como no Material.
    containerColor: Color? = null,
    // No lugar do título escrito (um logo, por exemplo); o `title` continua sendo o nome lido pelo TalkBack.
    titleContent: (@Composable () -> Unit)? = null
) {
    val colors = barColors(style, containerColor)
    val customTitle = titleContent
    val titleContent: @Composable () -> Unit = {
        if (customTitle != null) {
            // Lido como o título da tela (o nome da aba), não como o desenho que ocupa o lugar dele.
            Box(
                Modifier.clearAndSetSemantics {
                    heading()
                    contentDescription = title
                }
            ) { customTitle() }
        } else {
            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.semantics { heading() })
        }
    }

    // Com a fonte aumentada, o título da barra de uma linha quebra em até duas e a barra cresce junto: "Feriados de
    // 2026" a 200 % perdia o ano. Na fonte normal (ou menor) nada muda: uma linha, reticências.
    val wrapsTitle = customTitle == null && LocalDensity.current.fontScale > 1f
    var titleLines by remember { mutableIntStateOf(1) }
    var titleHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val singleRowTitle: @Composable () -> Unit = if (wrapsTitle) {
        {
            Text(
                title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (style == DSTopAppBarStyle.CenterAligned) TextAlign.Center else null,
                onTextLayout = { result ->
                    titleLines = result.lineCount
                    titleHeight = with(density) { result.multiParagraph.height.toDp() }
                },
                modifier = Modifier.semantics { heading() }
            )
        }
    } else {
        titleContent
    }
    val singleRowHeight = if (wrapsTitle && titleLines > 1) {
        max(TopAppBarDefaults.TopAppBarExpandedHeight, titleHeight + DSSpacing.sm * 2)
    } else {
        TopAppBarDefaults.TopAppBarExpandedHeight
    }
    val leading: @Composable () -> Unit = {
        when {
            onBack != null -> BackButton(onBack)
            navigationIcon != null -> navigationIcon()
        }
    }

    when (style) {
        DSTopAppBarStyle.Small -> TopAppBar(
            title = singleRowTitle,
            modifier = modifier,
            navigationIcon = leading,
            actions = actions,
            expandedHeight = singleRowHeight,
            colors = colors,
            scrollBehavior = scrollBehavior
        )

        DSTopAppBarStyle.CenterAligned -> CenterAlignedTopAppBar(
            title = singleRowTitle,
            modifier = modifier,
            navigationIcon = leading,
            actions = actions,
            expandedHeight = singleRowHeight,
            colors = colors,
            scrollBehavior = scrollBehavior
        )

        DSTopAppBarStyle.Medium -> MediumTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = leading,
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior
        )

        DSTopAppBarStyle.Large -> LargeTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = leading,
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun barColors(style: DSTopAppBarStyle, containerColor: Color?): TopAppBarColors {
    LocalDSTopAppBarBrandColors.current?.let { brand -> return brandColors(style, brand) }

    val screen = containerColor ?: return when (style) {
        DSTopAppBarStyle.Small -> TopAppBarDefaults.topAppBarColors()
        DSTopAppBarStyle.CenterAligned -> TopAppBarDefaults.centerAlignedTopAppBarColors()
        DSTopAppBarStyle.Medium -> TopAppBarDefaults.mediumTopAppBarColors()
        DSTopAppBarStyle.Large -> TopAppBarDefaults.largeTopAppBarColors()
    }

    return when (style) {
        DSTopAppBarStyle.Small -> TopAppBarDefaults.topAppBarColors(containerColor = screen)
        DSTopAppBarStyle.CenterAligned -> TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = screen)
        DSTopAppBarStyle.Medium -> TopAppBarDefaults.mediumTopAppBarColors(containerColor = screen)
        DSTopAppBarStyle.Large -> TopAppBarDefaults.largeTopAppBarColors(containerColor = screen)
    }
}

/** A barra de marca: o mesmo fundo parada e rolada, título e ícones na cor do conteúdo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun brandColors(style: DSTopAppBarStyle, brand: DSTopAppBarBrandColors): TopAppBarColors {
    val container = brand.container
    val content = brand.content

    return when (style) {
        DSTopAppBarStyle.Small -> TopAppBarDefaults.topAppBarColors(container, container, content, content, content)
        DSTopAppBarStyle.CenterAligned -> TopAppBarDefaults.centerAlignedTopAppBarColors(container, container, content, content, content)
        DSTopAppBarStyle.Medium -> TopAppBarDefaults.mediumTopAppBarColors(container, container, content, content, content)
        DSTopAppBarStyle.Large -> TopAppBarDefaults.largeTopAppBarColors(container, container, content, content, content)
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

            DSTopAppBar("Today", navigationIcon = { DSIconButton(DSIcons.Home, "Home", onClick = {}) })

            DSTopAppBar("Messages", style = DSTopAppBarStyle.Large)
        }
    }
}
