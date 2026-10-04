package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Contêiner de tela reutilizável: cabeçalho com título grande, subtítulo e slot à direita, conteúdo que
 * rola e uma área de ação fixa no rodapé.
 *
 * Trata os insets de borda a borda uma vez, aqui: o conteúdo começa abaixo da barra de status e do recorte
 * da câmera, e a ação de rodapé fica acima da barra de navegação e **sobe com o teclado** (`ime`), para o
 * botão principal nunca ficar escondido. Sem ação de rodapé, é o fim do conteúdo que ganha essa folga.
 *
 * Não pinta fundo de tela, como no iOS: o fundo é do app (`DSTheme.colors.background`, um degradê…).
 * A barra de rodapé é opaca em `background` com uma linha fina no topo; o iOS usa material translúcido,
 * que o Android não tem.
 *
 * ```
 * DSScreen(
 *     title = "Sorteio de nomes",
 *     subtitle = "Adicione nomes e sorteie",
 *     bottomAction = { DSPrimaryButton("Sortear", onClick = { … }) },
 * ) {
 *     DSListInput(…)
 * }
 * ```
 */
@Composable
public fun DSScreen(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val horizontalInsets = WindowInsets.displayCutout.union(WindowInsets.navigationBars).only(WindowInsetsSides.Horizontal)
    val bottomInsets = WindowInsets.navigationBars.union(WindowInsets.ime).only(WindowInsetsSides.Bottom)

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(horizontalInsets)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.displayCutout).only(WindowInsetsSides.Top))
                .then(if (bottomAction == null) Modifier.windowInsetsPadding(bottomInsets) else Modifier)
                .padding(start = DSSpacing.lg, end = DSSpacing.lg, top = DSSpacing.md, bottom = DSSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)
        ) {
            if (title != null || subtitle != null || trailing != null) {
                ScreenHeader(title, subtitle, trailing)
            }

            content()
        }

        if (bottomAction != null) {
            Column(modifier = Modifier.fillMaxWidth().background(DSTheme.colors.background)) {
                Box(Modifier.fillMaxWidth().height(0.75.dp).background(DSTheme.colors.hairline))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(bottomInsets)
                        .padding(horizontal = DSSpacing.lg, vertical = DSSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    bottomAction()
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(title: String?, subtitle: String?, trailing: (@Composable RowScope.() -> Unit)?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
        Column(
            modifier = Modifier.weight(1f).alignByBaseline(),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = DSTypography.largeTitle,
                    color = DSTheme.colors.onSurfaceHigh,
                    modifier = Modifier.semantics { heading() }
                )
            }

            if (subtitle != null) {
                Text(subtitle, style = DSTypography.subheadline, color = DSTheme.colors.onSurfaceMedium)
            }
        }

        if (trailing != null) {
            Row(modifier = Modifier.alignByBaseline(), verticalAlignment = Alignment.CenterVertically) {
                trailing()
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSScreenPreview() {
    DSPreviewContainer("Screen", modifier = Modifier.height(480.dp)) {
        DSScreen(
            title = "Name Picker",
            subtitle = "Add names and pick a winner",
            bottomAction = {
                DSCard(variant = DSCardVariant.Elevated) {
                    Text("Pick winner", style = DSTypography.headline, color = DSTheme.colors.accent)
                }
            }
        ) {
            DSCard { Text("Content goes here", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }
        }
    }
}
