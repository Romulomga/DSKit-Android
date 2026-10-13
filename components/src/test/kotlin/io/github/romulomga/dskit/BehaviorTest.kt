package io.github.romulomga.dskit

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.input.ImeAction
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
}
