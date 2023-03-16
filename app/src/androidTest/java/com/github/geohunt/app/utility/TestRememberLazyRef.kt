package com.github.geohunt.app.utility

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.ui.rememberLazyRef
import com.google.android.gms.tasks.TaskCompletionSource
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestRememberLazyRef {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRememberLazyRef() {
        val completionSource = TaskCompletionSource<String>()
        val lazyRef = MockLazyRef<String>("lzyRef") {
            completionSource.task
        }

        composeTestRule.setContent {
            val lazyRefMutable = rememberLazyRef { lazyRef }
            Text(text = lazyRefMutable.value ?: "null")
        }

        // Assert the lazy ref is null
        composeTestRule.onNodeWithText("null")
            .assertIsDisplayed()
        assertThat(lazyRef.isLoaded, equalTo(false))

        completionSource.setResult("Society of Depressed People")

        // Assert the lazy ref is no longer null and UI was updated
        composeTestRule.onNodeWithText("Society of Depressed People")
            .assertIsDisplayed()
        assertThat(lazyRef.isLoaded, equalTo(true))
    }

    @Test
    fun testRememberLazyRefWithDefault() {
        val completionSource = TaskCompletionSource<String>()
        val lazyRef = MockLazyRef<String>("lzyRef") {
            completionSource.task
        }

        composeTestRule.setContent {
            val lazyRefMutable = rememberLazyRef("SDP") { lazyRef }
            Text(lazyRefMutable.value)
        }

        // Assert the lazy ref is null
        composeTestRule.onNodeWithText("SDP")
            .assertIsDisplayed()

        completionSource.setResult("Society of Depressed People")

        // Assert the lazy ref is no longer null and UI was updated
        composeTestRule.onNodeWithText("Society of Depressed People")
            .assertIsDisplayed()
    }
}