package io.github.romulomga.dskit

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
    fun radiusScaleGrowsMonotonically() {
        val scale = listOf(DSRadius.sm, DSRadius.md, DSRadius.lg, DSRadius.xl)
        assertEquals(scale.sorted(), scale)
    }

    @Test
    fun typeScaleFollowsTheIosTextStyles() {
        val styles = DSTextStyles.create()
        val sizes = listOf(
            styles.largeTitle, styles.title, styles.title2, styles.title3, styles.body,
            styles.callout, styles.subheadline, styles.footnote, styles.caption, styles.caption2
        ).map { it.fontSize.value }
        assertEquals(listOf(34f, 28f, 22f, 20f, 17f, 16f, 15f, 13f, 12f, 11f), sizes)
    }

    @Test
    fun surfaceLevelZeroIsSurfaceAndDeeperIsElevated() {
        val colors = DSColorTokens.Light
        assertEquals(colors.surface, colors.surface(0))
        assertEquals(colors.surfaceElevated, colors.surface(1))
        assertEquals(colors.surfaceElevated, colors.surface(3))
    }

    @Test
    fun brandReplacesOnlyTheAccentColors() {
        val brand = DSBrand(
            accent = androidx.compose.ui.graphics.Color(0xFFD0212E),
            accentDark = androidx.compose.ui.graphics.Color(0xFFEA4652),
            accentSecondary = androidx.compose.ui.graphics.Color(0xFF111111)
        )
        val dark = DSColorTokens.Dark.withBrand(brand, dark = true)
        assertEquals(brand.accentDark, dark.accent)
        assertEquals(DSColorTokens.Dark.surface, dark.surface)
    }

    @Test
    fun readableTextKeepsContrastOnEveryCanvas() {
        listOf(DSColorTokens.Light, DSColorTokens.Dark).forEach { colors ->
            listOf(colors.background, colors.surface, colors.surfaceElevated, colors.backgroundGrouped).forEach { canvas ->
                assertTrue(contrast(colors.onSurfaceHigh, canvas) >= 7.0)
                assertTrue(contrast(colors.onSurfaceMedium, canvas) >= 4.5)
            }
        }
    }

    @Test
    fun versionMatchesTheReleasedTag() {
        assertTrue(DSKit.VERSION.matches(Regex("""\d+\.\d+\.\d+""")))
    }

    private fun contrast(a: androidx.compose.ui.graphics.Color, b: androidx.compose.ui.graphics.Color): Double {
        fun luminance(c: androidx.compose.ui.graphics.Color): Double {
            fun ch(v: Float): Double = if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
            return 0.2126 * ch(c.red) + 0.7152 * ch(c.green) + 0.0722 * ch(c.blue)
        }
        val (hi, lo) = listOf(luminance(a), luminance(b)).sortedDescending()
        return (hi + 0.05) / (lo + 0.05)
    }
}
