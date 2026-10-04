package io.github.romulomga.dskit

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Escala de espaçamento, o ritmo 4/8 do DSKit iOS (pt → dp). */
public object DSSpacing {
    public val xxs: Dp = 2.dp
    public val xs: Dp = 4.dp
    public val sm: Dp = 8.dp
    public val md: Dp = 12.dp
    public val lg: Dp = 16.dp
    public val xl: Dp = 24.dp
    public val xxl: Dp = 32.dp
}

/** Alvos de toque. O Android pede 48 dp (o iOS usa 44 pt). */
public object DSTouchTarget {
    public val minimum: Dp = 48.dp

    /** Altura mínima de botão retangular, a mesma do DSKit iOS. */
    public val button: Dp = 50.dp
}
