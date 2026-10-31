package io.github.romulomga.dskit

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** O que os componentes fazem quando tocados ou configurados, sem imagem. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun passwordFieldTellsAutofillItIsAPasswordEvenWithCustomKeyboardOptions() {
        composeRule.setContent {
            DSTheme {
                DSTextField(
                    value = "",
                    onValueChange = {},
                    label = "Password",
                    password = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }

        composeRule.onNodeWithText("Password")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.Password))
    }

    @Test
    fun textLinkWithABlockedSchemeIsPlainText() {
        composeRule.setContent {
            DSTheme {
                DSTextLink(
                    text = "Open the app settings or the Help center.",
                    links = listOf(DSLink.Url("app settings", "intent:#Intent;end"), DSLink.Url("Help center", "https://example.com/help"))
                )
            }
        }

        val urls = linkAnnotations().map { (it.item as LinkAnnotation.Url).url }

        assertEquals(listOf("https://example.com/help"), urls)
    }

    @Test
    fun textLinkReportsAFailureInsteadOfCrashing() {
        var failed: DSLink.Url? = null
        val link = DSLink.Url("Help center", "https://example.com/help")

        composeRule.setContent {
            CompositionLocalProvider(LocalUriHandler provides ThrowingUriHandler) {
                DSTheme {
                    DSTextLink(text = "Open the Help center.", links = listOf(link), onOpenFailure = { failed = it })
                }
            }
        }

        val annotation = linkAnnotations().single().item as LinkAnnotation.Url
        composeRule.runOnIdle { annotation.linkInteractionListener!!.onClick(annotation) }

        assertEquals(link, failed)
    }

    @Test
    fun loadingButtonIgnoresTaps() {
        var clicks = 0

        composeRule.setContent {
            DSTheme {
                DSTonalButton("Save", onClick = { clicks++ }, loading = true)
            }
        }

        composeRule.onNodeWithText("Save", useUnmergedTree = true).performClick()

        composeRule.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun enabledButtonClicks() {
        var clicks = 0

        composeRule.setContent {
            DSTheme {
                DSButton("Save", onClick = { clicks++ })
            }
        }

        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle { assertTrue(clicks == 1) }
    }

    @Test
    fun bottomSheetHoldsALazyListWhenItDoesNotScrollItself() {
        composeRule.setContent {
            DSTheme {
                DSBottomSheet(visible = true, onDismissRequest = {}, title = "Country", scrollable = false) {
                    LazyColumn {
                        items(200) { index -> DSListItem("Country $index") }
                    }
                }
            }
        }

        composeRule.onNodeWithText("Country 0").assertExists()
        composeRule.onNodeWithText("Country 199").assertDoesNotExist()
    }

    private fun linkAnnotations(): List<AnnotatedString.Range<LinkAnnotation>> {
        val text = composeRule.onNodeWithText("", substring = true).fetchSemanticsNode()
            .config.getOrNull(SemanticsProperties.Text)!!.single()

        return text.getLinkAnnotations(0, text.length)
    }

    private object ThrowingUriHandler : UriHandler {
        override fun openUri(uri: String): Unit = throw IllegalStateException("No activity for $uri")
    }

    @Test
    fun aSkeletonSwallowsTheTapOfTheElementUnderIt() {
        var taps = 0
        // O pulso do esqueleto anima para sempre: com o relógio andando sozinho a tela nunca fica ociosa.
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            DSTheme {
                DSButton("Publish", onClick = { taps++ }, modifier = Modifier.dsSkeleton(true))
            }
        }
        composeRule.mainClock.advanceTimeBy(FrameMillis)

        composeRule.onRoot().performTouchInput { click(center) }
        composeRule.mainClock.advanceTimeBy(FrameMillis * 10)

        assertEquals(0, taps)
    }

    @Test
    fun anInputChipIsARemoveButtonNotACheckbox() {
        var removed = false
        composeRule.setContent {
            DSTheme {
                DSInputChip("Ipanema", onRemove = { removed = true })
            }
        }

        composeRule.onNodeWithContentDescription("Remove Ipanema")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ToggleableState))
            .performClick()

        assertTrue(removed)
    }

    @Test
    fun aVisualTransformationMasksTheTextButTheValueKeepsOnlyWhatWasTyped() {
        var value by mutableStateOf("")
        composeRule.setContent {
            DSTheme {
                DSTextField(value = value, onValueChange = { value = it }, label = "Code", visualTransformation = GroupsOfTwo)
            }
        }

        composeRule.onNodeWithText("Code").performTextInput("1234")

        assertEquals("1234", value)
        composeRule.onNodeWithText("Code").assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("12 34")))
    }

    @Test
    fun aCollapsedExtendedFabKeepsItsName() {
        composeRule.setContent {
            DSTheme {
                DSExtendedFab("New post", DSIcons.Add, onClick = {}, expanded = false)
            }
        }

        composeRule.onNodeWithContentDescription("New post").assertExists()
    }

    @Test
    fun theRangeSliderStopsAtTheMinimumDistanceInsteadOfIgnoringTheDrag() {
        // Arrastar a alça de baixo de 1 para 3,6 com distância 1: ela encosta em 3 (4 − 1), não fica parada em 1.
        assertEquals(3f..4f, keepDistance(3.6f..4f, previous = 1f..4f, minimum = 1f))
        // Arrastar a de cima de 4 para 1,2: encosta em 2 (1 + 1).
        assertEquals(1f..2f, keepDistance(1f..1.2f, previous = 1f..4f, minimum = 1f))
        // Erro de ponto flutuante dos passos não trava uma posição válida.
        assertEquals(1f..1.9999999f, keepDistance(1f..1.9999999f, previous = 1f..3f, minimum = 1f))
    }
}

private const val FrameMillis = 16L

/** "1234" aparece como "12 34": um espaço depois de cada dois caracteres, menos no fim. */
private object GroupsOfTwo : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val masked = text.text.chunked(2).joinToString(" ")

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset + (offset - 1).coerceAtLeast(0) / 2

            override fun transformedToOriginal(offset: Int): Int = offset - offset / 3
        }

        return TransformedText(AnnotatedString(masked), mapping)
    }
}
