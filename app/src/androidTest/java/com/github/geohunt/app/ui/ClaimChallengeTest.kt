package com.github.geohunt.app.ui
import android.Manifest.permission
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockClaim
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.sensor.LocationRequestState
import com.github.geohunt.app.ui.components.ClaimChallenge
import com.github.geohunt.app.ui.components.SubmitClaimForm
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class ClaimChallengeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @get:Rule
    val grantLocationPermission : GrantPermissionRule = GrantPermissionRule.grant(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

    private val mockedLocation = Location(13.412471480006737, 103.86698070815994)

    private val mockChallenge = MockChallenge(
        cid = "cid",
        author = MockLazyRef<User>("uid") { TODO() },
        publishedDate = LocalDateTime.now(),
        expirationDate = LocalDateTime.of(2040, 12, 1, 12, 12, 12),
        thumbnail = MockLazyRef("iid") { TODO() },
        correctLocation = mockedLocation,
        claims = listOf()
    )

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun testClaimOnSuccess() {
        testChallenge(locationRequestFailed = false, submitClaimFailed = false)
    }

    @Test
    fun testClaimOnLocationFailure() {
        testChallenge(locationRequestFailed = true, submitClaimFailed = false)
    }

    @Test
    fun testClaimOnSubmitClaimFailure() {
        testChallenge(locationRequestFailed = false, submitClaimFailed = true)
    }

    @Test
    fun testClaimChallengeLaunchIntent() {
        composeTestRule.setContent {
            ClaimChallenge(
                database = object : BaseMockDatabase() {},
                challenge = mockChallenge,
                onClaimSubmitted = {},
                onFailure = {}
            )
        }

        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private fun testChallenge(
        locationRequestFailed : Boolean = false,
        submitClaimFailed : Boolean = false
    ) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<Claim>()
        val resultingPhoto = createTestBitmap(context)
        val futureLocation = CompletableFuture<Location>()
        val futureClaim = CompletableFuture<Claim>()
        val taskClaimCompletionSource = TaskCompletionSource<Claim>()
        var counter = 0

        val locationRequestStateFactory = @Composable {
            object : LocationRequestState {
                override val lastLocation: MutableState<Location?> = remember {
                    mutableStateOf(null)
                }

                override fun requestLocation(): CompletableFuture<Location> {
                    counter++
                    futureLocation.thenAccept {
                        lastLocation.value = it
                    }
                    return futureLocation
                }
            }
        }

        val mockDatabase = object: BaseMockDatabase() {
            override fun submitClaim(
                thumbnail: Bitmap,
                challenge: Challenge,
                location: Location
            ): Task<Claim> {
                val claim = MockClaim(
                    id = "id",
                    user = MockLazyRef<User>("uid") {  TODO() },
                    challenge = MockLazyRef<Challenge>("cid") { TODO() },
                    location = location,
                    time = LocalDateTime.now()
                )
                futureClaim.complete(claim)
                return taskClaimCompletionSource.task
            }
        }

        LocationRequestState.defaultFactory.mocked(locationRequestStateFactory).use {
            // Start the application
            composeTestRule.setContent {
                SubmitClaimForm(
                    bitmap = resultingPhoto,
                    database = mockDatabase,
                    challenge = mockChallenge,
                    onClaimSubmitted = future::complete,
                    onFailure = future::completeExceptionally
                )
            }

            composeTestRule.onNodeWithText("Submit Claim")
                .performScrollTo()
                .assertIsDisplayed()
                .assertIsNotEnabled()

            // Assert the request was launched
            assertThat(counter, greaterThanOrEqualTo(1))

            // Resolve the future
            if (locationRequestFailed) {
                futureLocation.completeExceptionally(IllegalStateException())
                composeTestRule.waitForIdle()
                assertThat(future.isCompletedExceptionally, equalTo(true))
                return@use // no longer anything to test
            }

            futureLocation.complete(mockedLocation)

            // Ensure the button get updated
            assertThat(futureClaim.isDone, equalTo(false))
            composeTestRule.onNodeWithText("Submit Claim")
                .performScrollTo()
                .assertIsDisplayed()
                .assertIsEnabled()
                .performClick()

            composeTestRule.waitForIdle()

            assertThat(futureClaim.isDone, equalTo(true))
            assertThat(futureClaim.get().location, equalTo(mockedLocation))
            assertThat(future.isDone, equalTo(false))

            // Finally check that this was completed successfully
            if (submitClaimFailed) {
                taskClaimCompletionSource.setException(IllegalStateException())
                composeTestRule.waitForIdle()

                assertThat(future.isCompletedExceptionally, equalTo(true))
                return@use // no longer anything to test
            }

            taskClaimCompletionSource.setResult(futureClaim.get())
            composeTestRule.waitForIdle()

            assertThat(future.isDone, equalTo(true))
            assertThat(future.get(), equalTo(futureClaim.get()))
        }
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}