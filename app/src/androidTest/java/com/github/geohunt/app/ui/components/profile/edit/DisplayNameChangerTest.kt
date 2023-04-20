package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.database.api.EditedUser
import org.junit.Rule
import org.junit.Test

class DisplayNameChangerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun textInputHasCorrectInitialValue() {
        val user = MockUser(displayName = "caca")
        val editedUser = EditedUser("caca")
        var mutableEditedUser: MutableState<EditedUser>? = null

        composeTestRule.setContent {
            mutableEditedUser = remember { mutableStateOf(editedUser) }
            DisplayNameChanger(editedUser = mutableEditedUser!!)
        }

        assert(mutableEditedUser != null)
        composeTestRule.onNodeWithTag("display-name-input").assert(hasText("caca"))
    }

    @Test
    fun modifyingTextInputUpdatesEditedUser() {
        val user = MockUser(displayName = "caca")
        val editedUser = EditedUser("caca")
        var mutableEditedUser: MutableState<EditedUser>? = null

        composeTestRule.setContent {
            mutableEditedUser = remember { mutableStateOf(editedUser) }
            DisplayNameChanger(editedUser = mutableEditedUser!!)
        }

        assert(mutableEditedUser != null)
        composeTestRule.onNodeWithTag("display-name-input").performTextInput("prout")
        assert(mutableEditedUser!!.value.displayName.contains("prout"))
    }
}