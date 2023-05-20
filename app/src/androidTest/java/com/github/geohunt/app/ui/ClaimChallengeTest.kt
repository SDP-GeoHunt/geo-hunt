package com.github.geohunt.app.ui
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
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.claims.ClaimChallenge
import com.github.geohunt.app.ui.components.claims.SubmitClaimForm
import com.github.geohunt.app.ui.components.claims.SubmitClaimViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClaimChallengeTest {
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
    fun testClaimChallengeLaunchIntent() {
        composeTestRule.setContent {
            ClaimChallenge(cid = "163f921c-ML2eCQ52mAQlvCEQZ2n")
        }

        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @Test
    fun testClaimChallenge() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = createTestBitmap(context)
        val awaitingThingy = CompletableDeferred<Unit>()

        // Start the application
        composeTestRule.setContent {
            SubmitClaimForm(bitmap = bitmap, state = SubmitClaimViewModel.State.READY_TO_CLAIM) {
                awaitingThingy.complete(Unit)
            }
        }

        composeTestRule.onNodeWithText("Submit Claim")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        // Awaiting thingy
        assertThat(awaitingThingy.isCompleted, equalTo(true))
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}