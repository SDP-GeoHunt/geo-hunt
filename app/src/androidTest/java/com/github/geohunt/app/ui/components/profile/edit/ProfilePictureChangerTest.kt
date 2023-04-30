package com.github.geohunt.app.ui.components.profile.edit

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class ProfilePictureChangerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsDefaultProfilePictureIfNoProfilePicture() {
        composeTestRule.setContent {
            ProfilePictureChanger(currentImage = null, onImageSelected = { }, { profilePictureProvider(it) })
        }
        composeTestRule.onNodeWithTag("default-pp").assertIsDisplayed()
    }

    @Test
    fun showsUsersProfilePictureIfProvided() {
        composeTestRule.setContent {
            ProfilePictureChanger(currentImage = "http://picsum.photos/200", onImageSelected = { }, { profilePictureProvider(it) })
        }
        composeTestRule.onNodeWithTag("user-pp").assertIsDisplayed()
    }

    @Test
    fun clickingOnEditButtonTriggersPicker() {
        val cf = CompletableFuture<Void?>()
        composeTestRule.setContent {
            ProfilePictureChanger(null, {  }) {
                {
                    cf.complete(null)
                    it(Uri.EMPTY)
                }
            }
        }
        composeTestRule.onNodeWithTag("edit-pick-image").performClick()

        cf.get(2, TimeUnit.SECONDS)
    }
}