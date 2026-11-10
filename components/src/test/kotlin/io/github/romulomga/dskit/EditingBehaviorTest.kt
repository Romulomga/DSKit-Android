package io.github.romulomga.dskit

import android.view.MotionEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

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

    @Test
    fun confirmingADeleteHandsOverThePendingItemClosesAndVibratesOnce() {
        val deleted = mutableListOf<String>()
        var pending by mutableStateOf<String?>("client-7")
        var haptics = 0

        composeRule.setContent {
            val view = remember(composeRule.activity) {
                object : View(composeRule.activity) {
                    override fun performHapticFeedback(feedbackConstant: Int): Boolean {
                        haptics++
                        return true
                    }
                }
            }

            CompositionLocalProvider(LocalView provides view) {
                DSTheme {
                    DSConfirmDeleteDialog(
                        pending = pending,
                        title = "Delete client?",
                        confirmText = "Delete client",
                        onDelete = { deleted += it },
                        onDismiss = { pending = null }
                    )
                }
            }
        }

        composeRule.onNodeWithText("Delete client").performClick()

        composeRule.onNodeWithText("Delete client?").assertDoesNotExist()
        composeRule.runOnIdle {
            assertEquals(listOf("client-7"), deleted)
            assertEquals(1, haptics)
        }
    }

    @Test
    fun cancellingADeleteOnlyCloses() {
        var deleted = 0
        var visible by mutableStateOf(true)

        composeRule.setContent {
            DSTheme {
                DSConfirmDeleteDialog(
                    visible = visible,
                    title = "Delete client?",
                    confirmText = "Delete client",
                    onDelete = { deleted++ },
                    onDismiss = { visible = false }
                )
            }
        }

        composeRule.onNodeWithText("Cancel").performClick()

        composeRule.onNodeWithText("Delete client?").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(0, deleted) }
    }

    @Test
    fun theKeyboardActionConfirmsTheTextInputDialog() {
        var confirmed = 0
        var name by mutableStateOf("")

        composeRule.setContent {
            DSTheme {
                DSTextInputDialog(
                    title = "New tag",
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    confirmText = "Add",
                    onConfirm = { confirmed++ },
                    onDismissRequest = {},
                    confirmEnabled = name.isNotBlank(),
                    fieldTestTag = "tag-name"
                )
            }
        }

        composeRule.onNodeWithTag("tag-name").assertIsFocused()
        composeRule.onNodeWithTag("tag-name").performImeAction()
        composeRule.runOnIdle { assertEquals(0, confirmed) }

        composeRule.onNodeWithTag("tag-name").performTextInput("Beach house")
        composeRule.onNodeWithTag("tag-name").performImeAction()
        composeRule.runOnIdle { assertEquals(1, confirmed) }
    }

    /** Um toque ao lado do teclado apagaria o que foi digitado: só o voltar e o "Cancelar" fecham. */
    @Test
    fun theTextInputDialogStaysOpenOnATapOutsideUnlikeTheAlert() {
        var withField by mutableStateOf(false)
        var dismissed = 0

        composeRule.setContent {
            DSTheme {
                if (withField) {
                    DSTextInputDialog(
                        title = "New tag",
                        value = "",
                        onValueChange = {},
                        label = "Name",
                        confirmText = "Add",
                        onConfirm = {},
                        onDismissRequest = { dismissed++ }
                    )
                } else {
                    DSAlertDialog(title = "Saved", confirmText = "OK", onConfirm = {}, onDismissRequest = { dismissed++ })
                }
            }
        }

        // O controle: o mesmo toque fecha um alerta comum.
        composeRule.onNodeWithText("Saved").assertExists()
        tapOutsideTheDialog()
        composeRule.runOnIdle { assertEquals(1, dismissed) }

        withField = true
        composeRule.onNodeWithText("New tag").assertExists()
        tapOutsideTheDialog()
        composeRule.runOnIdle { assertEquals(1, dismissed) }

        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.runOnIdle { assertEquals(2, dismissed) }
    }

    private fun tapOutsideTheDialog() {
        composeRule.runOnIdle {
            val dialog = ShadowDialog.getLatestDialog()

            listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP).forEach { action ->
                val event = MotionEvent.obtain(0L, 0L, action, -100f, -100f, 0)

                dialog.dispatchTouchEvent(event)
                event.recycle()
            }
        }
    }

    @Test
    fun theDatePickerDialogHandsOverTheOpeningDayAsALocalDate() {
        var picked: LocalDate? = null

        composeRule.setContent {
            DSTheme {
                DSDatePickerDialog(date = LocalDate.of(2026, 6, 29), onConfirm = { picked = it }, onDismissRequest = {})
            }
        }

        composeRule.onNodeWithText("OK").performClick()
        composeRule.runOnIdle { assertEquals(LocalDate.of(2026, 6, 29), picked) }
    }

    @Test
    fun theDatePickerDialogWaitsForADayWhenItOpensWithoutOne() {
        composeRule.setContent {
            DSTheme { DSDatePickerDialog(date = null, onConfirm = {}, onDismissRequest = {}) }
        }

        composeRule.onNodeWithText("OK").assertIsNotEnabled()
    }

    /** `LocalDate.MIN..hoje` fazia o calendário montar um mês por ano desde o ano -999999999 e fechava o app. */
    @Test
    fun theCalendarYearsStayInsideWhatMaterialCanBuild() {
        val today = LocalDate.of(2026, 11, 9)

        assertEquals(1900..2026, DSDatePickerBounds.yearRange(LocalDate.MIN..today))
        assertEquals(2020..2100, DSDatePickerBounds.yearRange(LocalDate.of(2020, 1, 1)..LocalDate.MAX))
        assertEquals(1900..2100, DSDatePickerBounds.yearRange(null))
        assertEquals(LocalDate.of(2020, 12, 31), DSDatePickerBounds.today(LocalDate.MIN..LocalDate.of(2020, 12, 31), today))
        assertTrue(DSDatePickerBounds.isSelectableYear(2026, LocalDate.MIN..today))
        assertEquals(today, DSDatePickerBounds.selectable(today, LocalDate.MIN..today))
        assertEquals(null, DSDatePickerBounds.selectable(today.plusDays(1), LocalDate.MIN..today))
        assertEquals(null, DSDatePickerBounds.selectable(today.plusYears(1), LocalDate.MIN..today))
        assertEquals(null, DSDatePickerBounds.selectable(LocalDate.of(1850, 1, 1), null))
        assertFalse(DSDatePickerBounds.isSelectableYear(2027, LocalDate.MIN..today))
        assertFalse(DSDatePickerBounds.isSelectable(utcMillis(2026, 11, 10), LocalDate.MIN..today))
        assertTrue(DSDatePickerBounds.isSelectable(utcMillis(2026, 11, 9), LocalDate.MIN..today))
    }

    private fun utcMillis(year: Int, month: Int, day: Int): Long =
        LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    @Test
    fun theStepperAddsOneStepPerTapAndStopsAtTheLimits() {
        var count by mutableStateOf(11)

        composeRule.setContent {
            DSTheme { DSStepper(value = count, onValueChange = { count = it }, range = 1..12, label = "Installments") }
        }

        composeRule.onNodeWithContentDescription("Increase Installments").performClick()
        composeRule.runOnIdle { assertEquals(12, count) }
        composeRule.onNodeWithContentDescription("Increase Installments").assertIsNotEnabled()

        composeRule.onNodeWithContentDescription("Decrease Installments").performClick()
        composeRule.runOnIdle { assertEquals(11, count) }
    }

    @Test
    fun holdingAStepperButtonRepeatsTheStepAndReleasingDoesNotAddOneMore() {
        var count by mutableStateOf(1)

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            DSTheme { DSStepper(value = count, onValueChange = { count = it }, range = 1..100, label = "Days") }
        }
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.onNodeWithContentDescription("Increase Days").performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(DSStepperMath.HoldDelayMillis + 400)

        val held = composeRule.runOnIdle { count }

        composeRule.onNodeWithContentDescription("Increase Days").performTouchInput { up() }
        composeRule.mainClock.advanceTimeBy(500)

        assertTrue("held for 750 ms, got $held", held >= 3)
        composeRule.runOnIdle { assertEquals(held, count) }
    }

    @Test
    fun theStepperMathHasNoFloatingPointNoiseAndTheRepeatAccelerates() {
        assertEquals(0.3, DSStepperMath.step(0.1, 0.2, 0.0..1.0), 0.0)
        assertEquals(1.0, DSStepperMath.step(0.95, 0.1, 0.0..1.0), 0.0)
        assertEquals(0.0, DSStepperMath.step(0.05, -0.1, 0.0..1.0), 0.0)
        assertEquals(150L, DSStepperMath.repeatInterval(0))
        assertTrue(DSStepperMath.repeatInterval(3) < DSStepperMath.repeatInterval(1))
        assertEquals(40L, DSStepperMath.repeatInterval(100))
    }

    /** O ponto descartado (0.5 lido como 50) é o erro que um campo de dose não pode ter, em nenhum idioma. */
    @Test
    fun theDecimalFieldReadsCommaAndDotAsTheDecimalSeparator() {
        var dose by mutableStateOf<Double?>(null)

        composeRule.setContent {
            DSTheme { DSDecimalField(value = dose, onValueChange = { dose = it }, label = "Dose") }
        }

        composeRule.onNodeWithText("Dose").performTextInput("0.5")
        composeRule.runOnIdle { assertEquals(0.5, dose) }

        composeRule.onNodeWithText("Dose").performTextClearance()
        composeRule.runOnIdle { assertEquals(null, dose) }

        composeRule.onNodeWithText("Dose").performTextInput("2,25")
        composeRule.runOnIdle { assertEquals(2.25, dose) }

        // Além das casas aceitas, o dígito não entra.
        composeRule.onNodeWithText("Dose").performTextReplacement("2,257")
        composeRule.runOnIdle { assertEquals(2.25, dose) }
    }

    @Test
    fun theDecimalFieldFlagsWhatIsNotANumberAndFollowsAValueSetFromOutside() {
        var weight by mutableStateOf<Double?>(80.0)

        composeRule.setContent {
            DSTheme { DSDecimalField(value = weight, onValueChange = { weight = it }, label = "Weight", maxFractionDigits = 1) }
        }

        composeRule.onNodeWithText("80").assertExists()

        composeRule.onNodeWithText("Weight").performTextReplacement("1.2.")
        composeRule.onNodeWithText("Enter a valid number").assertExists()
        composeRule.runOnIdle { assertEquals(null, weight) }

        weight = 79.5
        composeRule.onNodeWithText("79.5").assertExists()
        composeRule.onNodeWithText("Enter a valid number").assertDoesNotExist()
    }

    @Test
    fun aDecimalIsShownWithTheLocaleSeparatorInPlainDigitsAndWithoutGrouping() {
        assertEquals("1234,5", DSDecimalInput.format(1234.5, 2, Locale.forLanguageTag("pt-BR")))
        assertEquals("1234.5", DSDecimalInput.format(1234.5, 2, Locale.US))
        assertEquals("1234.5", DSDecimalInput.format(1234.5, 2, Locale.forLanguageTag("ar-EG")))
        assertEquals("80", DSDecimalInput.format(80.0, 1, Locale.US))
        assertEquals(0.5, DSDecimalInput.parse(".5"))
        assertEquals(1.0, DSDecimalInput.parse("1,"))
        assertEquals(null, DSDecimalInput.parse("1,2,3"))
        assertEquals(null, DSDecimalInput.parse("abc"))
    }

    /** A alça do Material é lida "Fechar planilha" em português; a do DSKit diz "Fechar" e fecha com o leitor. */
    @Test
    fun theSheetHandleIsReadCloseAndClosesWithTheScreenReader() {
        var dismissed = 0

        composeRule.setContent {
            DSTheme {
                DSBottomSheet(visible = true, onDismissRequest = { dismissed++ }, title = "Options") {
                    DSSheetAction("Edit", onClick = {})
                }
            }
        }

        composeRule.onAllNodesWithContentDescription("Drag handle").assertCountEquals(0)
        composeRule.onNodeWithContentDescription("Close").performSemanticsAction(SemanticsActions.OnClick)
        composeRule.runOnIdle { assertEquals(1, dismissed) }
    }

    @Test
    fun theSystemBackOnASheetGoesToTheCaller() {
        var dismissed = 0

        composeRule.setContent {
            DSTheme {
                DSBottomSheet(visible = true, onDismissRequest = { dismissed++ }, onDismissBlocked = {}) {
                    DSSheetAction("Edit", onClick = {})
                }
            }
        }

        composeRule.onNodeWithText("Edit").assertExists()
        composeRule.runOnIdle { ShadowDialog.getLatestDialog().onBackPressed() }
        composeRule.runOnIdle { assertEquals(1, dismissed) }
    }

    @Test
    fun aSheetHeldByItsStateComesDownOnHideAndReportsIt() {
        val state = DSBottomSheetState()
        var hidden = false

        composeRule.setContent {
            DSTheme {
                val scope = rememberCoroutineScope()

                DSTextButton(
                    "Leave",
                    onClick = {
                        scope.launch {
                            state.hide()
                            hidden = true
                        }
                    }
                )

                DSBottomSheet(state = state, onDismissRequest = {}) { DSSheetAction("Edit", onClick = {}) }
            }
        }

        composeRule.onNodeWithText("Edit").assertExists()
        composeRule.runOnIdle { assertTrue(state.isVisible) }

        composeRule.onNodeWithText("Leave").performSemanticsAction(SemanticsActions.OnClick)
        composeRule.waitUntil(2_000) { hidden }
        composeRule.runOnIdle { assertFalse(state.isVisible) }
    }
}
