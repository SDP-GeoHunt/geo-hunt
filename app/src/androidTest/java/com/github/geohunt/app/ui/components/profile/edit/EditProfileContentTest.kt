package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.model.EditedUser
import org.junit.Rule
import org.junit.Test

class EditProfileContentTest {
    @get:Rule
    val t = createComposeRule()

    private val mockUser = MockAuthRepository.defaultLoggedUser

    @Test
    fun showsSavingIfSaving() {
        t.setContent {
            EditProfileContent(
                user = mockUser,
                eu = EditedUser.fromUser(mockUser),
                onDisplayNameChange = { },
                onProfilePictureChange = { },
                isSaving = true
            ) { }
        }
        t.onNodeWithTag("wait-btn").assertIsDisplayed()
        t.onNodeWithTag("save-btn").assertDoesNotExist()
    }

    @Test
    fun doesNotShowSavingIfSaving() {
        t.setContent {
            EditProfileContent(
                user = mockUser,
                eu = EditedUser.fromUser(mockUser),
                onDisplayNameChange = { },
                onProfilePictureChange = { },
                isSaving = false
            ) { }
        }
        t.onNodeWithTag("save-btn").assertIsDisplayed()
        t.onNodeWithTag("wait-btn").assertDoesNotExist()
    }
}