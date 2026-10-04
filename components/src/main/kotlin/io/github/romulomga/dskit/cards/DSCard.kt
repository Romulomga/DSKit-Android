package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Variante de superfície do [DSCard]. Escolha pelo que está por baixo:
 * - [Default]: card padrão com contorno discreto e sombra suave; sempre se destaca do fundo da rolagem.
 * - [Elevated]: sombra mais forte, para cards de destaque.
 * - [Material]: no iOS é o vidro translúcido de overlays e barras flutuantes. O Android não tem material
 *   translúcido equivalente: aqui ele é desenhado como [Elevated], mas sem contorno.
 */
public enum class DSCardVariant {
    Default,
    Elevated,
    Material
}

/**
 * Contêiner de card com cantos arredondados. A variante [DSCardVariant.Default] tem contorno e sombra
 * discretos para o card nunca se confundir com o fundo da tela.
 *
 * O conteúdo fica dentro de [DSSurfaceContainer]: um campo dentro do card lê um nível de superfície a mais
 * e é pintado em `surfaceElevated`. No [DSGroupedCanvas] o próprio card já fica em `surfaceElevated`.
 */
@Composable
public fun DSCard(
    modifier: Modifier = Modifier,
    variant: DSCardVariant = DSCardVariant.Default,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = DSRadius.large
    val shadow = when (variant) {
        DSCardVariant.Default -> DSShadow.subtle
        DSCardVariant.Elevated, DSCardVariant.Material -> DSShadow.card
    }
    val bordered = variant != DSCardVariant.Material

    Column(
        modifier = modifier
            .fillMaxWidth()
            .dsShadow(shadow, shape)
            .clip(shape)
            .background(DSTheme.colors.surface(DSTheme.surfaceLevel))
            .then(if (bordered) Modifier.border(0.75.dp, DSTheme.colors.hairline, shape) else Modifier)
            .padding(DSSpacing.lg)
    ) {
        DSSurfaceContainer {
            Column(content = content)
        }
    }
}

@DSPreviews
@Composable
private fun DSCardVariantsPreview() {
    DSPreviewContainer("Card variants") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSCard { Text("Default — subtle border + soft shadow", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }

            DSCard(variant = DSCardVariant.Elevated) {
                Text("Elevated — stronger shadow", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)
            }

            DSCard(variant = DSCardVariant.Material) {
                Text("Material — no border", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)
            }
        }
    }
}

@DSPreviews
@Composable
private fun DSCardGroupedPreview() {
    DSPreviewContainer("Grouped canvas") {
        DSGroupedCanvas {
            Column(Modifier.padding(DSSpacing.lg), verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
                DSCard { Text("Elevated cell on grouped canvas", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }

                DSCard { Text("Settings look", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }
            }
        }
    }
}
