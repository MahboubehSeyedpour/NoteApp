package com.app.noteapp.presentation.home

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.noteapp.R
import com.app.noteapp.presentation.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fabClick_emitsAddNoteEvent() {
        composeRule.onNodeWithTag("fab-add-note").assertExists()

        composeRule.onNodeWithTag("fab-add-note").performClick()

        composeRule.onNodeWithTag("fab-add-note").assertHasClickAction()
    }

    @Test
    fun typingInSearch_updatesQueryField() {
        val text = "meeting"
        composeRule.onNodeWithTag("search-field").performTextInput(text)
        composeRule.onNodeWithTag("search-field").assertTextContains(text)
    }

    @Test
    fun longPressNote_entersSelectionMode() {
        val noteTag = "note-1"
        composeRule.onNodeWithTag(noteTag).assertExists()

        composeRule.onNodeWithTag(noteTag).performTouchInput { longClick() }

        composeRule.onNodeWithText("selected").assertExists()
    }

    @Test
    fun deleteNote_showsDeleteDialog() {
        composeRule.onNodeWithTag("note-1").performTouchInput { longClick() }

        composeRule.onNodeWithContentDescription("Delete").performClick()

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.delete_note_question)
        ).assertExists()
    }
}
