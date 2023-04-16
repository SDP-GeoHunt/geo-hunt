package com.github.geohunt.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.Logged
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.google.android.gms.tasks.Tasks
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }

    @Test
    fun testChallengeViewDisplayUserProperly() : Unit = Authenticator.authInstance.mocked(MockAuthenticator(MockConstant.Johny)).use {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val profilePicture = createTestBitmap(context)
        var route = ""
        val database = object : BaseMockDatabase() {

        }

        val author = MockUser(
            displayName = "John wick",
            score = 48723,
            profilePicture = InstantLazyRef("izufiozef", profilePicture)
        )

        val author2 = MockUser(
            displayName = "John Williams",
            score = 1248,
            profilePicture = InstantLazyRef("izufiozef", profilePicture)
        )

        val claim = object : Claim {
            override val id: String
                get() = "claim-ar4f165erf146a5c"
            override val challenge: LazyRef<Challenge>
                get() = MockLazyRef<Challenge>("cid") { TODO() }
            override val image: LazyRef<Bitmap>
                get() = MockLazyRef<Bitmap>("bitmap") { Tasks.forResult(createTestBitmap(context)) }
            override val user: LazyRef<User>
                get() = InstantLazyRef(author2.uid, author2)
            override val time: LocalDateTime
                get() = LocalDateTime.now()
            override val distance: Long
                get() = 62
            override val awardedPoints: Long
                get() = 100
            override val location: Location
                get() = Location()
        }

        val challenge = MockChallenge(
            author = MockLazyRef("user-f425zez6z4ef6z15f4") {
                Tasks.forResult(author)
            },
            thumbnail = MockLazyRef("img-ze5f16zaef1465") {
                Tasks.forResult(createTestBitmap(context))
            },
            claims = listOf(InstantLazyRef("claim", claim))
        )

        // Sets the composeTestRule content
        composeTestRule.setContent {
            database.Logged {
                ChallengeView(challenge = challenge, { route = it }, {
                    route = "../"
                }) { cid ->
                    route = "claim/$cid"
                }
            }
        }

        // Test stuff once loaded
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("profile-icon")
                .fetchSemanticsNodes()
                .size == 1
        }

        // Ensure click on challenge view image redirect to corresponding page
        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertIsDisplayed()
            .performClick()
        assertThat(route, equalTo("img-ze5f16zaef1465"))

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

        composeTestRule.onNodeWithText("John Williams")
            .performScrollTo()
            .assertExists()

        composeTestRule.onNodeWithText("62m")
            .performScrollTo()
            .assertExists()
    }

}