package io.github.romulomga.dskit

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.CoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BottomSheetSceneStrategyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val backStack = mutableStateListOf<Any>(Home)
    private lateinit var scope: CoroutineScope

    @Test
    fun anEntryMarkedAsBottomSheetOpensOverTheScreenBelow() {
        show()

        composeRule.onNodeWithText("Open filters").performClick()

        composeRule.onNodeWithText("Filters content").assertExists()
        composeRule.onNodeWithText("Open filters").assertExists()
        composeRule.onNodeWithContentDescription("Close").assertExists()
    }

    @Test
    fun closingTheSheetGoesBackInTheStack() {
        show()
        composeRule.onNodeWithText("Open filters").performClick()

        composeRule.onNodeWithContentDescription("Close").performSemanticsAction(SemanticsActions.OnClick)

        composeRule.onNodeWithText("Filters content").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(listOf<Any>(Home), backStack.toList()) }
    }

    @Test
    fun hideThenLowersTheSheetBeforeTheEntryLeaves() {
        var left = false

        show()
        composeRule.onNodeWithText("Open filters").performClick()
        composeRule.onNodeWithText("Filters content").assertExists()

        composeRule.runOnIdle {
            DSNavSheets.hideThen(scope, Filters) {
                backStack.remove(Filters)
                left = true
            }

            // Ainda descendo: a entrada continua na pilha.
            assertFalse(left)
        }

        composeRule.waitUntil(2_000) { left }
        composeRule.onNodeWithText("Filters content").assertDoesNotExist()
    }

    @Test
    fun hideThenRunsRightAwayWithoutAnOpenSheet() {
        var ran = false

        show()
        composeRule.runOnIdle { DSNavSheets.hideThen(scope, Filters) { ran = true } }

        assertTrue(ran)
    }

    @Test
    fun theMetadataMarksOnlySheetEntries() {
        assertTrue(DSBottomSheetSceneStrategy.isBottomSheet(DSBottomSheetSceneStrategy.bottomSheet(scrollable = false)))
        assertFalse(DSBottomSheetSceneStrategy.isBottomSheet(emptyMap()))
    }

    private fun show() {
        composeRule.setContent {
            scope = rememberCoroutineScope()

            DSTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    sceneStrategies = listOf(DSBottomSheetSceneStrategy()),
                    entryProvider = entryProvider {
                        entry<Home> { DSTextButton("Open filters", onClick = { backStack.add(Filters) }) }

                        entry<Filters>(metadata = DSBottomSheetSceneStrategy.bottomSheet()) { Text("Filters content") }
                    }
                )
            }
        }
    }

    private data object Home

    private data object Filters
}
