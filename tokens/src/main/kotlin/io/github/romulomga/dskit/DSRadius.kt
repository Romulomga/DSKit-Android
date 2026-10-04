package io.github.romulomga.dskit

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Escala de raio. O Compose não tem canto contínuo (squircle); `RoundedCornerShape` é a forma
 * equivalente, com diferença visual mínima.
 */
public object DSRadius {
    public val sm: Dp = 6.dp
    public val md: Dp = 10.dp
    public val lg: Dp = 14.dp
    public val xl: Dp = 20.dp

    public val small: RoundedCornerShape = RoundedCornerShape(sm)
    public val medium: RoundedCornerShape = RoundedCornerShape(md)
    public val large: RoundedCornerShape = RoundedCornerShape(lg)
    public val extraLarge: RoundedCornerShape = RoundedCornerShape(xl)

    /** Pílula: qualquer altura vira cápsula. */
    public val capsule: RoundedCornerShape = RoundedCornerShape(percent = 50)
}
