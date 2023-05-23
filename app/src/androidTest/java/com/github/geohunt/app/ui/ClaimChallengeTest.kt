@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.geohunt.app.ui
import android.Manifest.permission
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockClaim
import com.github.geohunt.app.mocks.MockClaimRepository
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.claims.ClaimChallenge
import com.github.geohunt.app.ui.components.claims.SubmitClaimViewModel
import com.github.geohunt.app.ui.components.utils.intents.IntentsMocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
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

    val mockedLocation = Location(0.0, 0.0)
    
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
    fun testClaimChallengeLaunchIntent() = runTest {
        val deferred = CompletableDeferred<Unit>()

        IntentsMocking.mock(
            contract = ActivityResultContracts.TakePicture(),
            callback = { uri, onResult ->
                deferred.complete(Unit)
            }
        ).use {
            composeTestRule.setContent {
                ClaimChallenge(cid = "163f921c-ML2eCQ52mAQlvCEQZ2n")
            }

            deferred.await()
        }
    }

    @Test
    fun testClaimChallenge() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
                    onResult(true)
                }
            ).run {
                // Start the application
                composeTestRule.setContent {
                    val vm = remember {
                        SubmitClaimViewModel(
                            MockChallengeRepository(),
                            MockClaimRepository()
                        )
                    }
                    val state = vm.challenge.collectAsState()

                    ClaimChallenge(
                        "1",
                        viewModel = vm
                    )
                }

                // Emit location
                runBlocking {
                    mockedLocationFlow.emit(mockedLocation)
                }

                composeTestRule.waitUntil {
                    composeTestRule.onAllNodesWithText("Submit claim")
                        .fetchSemanticsNodes().isNotEmpty()
                }

                // Test button is enabled
                composeTestRule.onNodeWithText("Submit claim")
                    .performScrollTo()
                    .assertIsDisplayed()
            }
        }
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}