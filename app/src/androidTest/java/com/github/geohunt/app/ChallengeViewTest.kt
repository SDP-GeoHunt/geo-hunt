package com.github.geohunt.app

import android.Manifest.permission
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.CreateNewChallenge
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import dalvik.annotation.TestTarget
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @Before
    fun beforeTest() {
        Intents.init()
    }

    @After
    fun afterTest() {
        Intents.release()
    }

    @Test
    fun testChallengeStateWithPermissions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<Challenge>()
        val createChallengeCalled = CompletableFuture<Challenge>()

        // Start the application
        composeTestRule.setContent {
            CreateNewChallenge(database = object: BaseMockDatabase() {
                override fun createChallenge(
                    thumbnail: Bitmap,
                    location: Location,
                    expirationDate: LocalDateTime?
                ): Task<Challenge> {
                    val mockChallenge = MockChallenge(
                        "cid",
                        MockLazyRef<User>("uid") {
                            Tasks.forException<User>(IllegalArgumentException())
                        },
                        LocalDateTime.MAX,
                        expirationDate,
                        MockLazyRef<Bitmap>("iid") {
                            Tasks.forException<Bitmap>(IllegalArgumentException())
                        },
                        location,
                        listOf()
                    )
                    createChallengeCalled.complete(mockChallenge)
                    return Tasks.forResult(mockChallenge)
                }
            }, onChallengeCreated = future::complete, onFailure = future::completeExceptionally)
        }

        // Check that the camera is launched
        Intents.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(createImageCaptureActivityResultStub(context))

//        composeTestRule.onNodeWithContentDescription("Take picture")
//            .assertIsDisplayed()
//            .performClick()

        // Wait until
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Create challenge")
                .fetchSemanticsNodes().size == 1
        }

        // Check that the button create challenge is displayed
        composeTestRule.onNodeWithText("Create challenge")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        // Check that challengeCreate was called
        val mockChallenge = createChallengeCalled.orTimeout(20, TimeUnit.SECONDS).join()

        // Check that future was completed
        assertThat(mockChallenge,
            equalTo(future.orTimeout(20, TimeUnit.SECONDS).join()))
    }

//    @Test
//    fun testChallengeStateWithPermissions() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//
//        // Start the application
//        composeTestRule.setContent {
//            CreateNewChallenge()
//        }
//
//        // Test that the button Continue is disabled
//                composeTestRule.onNode(hasText("Create Challenge")
//                        .and(hasClickAction()))
//                .assertIsDisplayed()
//                .assertIsNotEnabled()
//
//        // Check the community guidelines button
//        composeTestRule.onNodeWithText("Community Guidelines", substring = true)
//                .assertIsDisplayed()
////                .performTouchInput { click(Offset(0.9f, 0.5f)) }
////
////        val openGuidelinesExpectedIntent = allOf(
////                hasAction(Intent.ACTION_VIEW),
////                hasData(context.resources.getString(R.string.community_guidelines_url)))
////        Intents.intended(openGuidelinesExpectedIntent)
//
//        // Check photo intent is created upon click on the image button
//        composeTestRule.onNodeWithContentDescription("Take a photo!")
//                .assertIsDisplayed()
//                .performClick()
//
//        val takePictureIntent = allOf(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
//        Intents.intending(takePictureIntent)
//                .respondWith(createImageCaptureActivityResultStub(context))
//
//    }

    private fun createImageCaptureActivityResultStub(context: Context): Instrumentation.ActivityResult {
        val bundle = Bundle()
        bundle.putParcelable("IMG_DATA", BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        // Create the Intent that will include the bundle.
        val resultData = Intent()
        resultData.putExtras(bundle)
        // Create the ActivityResult with the Intent.
        return Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
    }
}