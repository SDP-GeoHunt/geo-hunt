package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class DisplayNameChangerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun textInputHasCorrectInitialValue() {
        val v = "caca"

        composeTestRule.setContent {
            DisplayNameChanger(value = v, onChange = { })
        }

        composeTestRule.onNodeWithTag("display-name-input").assert(hasText("caca"))
    }

    @Test
    fun modifyingTextInputUpdatesEditedUser() {
        var v = "caca"

        composeTestRule.setContent {
            DisplayNameChanger(value = v, onChange = { v = it; Unit })
        }

        composeTestRule.onNodeWithTag("display-name-input").performTextInput("prout")
        assert(v.contains("prout"))
    }
}