package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage
import org.junit.Rule
import org.junit.Test

/**
 * Diálogo e painel abertos de verdade: abrem em janela própria, então a captura é da tela inteira
 * (`captureScreenRoboImage`), em claro e escuro. Sem variante
 * da direita para a esquerda: no Robolectric a janela própria não recebe o RTL do aparelho. Classe própria porque a regra do Compose muda a captura
 * do `snapshot` das outras classes.
 */
@OptIn(ExperimentalRoborazziApi::class)
class DialogScreenshotTest : ScreenshotTestBase() {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun alertDialogLight() = screen("overlays_alert_dialog_light", dark = false) { Alert() }

    @Test
    fun alertDialogDark() = screen("overlays_alert_dialog_dark", dark = true) { Alert() }

    @Test
    fun bottomSheetLight() = screen("overlays_bottom_sheet_light", dark = false) { Sheet() }

    @Test
    fun bottomSheetDark() = screen("overlays_bottom_sheet_dark", dark = true) { Sheet() }

    /** O menu de sugestões só abre depois de digitar, então o teste digita no campo antes da captura. */
    @Test
    fun autocompleteOpen() {
        composeRule.setContent {
            DSPreviewContainer(title = "inputs_autocomplete_open_light", modifier = Modifier.fillMaxSize(), darkTheme = false) {
                var query by remember { mutableStateOf("") }

                DSAutocompleteField(
                    query = query,
                    onQueryChange = { query = it },
                    suggestions = listOf("Ipanema", "Lagoa", "Laranjeiras", "Leblon")
                        .filter { query.isNotEmpty() && it.startsWith(query, ignoreCase = true) }
                        .map { DSOption(it, it) },
                    onSuggestionSelect = { query = it.label },
                    label = "Neighborhood"
                )
            }
        }
        composeRule.onNodeWithText("Neighborhood").performTextInput("La")
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/inputs_autocomplete_open_light.png")
    }

    /** A dica rica aparece ao segurar o ícone. */
    @Test
    fun tooltipOpen() {
        composeRule.setContent {
            DSPreviewContainer(title = "overlays_tooltip_light", modifier = Modifier.fillMaxSize(), darkTheme = false) {
                DSTooltip(text = "Pinned posts stay at the top of the feed.", title = "Pin") {
                    Icon(DSIcons.Info, contentDescription = "About pins")
                }
            }
        }
        composeRule.onNodeWithContentDescription("About pins").performTouchInput { longClick() }
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/overlays_tooltip_light.png")
    }

    @Composable
    private fun Alert() {
        DSAlertDialog(
            title = "Delete post?",
            message = "The post leaves the feed and your partners stop seeing it.",
            confirmText = "Delete",
            dismissText = "Cancel",
            destructive = true,
            onConfirm = {},
            onDismissRequest = {}
        )
    }

    @Composable
    private fun Sheet() {
        DSBottomSheet(visible = true, onDismissRequest = {}, title = "Post options") {
            Column {
                DSSheetAction(text = "Edit", icon = DSIcons.Edit, onClick = {})

                DSSheetAction(text = "Share", icon = DSIcons.Share, onClick = {})

                DSSheetAction(text = "Delete post", icon = DSIcons.Delete, destructive = true, onClick = {})
            }
        }
    }

    private fun screen(file: String, dark: Boolean, content: @Composable () -> Unit) {
        composeRule.setContent {
            DSPreviewContainer(title = file, modifier = Modifier.fillMaxSize(), darkTheme = dark) {
                content()
            }
        }
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/$file.png")
    }
}
