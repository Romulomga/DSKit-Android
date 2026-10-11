package io.github.romulomga.dskit.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.github.romulomga.dskit.DSTheme

/** Catálogo do DSKit Android: todos os componentes num app de verdade, com tema claro/escuro e Material You. */
class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var dark by rememberSaveable { mutableStateOf(systemDark) }
            var dynamicColor by rememberSaveable { mutableStateOf(false) }

            DSTheme(darkTheme = dark, dynamicColor = dynamicColor) {
                CatalogApp(
                    dark = dark,
                    onDarkChange = { dark = it },
                    dynamicColor = dynamicColor,
                    onDynamicColorChange = { dynamicColor = it }
                )
            }
        }
    }
}
