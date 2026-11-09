package io.github.romulomga.dskit

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** O que as peças de edição fazem (guarda de descarte, diálogos, campo decimal, stepper, painel), sem imagem. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class EditingBehaviorTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun theDiscardGuardLeavesRightAwayWithoutChangesAndAsksWithThem() {
        var exits = 0

        composeRule.setContent { DSTheme { GuardedForm(onExit = { exits++ }) } }

        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.runOnIdle { assertEquals(1, exits) }

        composeRule.onNodeWithText("Note").performTextInput("Beach house")
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Discard changes?").assertExists()
        composeRule.runOnIdle { assertEquals(1, exits) }

        composeRule.onNodeWithText("Keep editing").performClick()
        composeRule.onNodeWithText("Discard changes?").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(1, exits) }

        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Discard").performClick()
        composeRule.onNodeWithText("Discard changes?").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(2, exits) }
    }

    @Test
    fun theSystemBackAsksBeforeDiscardingWhatWasTyped() {
        var exits = 0

        composeRule.setContent { DSTheme { GuardedForm(onExit = { exits++ }) } }

        composeRule.onNodeWithText("Note").performTextInput("Beach house")
        composeRule.runOnIdle { composeRule.activity.onBackPressedDispatcher.onBackPressed() }

        composeRule.onNodeWithText("Discard changes?").assertExists()
        composeRule.runOnIdle { assertEquals(0, exits) }
    }

    /** A Activity recriada (rotação, tema, processo morto) não pode fechar a pergunta nem esquecer o que mudou. */
    @Test
    fun theDiscardQuestionAndTheOpeningSnapshotSurviveActivityRecreation() {
        var exits = 0
        val restoration = StateRestorationTester(composeRule)

        restoration.setContent { DSTheme { GuardedForm(onExit = { exits++ }) } }

        composeRule.onNodeWithText("Note").performTextInput("Beach house")
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Discard changes?").assertExists()

        restoration.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithText("Discard changes?").assertExists()
        composeRule.onNodeWithText("Keep editing").performClick()

        // O retrato continua sendo o de quando a tela abriu (vazio): o texto restaurado ainda é mudança.
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Discard changes?").assertExists()
        composeRule.runOnIdle { assertEquals(0, exits) }
    }

    @Composable
    private fun GuardedForm(onExit: () -> Unit) {
        var note by rememberSaveable { mutableStateOf("") }
        val guard = rememberDSDiscardGuardState(onExit)

        DSDiscardGuard(guard, hasChanges = rememberDSHasChanges(note))

        Column {
            DSTextField(value = note, onValueChange = { note = it }, label = "Note")

            DSTextButton("Cancel", onClick = guard::requestExit)
        }
    }

    // <novos testes acima>
}
