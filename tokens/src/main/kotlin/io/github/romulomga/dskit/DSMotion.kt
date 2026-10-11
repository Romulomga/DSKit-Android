package io.github.romulomga.dskit

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween

/**
 * Durações e curvas da especificação de movimento do Material 3, para as animações que o componente monta
 * sozinho (os componentes do Material já trazem as deles). As animações do Compose respeitam "Remover
 * animações" do sistema.
 */
public object DSMotion {
    /** Curva padrão: entra e sai suave, para mudanças dentro da tela. */
    public val standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Curva enfática de entrada: o elemento chega rápido e assenta devagar. */
    public val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    /** Curva enfática de saída: o elemento sai acelerando. */
    public val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    /** 150 ms: troca de cor, de estado, de ícone. */
    public fun <T> short(): FiniteAnimationSpec<T> = tween(durationMillis = 150, easing = standard)

    /** 300 ms: elemento que aparece, some ou muda de tamanho. */
    public fun <T> medium(): FiniteAnimationSpec<T> = tween(durationMillis = 300, easing = standard)

    /** 500 ms: transição que atravessa a tela. */
    public fun <T> long(): FiniteAnimationSpec<T> = tween(durationMillis = 500, easing = emphasizedDecelerate)
}
