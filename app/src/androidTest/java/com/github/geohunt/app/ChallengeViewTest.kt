package com.github.geohunt.app

import android.Manifest.permission
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.ui.components.CreateNewChallenge
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

        // Start the application
        composeTestRule.setContent {
            CreateNewChallenge()
        }

        // Test that the button Continue is disabled
                composeTestRule.onNode(hasText("Create Challenge")
                        .and(hasClickAction()))
                .assertIsDisplayed()
                .assertIsNotEnabled()

        // Check the community guidelines button
        composeTestRule.onNodeWithText("Community Guidelines", substring = true)
                .assertIsDisplayed()
//                .performTouchInput { click(Offset(0.9f, 0.5f)) }
//
//        val openGuidelinesExpectedIntent = allOf(
//                hasAction(Intent.ACTION_VIEW),
//                hasData(context.resources.getString(R.string.community_guidelines_url)))
//        Intents.intended(openGuidelinesExpectedIntent)

        // Check photo intent is created upon click on the image button
        composeTestRule.onNodeWithContentDescription("Take a photo!")
                .assertIsDisplayed()
                .performClick()

        val takePictureIntent = allOf(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
        Intents.intending(takePictureIntent)
                .respondWith(createImageCaptureActivityResultStub(context))

    }

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