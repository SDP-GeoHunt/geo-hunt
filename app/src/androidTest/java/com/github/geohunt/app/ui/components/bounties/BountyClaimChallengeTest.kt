package com.github.geohunt.app.ui.components.bounties

import android.Manifest.permission
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BountyClaimChallengeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @get:Rule
    val grantLocationPermission : GrantPermissionRule = GrantPermissionRule.grant(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        Intents.init()
        AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun testClaimChallengeLaunchMatchesIntent() {
        composeTestRule.setContent {
            BountyClaimChallenge(
                bid = "063f921c-ML2eCQ52mAQlvCEQZ2n",
                cid = "163f921c-ML2eCQ52mAQlvCEQZ2n"
            )
        }

        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClaimingBountyChallenge() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = createTestBitmap(context)
        val awaitingAnimation = CompletableDeferred<Unit>()

        composeTestRule.setContent {
            BountyChallengeSubmitClaimForm(bitmap = bitmap, state = BountyClaimViewModel.State.READY_TO_CLAIM) {
                awaitingAnimation.complete(Unit)
            }
        }

        composeTestRule.onNodeWithText("Submit Claim")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        assertThat(awaitingAnimation.isCompleted, equalTo(true))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClaimingBountyChallengeWithAwaitingLocation() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = createTestBitmap(context)

        composeTestRule.setContent {
            BountyChallengeSubmitClaimForm(bitmap = bitmap, state = BountyClaimViewModel.State.AWAITING_LOCATION) {
            }
        }

        composeTestRule
            .onNodeWithTag("CircularProgressIndicator")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("AwaitingLocationText")
            .assertIsDisplayed()
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}