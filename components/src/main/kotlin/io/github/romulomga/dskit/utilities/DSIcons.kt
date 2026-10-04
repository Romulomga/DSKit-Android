// Caminhos dos Material Icons (Google, Apache 2.0), copiados para a lib não depender do
// material-icons-extended, que é pesado e não recebe mais atualização.
package io.github.romulomga.dskit

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Os poucos ícones que os próprios componentes do DSKit desenham (check do chip, chevron da linha de
 * ajustes, olho da senha…). Ícones do app entram pelos parâmetros `icon: ImageVector?` dos componentes.
 */
public object DSIcons {
    public val Check: ImageVector by lazy { icon("Check", "M9,16.17L4.83,12l-1.42,1.41L9,19 21,7l-1.41,-1.41z") }
    public val Close: ImageVector by lazy {
        icon("Close", "M19,6.41L17.59,5 12,10.59 6.41,5 5,6.41 10.59,12 5,17.59 6.41,19 12,13.41 17.59,19 19,17.59 13.41,12z")
    }
    public val ChevronRight: ImageVector by lazy { icon("ChevronRight", "M10,6L8.59,7.41 13.17,12l-4.58,4.59L10,18l6,-6z") }
    public val ChevronDown: ImageVector by lazy { icon("ChevronDown", "M16.59,8.59L12,13.17 7.41,8.59 6,10l6,6 6,-6z") }
    public val ChevronUpDown: ImageVector by lazy {
        icon(
            "ChevronUpDown",
            "M12,5.83L15.17,9l1.41,-1.41L12,3 7.41,7.59 8.83,9 12,5.83zM12,18.17L8.83,15l-1.41,1.41L12,21l4.59,-4.59L15.17,15 12,18.17z",
        )
    }
    public val Eye: ImageVector by lazy {
        icon(
            "Eye",
            "M12,4.5C7,4.5 2.73,7.61 1,12c1.73,4.39 6,7.5 11,7.5s9.27,-3.11 11,-7.5c-1.73,-4.39 -6,-7.5 -11,-7.5zM12,17c-2.76,0 -5," +
                "-2.24 -5,-5s2.24,-5 5,-5 5,2.24 5,5 -2.24,5 -5,5zM12,9c-1.66,0 -3,1.34 -3,3s1.34,3 3,3 3,-1.34 3,-3 -1.34,-3 -3,-3z",
        )
    }
    public val EyeSlash: ImageVector by lazy {
        icon(
            "EyeSlash",
            "M12,7c2.76,0 5,2.24 5,5 0,0.65 -0.13,1.26 -0.36,1.83l2.92,2.92c1.51,-1.26 2.7,-2.89 3.43,-4.75 -1.73,-4.39 -6,-7.5 -11," +
                "-7.5 -1.4,0 -2.74,0.25 -3.98,0.7l2.16,2.16C10.74,7.13 11.35,7 12,7zM2,4.27l2.28,2.28 0.46,0.46C3.08,8.3 1.78,10.02 1," +
                "12c1.73,4.39 6,7.5 11,7.5 1.55,0 3.03,-0.3 4.38,-0.84l0.42,0.42L19.73,22 21,20.73 3.27,3 2,4.27zM7.53,9.8l1.55,1.55c-0.05," +
                "0.21 -0.08,0.43 -0.08,0.65 0,1.66 1.34,3 3,3 0.22,0 0.44,-0.03 0.65,-0.08l1.55,1.55c-0.67,0.33 -1.41,0.53 -2.2,0.53 -2.76," +
                "0 -5,-2.24 -5,-5 0,-0.79 0.2,-1.53 0.53,-2.2zM11.84,9.02l3.15,3.15 0.02,-0.16c0,-1.66 -1.34,-3 -3,-3l-0.17,0.01z",
        )
    }
    public val ExclamationCircle: ImageVector by lazy {
        icon("ExclamationCircle", "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM13,17h-2v-2h2v2zM13,13h-2L11,7h2v6z")
    }
    public val InfoCircle: ImageVector by lazy {
        icon("InfoCircle", "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM13,17h-2v-6h2v6zM13,9h-2L11,7h2v2z")
    }
    public val ExclamationTriangle: ImageVector by lazy {
        icon("ExclamationTriangle", "M1,21h22L12,2 1,21zM13,18h-2v-2h2v2zM13,14h-2v-4h2v4z")
    }
    public val CheckCircle: ImageVector by lazy {
        icon(
            "CheckCircle",
            "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM10,17l-5,-5 1.41,-1.41L10,14.17l7.59,-7.59L19,8l-9,9z",
        )
    }
    public val XCircle: ImageVector by lazy {
        icon(
            "XCircle",
            "M12,2C6.47,2 2,6.47 2,12s4.47,10 10,10 10,-4.47 10,-10S17.53,2 12,2zM17,15.59L15.59,17 12,13.41 8.41,17 7,15.59 10.59," +
                "12 7,8.41 8.41,7 12,10.59 15.59,7 17,8.41 13.41,12 17,15.59z",
        )
    }
    public val Plus: ImageVector by lazy { icon("Plus", "M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z") }
    public val Minus: ImageVector by lazy { icon("Minus", "M19,13H5v-2h14v2z") }
    public val Lock: ImageVector by lazy {
        icon(
            "Lock",
            "M18,8h-1L17,6c0,-2.76 -2.24,-5 -5,-5S7,3.24 7,6v2L6,8c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2L20,10c0," +
                "-1.1 -0.9,-2 -2,-2zM12,17c-1.1,0 -2,-0.9 -2,-2s0.9,-2 2,-2 2,0.9 2,2 -0.9,2 -2,2zM15.1,8L8.9,8L8.9,6c0,-1.71 1.39,-3.1 " +
                "3.1,-3.1 1.71,0 3.1,1.39 3.1,3.1v2z",
        )
    }
    public val ArrowClockwise: ImageVector by lazy {
        icon(
            "ArrowClockwise",
            "M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 " +
                "-3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z",
        )
    }
    public val Tray: ImageVector by lazy {
        icon(
            "Tray",
            "M19,3H4.99c-1.11,0 -1.98,0.89 -1.98,2L3,19c0,1.1 0.88,2 1.99,2H19c1.1,0 2,-0.9 2,-2V5c0,-1.11 -0.9,-2 -2,-2zM19,15h-4c0," +
                "1.66 -1.35,3 -3,3s-3,-1.34 -3,-3H4.99V5H19v10z",
        )
    }
    public val Star: ImageVector by lazy {
        icon("Star", "M12,17.27L18.18,21l-1.64,-7.03L22,9.24l-7.19,-0.61L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21z")
    }
    public val Circle: ImageVector by lazy {
        icon(
            "Circle",
            "M12,2C6.47,2 2,6.47 2,12s4.47,10 10,10 10,-4.47 10,-10S17.53,2 12,2zM12,20c-4.42,0 -8,-3.58 -8,-8s3.58,-8 8,-8 8,3.58 8,8 " +
                "-3.58,8 -8,8z",
        )
    }

    private fun icon(name: String, path: String): ImageVector = ImageVector.Builder(
        name = "DSIcons.$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(pathData = addPathNodes(path), fill = SolidColor(Color.Black)).build()
}
