package io.github.romulomga.dskit.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.romulomga.dskit.DSFieldLabelStyle
import io.github.romulomga.dskit.DSTheme
import io.github.romulomga.dskit.DSToastHost
import io.github.romulomga.dskit.DSToastMessage

/** Catálogo do DSKit Android: todos os componentes num app de verdade, com tema claro/escuro e toast. */
class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var dark by rememberSaveable { mutableStateOf(systemDark) }
            var prominentLabels by rememberSaveable { mutableStateOf(false) }
            var toast by androidx.compose.runtime.remember { mutableStateOf<DSToastMessage?>(null) }

            DSTheme(
                appName = "DSKit",
                darkTheme = dark,
                fieldLabelStyle = if (prominentLabels) DSFieldLabelStyle.Prominent else DSFieldLabelStyle.Compact
            ) {
                Box(Modifier.fillMaxSize()) {
                    CatalogScreen(
                        dark = dark,
                        onDarkChange = { dark = it },
                        prominentLabels = prominentLabels,
                        onProminentLabelsChange = { prominentLabels = it },
                        onToast = { toast = it }
                    )

                    DSToastHost(message = toast, onDismiss = { toast = null })
                }
            }
        }
    }
}
