package com.github.geohunt.app

import android.Manifest.permission
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.components.CreateChallengeForm
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class CreateChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val grantCameraPermission: GrantPermissionRule = GrantPermissionRule.grant(permission.CAMERA)

    @get:Rule
    val grantLocationPermission : GrantPermissionRule = GrantPermissionRule.grant(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

    @Test
    fun testChallengeStateWithPermissions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<Challenge>()
        val resultingPhoto = createImageCaptureResult(context)

        val mockDatabase = object: BaseMockDatabase() {}

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