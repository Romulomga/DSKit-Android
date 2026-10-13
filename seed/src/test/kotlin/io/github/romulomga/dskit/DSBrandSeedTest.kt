package io.github.romulomga.dskit

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.pow
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DSBrandSeedTest {
    /** Marcas comuns e as difíceis: preto, branco, verde neon, amarelo claro. */
    private val seeds = listOf(
        0xFF3C0079, 0xFFE66406, 0xFF000000, 0xFFFFFFFF, 0xFF03FF00,
        0xFFF1DA10, 0xFF0061A4, 0xFFD0212E, 0xFF2FBAA7, 0xFF6750A4
    ).map { Color(it) }

    @Test
    fun everySeedAndStyleKeepsTheMaterialContrast() {
        seeds.forEach { seed ->
            DSSeedStyle.entries.forEach { style ->
                val brand = DSBrand.fromSeed(seed, style)

                listOf(brand.light, brand.dark).forEach { assertReadable(it, 4.5, "$seed $style") }
                listOf(brand.lightHighContrast!!, brand.darkHighContrast!!).forEach { assertReadable(it, 7.0, "$seed $style alto") }
                listOf(brand.lightStatus, brand.darkStatus).forEach { assertReadable(it, 4.5, "$seed status") }
                listOf(brand.lightHighContrastStatus, brand.darkHighContrastStatus).forEach { assertReadable(it, 7.0, "$seed status alto") }
            }
        }
    }

    @Test
    fun fidelityStaysCloserToTheBrandThanTonalSpot() {
        val orange = Color(0xFFE66406)

        val fidelity = DSBrand.fromSeed(orange, DSSeedStyle.Fidelity).light.primary
        val tonalSpot = DSBrand.fromSeed(orange, DSSeedStyle.TonalSpot).light.primary

        assertTrue(distance(fidelity, orange) < distance(tonalSpot, orange))
    }

    @Test
    fun contrastLevelsAreDifferentSchemes() {
        val brand = DSBrand.fromSeed(Color(0xFF3C0079))

        assertNotEquals(brand.light.onSurfaceVariant, brand.lightHighContrast!!.onSurfaceVariant)
        assertNotEquals(brand.dark.outline, brand.darkMediumContrast!!.outline)
    }

    private fun assertReadable(scheme: ColorScheme, minimum: Double, label: String) {
        listOf(
            scheme.onPrimary to scheme.primary,
            scheme.onPrimaryContainer to scheme.primaryContainer,
            scheme.onSecondaryContainer to scheme.secondaryContainer,
            scheme.onError to scheme.error,
            scheme.onSurface to scheme.surface,
            scheme.onSurfaceVariant to scheme.surfaceContainerHighest
        ).forEach { (content, background) ->
            assertTrue("$label: ${contrast(content, background)} < $minimum", contrast(content, background) >= minimum)
        }
    }

    private fun assertReadable(status: DSStatusColors, minimum: Double, label: String) {
        listOf(status.success, status.warning, status.info).forEach { role ->
            assertTrue("$label: cor", contrast(role.onColor, role.color) >= minimum)
            assertTrue("$label: contêiner", contrast(role.onContainer, role.container) >= minimum)
        }
    }

    private fun contrast(a: Color, b: Color): Double {
        fun channel(v: Float): Double = if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        fun luminance(c: Color): Double = 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
        val (high, low) = listOf(luminance(a), luminance(b)).sortedDescending()

        return (high + 0.05) / (low + 0.05)
    }

    private fun distance(a: Color, b: Color): Float = abs(a.red - b.red) + abs(a.green - b.green) + abs(a.blue - b.blue)
}
