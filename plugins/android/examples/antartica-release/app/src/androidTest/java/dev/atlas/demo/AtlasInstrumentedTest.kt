package dev.atlas.demo

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AtlasInstrumentedTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun searchEmptyAndClearRecoverResults() {
        compose.onNodeWithText("Search lessons").performTextInput("frame")
        compose.onNodeWithText("1 lessons").assertExists()
        compose.onNodeWithText("From frame to display").assertExists()
        compose.onNodeWithText("Search lessons").performTextReplacement("zz-no-match-913")
        compose.onNodeWithText("0 lessons").assertExists()
        compose.onNodeWithText("No matching lesson in this small demo. Try “frame”, “boot”, or “ART”.").assertExists()
        compose.onNodeWithText("Clear").performClick()
        compose.onNodeWithText("5 lessons").assertExists()
        compose.onNodeWithText("Search lessons").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(""))
        )
    }

    @Test fun expansionExposesStateAndSourceAction() {
        val card = compose.onNode(hasText("From frame to display") and hasClickAction())
        card.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        card.performClick()
        card.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
        compose.onNodeWithText("Open source in browser").assertExists().assertHasClickAction()
        card.performClick()
        card.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        compose.onNodeWithText("Open source in browser").assertDoesNotExist()
    }

    private fun scrollValue(): Float = compose.onNodeWithTag("atlas-list")
        .fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value()

    @Test fun destinationScrollPositionsStayIndependent() {
        compose.onNodeWithTag("atlas-list").performScrollToNode(hasText("Binder between processes"))
        compose.waitForIdle()
        val learnPosition = scrollValue()
        assertTrue("Learn should be scrolled before switching", learnPosition > 0f)
        compose.onNodeWithText("Evidence", useUnmergedTree = true).performClick()
        compose.waitForIdle()
        assertEquals("Evidence starts independently at top", 0f, scrollValue(), 0.01f)
        compose.onNodeWithTag("atlas-list").performScrollToNode(hasText("Still unknown"))
        compose.onNodeWithText("Learn", useUnmergedTree = true).performClick()
        compose.waitForIdle()
        assertEquals("Learn restores its own position", learnPosition, scrollValue(), 0.01f)
    }

    @Test fun activityRecreationRestoresQueryAndExpansion() {
        compose.onNodeWithText("Search lessons").performTextInput("frame")
        compose.onNode(hasText("From frame to display") and hasClickAction()).performClick()
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        compose.onNodeWithText("Search lessons").assertTextContains("frame")
        compose.onNodeWithText("1 lessons").assertExists()
        compose.onNode(hasText("From frame to display") and hasClickAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
        compose.onNodeWithText("Open source in browser").assertExists()
    }
}
