package com.github.geohunt.app.ui.components.utility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.mocks.MockLiveLazyRef
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.ui.rememberLiveLazyRef
import com.google.android.gms.tasks.TaskCompletionSource
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RememberLazyRefTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val longString = "The only verdict is vengeance; a vendetta, held as a votive"

    @Test
    fun testFetchComponentWhenFailure()
    {
        val completionSource = TaskCompletionSource<String>()
        val lazyRef = MockLazyRef<String>("ref-id-d98d44f1200d7d45d29867fa27730666") {
            completionSource.task
        }
        var counter = 0
        var counter2 = 0

        composeTestRule.setContent {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()))
            {
                FetchComponent(lazyRef = {
                    counter += 1
                    lazyRef
                }, onFailure = { counter2 += 1 }) { value ->
                    Text(text = "result: $value")
                }
            }
        }

        // check counter has been incremented only once
        assertThat(counter, equalTo(1))
        assertThat(counter2, equalTo(0))

        // Assert still loading
        composeTestRule.onNodeWithText("An exception has occurred, failed to fetch reference @ref-id-d98d44f1200d7d45d29867fa27730666")
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("circular-progress-indicator")
            .assertIsDisplayed()

        // Secondly the object failed
        completionSource.setException(RuntimeException())
        composeTestRule.onNodeWithText("An exception has occurred")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("failed to fetch reference @ref-id-d98d44f1200d7d45d29867fa27730666")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("circular-progress-indicator")
            .assertDoesNotExist()

        assertThat(counter, equalTo(1))
        assertThat(counter2, equalTo(1))
    }

    @Test
    fun testFetchComponentWhenSucceed()
    {
        val completionSource = TaskCompletionSource<String>()
        val lazyRef = MockLazyRef<String>("ref-id-d98d44f1200d7d45d29867fa27730666") {
            completionSource.task
        }
        var counter = 0

        composeTestRule.setContent {
            FetchComponent(lazyRef = {
                counter += 1
                lazyRef
            }) { value ->
                Text(text = "result: $value")
            }
        }

        // check counter has been incremented only once
        assertThat(counter, equalTo(1))

        // Assert still loading
        composeTestRule.onNodeWithText("result: $longString")
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("circular-progress-indicator")
            .assertIsDisplayed()

        // Secondly complete the object
        completionSource.setResult(longString)

        composeTestRule.onNodeWithTag("circular-progress-indicator")
            .assertDoesNotExist()

        composeTestRule.onNodeWithText("result: $longString")
            .assertIsDisplayed()

        // check counter has been incremented only once
        assertThat(counter, equalTo(1))
    }

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

    @Test
    fun rememberLiveLazyRefUpdatesAccordingly() {
        val mockLiveLazyRef = MockLiveLazyRef("1", "machin")

        composeTestRule.setContent {
            val theRef = rememberLiveLazyRef { mockLiveLazyRef }
            theRef.value?.let { Text(it) }
        }

        composeTestRule.onNodeWithText("machin").assertIsDisplayed()
        mockLiveLazyRef.updateValue("newValue")
        composeTestRule.onNodeWithText("newValue").assertIsDisplayed()
    }
}