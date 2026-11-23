package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * O indicador de carregamento do app inteiro, no lugar do círculo do Material. O app liga uma vez no tema,
 * `CompositionLocalProvider(LocalDSLoadingIndicator provides { modifier, color -> MeuIndicador(modifier, color) })`, e o
 * [DSButton] em andamento, o [DSLoadingOverlay] e o [DSLoadingState] passam a desenhá-lo. O indicador recebe o tamanho
 * no `modifier` e a cor que o componente já usa. Sem nada ligado (o padrão), fica o círculo do Material.
 *
 * O "puxar para atualizar" do [DSScreen] não usa este indicador: continua com o do Material.
 *
 * Quem cuida da acessibilidade é o componente: o que o indicador do app disser ao TalkBack é descartado, e fica só o
 * "em andamento" do círculo do Material.
 */
public val LocalDSLoadingIndicator: ProvidableCompositionLocal<(@Composable (modifier: Modifier, color: Color) -> Unit)?> =
    staticCompositionLocalOf { null }

/** O indicador grande (do [DSLoadingOverlay] e do [DSLoadingState]): o do app, ou o círculo do Material sem argumentos. */
@Composable
internal fun LoadingIndicator() {
    val custom = LocalDSLoadingIndicator.current

    if (custom == null) {
        CircularProgressIndicator()
    } else {
        CustomLoadingIndicator(custom, Modifier.size(MaterialCircularIndicatorSize), ProgressIndicatorDefaults.circularColor)
    }
}

/** O indicador pequeno do botão em andamento, do tamanho de um ícone de botão e na cor do texto. */
@Composable
internal fun ButtonLoadingIndicator(size: Dp, color: Color) {
    val custom = LocalDSLoadingIndicator.current

    if (custom == null) {
        CircularProgressIndicator(modifier = Modifier.size(size), color = color, strokeWidth = 2.dp)
    } else {
        CustomLoadingIndicator(custom, Modifier.size(size), color)
    }
}

/** O indicador do app com a mesma semântica do círculo do Material: só "em andamento", nada do que ele disser. */
@Composable
private fun CustomLoadingIndicator(indicator: @Composable (Modifier, Color) -> Unit, modifier: Modifier, color: Color) {
    Box(Modifier.clearAndSetSemantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }) {
        indicator(modifier, color)
    }
}

/** O diâmetro do `CircularProgressIndicator` do Material (o `BehaviorTest` confere contra o de verdade). */
internal val MaterialCircularIndicatorSize = 40.dp
