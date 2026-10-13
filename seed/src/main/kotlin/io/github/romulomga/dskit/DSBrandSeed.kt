package io.github.romulomga.dskit

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.blend.Blend
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.dynamiccolor.DynamicColor
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeFidelity
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant

/** Como o esquema nasce da cor da marca: os mesmos estilos do Material Theme Builder. */
public enum class DSSeedStyle {
    /** O padrão do Material: a cor da marca suavizada, tons calmos. O `primary` não é exatamente a cor dada. */
    TonalSpot,

    /** Fiel à marca: o `primary` fica o mais perto possível da cor dada, ainda com o contraste do Material. */
    Fidelity,

    /** Mais saturado que o [TonalSpot], para marcas que pedem energia. */
    Vibrant
}

/**
 * Gera a marca inteira a partir de uma cor só, como o Material Theme Builder: os 30 papéis do Material em claro e
 * escuro, as versões de contraste médio e alto, e as cores de status (sucesso, aviso, informação) puxadas para o tom
 * da marca. Qualquer cor serve, inclusive preto ou verde neon: os tons saem calculados para passar no contraste.
 *
 * ```
 * DSTheme(brand = DSBrand.fromSeed(Color(0xFF3C0079))) { App() }
 * ```
 *
 * Calcule uma vez (fora da composição ou num `remember`): gerar os seis esquemas custa alguns milissegundos.
 */
public fun DSBrand.Companion.fromSeed(seed: Color, style: DSSeedStyle = DSSeedStyle.TonalSpot): DSBrand {
    val source = Hct.fromInt(seed.toArgb())

    fun scheme(dark: Boolean, contrast: Double): ColorScheme = style.scheme(source, dark, contrast).toColorScheme()

    return DSBrand(
        light = scheme(dark = false, contrast = StandardContrast),
        dark = scheme(dark = true, contrast = StandardContrast),
        lightStatus = statusColors(seed, StatusTones.Light),
        darkStatus = statusColors(seed, StatusTones.Dark),
        lightMediumContrast = scheme(dark = false, contrast = MediumContrast),
        darkMediumContrast = scheme(dark = true, contrast = MediumContrast),
        lightHighContrast = scheme(dark = false, contrast = HighContrast),
        darkHighContrast = scheme(dark = true, contrast = HighContrast),
        lightHighContrastStatus = statusColors(seed, StatusTones.LightHighContrast),
        darkHighContrastStatus = statusColors(seed, StatusTones.DarkHighContrast)
    )
}

private const val StandardContrast = 0.0
private const val MediumContrast = 0.5
private const val HighContrast = 1.0

// A especificação de 2021 é a do Material 3 estável do Compose (a de 2025 é a do Expressive).
private val Spec = ColorSpec.SpecVersion.SPEC_2021

private fun DSSeedStyle.scheme(source: Hct, dark: Boolean, contrast: Double): DynamicScheme = when (this) {
    DSSeedStyle.TonalSpot -> SchemeTonalSpot(source, dark, contrast, Spec, DynamicScheme.Platform.PHONE)
    DSSeedStyle.Fidelity -> SchemeFidelity(source, dark, contrast, Spec, DynamicScheme.Platform.PHONE)
    DSSeedStyle.Vibrant -> SchemeVibrant(source, dark, contrast, Spec, DynamicScheme.Platform.PHONE)
}

private fun DynamicScheme.toColorScheme(): ColorScheme {
    val roles = MaterialDynamicColors()
    fun DynamicColor.color() = Color(getArgb(this@toColorScheme))

    return ColorScheme(
        primary = roles.primary().color(),
        onPrimary = roles.onPrimary().color(),
        primaryContainer = roles.primaryContainer().color(),
        onPrimaryContainer = roles.onPrimaryContainer().color(),
        inversePrimary = roles.inversePrimary().color(),
        secondary = roles.secondary().color(),
        onSecondary = roles.onSecondary().color(),
        secondaryContainer = roles.secondaryContainer().color(),
        onSecondaryContainer = roles.onSecondaryContainer().color(),
        tertiary = roles.tertiary().color(),
        onTertiary = roles.onTertiary().color(),
        tertiaryContainer = roles.tertiaryContainer().color(),
        onTertiaryContainer = roles.onTertiaryContainer().color(),
        background = roles.background().color(),
        onBackground = roles.onBackground().color(),
        surface = roles.surface().color(),
        onSurface = roles.onSurface().color(),
        surfaceVariant = roles.surfaceVariant().color(),
        onSurfaceVariant = roles.onSurfaceVariant().color(),
        surfaceTint = roles.surfaceTint().color(),
        inverseSurface = roles.inverseSurface().color(),
        inverseOnSurface = roles.inverseOnSurface().color(),
        error = roles.error().color(),
        onError = roles.onError().color(),
        errorContainer = roles.errorContainer().color(),
        onErrorContainer = roles.onErrorContainer().color(),
        outline = roles.outline().color(),
        outlineVariant = roles.outlineVariant().color(),
        scrim = roles.scrim().color(),
        surfaceBright = roles.surfaceBright().color(),
        surfaceDim = roles.surfaceDim().color(),
        surfaceContainer = roles.surfaceContainer().color(),
        surfaceContainerHigh = roles.surfaceContainerHigh().color(),
        surfaceContainerHighest = roles.surfaceContainerHighest().color(),
        surfaceContainerLow = roles.surfaceContainerLow().color(),
        surfaceContainerLowest = roles.surfaceContainerLowest().color(),
        primaryFixed = roles.primaryFixed().color(),
        primaryFixedDim = roles.primaryFixedDim().color(),
        onPrimaryFixed = roles.onPrimaryFixed().color(),
        onPrimaryFixedVariant = roles.onPrimaryFixedVariant().color(),
        secondaryFixed = roles.secondaryFixed().color(),
        secondaryFixedDim = roles.secondaryFixedDim().color(),
        onSecondaryFixed = roles.onSecondaryFixed().color(),
        onSecondaryFixedVariant = roles.onSecondaryFixedVariant().color(),
        tertiaryFixed = roles.tertiaryFixed().color(),
        tertiaryFixedDim = roles.tertiaryFixedDim().color(),
        onTertiaryFixed = roles.onTertiaryFixed().color(),
        onTertiaryFixedVariant = roles.onTertiaryFixedVariant().color()
    )
}

/** Os tons de cada papel de status (cor, conteúdo, contêiner, conteúdo do contêiner), os mesmos do `DSStatusColors`. */
private enum class StatusTones(val color: Int, val onColor: Int, val container: Int, val onContainer: Int) {
    Light(40, 100, 90, 10),
    Dark(80, 20, 30, 90),
    LightHighContrast(20, 100, 30, 100),
    DarkHighContrast(90, 10, 80, 10)
}

/**
 * Verde, âmbar e azul puxados um pouco para o tom da marca (a "harmonização" do Theme Builder): continuam lidos como
 * sucesso, aviso e informação, mas combinam com a marca.
 */
private fun statusColors(seed: Color, tones: StatusTones): DSStatusColors {
    fun role(base: Long): DSColorRole {
        val palette = TonalPalette.fromInt(Blend.harmonize(base.toInt(), seed.toArgb()))

        return DSColorRole(
            color = Color(palette.tone(tones.color)),
            onColor = Color(palette.tone(tones.onColor)),
            container = Color(palette.tone(tones.container)),
            onContainer = Color(palette.tone(tones.onContainer))
        )
    }

    return DSStatusColors(success = role(SuccessBase), warning = role(WarningBase), info = role(InfoBase))
}

// As cores de partida das cores de status padrão do DSKit (tom 40).
private const val SuccessBase = 0xFF2E6B30
private const val WarningBase = 0xFF795900
private const val InfoBase = 0xFF0061A4
