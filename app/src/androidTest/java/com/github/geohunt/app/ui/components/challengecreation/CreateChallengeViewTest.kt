@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.geohunt.app.ui.components.challengecreation

import android.Manifest.permission
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.LocationRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.utils.intents.IntentsMocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CreateChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @get:Rule
    val grantLocationPermission: GrantPermissionRule = GrantPermissionRule.grant(
        permission.ACCESS_COARSE_LOCATION,
        permission.ACCESS_FINE_LOCATION
    )

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
    fun testCreateChallengeLaunchIntent() = runTest {
        val deferred = CompletableDeferred<Unit>()

        IntentsMocking.mock(
            contract = ActivityResultContracts.TakePicture(),
            callback = { uri, onResult ->
                deferred.complete(Unit)
            }
        ).use {
            composeTestRule.setContent {
                CreateNewChallenge()
            }

            deferred.await()
        }
    }

    @Test
    fun testCreateChallenge() {
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
                    val vm : CreateChallengeViewModel = viewModel(factory = CreateChallengeViewModel.Factory)
                    CreateNewChallenge(
                        viewModel = vm
                    )
                }

                // Emit location
                runBlocking {
                    mockedLocationFlow.emit(mockedLocation)
                }

                // Emit a location update
                composeTestRule.waitUntil(10000) {
                    composeTestRule.onAllNodesWithText("Create challenge")
                        .fetchSemanticsNodes().isNotEmpty()
                }

                // Test button is enabled
                composeTestRule.onNodeWithText("Create challenge")
                    .performScrollTo()
                    .assertIsDisplayed()
            }
        }
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}