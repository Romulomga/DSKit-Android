package io.github.romulomga.dskit.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import io.github.romulomga.dskit.DSBrand
import io.github.romulomga.dskit.DSTheme
import io.github.romulomga.dskit.fromSeed

/** Marcas de exemplo: o esquema base do Material e três geradas por `DSBrand.fromSeed`, inclusive uma cor difícil. */
internal enum class SampleBrand(val label: String, val seed: Long?) {
    Base("Material (base)", null),
    Purple("Roxo #3C0079", 0xFF3C0079),
    Orange("Laranja #E66406", 0xFFE66406),
    Neon("Verde neon #03FF00", 0xFF03FF00)
}

/** Catálogo do DSKit Android: todos os componentes num app de verdade, com tema claro/escuro e Material You. */
class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var dark by rememberSaveable { mutableStateOf(systemDark) }
            var dynamicColor by rememberSaveable { mutableStateOf(false) }
            var brand by rememberSaveable { mutableStateOf(SampleBrand.Base) }
            val dsBrand = remember(brand) { brand.seed?.let { DSBrand.fromSeed(Color(it)) } ?: DSBrand.Default }

            DSTheme(brand = dsBrand, darkTheme = dark, dynamicColor = dynamicColor) {
                CatalogApp(
                    dark = dark,
                    onDarkChange = { dark = it },
                    dynamicColor = dynamicColor,
                    onDynamicColorChange = { dynamicColor = it },
                    brand = brand,
                    onBrandChange = { brand = it }
                )
            }
        }
    }
}
