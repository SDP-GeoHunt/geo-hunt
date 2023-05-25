@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.geohunt.app.utility

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.sensor.ShowPermissionRequestPage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestPermissionPage {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNegativePermissionTest() = runTest {
        val deferred = CompletableDeferred<Unit>()

        composeTestRule.setContent {
            ShowPermissionRequestPage(
                launchPermissionRequest = { deferred.complete(Unit) },
                textToShow = "A small descriptive test to explain why",
                buttonText = "Request permission"
            )
        }

        composeTestRule.onNodeWithText("A small descriptive test to explain why")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Request permission")
            .assertIsDisplayed()
            .performClick()

        deferred.await()
    }
}
