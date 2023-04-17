package com.github.geohunt.app.ui.components

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
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.sensor.LocationRequestState
import com.github.geohunt.app.ui.components.challengecreation.CreateChallengeForm
import com.github.geohunt.app.ui.components.challengecreation.CreateNewChallenge
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
class CreateChallengeViewTest {
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
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun testChallengeOnSuccess() {
        testChallenge(locationRequestFailed = false, createChallengeFailed = false)
    }

    @Test
    fun testChallengeOnLocationFailure() {
        testChallenge(locationRequestFailed = true, createChallengeFailed = false)
    }

    @Test
    fun testChallengeOnChallengeCreationFailure() {
        testChallenge(locationRequestFailed = false, createChallengeFailed = true)
    }

    @Test
    fun testCreateChallengeLaunchIntent() {
        composeTestRule.setContent {
            CreateNewChallenge(
                database = object : BaseMockDatabase() {},
                onChallengeCreated = {},
                onFailure = {}
            )
        }

        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private fun testChallenge(
        locationRequestFailed : Boolean = false,
        createChallengeFailed : Boolean = false
    ) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<Challenge>()
        val resultingPhoto = createTestBitmap(context)
        val futureLocation = CompletableFuture<Location>()
        val futureChallenge = CompletableFuture<Challenge>()
        val taskChallengeCompletionSource = TaskCompletionSource<Challenge>()
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
            override fun createChallenge(
                thumbnail: Bitmap,
                location: Location,
                expirationDate: LocalDateTime?,
                difficulty: Challenge.Difficulty
            ): Task<Challenge> {
                val challenge = MockChallenge(
                    cid = "cid",
                    author = MockLazyRef<User>("uid") { TODO() },
                    publishedDate = LocalDateTime.now(),
                    expirationDate = expirationDate,
                    thumbnail = MockLazyRef("iid") { TODO() },
                    correctLocation = location,
                    claims = listOf()
                )
                futureChallenge.complete(challenge)
                return taskChallengeCompletionSource.task
            }
        }

        LocationRequestState.defaultFactory.mocked(locationRequestStateFactory).use {
            // Start the application
            composeTestRule.setContent {
                CreateChallengeForm(
                    bitmap = resultingPhoto,
                    database = mockDatabase,
                    onChallengeCreated = future::complete,
                    onFailure = future::completeExceptionally
                )
            }

            composeTestRule.onNodeWithText("Create challenge")
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
            assertThat(futureChallenge.isDone, equalTo(false))
            composeTestRule.onNodeWithText("Create challenge")
                .performScrollTo()
                .assertIsDisplayed()
                .assertIsEnabled()
                .performClick()

            composeTestRule.waitForIdle()

            assertThat(futureChallenge.isDone, equalTo(true))
            assertThat(futureChallenge.get().correctLocation, equalTo(mockedLocation))
            assertThat(future.isDone, equalTo(false))

            // Finally check that this was completed successfully
            if (createChallengeFailed) {
                taskChallengeCompletionSource.setException(IllegalStateException())
                composeTestRule.waitForIdle()

                assertThat(future.isCompletedExceptionally, equalTo(true))
                return@use // no longer anything to test
            }

            taskChallengeCompletionSource.setResult(futureChallenge.get())
            composeTestRule.waitForIdle()

            assertThat(future.isDone, equalTo(true))
            assertThat(future.get(), equalTo(futureChallenge.get()))
        }
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}