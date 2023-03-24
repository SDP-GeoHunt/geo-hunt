package com.github.geohunt.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.BaseExpandableListAdapter
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.mocks.BaseMockDatabase
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockLazyRef
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.components.navigation.NavigationBar
import com.github.geohunt.app.ui.components.navigation.NavigationController
import com.google.android.gms.tasks.Tasks
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }

    @Test
    fun testChallengeViewDisplayUserProperly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val author = MockUser(
            displayName = "John wick",
            score = 48723
        )

        val challenge = MockChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            }
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            ChallengeView(challenge = challenge, onButtonBack = {}, {})
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("profile-icon")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Ensure click on challenge view image redirect to corresponding page
        composeTestRule.onNodeWithText("John wick")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Follow")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithTag("btn-notification")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("published just now")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun testChallengeViewDisplayChallengeProperly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val future = CompletableFuture<String>()

        val author = MockUser(
            displayName = "John wick",
            score = 48723
        )

        val challenge = MockChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            }
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            ChallengeView(challenge = challenge, {},
                displayImage = { iid ->
                    future.complete(iid)
                })
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithContentDescription("Challenge Image")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Ensure click on challenge view image redirect to corresponding page
        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        assertThat(future.join(),
            equalTo("img-ze5f16zaef1465"))
    }
}