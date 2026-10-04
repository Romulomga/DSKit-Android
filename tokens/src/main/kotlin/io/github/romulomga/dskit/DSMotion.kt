package io.github.romulomga.dskit

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Curvas de animação do DSKit iOS. Movimento curto e firme.
 *
 * As molas usam a conversão do SwiftUI: `stiffness = (2π / response)²` e o mesmo `dampingRatio`.
 * Quem respeita "Remover animações" é o componente: com `LocalDSReduceMotion` ligado, troque o
 * movimento por um crossfade ou por nada.
 */
public object DSMotion {
    /** easeInOut de 180 ms. */
    public fun <T> short(): AnimationSpec<T> = tween(durationMillis = 180, easing = FastOutSlowInEasing)

    /** easeInOut de 280 ms. */
    public fun <T> medium(): AnimationSpec<T> = tween(durationMillis = 280, easing = FastOutSlowInEasing)

    /** Mola padrão (response 0,36 s, amortecimento 0,78). */
    public fun <T> spring(): AnimationSpec<T> = spring(dampingRatio = 0.78f, stiffness = stiffness(0.36f))

    /** Mola interativa e rápida (response 0,28 s, amortecimento 0,88). */
    public fun <T> snappy(): AnimationSpec<T> = spring(dampingRatio = 0.88f, stiffness = stiffness(0.28f))

    /** Mola maior para mudanças de estado com destaque (response 0,5 s, amortecimento 0,82). */
    public fun <T> emphasized(): AnimationSpec<T> = spring(dampingRatio = 0.82f, stiffness = stiffness(0.5f))

    private fun stiffness(response: Float): Float {
        val omega = (2 * Math.PI / response).toFloat()
        return omega * omega
    }
}
