package com.github.geohunt.app.ui.components.profile.edit

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.database.api.EditedUser
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class ProfilePictureChangerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fetchesProfilePicture() {
        val cf = CompletableFuture<Void?>()
        val mockedLazyRefPicture = MockLazyRef("caca") {
            cf.complete(null)
            Tasks.forResult(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
        }

        val user = MockUser(profilePicture = mockedLazyRefPicture)
        composeTestRule.setContent {
            val editedUser = remember { mutableStateOf(EditedUser.fromUser(user)) }
            ProfilePictureChanger(user, editedUser) {
                { it(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)) }
            }
        }

        cf.get(2, TimeUnit.SECONDS)
    }

    @Test
    fun clickingOnEditButtonTriggersPicker() {
        val cf = CompletableFuture<Void?>()

        val user = MockUser()
        composeTestRule.setContent {
            val editedUser = remember { mutableStateOf(EditedUser.fromUser(user)) }
            ProfilePictureChanger(user, editedUser) {
                {
                    cf.complete(null)
                    it(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
                }
            }
        }
        composeTestRule.onNodeWithTag("edit-pick-image").performClick()

        cf.get(2, TimeUnit.SECONDS)
    }
}