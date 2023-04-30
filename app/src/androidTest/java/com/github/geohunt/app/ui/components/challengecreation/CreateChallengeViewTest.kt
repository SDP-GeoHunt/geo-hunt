package com.github.geohunt.app.ui.components.challengecreation

import android.Manifest.permission
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
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
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.google.android.gms.tasks.TaskCompletionSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
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
        AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun testCreateChallengeLaunchIntent() {
        composeTestRule.setContent {
            CreateNewChallenge()
        }

        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @Test
    fun testCreateChallenge() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resultingBitmap = createTestBitmap(context)
        val future = CompletableFuture<Challenge>()

        val mockedLocationFlow = MutableSharedFlow<Location>()

        LocationRepository.DefaultLocationFlow.mocked(mockedLocationFlow).use {
            // Start the application
            composeTestRule.setContent {
                CreateChallengeForm(
                    bitmap = resultingBitmap,
                    viewModel = viewModel<CreateChallengeViewModel>(factory = CreateChallengeViewModel.Factory).apply {
                        this.withPhoto({ resultingBitmap }, future::completeExceptionally)
                        this.startLocationUpdate(future::completeExceptionally)
                    },
                    onSuccess = future::complete,
                    onFailure = future::completeExceptionally)
            }

            // Wait for idle
            composeTestRule.waitForIdle()

            runBlocking {
                mockedLocationFlow.emit(mockedLocation)
            }

            // Ensure the button get updated
            composeTestRule.onNodeWithText("Create challenge")
                .performScrollTo()
                .assertIsDisplayed()
                .assertIsEnabled()
                .performClick()

            composeTestRule.waitForIdle()

            // Ensure the button is now disabled
            composeTestRule.onNodeWithText("Create challenge")
                .assertDoesNotExist()
        }
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
}