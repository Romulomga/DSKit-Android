package io.github.romulomga.dskit

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DSTokensTest {
    @Test
    fun spacingScaleGrowsMonotonically() {
        val scale = listOf(DSSpacing.xxs, DSSpacing.xs, DSSpacing.sm, DSSpacing.md, DSSpacing.lg, DSSpacing.xl, DSSpacing.xxl)
        assertEquals(scale.sorted(), scale)
        assertEquals(scale.size, scale.toSet().size)
    }

    @Test
    fun typographyKeepsTheMaterialScaleAndOnlySwapsTheFamily() {
        val base = Typography()
        val branded = DSTypography.create(FontFamily.Serif)

        assertEquals(base.titleLarge.fontSize, branded.titleLarge.fontSize)
        assertEquals(base.bodyMedium.lineHeight, branded.bodyMedium.lineHeight)
        assertEquals(FontFamily.Serif, branded.labelSmall.fontFamily)
        assertEquals(base, DSTypography.create(null))
    }

    @Test
    fun statusColorsKeepContrastWithTheirContent() {
        listOf(DSStatusColors.Light, DSStatusColors.Dark).forEach { status ->
            listOf(status.success, status.warning, status.info).forEach { role ->
                assertTrue(contrast(role.onColor, role.color) >= 4.5)
                assertTrue(contrast(role.onContainer, role.container) >= 4.5)
            }
        }
    }

    @Test
    fun highContrastStatusColorsReachAaa() {
        listOf(DSStatusColors.LightHighContrast, DSStatusColors.DarkHighContrast).forEach { status ->
            listOf(status.success, status.warning, status.info).forEach { role ->
                assertTrue(contrast(role.onColor, role.color) >= 7.0)
                assertTrue(contrast(role.onContainer, role.container) >= 7.0)
            }
        }
    }

    @Test
    fun highContrastStatusColorsOnlyApplyToHighContrast() {
        val brand = DSBrand.Default

        assertEquals(DSStatusColors.LightHighContrast, brand.statusColors(darkTheme = false, contrast = DSContrast.High))
        assertEquals(DSStatusColors.DarkHighContrast, brand.statusColors(darkTheme = true, contrast = DSContrast.High))
        assertEquals(DSStatusColors.Light, brand.statusColors(darkTheme = false, contrast = DSContrast.Medium))
        assertEquals(DSStatusColors.Dark, brand.statusColors(darkTheme = true, contrast = DSContrast.Standard))
    }

    @Test
    fun contrastFallsBackToTheNearestSchemeTheBrandHas() {
        val medium = lightColorScheme(primary = Color.Blue)
        val high = darkColorScheme(primary = Color.Red)
        val brand = DSBrand.Default.copy(lightMediumContrast = medium, darkHighContrast = high)

        assertEquals(medium, brand.colorScheme(darkTheme = false, contrast = DSContrast.High))
        assertEquals(brand.light, brand.colorScheme(darkTheme = false, contrast = DSContrast.Standard))
        assertEquals(brand.dark, brand.colorScheme(darkTheme = true, contrast = DSContrast.Medium))
        assertEquals(high, brand.colorScheme(darkTheme = true, contrast = DSContrast.High))
    }

    @Test
    fun versionMatchesTheReleasedTag() {
        assertTrue(DSKit.VERSION.matches(Regex("""\d+\.\d+\.\d+""")))
    }

    private fun contrast(a: Color, b: Color): Double {
        fun luminance(c: Color): Double {
            fun ch(v: Float): Double = if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
            return 0.2126 * ch(c.red) + 0.7152 * ch(c.green) + 0.0722 * ch(c.blue)
        }
        val (hi, lo) = listOf(luminance(a), luminance(b)).sortedDescending()
        return (hi + 0.05) / (lo + 0.05)
    }
}
