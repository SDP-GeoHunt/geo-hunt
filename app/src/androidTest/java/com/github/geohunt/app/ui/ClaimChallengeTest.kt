package com.github.geohunt.app.ui
import android.Manifest.permission
import android.app.Application
import android.app.VoiceInteractor.CompleteVoiceRequest
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.components.challengecreation.CreateChallengeViewModel
import com.github.geohunt.app.ui.components.claims.ClaimChallenge
import com.github.geohunt.app.ui.components.claims.SubmitClaimForm
import com.github.geohunt.app.ui.components.claims.SubmitClaimViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
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
        val resultingBitmap = createTestBitmap(context)
        val future = CompletableFuture<Claim>()
        val future1 = CompletableDeferred<Unit>()

        val mockedLocationFlow = MutableSharedFlow<Location>()
        var viewModel : SubmitClaimViewModel? = null

        LocationRepository.DefaultLocationFlow.mocked(mockedLocationFlow).use {
            // Start the application
            composeTestRule.setContent {
                viewModel = viewModel<SubmitClaimViewModel>(factory = SubmitClaimViewModel.Factory)
                val state = viewModel!!.submittingState.collectAsState()

                if (state.value != SubmitClaimViewModel.State.AWAITING_CHALLENGE) {
                    LaunchedEffect(Unit) {
                        viewModel!!.withPhoto({ resultingBitmap }, future::completeExceptionally)
                        viewModel!!.startLocationUpdate(future::completeExceptionally)
                    }

                    if (state.value != SubmitClaimViewModel.State.AWAITING_LOCATION_PERMISSION ||
                        state.value != SubmitClaimViewModel.State.AWAITING_CAMERA) {
                        future1.complete(Unit)
                        SubmitClaimForm(
                            bitmap = resultingBitmap,
                            viewModel = viewModel!!,
                            onClaimSubmitted = future::complete,
                            onFailure = future::completeExceptionally
                        )
                    }
                }
            }

            viewModel!!.start("163f921c-ML2eCQ52mAQlvCEQZ2n", onFailure = {
                Assert.fail()
            })

            // Wait for idle
            composeTestRule.waitForIdle()

            mockedLocationFlow.emit(mockedLocation)
            future1.await()

            // Ensure the button get updated
            composeTestRule.onNodeWithText("Submit Claim")
                .performScrollTo()
                .assertIsDisplayed()
                .assertIsEnabled()
        }
    }

//    private fun testChallenge(
//        locationRequestFailed : Boolean = false,
//        submitClaimFailed : Boolean = false
//    ) {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val future = CompletableFuture<Claim>()
//        val resultingPhoto = createTestBitmap(context)
//        val futureLocation = CompletableFuture<Location>()
//        val futureClaim = CompletableFuture<Claim>()
//        val taskClaimCompletionSource = TaskCompletionSource<Claim>()
//        var counter = 0
//
//        val locationRequestStateFactory = @Composable {
//            object : LocationRequestState {
//                override val lastLocation: MutableState<Location?> = remember {
//                    mutableStateOf(null)
//                }
//
//                override fun requestLocation(): CompletableFuture<Location> {
//                    counter++
//                    futureLocation.thenAccept {
//                        lastLocation.value = it
//                    }
//                    return futureLocation
//                }
//            }
//        }
//
//        val mockDatabase = object: BaseMockDatabase() {
//            override fun submitClaim(
//                thumbnail: Bitmap,
//                challenge: Challenge,
//                location: Location
//            ): Task<Claim> {
//                val claim = MockClaim(
//                    id = "id",
//                    user = MockLazyRef<User>("uid") {  TODO() },
//                    challenge = MockLazyRef<Challenge>("cid") { TODO() },
//                    location = location,
//                    time = LocalDateTime.now(),
//                    image = MockLazyRef("iid") { TODO() },
//                    distance = 5,
//                    awardedPoints = 10
//                )
//                futureClaim.complete(claim)
//                return taskClaimCompletionSource.task
//            }
//        }
//
//        LocationRequestState.defaultFactory.mocked(locationRequestStateFactory).use {
//            // Start the application
//            composeTestRule.setContent {
//                SubmitClaimForm(
//                    bitmap = resultingPhoto,
//                    database = mockDatabase,
//                    challenge = mockChallenge,
//                    onClaimSubmitted = future::complete,
//                    onFailure = future::completeExceptionally
//                )
//            }
//
//            composeTestRule.onNodeWithText("Submit Claim")
//                .performScrollTo()
//                .assertIsDisplayed()
//                .assertIsNotEnabled()
//
//            // Assert the request was launched
//            assertThat(counter, greaterThanOrEqualTo(1))
//
//            // Resolve the future
//            if (locationRequestFailed) {
//                futureLocation.completeExceptionally(IllegalStateException())
//                composeTestRule.waitForIdle()
//                assertThat(future.isCompletedExceptionally, equalTo(true))
//                return@use // no longer anything to test
//            }
//
//            futureLocation.complete(mockedLocation)
//
//            // Ensure the button get updated
//            assertThat(futureClaim.isDone, equalTo(false))
//            composeTestRule.onNodeWithText("Submit Claim")
//                .performScrollTo()
//                .assertIsDisplayed()
//                .assertIsEnabled()
//                .performClick()
//
//            composeTestRule.waitForIdle()
//
//            assertThat(futureClaim.isDone, equalTo(true))
//            assertThat(futureClaim.get().location, equalTo(mockedLocation))
//            assertThat(future.isDone, equalTo(false))
//
//            // Finally check that this was completed successfully
//            if (submitClaimFailed) {
//                taskClaimCompletionSource.setException(IllegalStateException())
//                composeTestRule.waitForIdle()
//
//                assertThat(future.isCompletedExceptionally, equalTo(true))
//                return@use // no longer anything to test
//            }
//
//            taskClaimCompletionSource.setResult(futureClaim.get())
//            composeTestRule.waitForIdle()
//
//            assertThat(future.isDone, equalTo(true))
//            assertThat(future.get(), equalTo(futureClaim.get()))
//        }
//    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}