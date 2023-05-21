package com.github.geohunt.app.ui.components.bounties

import android.Manifest.permission
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
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
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.challengecreation.CreateNewChallenge
import com.github.geohunt.app.ui.components.utils.intents.IntentsMocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class BountyClaimChallengeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @get:Rule
    val grantLocationPermission : GrantPermissionRule = GrantPermissionRule.grant(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

    private val mockedLocation = Location(13.412471480006737, 103.86698070815994)

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
        val deferred = CompletableDeferred<Claim>()
        val returnFromCoroutine = CompletableDeferred<Unit>()

        val mockedLocationFlow = MutableSharedFlow<Location>()

        LocationRepository.DefaultLocationFlow.mocked(mockedLocationFlow).use {
            IntentsMocking.mock(
                contract = ActivityResultContracts.TakePicture(),
                callback = { uri, onResult ->
                    val oStream = context.contentResolver.openOutputStream(uri)
                    val bitmap = createTestBitmap(context)
                    runBlocking {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream)
                    }
                    returnFromCoroutine.complete(Unit)
                    onResult(true)
                }
            ).run {
                // Start the application
                composeTestRule.setContent {
                    BountyClaimChallenge(
                        bid = "98d755ad-NVP5y7V0SyObpqi226o",
                        cid = "testChalllengeId",
                        onFailure = deferred::completeExceptionally,
                        onSuccess = deferred::complete
                    )
                }

                // Emit location
                mockedLocationFlow.emit(mockedLocation)

                // Emit a location update
                returnFromCoroutine.await()
                composeTestRule.awaitIdle()

                // Test button is enabled
                composeTestRule.onNodeWithText("Submit Claim")
                    .performScrollTo()
                    .assertIsDisplayed()
            }
        }
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}