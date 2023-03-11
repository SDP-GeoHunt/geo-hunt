package com.github.geohunt.app

import android.Manifest.permission
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toFile
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.components.CreateChallengeForm
import com.github.geohunt.app.ui.components.CreateNewChallenge
import com.github.geohunt.app.utility.BitmapUtils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CreateChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @get:Rule
    val grantLocationPermission : GrantPermissionRule = GrantPermissionRule.grant(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

    fun mockLocation(location: Location) {

    }

    @Test
    fun testChallengeStateWithPermissions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<Challenge>()
        val createChallengeCalled = CompletableFuture<Challenge>()
        val resultingPhoto = createImageCaptureResult(context)
        val mockDatabase = object: BaseMockDatabase() {
            override fun createChallenge(
                thumbnail: Bitmap,
                location: Location,
                expirationDate: LocalDateTime?
            ): Task<Challenge> {
                val mockChallenge = MockChallenge(
                    "cid",
                    MockLazyRef<User>("uid") {
                        Tasks.forException<User>(IllegalArgumentException())
                    },
                    LocalDateTime.MAX,
                    expirationDate,
                    MockLazyRef<Bitmap>("iid") {
                        Tasks.forException<Bitmap>(IllegalArgumentException())
                    },
                    location,
                    listOf()
                )
                createChallengeCalled.complete(mockChallenge)
                return Tasks.forResult(mockChallenge)
            }
        }

        // Start the application
        composeTestRule.setContent {
            CreateChallengeForm(
                bitmap = resultingPhoto,
                database = mockDatabase,
                onChallengeCreated = future::complete,
                onFailure = future::completeExceptionally
            )
        }

        // Wait until
        composeTestRule.waitForIdle()

        // Check that the button create challenge is displayed
        composeTestRule.onNodeWithText("Create challenge")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        // Check that challengeCreate was called
        val mockChallenge = createChallengeCalled.orTimeout(20, TimeUnit.SECONDS).join()

        // Check that future was completed
        assertThat(mockChallenge,
            equalTo(future.orTimeout(20, TimeUnit.SECONDS).join()))
    }

    private fun createImageCaptureResult(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
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